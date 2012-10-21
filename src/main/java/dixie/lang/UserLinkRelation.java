package dixie.lang;

/**
 *
 * @author jferland
 */
public enum UserLinkRelation
{
	SUBMITTED(0),
	FAVORITED(1),
	PROMOTED(2);
	private final int intValue;

	private UserLinkRelation(int intValue)
	{
		this.intValue = intValue;
	}

	public int getIntValue()
	{
		return intValue;
	}
}
