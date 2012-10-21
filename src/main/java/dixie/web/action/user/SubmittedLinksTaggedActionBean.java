package dixie.web.action.user;

import dixie.dao.DaoPage;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.web.nonext.converter.TagListFromDaoTypeConverter;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

/**
 *
 * @author jferland
 */
@UrlBinding("/user/{user}/submitted/{format=all}/tagged/{tags}/page{page.index}")
public class SubmittedLinksTaggedActionBean extends SubmittedLinksActionBean
{
	@Validate(required = true, converter = TagListFromDaoTypeConverter.class)
	public TagList tags;

	@Override
	public DaoPage<Link> getLinks()
	{
		return getLinks(format, tags, user, UserLinkRelation.SUBMITTED);
	}

	@Override
	public DaoPage<Tag> getRelatedTags()
	{
		return getRelatedTags(format, tags, user, UserLinkRelation.SUBMITTED);
	}
}