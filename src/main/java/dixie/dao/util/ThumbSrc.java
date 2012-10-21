package dixie.dao.util;

/**
 *
 * @author jferland
 */
public interface ThumbSrc
{
	/**
	 * Get a unique URL (relative or absolute, depending on the implementation)
	 * for this thumbnail.
	 * 
	 * @return an absolute or relative source path/ URL to the thumbnail.
	 */
	public String getSrc();

	/**
	 * Tests whether the source is valid/ exists. May not be 100% accurate,
	 * depending on the implementation.
	 * 
	 * @return true if the source exists, otherwise false.
	 */
	public boolean getIsValid();
}
