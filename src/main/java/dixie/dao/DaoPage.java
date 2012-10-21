package dixie.dao;

import com.google.inject.internal.ImmutableList;
import dixie.model.BaseModel;
import java.util.List;

/**
 * A result for Dao operations that return more than one BaseModel extension.
 *
 * @param <MODEL> result class that extends BaseModel.
 */
public final class DaoPage<MODEL extends BaseModel>
{
	private List<MODEL> page;
	private int total;

	public DaoPage(List<MODEL> result, int size)
	{
		this.page = ImmutableList.copyOf(result);
		this.total = size;
	}

	/**
	 * An immutable list of MODEL-s that cannot be mutated.
	 *
	 * @return an immutable list of MODEL-s.
	 */
	public List<MODEL> getPage()
	{
		return this.page;
	}

	public int getTotal()
	{
		return this.total;
	}
}