package dixie.dao.order;

import dixie.lang.EnumOption;

/**
 * 
 * @author jferland
 */
public enum CommentOrder implements EnumOption
{
	OLDEST(0, "oldest first"),
	NEWEST(1, "newest first"),
	CONTROVERSIAL(2, "most controversial"),
	AGREEABLE(3, "least controversial"),
	BEST(4, "highest rated"),
	WORST(5, "lowest rated");
	private final int value;
	private final String label;

	CommentOrder(int value, String label)
	{
		this.value = value;
		this.label = label;
	}

	@Override
	public int getIntValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return this.label;
	}
}