package dixie.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
@UrlBinding("/search/links")
public class SearchLinksActionBean extends BaseActionBean
{
	// TODO: implement this.
	private static final Logger logger = Logger.getLogger(SearchLinksActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/link/search.ftl";
	@Validate
	public String q;

	@DefaultHandler
	public Resolution view()
	{
		return new ForwardResolution(VIEW);
	}
}
