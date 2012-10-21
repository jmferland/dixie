package dixie.web.nonext;

import dixie.model.User;
import dixie.web.action.BaseActionBean;
import dixie.web.action.LoginActionBean;
import dixie.web.action.helper.AjaxResolution;
import java.lang.reflect.Method;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import org.stripesstuff.plugin.security.InstanceBasedSecurityManager;
import org.stripesstuff.plugin.security.SecurityHandler;

/**
 *
 * @author jferland
 */
public class CustomSecurityManager extends InstanceBasedSecurityManager implements SecurityHandler
{
	@Override
	public Boolean isUserAuthenticated(ActionBean bean, Method handler)
	{
		return getUser(bean) != null;
	}

	@Override
	protected Boolean hasRoleName(ActionBean bean, Method handler, String role)
	{
// TODO: if and when we need roles, need to update this.
//		User user = getUser(bean);
//		if (user != null)
//		{
//			Collection<Role> roles = user.getRoles();
//			return roles != null && roles.contains(new Role(role));
//		}

		return false;
	}

	@Override
	public Resolution handleAccessDenied(ActionBean bean, Method handler)
	{
		if (BaseActionBean.isAjaxRequest(bean))
		{
			// We do not want to allow existing errors to be shown if the user
			// is not allowed access.
			bean.getContext().getValidationErrors().clear();
			bean.getContext().getValidationErrors().addGlobalError(new ScopedLocalizableError("security", "notAllowed"));

			return new AjaxResolution(bean);
		}

		RedirectResolution resolution = new RedirectResolution(LoginActionBean.class);
		if (bean.getContext().getRequest().getMethod().equalsIgnoreCase("GET"))
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append(bean.getContext().getRequest().getServletPath());

			String queryString = bean.getContext().getRequest().getQueryString();
			if (queryString != null &&
				queryString.isEmpty() == false)
			{
				buffer.append("?");
				buffer.append(queryString);
			}

			resolution.addParameter("referrer", buffer.toString());
		}

		return resolution;
	}

	protected User getUser(ActionBean bean)
	{
		return ((BaseActionBean) bean).getContext().getUser();
	}
}
