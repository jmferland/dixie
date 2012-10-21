package dixie.web.nonext.converter;

import com.google.inject.Inject;
import dixie.dao.DaoManager;
import dixie.lang.EnumOption;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Convert and format anything that extends EnumOption. Convert an int value
 * into a EnumOption and format a EnumOption into a String.
 *
 * We expect anything that implements EnumOption to be an Enum.
 *
 * @author jferland
 */
public class EnumOptionTypeConverterFormatter implements TypeConverter<EnumOption>, Formatter<EnumOption>
{
	private static final Logger logger = Logger.getLogger(EnumOptionTypeConverterFormatter.class);
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@Override
	public EnumOption convert(final String input, Class<? extends EnumOption> type, Collection<ValidationError> errors)
	{
		EnumOption result = null;

		if (type.isEnum())
		{
			int value = Integer.parseInt(input);
			EnumOption[] orders = type.getEnumConstants();

			if (orders != null)
			{
				for (EnumOption order : orders)
				{
					if (value == order.getIntValue())
					{
						result = order;
						break;
					}
				}
			}
		}

		if (result == null)
		{
			errors.add(new ScopedLocalizableError("converter.daoOrder", "notFound"));
		}

		return result;
	}

	@Override
	public String format(EnumOption model)
	{
		return BigInteger.valueOf(model.getIntValue()).toString();
	}

	@Override
	public void setFormatType(String formatType)
	{
	}

	@Override
	public void setLocale(Locale locale)
	{
	}

	@Override
	public void setFormatPattern(String formatPattern)
	{
	}

	@Override
	public void init()
	{
	}
}