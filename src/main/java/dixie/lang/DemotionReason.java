package dixie.lang;

/**
 *
 * @author jferland
 */
public enum DemotionReason implements EnumOption
{
	// These numbers are refrenced in other locations, update all at once:
	// (1) StripesResources.properties
	// (2) Database
	// BE CAREFUL AND THINK IT THROUGH BEFORE CHANGING THESE VALUES, IS IT
	// EVEN NECESSARY?
	NO_REASON(0, "no reason"),
	DUPLICATE(1, "duplicate"),
	SPAM(2, "spam"),
	BAD_TAGS(3, "bad tags"),
	INACCURATE(4, "inaccurate"),
	INAPPROPRIATE(5, "inappropriate");
	private final int value;
	private final String label;

	DemotionReason(int id, String label)
	{
		this.value = id;
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
		return label;
	}
}
