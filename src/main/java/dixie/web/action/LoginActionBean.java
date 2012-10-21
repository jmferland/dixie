package dixie.web.action;

import dixie.web.action.user.ViewUserActionBean;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.User;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;

/**
 * Login.
 *
 * @author jferland
 */
@UrlBinding("/login")
public class LoginActionBean extends BaseActionBean
{
	private static final String VIEW = "/WEB-INF/ftl/user/register.ftl";
	private static final Logger logger = Logger.getLogger(LoginActionBean.class);
	private User user;
	@Validate(required = true)
	public String username;
	@Validate(required = true)
	public String password;
	@Validate
	public String referrer;

	@ValidationMethod
	public void validateUser(ValidationErrors errors)
	{
		try
		{
			user = (User) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return getDaoManager().getUserDao().readByUsername(username);
				}
			});
		}
		catch (Exception e)
		{
			logger.debug("Username lookup error: " + e);
			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));
			return;
		}
		
		if (this.user == null)
		{
			errors.add("username", new ScopedLocalizableError("validation", "usernameNotFound"));
		}
		else if (user.checkPassword(password) == false)
		{
			errors.add("password", new ScopedLocalizableError("validation", "passwordIncorrect"));
		}
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
		
		return new ForwardResolution(LoginActionBean.VIEW);
	}

	public Resolution login()
	{
		getContext().setUser(user);

		if (referrer != null)
		{
			return new RedirectResolution(referrer);
		}

		return new RedirectResolution(ViewUserActionBean.class).addParameter("user", user.getUsername());
	}
}