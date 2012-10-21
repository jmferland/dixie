package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.Format;
import java.util.List;

public interface FormatDao extends Dao<Format>
{
	/**
	 * Find a Format by its folder name (as it might appear in a path
	 * or URL).
	 *
	 * @param name the Format folder name.
	 * @return a (only one should exist) Format with the given name, or
	 * null if none with the supplied name exists.
	 */
	public Format readByFolder(String name) throws DaoException;

	/**
	 * Finds every Format and returns them as an immutable List.
	 *
	 * @return a list of every Format.
	 */
	public List<Format> readAll() throws DaoException;
}
