package dixie.dao.order;

import dixie.lang.EnumOption;

/**
 * 
 * @author jferland
 */
public enum TagOrder implements EnumOption
{
	COMMON(0, "common"),
	RARE(1, "rare"),
	ALPHABETICAL(2, "alphabetical"),
	REVERSE_ALPHABETICAL(3, "reverse alphabetical"),
	NEWEST(4, "newest"),
	OLDEST(5, "oldest");
	private final int value;
	private final String label;

	TagOrder(int value, String label)
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