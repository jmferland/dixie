package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.Captcha;
import java.util.UUID;

public interface CaptchaDao extends Dao<Captcha>
{
	/**
	 * Find a Captcha by UUID.
	 *
	 * @param name the Category name.
	 * @return a (only one should exist) Captcha with the given UUID, or
	 * null if none with the supplied UUID exists.
	 */
	public Captcha readByUUID(UUID uuid) throws DaoException;

	/**
	 * Delete a Captcha by UUID.
	 */
	public void deleteByUUID(UUID uuid) throws DaoException;

	/**
	 * Delete Captcha-s created before the given time.
	 *
	 * @param timeInMillisSinceEpoch the time, in milliseconds, since the last
	 * epoch.
	 */
	public void deleteByCreatedBefore(long timeInMillisSinceEpoch) throws DaoException;
}
