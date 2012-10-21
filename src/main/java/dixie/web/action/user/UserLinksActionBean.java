package dixie.web.action.user;

import dixie.web.action.*;
import dixie.dao.DaoCommand;
import dixie.dao.DaoPage;
import dixie.dao.exception.DaoException;
import dixie.dao.order.TagOrder;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.model.User;
import net.sourceforge.stripes.validation.Validate;

/**
 *
 * @author jferland
 */
public abstract class UserLinksActionBean extends LinksActionBean
{
	@Validate
	public User user;

	@Override
	public DaoPage<Link> getLinks()
	{
		return getLinks(format, TagList.EMPTY, user, UserLinkRelation.FAVORITED);
	}

	@Override
	public DaoPage<Tag> getRelatedTags()
	{
		return getRelatedTags(format, TagList.EMPTY, user, UserLinkRelation.FAVORITED);
	}

	protected DaoPage<Link> getLinks(final Format format, final TagList tags, final User user, final UserLinkRelation relation)
	{
		if (links == null)
		{
			try
			{
				links = (DaoPage<Link>) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getLinkDao().pageByFormatAndTagsWithUserRelation(format, tags, user, relation, page.
								getOffset(), page.getSize(), page.getOrder());
					}
				});

				page.setMax(links);
			}
			catch (Exception e)
			{
			}
		}

		return links;
	}

	protected DaoPage<Tag> getRelatedTags(final Format format, final TagList tags, final User user, final UserLinkRelation relation)
	{
		if (relatedTags == null)
		{
			try
			{
				relatedTags = (DaoPage<Tag>) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getTagDao().pageByLinkFormatAndTagsWithUserRelation(format, tags, user, relation, 0, 50, TagOrder.COMMON);
					}
				});
			}
			catch (Exception e)
			{
			}
		}

		return relatedTags;
	}
}