package dixie.web.action.user;

import dixie.dao.DaoPage;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Link;
import dixie.model.Tag;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 *
 * @author jferland
 */
@UrlBinding("/user/{user}/promoted/{format=all}/page{page.index}")
public class PromotedLinksActionBean extends UserLinksActionBean
{
	private static final String VIEW = "/WEB-INF/ftl/user/promoted.ftl";

	@Override
	public DaoPage<Link> getLinks()
	{
		return getLinks(format, TagList.EMPTY, user, UserLinkRelation.PROMOTED);
	}

	@Override
	public DaoPage<Tag> getRelatedTags()
	{
		return getRelatedTags(format, TagList.EMPTY, user, UserLinkRelation.PROMOTED);
	}

	@DefaultHandler
	@Override
	public Resolution view()
	{
		if (user == null)
		{
			return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
		}

		return new ForwardResolution(VIEW);
	}
}