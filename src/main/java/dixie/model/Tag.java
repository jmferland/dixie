package dixie.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jferland
 */
public final class Tag extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	public static final int NAME_MAX_LENGTH = 25;
	public static final int NAME_MIN_LENGTH = 2;
	private final String name;
	private final long count;

	public Tag(String name)
	{
		this(BaseModel.NULL_ID, name, 0);
	}

	public Tag(long id, String name, long count)
	{
		super(id);
		this.name = name;
		this.count = count;
	}

	public long getCount()
	{
		return count;
	}

	public String getName()
	{
		return name;
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
		if (super.equals(obj) == false)
		{
			return false;
		}
		final Tag other = (Tag) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
		{
			return false;
		}
		if (this.count != other.count)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 67 * hash + (int) (this.count ^ (this.count >>> 32));
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("name", this.name);
		map.put("count", this.count);
		return map.toString();
	}
}
