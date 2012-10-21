package dixie.dao;

import com.google.inject.Inject;
import dixie.dao.db.DbManager;
import dixie.dao.exception.DaoException;
import dixie.dao.util.LinkThumbsUtil;
import dixie.model.BaseModel;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * A DaoManager should exist per session/ thread as Dao operations should be
 * single-threaded and sequential.
 *
 * Provides access to DAOs and transaction support.
 *
 * NOTE: DAOs must be aware of the manager to get access to other DAOs.
 * 
 * @author jferland
 */
public class DaoManager
{
	private static final Logger logger = Logger.getLogger(DaoManager.class.getName());
	private Map<Class<? extends BaseModel>, Dao> daoMap = new HashMap<Class<? extends BaseModel>, Dao>();
	private DbManager db;
	private LinkThumbsUtil linkThumbsUtil;
	private LinkDao linkDao;
	private UserDao userDao;
	private TagDao tagDao;
	private CommentDao commentDao;
	private CategoryDao categoryDao;
	private FormatDao formatDao;
	private CaptchaDao captchaDao;

	@Inject
	public void initialize(DbManager db, LinkThumbsUtil linkThumbsUtil, LinkDao linkDao, UserDao userDao, TagDao tagDao, CommentDao commentDao, CategoryDao categoryDao, FormatDao formatDao, CaptchaDao captchaDao)
	{
		this.db = db;
		this.linkThumbsUtil = linkThumbsUtil;

		this.linkDao = registerDao(linkDao);
		this.userDao = registerDao(userDao);
		this.tagDao = registerDao(tagDao);
		this.commentDao = registerDao(commentDao);
		this.categoryDao = registerDao(categoryDao);
		this.formatDao = registerDao(formatDao);
		this.captchaDao = registerDao(captchaDao);
	}

	/**
	 * Registers a Dao by giving it a copy of this DaoManager and associating
	 * the Dao with a Model. Private and final so it cannot be overriden by
	 * a derived class.
	 * 
	 * @param <T>
	 * @param dao the DAO.
	 * @return the same unmodified but now registered DAO passed in.
	 */
	private final <T extends Dao> T registerDao(T dao)
	{
		this.daoMap.put(dao.getModelClass(), dao);
		dao.setDaoManager(this);
		return dao;
	}

	/**
	 * Get the Dao associated with the given Model Class.
	 * 
	 * @param modelClass the Class of the Model.
	 * @return the Dao associated with this Model.
	 */
	public Dao getDao(Class<? extends BaseModel> modelClass)
	{
		return this.daoMap.get(modelClass);
	}

	public DbManager getDbManager()
	{
		return db;
	}

	public LinkThumbsUtil getLinkThumbsUtil()
	{
		return linkThumbsUtil;
	}

	public FormatDao getFormatDao()
	{
		return formatDao;
	}

	public CaptchaDao getCaptchaDao()
	{
		return captchaDao;
	}

	public CategoryDao getCategoryDao()
	{
		return categoryDao;
	}

	public CommentDao getCommentDao()
	{
		return commentDao;
	}

	public LinkDao getLinkDao()
	{
		return linkDao;
	}

	public TagDao getTagDao()
	{
		return tagDao;
	}

	public UserDao getUserDao()
	{
		return userDao;
	}

	/**
	 * Executes the given DaoCommand, transaction style. If any exceptions
	 * are thrown, the transaction is rolled back.
	 *
	 * @param command the DaoCommand to execute.
	 * @return exactly what the DaoCommand execute() method returns.
	 * @throws java.lang.DaoException
	 */
	public Object transaction(DaoCommand command) throws DaoException
	{
		Object result = null;

		try
		{
			db.connect();

			db.startTransaction();
			logger.debug("Start Transaction");

			result = command.execute();

			db.commit();
			logger.debug("End Transaction");

			db.disconnect();
		}
		// No need to wrap a DaoException (it IS the wrapper).
		catch (DaoException e)
		{
			rollback(e);
			throw e;
		}
		// Must catch all exceptions so we can rollback.
		catch (Exception e)
		{
			rollback(e);
			throw new DaoException(e);
		}
		finally
		{
			db.destroy();
		}

		return result;
	}

	/**
	 * Attempt to rollback the transaction, given the reason/ exception for
	 * the rollback.
	 *
	 * @param e the reason for the rollback.
	 */
	private void rollback(Exception e)
	{
		logger.error("Rolling back because of error: " + e);

		try
		{
			db.rollback();
		}
		catch (Exception e2)
		{
			logger.error("Uh oh! Rollback failed: " + e2);
		}
	}

	/**
	 * Executes the given DaoCommand, with auto-commit. Any statement that is
	 * successfully executed is final and will NOT be rolled back.
	 *
	 * @param command the DaoCommand to execute.
	 * @return exactly what the DaoCommand execute() method returns.
	 * @throws dixie.dao.exception.DaoException
	 */
	public Object autoCommit(DaoCommand command) throws DaoException
	{
		Object result = null;

		try
		{
			db.connect();

			db.autoCommit();
			logger.debug("Start AutoCommit");

			result = command.execute();
			logger.debug("End AutoCommit");

			db.disconnect();
		}
		// No need to wrap a DaoException (it IS the wrapper).
		catch (DaoException e)
		{
			rollback(e);
			throw e;
		}
		// Must catch all exceptions so we can rollback.
		catch (Exception e)
		{
			rollback(e);
			throw new DaoException(e);
		}
		finally
		{
			db.destroy();
		}

		return result;
	}
}
