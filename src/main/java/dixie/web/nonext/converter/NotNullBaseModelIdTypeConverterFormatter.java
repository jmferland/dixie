package dixie.web.nonext.converter;

import dixie.model.BaseModel;
import java.util.Collection;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.ValidationError;

/**
 * Convert and format anything that extends BaseModel. Convert an ID into a
 * BaseModel and format a BaseModel into an ID String. This does not allow
 * for null values.
 *
 * @author jferland
 */
public class NotNullBaseModelIdTypeConverterFormatter extends NullBaseModelIdTypeConverterFormatter
{
	@Override
	public BaseModel convert(final String input, Class<? extends BaseModel> type, Collection<ValidationError> errors)
	{
		BaseModel result = super.convert(input, type, errors);

		if (result == null)
		{
			errors.add(new ScopedLocalizableError("converter.baseModel", "notFound"));
		}

		return result;
	}
}