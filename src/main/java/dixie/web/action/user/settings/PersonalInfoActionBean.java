package dixie.web.action.user.settings;

import dixie.web.action.*;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.User;
import dixie.util.ValidateUtil;
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
@UrlBinding("/settings/personal")
public class PersonalInfoActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(PersonalInfoActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/settings/personal.ftl";
	private static final String ILLEGAL_NAME_CHARS = "<>";
	private static final String FIRST_NAME_FIELD = "firstName";
	private static final String LAST_NAME_FIELD = "lastName";
	@Validate(maxlength = 100)
	public String firstName;
	@Validate(maxlength = 100)
	public String lastName;
	// TODO: allow user to change birthday?

	@ValidationMethod(priority = 1)
	public void validateName(ValidationErrors errors)
	{
		ValidateUtil.checkChars(errors, FIRST_NAME_FIELD, firstName, ILLEGAL_NAME_CHARS);
		ValidateUtil.checkChars(errors, LAST_NAME_FIELD, lastName, ILLEGAL_NAME_CHARS);
	}

	@DontValidate
	@DefaultHandler
	public Resolution view()
	{
		firstName = getLoggedInUser().getFirstName();
		lastName = getLoggedInUser().getLastName();

		return new ForwardResolution(VIEW);
	}

	public Resolution update()
	{
		final User.Builder user = getLoggedInUser().getBuilder();
		user.firstName = firstName;
		user.lastName = lastName;

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

			getContext().getMessages().add(new LocalizableMessage("message.settingsUpdated"));
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
