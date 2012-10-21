package dixie.web.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

/**
 *
 * @author jferland
 */
@UrlBinding("/")
public class HomeActionBean extends BaseActionBean
{
	private static final String VIEW = "/WEB-INF/ftl/index.ftl";
	private int page = 1;

	@Validate
	public void setPage(int page)
	{
		this.page = Math.max(page, 1);
	}

	public int getPage()
	{
		return this.page;
	}

	@DefaultHandler
	public Resolution view()
	{
		return new ForwardResolution(VIEW);
	}
}