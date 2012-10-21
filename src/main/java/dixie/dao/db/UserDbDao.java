package dixie.dao.db;

import dixie.dao.DaoPage;
import dixie.dao.UserDao;
import dixie.dao.db.vocab.UserVocab;
import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;
import dixie.model.User;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
public class UserDbDao extends BaseDbDao<User> implements UserDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());

	@Override
	public User create(User user) throws DaoException
	{
		logger.debug("create: User=" + user);

		User result = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call createUser(?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPasswordHash());
			stmt.setString(3, user.getEmail());
			stmt.setLong(4, user.getBirthDate().getTime());
			stmt.setString(5, user.getFirstName());
			stmt.setString(6, user.getLastName());
			stmt.setInt(7, user.getAccountStatus());
			stmt.setLong(8, user.getCreatedOn().getTime());
			stmt.registerOutParameter(9, Types.INTEGER);

			this.db.executeUpdate(stmt);

			long id = stmt.getInt(9);

			User.Builder builder = user.getBuilder();
			builder.id = id;
			result = builder.build();
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public void update(User user) throws DaoException
	{
		logger.debug("update: User=" + user);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call updateUser(?,?,?,?,?,?,?,?)");
			stmt.setLong(1, user.getId());
			stmt.setString(2, user.getUsername());
			stmt.setString(3, user.getPasswordHash());
			stmt.setString(4, user.getEmail());
			stmt.setLong(5, user.getBirthDate().getTime());
			stmt.setString(6, user.getFirstName());
			stmt.setString(7, user.getLastName());
			stmt.setInt(8, user.getAccountStatus());

			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public DaoPage<User> page(int offset, int count, int sortBy, int order) throws DaoException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public User readByUsername(String username) throws DaoException
	{
		logger.debug("readByUsername: username=" + username);

		User user = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call readUserByUsername(?)");
			stmt.setString(1, username);

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				user = build(rs);
				logger.debug("found: User=" + user);
			}
			else
			{
				logger.debug("User not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return user;
	}

	@Override
	public User readByEmail(String email) throws DaoException
	{
		logger.debug("readByEmail: email=" + email);

		User user = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call readUserByEmail(?)");
			stmt.setString(1, email);

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				user = build(rs);
				logger.debug("found: User=" + user);
			}
			else
			{
				logger.debug("User not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return user;
	}

	@Override
	public User build(ResultSet resultSet) throws SQLException
	{
		if (resultSet == null)
		{
			return null;
		}

		User user = null;

		// Important check since User information __could__ be null (ex: a LEFT
		// JOIN). So if it is null, use the defaults User has when created.
		long id = resultSet.getLong(UserVocab.ID);
		if (id != BaseModel.NULL_ID)
		{
			User.Builder builder = new User.Builder();
			builder.id = id;
			builder.username = resultSet.getString(UserVocab.USERNAME);
			builder.passwordHash = resultSet.getString(UserVocab.PASSWORD_HASH);
			builder.email = resultSet.getString(UserVocab.EMAIL);
			builder.birthDate = resultSet.getLong(UserVocab.BIRTH_DATE);
			builder.firstName = resultSet.getString(UserVocab.FIRST_NAME);
			builder.lastName = resultSet.getString(UserVocab.LAST_NAME);
			builder.accountStatus = resultSet.getInt(UserVocab.ACCOUNT_STATUS);
			builder.createdOn = resultSet.getLong(UserVocab.CREATED_ON);

			user = builder.build();
		}
		else
		{
			user = User.DEFAULT_USER;
		}

		return user;
	}
}
