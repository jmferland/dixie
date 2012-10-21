package dixie.util;

import java.text.Normalizer;

/**
 *
 * @author jferland
 */
public class StringUtil
{
	public static final char URL_SAFE_SUBSTITUTE = '-';

	/**
	 * Return a url-safe version of the given String. That is, only URL-legal
	 * characters will be returned. Specifically, only A-Z, a-z, 0-9 and '-'
	 * characters are allowed, anything else will be converted to '-' with
	 * leading and trailing '-' removed. Two or more '-' in a row will be
	 * reduced to a single '-'.
	 *
	 * @param string the string to make url-safe.
	 * @return a url-save version of the given String.
	 */
	public static String urlSafe(String string)
	{
		char[] chars = Normalizer.normalize(string, Normalizer.Form.NFD).toCharArray();

		// Find the first alpha-numeric character.
		int first = 0;
		for (; first < chars.length; first++)
		{
			if (StringUtil.isAlphaNumeric(chars[first]))
			{
				break;
			}
		}

		// Find the last alpha-numeric character.
		int last = chars.length - 1;
		for (; last >= 0; last--)
		{
			if (StringUtil.isAlphaNumeric(chars[last]))
			{
				break;
			}
		}

		// Build the string.
		StringBuffer buffer = new StringBuffer();
		for (int i = first; i <= last; i++)
		{
			// Any alpha-numeric character is valid.
			if (StringUtil.isAlphaNumeric(chars[i]))
			{
				buffer.append(chars[i]);
			}
			// Otherwise if there is a previous character and it is not already
			// a substitute, then add the substitute character. This is to
			// allow no more than one substitute character in a row.
			else if (buffer.length() > 0 &&
					 buffer.charAt(buffer.length() - 1) != StringUtil.URL_SAFE_SUBSTITUTE)
			{
				buffer.append(StringUtil.URL_SAFE_SUBSTITUTE);
			}
		}

		return buffer.toString();
	}

	/**
	 * Answers the question "is this char alpha-numeric?"
	 *
	 * @param c the char to test.
	 * @return true if the given char is within A-Z, a-z, or 0-9, inclusive.
	 * Otherwise false.
	 */
	public static boolean isAlphaNumeric(char c)
	{
		if ((c >= 'a' && c <= 'z') ||
			(c >= 'A' && c <= 'Z') ||
			(c >= '0' && c <= '9'))
		{
			return true;
		}

		return false;
	}
}
