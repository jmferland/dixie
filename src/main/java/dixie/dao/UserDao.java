package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.User;

public interface UserDao extends Dao<User>, Pageable<User>
{
	/**
	 * Find a user by username.
	 *
	 * @param username required username.
	 * @return a (only one should exist) User with the given username, or
	 * null if none with the supplied username exists.
	 */
	public User readByUsername(String username) throws DaoException;

	/**
	 * Find a user by email.
	 *
	 * @param email required email.
	 * @return a (only one should exist) User with the given email, or
	 * null if none with the supplied username exists.
	 */
	public User readByEmail(String email) throws DaoException;
}
