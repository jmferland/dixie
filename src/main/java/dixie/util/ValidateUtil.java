package dixie.util;

import java.util.regex.Pattern;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

/**
 *
 * @author jferland
 */
public class ValidateUtil
{
	/**
	 * If the given {@code value} contains any of the {@code illegalChars} given,
	 * an error is added to the {@code ValidationErrors} given.
	 * 
	 * @param errors
	 * @param field
	 * @param value
	 * @param illegalChars
	 */
	public static void checkChars(ValidationErrors errors, String field, String value, CharSequence illegalChars)
	{
		if (value != null &&
			illegalChars != null &&
			illegalChars.length() > 0 &&
			value.contains(illegalChars))
		{
			StringBuffer tmp = new StringBuffer();
			for (int i = 0; i < illegalChars.length(); i++)
			{
				tmp.append(illegalChars.charAt(i));
				tmp.append(" ");
			}
			tmp.deleteCharAt(tmp.length());

			errors.add(field, new ScopedLocalizableError("validation", "illegalCharacters", tmp));
		}
	}

	/**
	 * If the given {@code value} does not match the given {@code pattern} then a
	 * an error is added to the {@code ValidationErrors} given. Note that if the
	 * {@code value} or {@code pattern} are {@code null} then they do not match and
	 * an error will be added.
	 * 
	 * @param errors
	 * @param field
	 * @param value
	 * @param pattern
	 * @param patternDescription
	 */
	public static void matchPattern(ValidationErrors errors, String field, String value, Pattern pattern, String patternDescription)
	{
		if (value == null ||
			pattern.matcher(value).matches())
		{
			errors.add(field, new ScopedLocalizableError("validation", "patternNotMatched", patternDescription));
		}
	}
}
