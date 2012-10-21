package dixie.web.action;

import dixie.dao.DaoPage;
import dixie.lang.TagList;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.web.nonext.converter.TagListFromDaoTypeConverter;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

/**
 *
 * @author jferland
 */
@UrlBinding("/links/{format=all}/tagged/{tags}/page{page.index}")
public class LinksTaggedActionBean extends LinksActionBean
{
	@Validate(required = true, converter = TagListFromDaoTypeConverter.class)
	public TagList tags;

	@Override
	public DaoPage<Link> getLinks()
	{
		return getLinks(format, tags);
	}

	@Override
	public DaoPage<Tag> getRelatedTags()
	{
		return getRelatedTags(format, tags);
	}
}