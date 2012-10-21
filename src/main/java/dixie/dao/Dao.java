package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;

public interface Dao<MODEL extends BaseModel>
{
	/**
	 * Create the given model. Return a copy of the given model with the
	 * correct id assigned to it.
	 * @param model model to create.
	 * @return true on success, othewrise false.
	 */
	public MODEL create(MODEL model) throws DaoException;

	/**
	 * Read and return the model corresponding to the given id.
	 * @param id model identifier.
	 * @return model with the given id or null if not found.
	 */
	public MODEL read(long id) throws DaoException;

	/**
	 * Updates the given persisting model.
	 * @param model model to update.
	 * @return true on success, otherwise false.
	 */
	public void update(MODEL model) throws DaoException;

	/**
	 * Remove the given id from persistance.
	 * @param id model identifier.
	 * @return true on success, otherwise false.
	 */
	public void delete(long id) throws DaoException;

	/**
	 * Give the DAO a copy of the DaoManager so it can access other DAOs and
	 * obtain a connection to whatever DataSource it uses.
	 * @param daoManager the DaoManager.
	 */
	public void setDaoManager(DaoManager daoManager);

	/**
	 * Gets the Class of the supported Model.
	 * @return the Model Class this DAO supports.
	 */
	public Class<MODEL> getModelClass();
}
