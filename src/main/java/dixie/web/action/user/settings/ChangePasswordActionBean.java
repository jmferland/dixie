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
@UrlBinding("/settings/password")
public class ChangePasswordActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(ChangePasswordActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/settings/password.ftl";
	private static final String OLD_PASSWORD_FIELD = "oldPassword";
	private static final String NEW_PASSWORD2_FIELD = "newPassword2";
	@Validate(required = true)
	public String oldPassword;
	@Validate(required = true, minlength = 6, maxlength = 128)
	public String newPassword;
	@Validate(required = true)
	public String newPassword2;

	@ValidationMethod(priority = 1)
	public void validatePassword(ValidationErrors errors)
	{
		if (getLoggedInUser().checkPassword(oldPassword) == false)
		{
			errors.add(OLD_PASSWORD_FIELD, new ScopedLocalizableError("validation", "passwordIncorrect"));
			return;
		}

		if (newPassword.equals(newPassword2) == false)
		{
			errors.add(NEW_PASSWORD2_FIELD, new ScopedLocalizableError("validation", "passwordMisMatch"));
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
		user.setPassword(newPassword);

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

			getContext().getMessages().add(new LocalizableMessage("message.passwordChanged"));
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
