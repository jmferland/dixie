package dixie.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseModel implements Serializable
{
	protected static final String EMPTY_STRING = "";
	public static final long NULL_ID = 0;
	private final long id;

	public BaseModel(long id)
	{
		this.id = id;
	}

	public long getId()
	{
		return this.id;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		// Do not want to call equals on super if this class directly extends
		// Object since that will require reference equality, which we do not
		// want.
		final BaseModel other = (BaseModel) obj;
		if (this.id != other.id)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.id);
		return map.toString();
	}
}
