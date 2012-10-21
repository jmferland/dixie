package dixie.dao.order;

import dixie.lang.EnumOption;

/**
 *
 * @author jferland
 */
public enum LinkOrder implements EnumOption
{
	NEWEST(0, "newest"),
	OLDEST(1, "oldest"),
	BEST(2, "best rated"),
	WORST(3, "worst rated");
	private final int value;
	private final String label;

	LinkOrder(int value, String label)
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