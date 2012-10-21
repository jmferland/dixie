package dixie.dao.db;

import dixie.dao.CategoryDao;
import dixie.dao.db.vocab.CategoryVocab;
import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;
import dixie.model.Category;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 * Provides cached database access for Categories.
 *
 * @author jferland
 */
public class CategoryDbDao extends BaseDbDao<Category> implements CategoryDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());
	// No cache object should be accessed directly. Instead use getCache().
	private static CategoryDbCache latestCache;
	private CategoryDbCache localCache;

	@Override
	public Category create(Category category) throws DaoException
	{
		logger.debug("create: Category=" + category);
		throw new UnsupportedOperationException();
	}

	@Override
	public Category read(long id) throws DaoException
	{
		logger.debug("read: id=" + id);

		Category category = this.getCache().idLookup.get(BigInteger.valueOf(id));

		if (category != null)
		{
			logger.debug("found: Category=" + category);
		}
		else
		{
			logger.debug("Category not found");
		}

		return category;
	}

	@Override
	public Category readByName(String name) throws DaoException
	{
		logger.debug("readByName: name=" + name);

		Category category = this.getCache().nameLookup.get(name);

		if (category != null)
		{
			logger.debug("found: Category=" + category);
		}
		else
		{
			logger.debug("Category not found");
		}

		return category;
	}

	@Override
	public Category readRoot() throws DaoException
	{
		logger.debug("readRoot");

		return this.getCache().root;
	}

	@Override
	public void update(Category category) throws DaoException
	{
		logger.debug("update: Category=" + category);
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(long id) throws DaoException
	{
		logger.debug("delete: id=" + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public Category build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		return this.read(resultSet.getLong(CategoryVocab.ID));
	}

	/**
	 * Builds a Category tree. This function expects the ResultSet to be
	 * in an order that would be made if the Category tree were walked
	 * with a depth-first search (i.e. the children of a node always come
	 * immediately after the node).
	 *
	 * @param resultSet depth-first search ordered ResultSet.
	 * @param idLookup id to Category map.
	 * @param nameLookup name to Category map.
	 * @param aParentId parent ID a child Category must have.
	 * @return a Category tree.
	 * @throws java.sql.SQLException
	 */
	private List<Category> buildTree(ResultSet resultSet, Map<BigInteger, Category> idLookup, Map<String, Category> nameLookup, long aParentId) throws SQLException
	{
		List<Category> list = new ArrayList<Category>();

		while (resultSet.next() != false)
		{
			long id = resultSet.getLong(CategoryVocab.ID);
			long parentId = resultSet.getLong(CategoryVocab.PARENT_ID);
			String name = resultSet.getString(CategoryVocab.NAME);

			if (parentId == aParentId)
			{
				Category category = new Category(id, parentId, name, buildTree(resultSet, idLookup, nameLookup, id), idLookup);
				list.add(category);
				idLookup.put(BigInteger.valueOf(id), category);
				nameLookup.put(name, category);
			}
			else
			{
				resultSet.previous();
				break;
			}
		}

		return list;
	}

	/**
	 * Useful for determining whether a table has changed.
	 * 
	 * @return a CRC of the entire Category table.
	 * @throws dixie.dao.exception.DaoException
	 */
	private long crcTable() throws DaoException
	{
		long crc = 0;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call crcCategoryTable(?)");
			stmt.registerOutParameter(1, Types.INTEGER);

			this.db.executeQuery(stmt);

			crc = stmt.getLong(1);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return crc;
	}
	
	/**
	 * Checks if the cache needs to be updated and if so does so. However, the
	 * cache will only be updated once per instance of this class to support
	 * consistency within a session (it is assumed that one DAO instance will
	 * exist per session). That is, this will return the same cache object
	 * every call per instance of this Class.
	 *
	 * The updated (or not if unnecessary) cache object is returned so that
	 * a local reference to a CONSISTENT set of cached values can be used.
	 * This is to prevent references being used from two different updates/
	 * reads.
	 *
	 * @return an Object containing cached values.
	 * @throws dixie.dao.exception.DaoException
	 */
	private CategoryDbCache getCache() throws DaoException
	{
		// Always use the same cache within an instance of this DAO.
		if (this.localCache != null)
		{
			return this.localCache;
		}

		// If the most recently updated cache is still valid then use it.
		if (CategoryDbDao.latestCache != null &&
			CategoryDbDao.latestCache.version == this.crcTable())
		{
			this.localCache = CategoryDbDao.latestCache;
			return this.localCache;
		}

		// Otherwise get the most recent Category information. This part is
		// synchronized since there is little purpose in multiple threads
		// reading at the same time since this data rarely (if ever) changes.
		synchronized (this)
		{
			// Check AGAIN here since it's possible 2 threads were waiting
			// to enter the synchronized block and only one should need to
			// pull the data.
			long version = this.crcTable();
			
			if (CategoryDbDao.latestCache != null &&
				CategoryDbDao.latestCache.version == version)
			{
				this.localCache = CategoryDbDao.latestCache;
				return this.localCache;
			}

			try
			{
				// TODO: Not sure concurrency is necessary here. A Category is immutable.
				Map<BigInteger, Category> idLookup = new ConcurrentHashMap<BigInteger, Category>();
				Map<String, Category> nameLookup = new ConcurrentHashMap<String, Category>();

				CallableStatement stmt = this.db.prepareCall("call readEveryCategoryDepthFirst()");
				ResultSet resultSet = this.db.executeQuery(stmt);

				List<Category> rootChildren = this.buildTree(resultSet, idLookup, nameLookup, 0);

				// Debatable whether to store a root in the database or
				// not, but I doubt "Root" will ever actually appear
				// anywhere, it's just easier than having a handle on
				// a list of root children.
				Category root = new Category(BaseModel.NULL_ID, BaseModel.NULL_ID, "Root", rootChildren, idLookup);

				this.localCache = new CategoryDbCache(idLookup, nameLookup, root, version);

				// Our cache is now the most recent one, so let everyone else
				// take advantage of the work we did.
				CategoryDbDao.latestCache = this.localCache;
			}
			catch (SQLException e)
			{
				logger.fatal(e);
				throw new DaoException(e);
			}
		}

		return this.localCache;
	}

	/**
	 * Say n Objects need to be cached and n > 1. If n static references are
	 * stored and are inter-dependent it is possible to update them while they
	 * are being read from. This could break dependencies.
	 *
	 * So, all references are stored in a single Object so a local copy (to
	 * all contained objects) may be stored and used throughout.
	 *
	 * I allow direct member access (instead of getters and setters) since this
	 * is a private class, a simple one, and really only to aide with multi-
	 * threading.
	 */
	private class CategoryDbCache
	{
		public final Map<BigInteger, Category> idLookup;
		public final Map<String, Category> nameLookup;
		public final Category root;
		public final long version;

		public CategoryDbCache(Map<BigInteger, Category> idLookup, Map<String, Category> nameLookup, Category root, long version)
		{
			this.idLookup = idLookup;
			this.nameLookup = nameLookup;
			this.root = root;
			this.version = version;
		}
	}
}