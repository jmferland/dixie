package dixie.web.action.user;

import dixie.web.action.*;
import dixie.model.User;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
@UrlBinding("/user/{user}")
public class ViewUserActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(ViewUserActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/view.ftl";
	@Validate
	public User user;

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		if (user == null)
		{
			return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
		}

		return new ForwardResolution(VIEW);
	}
}
