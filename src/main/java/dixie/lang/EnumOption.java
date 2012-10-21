package dixie.lang;

/**
 * Necessary to format and convert options easily. We expect implementing
 * classes to be Enum; however, I'm not sure how to enforce this.
 *
 * @author jferland
 */
public interface EnumOption
{
	/**
	 * Get the integer value that corresponds to this option's label.
	 *
	 * @return integer value.
	 */
	public int getIntValue();

	/**
	 * Get the label that corresponds to this option's value.
	 *
	 * @return label.
	 */
	@Override
	public String toString();
}