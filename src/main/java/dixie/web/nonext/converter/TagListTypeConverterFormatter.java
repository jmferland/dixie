package dixie.web.nonext.converter;

import dixie.lang.TagList;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Convert a list of Tag names into a list of Tags. The Tags need not
 * exist in the "persistant store"/ database.
 *
 * @author jferland
 */
public class TagListTypeConverterFormatter implements TypeConverter<TagList>, Formatter<TagList>
{
	private static final Logger logger = Logger.getLogger(TagListTypeConverterFormatter.class);
	private String delimiter = ", ";

	@Override
	public TagList convert(String input, Class<? extends TagList> type, Collection<ValidationError> errors)
	{
		// TODO: if we check and enforce tag name size, composition, and uniqueness
		// at the conversion level, then we can run into a problem where, on a Wizard,
		// if a user goes back a step, they are unable to edit the field that has a
		// conversion validation error, and so become stuck (unless they use the
		// browser's back button).
		//
		// I'm not sure what the best way to solve this is. So far I have (1) do
		// validation in AddLinkActionBean or (2) don't allow backing up in wizards.
		TagList tags = TagList.split(input);
		return tags.getList().size() > 0 ? tags : null;
	}

	@Override
	public String format(TagList tagList)
	{
		return tagList.joinNames(delimiter);
	}

	@Override
	public void setLocale(Locale locale)
	{
	}

	@Override
	public void setFormatType(String formatType)
	{
	}

	@Override
	public void setFormatPattern(String formatPattern)
	{
		if (formatPattern == null)
		{
			return;
		}

		this.delimiter = formatPattern;
	}

	@Override
	public void init()
	{
	}
}