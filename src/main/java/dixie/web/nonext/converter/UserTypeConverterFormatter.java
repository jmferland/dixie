package dixie.web.nonext.converter;

import com.google.inject.Inject;
import dixie.dao.DaoManager;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.User;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.format.Formatter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Users should be identified by their (unique) username, not an id.
 * 
 * @author jferland
 */
public class UserTypeConverterFormatter implements TypeConverter<User>, Formatter<User>
{
	private static final Logger logger = Logger.getLogger(UserTypeConverterFormatter.class);
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@Override
	public User convert(final String input, Class<? extends User> type, Collection<ValidationError> errors)
	{
		User user = null;

		try
		{
			user = (User) this.daoManager.transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return daoManager.getUserDao().readByUsername(input);
				}
			});
		}
		catch (Exception e)
		{
			logger.fatal("User converter error: " + e);
		}

		if (user == null)
		{
			errors.add(new ScopedLocalizableError("converter.user", "notFound"));
		}

		return user;
	}

	@Override
	public String format(User model)
	{
		return model.getUsername();
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