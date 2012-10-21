package dixie.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import org.apache.log4j.Logger;

/**
 * Logout.
 *
 * @author jferland
 */
@UrlBinding("/logout")
public class LogoutActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(LoginActionBean.class);
	@Validate
	public String referrer;

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		getContext().logout();

		if (referrer != null)
		{
			return new RedirectResolution(referrer);
		}

		return new RedirectResolution(HomeActionBean.class);
	}
}