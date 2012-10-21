package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.Category;

public interface CategoryDao extends Dao<Category>
{
	/**
	 * Find a Category by name.
	 *
	 * @param name the Category name.
	 * @return a (only one should exist) Category with the given name, or
	 * null if none with the supplied name exists.
	 */
	public Category readByName(String name) throws DaoException;

	/**
	 * Finds the root Category and returns it.
	 *
	 * @return the root.
	 */
	public Category readRoot() throws DaoException;
}
