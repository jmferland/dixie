package dixie.web.action;

import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.lang.DemotionReason;
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
@UrlBinding("/demote/link/{link}")
public class DemoteLinkActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(DemoteLinkActionBean.class);
	private static final String DEMOTE = "/WEB-INF/ftl/link/demote.ftl";
	@Validate(required = true)
	public Link link;
	@Validate
	public DemotionReason reason = DemotionReason.NO_REASON;

	@DontValidate
	@DefaultHandler
	public Resolution view()
	{
		if (link != null &&
			reason != null)
		{
			try
			{
				getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						getDaoManager().getLinkDao().unPromote(link, getLoggedInUser());
						getDaoManager().getLinkDao().demote(link, getLoggedInUser(), reason, new Date().getTime());
						return null;
					}
				});
			}
			catch (Exception e)
			{
				logger.fatal("Error demoting link: " + e);
				getContext().getValidationErrors().addGlobalError(new ScopedLocalizableError("validation", "daoException"));
			}
		}

		if (isAjaxRequest())
		{
			return new AjaxResolution(this);
		}

		return new ForwardResolution(DEMOTE);
	}
}
