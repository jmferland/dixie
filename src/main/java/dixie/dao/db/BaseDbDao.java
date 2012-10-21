package dixie.dao.db;

import dixie.dao.DaoManager;
import dixie.dao.BaseDao;
import dixie.dao.Dao;
import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
abstract public class BaseDbDao<MODEL extends BaseModel> extends BaseDao<MODEL>
{
	private static final long serialVersionUID = -4984150946474083985L;
	private static final Logger logger = Logger.getLogger(BaseDbDao.class.getName());
	private String modelName;
	protected DbManager db;

	public BaseDbDao()
	{
		this.modelName = this.modelClass.getSimpleName();
	}

	@Override
	public void setDaoManager(DaoManager daoManager)
	{
		super.setDaoManager(daoManager);

		this.db = daoManager.getDbManager();
	}

	/**
	 * Try to build a Model from (1) the ResultSet (2) whatever DAO it can
	 * be read from or (3) the default given. This is to promote DAO de-
	 * coupling, but I wouldn't recommend taking advantage of #2.
	 * 
	 * @param <T>
	 * @param resultSet what to first try to build the Model from.
	 * @param defaultValue what to default to if the Model cannot be read.
	 * @return a Model.
	 * @throws java.sql.SQLException
	 * @throws dixie.dao.exception.DaoException
	 */
	public <T extends BaseModel> T build(ResultSet resultSet, T defaultModel) throws SQLException, DaoException
	{
		T result = null;
		
		if (defaultModel != null)
		{
			Dao dao = this.daoManager.getDao(defaultModel.getClass());

			if (dao instanceof BaseDbDao)
			{
				result = (T) ((BaseDbDao) dao).build(resultSet);
			}
			else if (defaultModel.getId() != BaseModel.NULL_ID)
			{
				result = (T) dao.read(defaultModel.getId());
			}
		}

		if (result == null)
		{
			result = defaultModel;
		}

		return result;
	}

	/**
	 * Builds a Model from a ResultSet. When implementing, care should be taken
	 * to allow for missing information as not all result sets will contain
	 * the same amount of information. But there should still be some non-empty
	 * subset of required information.
	 *
	 * @param resultSet data for Model.
	 * @return a Model.
	 * @throws javax.sql.SQLException
	 * @throws dixie.dao.exception.DaoException
	 */
	abstract public MODEL build(ResultSet resultSet) throws SQLException, DaoException;

	/**
	 * Generic base implementation of read.
	 *
	 * @param id of Model.
	 * @return Model or null.
	 * @throws dixie.dao.exception.DaoException
	 */
	public MODEL read(long id) throws DaoException
	{
		logger.debug("read: " + this.modelName + ".id= " + id);

		if (id < 0 || id == BaseModel.NULL_ID)
		{
			return null;
		}

		MODEL model = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call read" + this.modelName + "(?)");
			stmt.setLong(1, id);

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				model = build(rs);
				logger.debug("found: " + this.modelName + "=" + model);
			}
			else
			{
				logger.debug(this.modelName + " not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return model;
	}

	/**
	 * Generic base implementation of delete.
	 *
	 * @param id of Model.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void delete(long id) throws DaoException
	{
		logger.debug("delete: " + this.modelName + ".id= " + id);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call delete" + this.modelName + "(?)");
			stmt.setLong(1, id);

			db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}
}
