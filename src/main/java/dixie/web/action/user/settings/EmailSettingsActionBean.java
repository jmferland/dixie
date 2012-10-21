package dixie.web.action.user.settings;

import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.User;
import dixie.web.action.*;
import javax.annotation.security.PermitAll;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
@PermitAll
@UrlBinding("/settings/email")
// TODO: send email before changing. At least password is required (;
public class EmailSettingsActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(EmailSettingsActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/settings/email.ftl";
	private static final String PASSWORD_FIELD = "password";
	private static final String EMAIL_FIELD = "email";
	@Validate(required = true)
	public String password;
	@Validate(required = true, maxlength = 40, converter = EmailTypeConverter.class)
	public String email;

	@ValidationMethod(priority = 1)
	public void validatePassword(ValidationErrors errors)
	{
		if (getLoggedInUser().checkPassword(password) == false)
		{
			errors.add(PASSWORD_FIELD, new ScopedLocalizableError("validation", "passwordIncorrect"));
			return;
		}
	}

	@ValidationMethod(priority = 2)
	public void validateEmail(ValidationErrors errors)
	{
		User existingUser = null;
		try
		{
			existingUser = (User) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return getDaoManager().getUserDao().readByEmail(email);
				}
			});
		}
		catch (Exception e)
		{
			logger.debug("Email validation error: " + e);
			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));
			return;
		}

		if (existingUser != null)
		{
			errors.add(EMAIL_FIELD, new ScopedLocalizableError("validation", "emailDuplicate"));
			return;
		}
	}

	@DontValidate
	@DefaultHandler
	public Resolution view()
	{
		return new ForwardResolution(VIEW);
	}

	public Resolution update()
	{
		final User.Builder user = getLoggedInUser().getBuilder();
		user.email = email;

		try
		{
			getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					getDaoManager().getUserDao().update(user.build());
					return null;
				}
			});

			getContext().getMessages().add(new LocalizableMessage("message.emailChanged"));
		}
		catch (Exception e)
		{
			ValidationErrors errors = new ValidationErrors();

			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));

			getContext().setValidationErrors(errors);
			return getContext().getSourcePageResolution();
		}

		return new ForwardResolution(VIEW);
	}
}
