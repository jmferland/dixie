package dixie.web.action;

import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.Link;
import dixie.web.action.helper.AjaxResolution;
import java.util.Date;
import javax.annotation.security.PermitAll;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
@PermitAll
@UrlBinding("/promote/link/{link}")
public class PromoteLinkActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(PromoteLinkActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/link/promote.ftl";
	@Validate(required = true)
	public Link link;

	@DontValidate
	@DefaultHandler
	public Resolution view()
	{
		if (link != null)
		{
			try
			{
				getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						getDaoManager().getLinkDao().unDemote(link, getLoggedInUser());
						getDaoManager().getLinkDao().promote(link, getLoggedInUser(), new Date().getTime());
						return null;
					}
				});
			}
			catch (Exception e)
			{
				logger.fatal("Error promoting link: " + e);
				getContext().getValidationErrors().addGlobalError(new ScopedLocalizableError("validation", "daoException"));
			}
		}

		if (isAjaxRequest())
		{
			return new AjaxResolution(this);
		}

		return new ForwardResolution(VIEW);
	}
}
