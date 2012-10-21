package dixie.model;

import com.google.inject.internal.ImmutableList;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An immutable class for representing a Category.
 *
 * @author jferland
 */
public final class Category extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	public static final Category DEFAULT_CATEGORY = new Category();
	private final Map<BigInteger, Category> idLookup;
	private final long parentId;
	private final String name;
	private final ImmutableList<Category> children;

	private Category()
	{
		this(BaseModel.NULL_ID, BaseModel.NULL_ID, BaseModel.EMPTY_STRING, null, null);
	}

	public Category(long id)
	{
		this(id, BaseModel.NULL_ID, BaseModel.EMPTY_STRING, null, null);
	}

	public Category(long id, long parentId, String name,
					List<Category> children, Map<BigInteger, Category> idLookup)
	{
		super(id);
		this.parentId = parentId;
		this.name = name;

		if (children != null)
		{
			this.children = ImmutableList.copyOf(children);
		}
		else
		{
			this.children = ImmutableList.copyOf(new ArrayList<Category>());
		}

		this.idLookup = idLookup;
	}

	public List<Category> getChildren()
	{
		return children;
	}

	public String getName()
	{
		return name;
	}

	public Category getParent()
	{
		if (this.idLookup == null)
		{
			return null;
		}

		return this.idLookup.get(BigInteger.valueOf(this.parentId));
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
		final Category other = (Category) obj;
		if (this.parentId != other.parentId)
		{
			return false;
		}
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
		{
			return false;
		}
		if (this.children != other.children && (this.children == null || !this.children.equals(other.children)))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = this.hashCode();
		hash = 83 * hash + (int) (this.parentId ^ (this.parentId >>> 32));
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 83 * hash + (this.children != null ? this.children.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("parent", this.parentId);
		map.put("name", this.name);
		map.put("children", this.children.size());
		return map.toString();
	}
}