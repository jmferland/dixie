package dixie.dao.db;

import com.google.inject.internal.ImmutableList;
import dixie.dao.FormatDao;
import dixie.dao.db.vocab.FormatVocab;
import dixie.dao.exception.DaoException;
import dixie.model.Format;
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
 * Provides cached database access for Formats.
 *
 * @author jferland
 */
public class FormatDbDao extends BaseDbDao<Format> implements FormatDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());
	// No cache object should be accessed directly. Instead use getCache().
	private static FormatDbCache latestCache;
	private FormatDbCache localCache;

	@Override
	public Format create(Format format) throws DaoException
	{
		logger.debug("create: Format=" + format);
		throw new UnsupportedOperationException();
	}

	@Override
	public Format read(long id) throws DaoException
	{
		logger.debug("read: id=" + id);

		Format format = this.getCache().idLookup.get(BigInteger.valueOf(id));

		if (format != null)
		{
			logger.debug("found: Format=" + format);
		}
		else
		{
			logger.debug("Format not found");
		}

		return format;
	}

	@Override
	public Format readByFolder(String folder) throws DaoException
	{
		logger.debug("readByName: folder=" + folder);

		Format format = this.getCache().folderLookup.get(folder);

		if (format != null)
		{
			logger.debug("found: Format=" + format);
		}
		else
		{
			logger.debug("Format not found");
		}

		return format;
	}

	@Override
	public List<Format> readAll() throws DaoException
	{
		logger.debug("readAll");

		return this.getCache().allFormats;
	}

	@Override
	public void update(Format format) throws DaoException
	{
		logger.debug("update: Format=" + format);
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(long id) throws DaoException
	{
		logger.debug("delete: id=" + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public Format build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		return this.read(resultSet.getLong(FormatVocab.ID));
	}

	/**
	 * Builds an immutable Format list, populating the supplied indexes as it
	 * goes.
	 *
	 * @param resultSet ordered information.
	 * @param idLookup id to Format map.
	 * @param folderLookup name to Format map.
	 * @return a Category tree.
	 * @throws java.sql.SQLException
	 */
	private ImmutableList<Format> buildList(ResultSet resultSet, Map<BigInteger, Format> idLookup, Map<String, Format> folderLookup) throws SQLException
	{
		List<Format> allFormats = new ArrayList<Format>();

		while (resultSet.next() != false)
		{
			long id = resultSet.getLong(FormatVocab.ID);
			String name = resultSet.getString(FormatVocab.NAME);
			String folder = resultSet.getString(FormatVocab.FOLDER);

			Format format = new Format(id, name, folder);

			idLookup.put(BigInteger.valueOf(id), format);
			folderLookup.put(folder, format);

			allFormats.add(format);
		}

		return ImmutableList.copyOf(allFormats);
	}

	/**
	 * Useful for determining whether a table has changed.
	 *
	 * @return a CRC of the entire Format table.
	 * @throws dixie.dao.exception.DaoException
	 */
	private long crcTable() throws DaoException
	{
		long crc = 0;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call crcFormatTable(?)");
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
	private FormatDbCache getCache() throws DaoException
	{
		// Always use the same cache within an instance of this DAO.
		if (this.localCache != null)
		{
			return this.localCache;
		}

		// If the most recently updated cache is still valid then use it.
		if (FormatDbDao.latestCache != null &&
			FormatDbDao.latestCache.version == this.crcTable())
		{
			this.localCache = FormatDbDao.latestCache;
			return this.localCache;
		}

		// Otherwise get the most recent Format information. This part is
		// synchronized since there is little purpose in multiple threads
		// reading at the same time since this data rarely (if ever) changes.
		synchronized (this)
		{
			// Check AGAIN here since it's possible 2 threads were waiting
			// to enter the synchronized block and only one should need to
			// pull the data.
			long version = this.crcTable();

			if (FormatDbDao.latestCache != null &&
				FormatDbDao.latestCache.version == version)
			{
				this.localCache = FormatDbDao.latestCache;
				return this.localCache;
			}

			try
			{
				// TODO: Not sure concurrency is necessary here. A Format is immutable.
				Map<BigInteger, Format> idLookup = new ConcurrentHashMap<BigInteger, Format>();
				Map<String, Format> folderLookup = new ConcurrentHashMap<String, Format>();

				CallableStatement stmt = this.db.prepareCall("call readEveryFormat()");
				ResultSet resultSet = this.db.executeQuery(stmt);

				List<Format> allFormats = this.buildList(resultSet, idLookup, folderLookup);

				this.localCache = new FormatDbCache(allFormats, idLookup, folderLookup, version);

				// Our cache is now the most recent one, so let everyone else
				// take advantage of the work we did.
				FormatDbDao.latestCache = this.localCache;
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
	private class FormatDbCache
	{
		public final List<Format> allFormats;
		public final Map<BigInteger, Format> idLookup;
		public final Map<String, Format> folderLookup;
		public final long version;

		public FormatDbCache(List<Format> allFormats, Map<BigInteger, Format> idLookup, Map<String, Format> folderLookup, long version)
		{
			this.allFormats = allFormats;
			this.idLookup = idLookup;
			this.folderLookup = folderLookup;
			this.version = version;
		}
	}
}