package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;

/**
 *
 * @author jferland
 */
public interface Pageable<MODEL extends BaseModel>
{
	/**
	 * Get a page of BaseModels.
	 *
	 * @param offset the BaseModel to start count-ing from.  Zero-indexed.
	 * @param count the number of BaseModels per page.
	 * @param sortBy the data (ex: column) to sort by.
	 * @param order ascending or descending
	 */
	public DaoPage<MODEL> page(int offset, int count, int sortBy, int order) throws DaoException;
}