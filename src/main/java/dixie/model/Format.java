package dixie.model;

import java.util.HashMap;
import java.util.Map;

/**
 * An immutable class for representing a Format.
 *
 * @author jferland
 */
public final class Format extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	public static final Format NONE = new Format();
	public static final Format ALL = new Format(BaseModel.NULL_ID, "All", "all");
	private final String name;
	private final String folder;

	private Format()
	{
		this(BaseModel.NULL_ID, BaseModel.EMPTY_STRING, BaseModel.EMPTY_STRING);
	}

	public Format(long id)
	{
		this(id, BaseModel.EMPTY_STRING, BaseModel.EMPTY_STRING);
	}

	public Format(long id, String name, String folder)
	{
		super(id);
		this.name = name;
		this.folder = folder;
	}

	public String getName()
	{
		return name;
	}

	public String getFolder()
	{
		return folder;
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
		final Format other = (Format) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
		{
			return false;
		}
		if ((this.folder == null) ? (other.folder != null) : !this.folder.equals(other.folder))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = this.hashCode();
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 83 * hash + (this.folder != null ? this.folder.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("name", this.name);
		map.put("folder", this.folder);
		return map.toString();
	}
}