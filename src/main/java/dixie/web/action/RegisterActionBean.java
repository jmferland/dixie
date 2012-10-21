package dixie.web.action;

import dixie.web.action.user.ViewUserActionBean;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.User;
import dixie.util.ValidateUtil;
import dixie.web.action.helper.CaptchaHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.EmailTypeConverter;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;

/**
 * Registration.
 *
 * @author jferland
 */
@UrlBinding("/register")
public class RegisterActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(RegisterActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/register.ftl";
	private static final Pattern VALID_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
	private static final String VALID_USERNAME_PATTERN_DESCRIPTION = "alpha-numeric characters only.";
	private static final String USERNAME_FIELD = "user.username";
	private static final String PASSWORD2_FIELD = "password2";
	private static final String EMAIL_FIELD = "user.email";
	private static final String BIRTHDATE_FIELD = "birthDate";
	private static final int MINIMUM_AGE = 13;
	private long birthDate;
	@ValidateNestedProperties(
	{
		@Validate(field = "username", required = true, minlength = 4, maxlength = 16),
		@Validate(field = "email", required = true, maxlength = 40, converter = EmailTypeConverter.class)
	})
	public User.Builder user;
	@Validate(required = true, minlength = 6, maxlength = 128)
	public String password;
	@Validate(required = true)
	public String password2;
	@Validate(required = true, minvalue = 0, maxvalue = 11)
	public int birthMonth;
	@Validate(required = true, minvalue = 1, maxvalue = 31)
	public int birthDay;
	@Validate(required = true, minvalue = 1900)
	public int birthYear;
	@Validate
	public String referrer;
	@ValidateNestedProperties(
	{
		@Validate(field = "uuid"),
		@Validate(field = "answer")
	})
	public CaptchaHelper captcha = new CaptchaHelper(this);

	@ValidationMethod(priority = 1)
	public void validateUsername(ValidationErrors errors)
	{
		ValidateUtil.matchPattern(errors, USERNAME_FIELD, user.username, VALID_USERNAME_PATTERN, VALID_USERNAME_PATTERN_DESCRIPTION);

		User existingUser = null;
		try
		{
			existingUser = (User) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return getDaoManager().getUserDao().readByUsername(user.username);
				}
			});
		}
		catch (Exception e)
		{
			logger.debug("Username validation error: " + e);
			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));
			return;
		}

		if (existingUser != null)
		{
			errors.add(USERNAME_FIELD, new ScopedLocalizableError("validation", "usernameDuplicate"));
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
					return getDaoManager().getUserDao().readByEmail(user.email);
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

	@ValidationMethod(priority = 3)
	public void validateBirthDate(ValidationErrors errors)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(birthYear, birthMonth, birthDay);
		birthDate = calendar.getTimeInMillis();

		calendar.add(Calendar.YEAR, MINIMUM_AGE);

		Date now = new Date();

		if (now.after(calendar.getTime()) == false)
		{
			errors.add(BIRTHDATE_FIELD, new ScopedLocalizableError("validation", "birthDateLessThanThirteen"));
			return;
		}
	}

	@ValidationMethod(priority = 4)
	public void validatePassword(ValidationErrors errors)
	{
		if (password.equals(password2) == false)
		{
			errors.add(PASSWORD2_FIELD, new ScopedLocalizableError("validation", "passwordMisMatch"));
			return;
		}
	}

	@ValidationMethod(priority = 5)
	public void validateCaptcha(ValidationErrors errors)
	{
		captcha.validate();
	}

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		// If the user is already logged in, send them somewhere else.
		if (getLoggedInUser() != null)
		{
			return new RedirectResolution(HomeActionBean.class);
		}

		return new ForwardResolution(VIEW);
	}

	public Resolution register()
	{
		user.createdOn = new Date().getTime();
		user.setPassword(password);
		user.birthDate = birthDate;

		User newUser = null;
		try
		{
			newUser = (User) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return getDaoManager().getUserDao().create(user.build());
				}
			});
		}
		catch (Exception e)
		{
			ValidationErrors errors = new ValidationErrors();

			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));

			getContext().setValidationErrors(errors);
			return getContext().getSourcePageResolution();
		}

		getContext().setUser(newUser);

		if (referrer != null)
		{
			return new RedirectResolution(referrer);
		}

		return new RedirectResolution(ViewUserActionBean.class).addParameter("user", newUser.
				getUsername());
	}
}