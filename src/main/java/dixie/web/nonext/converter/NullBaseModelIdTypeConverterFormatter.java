package dixie.web.nonext.converter;

import com.google.inject.Inject;
import dixie.dao.DaoManager;
import dixie.dao.Dao;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.BaseModel;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Convert and format anything that extends BaseModel. Convert an ID into a
 * BaseModel and format a BaseModel into an ID String. This allows for null
 * values.
 *
 * @author jferland
 */
public class NullBaseModelIdTypeConverterFormatter implements TypeConverter<BaseModel>, Formatter<BaseModel>
{
	private static final Logger logger = Logger.getLogger(NullBaseModelIdTypeConverterFormatter.class);
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@Override
	public BaseModel convert(final String input, Class<? extends BaseModel> type, Collection<ValidationError> errors)
	{
		final Dao dao = this.daoManager.getDao(type);

		if (dao == null)
		{
			errors.add(new ScopedLocalizableError("converter.baseModel", "notManaged"));
			return null;
		}

		BaseModel result = null;

		try
		{
			result = (BaseModel) this.daoManager.transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return dao.read((new BigInteger(input)).longValue());
				}
			});
		}
		catch (Exception e)
		{
			logger.fatal(e);
		}

		return result;
	}

	@Override
	public String format(BaseModel model)
	{
		return BigInteger.valueOf(model.getId()).toString();
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