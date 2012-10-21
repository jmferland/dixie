package dixie.dao.db;

import dixie.dao.CaptchaDao;
import dixie.dao.db.vocab.CaptchaVocab;
import dixie.dao.exception.DaoException;
import dixie.model.Captcha;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * 
 * @author jferland
 */
public class CaptchaDbDao extends BaseDbDao<Captcha> implements CaptchaDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());

	@Override
	public Captcha create(Captcha captcha) throws DaoException
	{
		logger.debug("create: Captcha=" + captcha);

		Captcha result = null;

		try
		{
			// Create captcha.
			CallableStatement stmt = this.db.prepareCall("call createCaptcha(?,?,?,?,?)");
			stmt.setLong(1, captcha.getUuid().getMostSignificantBits());
			stmt.setLong(2, captcha.getUuid().getLeastSignificantBits());
			stmt.setString(3, captcha.getAnswer());
			stmt.setLong(4, captcha.getSeed());
			stmt.setLong(5, captcha.getCreatedOn().getTime());

			this.db.executeUpdate(stmt);

			result = captcha;
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Captcha read(long id) throws DaoException
	{
		logger.debug("read: id=" + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public Captcha readByUUID(UUID uuid) throws DaoException
	{
		logger.debug("readByUUID: UUID=" + uuid);

		Captcha result = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call readCaptchaByUUID(?,?)");
			stmt.setLong(1, uuid.getMostSignificantBits());
			stmt.setLong(2, uuid.getLeastSignificantBits());

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				result = build(rs);
				logger.debug("found: Captcha=" + result);
			}
			else
			{
				logger.debug("Captcha not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public void update(Captcha captcha) throws DaoException
	{
		logger.debug("update: Captcha=" + captcha);
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(long id) throws DaoException
	{
		logger.debug("delete: id=" + id);
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteByUUID(UUID uuid) throws DaoException
	{
		logger.debug("deleteByUUID: UUID=" + uuid);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call deleteCaptchaByUUID(?,?)");
			stmt.setLong(1, uuid.getMostSignificantBits());
			stmt.setLong(2, uuid.getLeastSignificantBits());

			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void deleteByCreatedBefore(long timeInMillisSinceEpoch) throws DaoException
	{
		logger.debug("deleteCaptchaByCreatedBefore: timeInMillisSinceEpoch=" + timeInMillisSinceEpoch);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call deleteCaptchaByCreatedBefore(?)");
			stmt.setLong(1, timeInMillisSinceEpoch);

			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public Captcha build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		long mostSigUuidBits = resultSet.getLong(CaptchaVocab.UUID_MOST_SIG);
		long leastSigUuidBits = resultSet.getLong(CaptchaVocab.UUID_LEAST_SIG);
		
		UUID uuid = new UUID(mostSigUuidBits, leastSigUuidBits);
		String answer = resultSet.getString(CaptchaVocab.ANSWER);
		long seed = resultSet.getLong(CaptchaVocab.SEED);
		long createdOn = resultSet.getLong(CaptchaVocab.CREATED_ON);

		return new Captcha(uuid, answer, seed, createdOn);
	}
}