package dixie.dao.db;

import com.google.inject.Inject;
import dixie.dao.exception.DaoException;
import dixie.dao.exception.DaoException.DaoError;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * A place to tuck away the proper setup and tear down details of connecting to a database
 * (pool, specifically).
 *
 * connect() and disconnect() should be called at the absolute startTransaction and end of
 * a try {} block, respectively. destroy() should be called in a finally block
 * to guarantee that everything that is closed can be closed but not closed
 * twice.
 *
 * Also note that individual statements, result sets, etc. should NOT be closed
 * outside of the DbManager. The DbManager owns and will close everything it
 * returns (creates).
 *
 * @see http://tomcat.apache.org/tomcat-6.0-doc/jndi-datasource-examples-howto.html
 * @author jferland
 */
public class DbManager
{
	private final DataSource dataSource;
	private final List<PreparedStatement> statements;
	private final List<ResultSet> resultSets;
	private Connection connection;

	@Inject
	public DbManager(DataSource dataSource)
	{
		this.dataSource = dataSource;
		this.statements = new ArrayList<PreparedStatement>();
		this.resultSets = new ArrayList<ResultSet>();
	}

	/**
	 * Attempt to connect to the data source.
	 *
	 * @throws java.sql.SQLException
	 */
	public void connect() throws SQLException
	{
		if (this.connection == null)
		{
			this.connection = this.dataSource.getConnection();
		}
	}

	/**
	 * Start a transaction.
	 * 
	 * @throws java.sql.SQLException
	 */
	public void startTransaction() throws SQLException
	{
		this.connection.setAutoCommit(false);
	}

	/**
	 * Rollback a transaction and restore the old AutoCommit value.
	 *
	 * @throws java.sql.SQLException
	 */
	public void rollback() throws SQLException
	{
		this.connection.rollback();
		this.connection.setAutoCommit(true);
	}

	/**
	 * Commit a transaction and restore the old AutoCommit value.
	 *
	 * @throws java.sql.SQLException
	 */
	public void commit() throws SQLException
	{
		this.connection.commit();
		this.connection.setAutoCommit(true);
	}

	/**
	 * Ensure statements are committed whenever called (not transactional) by
	 * setting Auto-Commit to true.
	 * 
	 * @throws java.sql.SQLException
	 */
	public void autoCommit() throws SQLException
	{
		this.connection.setAutoCommit(true);
	}

	/**
	 * Throws an exception if the connection is not in a transactional state.
	 *
	 * @throws dixie.dao.exception.DaoException
	 * @throws java.sql.SQLException
	 */
	public void checkTransaction() throws DaoException, SQLException
	{
		if (this.connection.getAutoCommit() == true)
		{
			throw new DaoException("Transaction required.");
		}
	}

	/**
	 * Prepare and return a CallableStatement. This class will track it and disconnect any
	 * prepared statements with this classes disconnect() call.
	 *
	 * @param sql SQL
	 * @return a CallableStatement.
	 * @throws java.sql.SQLException
	 */
	public CallableStatement prepareCall(String sql) throws SQLException
	{
		CallableStatement statement = this.connection.prepareCall(sql);
		this.statements.add(statement);
		return statement;
	}

	/**
	 * Executes the query and returns the result set but keeps a copy to disconnect.
	 *
	 * @param statement to execute.
	 * @return a ResultSet.
	 * @throws java.sql.SQLException
	 */
	public ResultSet executeQuery(PreparedStatement statement) throws SQLException
	{
		ResultSet resultSet = statement.executeQuery();
		this.resultSets.add(resultSet);
		return resultSet;
	}

	/**
	 * Executes the query and returns the number of effected rows.  Should be used
	 * with INSERT, DELETE and UPDATE queries only.
	 *
	 * @param statement to execute.
	 * @return number of effected rows.
	 * @throws java.sql.SQLException
	 */
	public int executeUpdate(PreparedStatement statement) throws SQLException
	{
		return statement.executeUpdate();
	}

	/**
	 * Attempt to disconnect any result sets, opened statments and then the connection.
	 * If any one of these fails, stop and throw the exception.
	 *
	 * @throws java.sql.SQLException
	 */
	public void disconnect() throws SQLException
	{
		// Walk backwards since we're deleting as we go. Also remove before
		// closing so we don't try and fail twice.

		// Close result sets.
		for (int i = this.resultSets.size() - 1; i >= 0; i--)
		{
			ResultSet rs = this.resultSets.get(i);
			this.resultSets.remove(i);
			rs.close();
		}

		// Close statements.
		for (int i = this.statements.size() - 1; i >= 0; i--)
		{
			PreparedStatement stmt = this.statements.get(i);
			this.statements.remove(i);
			stmt.close();
		}

		this.connection.close();	// Return to connection pool
		this.connection = null;		// Make sure we don't disconnect it twice
	}

	/**
	 * Final attempt to disconnect each result set, statement and the connection.
	 * Must be called and so should go in a finally block.
	 */
	public void destroy()
	{
		// Clear ResultSet, PreparedStatement, etc. Lists after they are closed
		// since the DbManager may be reused.

		// Close result sets.
		for (int i = 0; i < this.resultSets.size(); i++)
		{
			if (this.resultSets.get(i) != null)
			{
				try
				{
					this.resultSets.get(i).close();
				}
				catch (SQLException e)
				{
				}
				this.resultSets.set(i, null);
			}
		}
		this.resultSets.clear();

		// Close statements.
		for (int i = 0; i < this.statements.size(); i++)
		{
			if (this.statements.get(i) != null)
			{
				try
				{
					this.statements.get(i).close();
				}
				catch (SQLException e)
				{
				}
				this.statements.set(i, null);
			}
		}
		this.statements.clear();

		// Return connection to pool, if we haven't already.
		if (this.connection != null)
		{
			try
			{
				this.connection.close();
			}
			catch (SQLException e)
			{
			}
			this.connection = null;
		}
	}
}