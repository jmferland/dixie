package dixie.web.action;

import dixie.dao.DaoCommand;
import dixie.dao.DaoPage;
import dixie.dao.exception.DaoException;
import dixie.dao.order.LinkOrder;
import dixie.dao.order.TagOrder;
import dixie.lang.TagList;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Settings;
import dixie.model.Tag;
import dixie.web.action.helper.PageHelper;
import dixie.web.nonext.converter.FormatFromDaoTypeConverter;
import java.util.List;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

/**
 *
 * @author jferland
 */
@UrlBinding("/links/{format=all}/page{page.index}")
public class LinksActionBean extends BaseActionBean
{
	private static final String VIEW = "/WEB-INF/ftl/link/browse.ftl";
	private List<Format> allFormats;
	protected static final int DEFAULT_PAGE_SIZE = 50;
	protected static final LinkOrder DEFAULT_ORDER = LinkOrder.NEWEST;
	protected DaoPage<Link> links;
	protected DaoPage<Tag> relatedTags;
	@Validate(converter = FormatFromDaoTypeConverter.class)
	public Format format;
	@ValidateNestedProperties(
	{
		@Validate(field = "index"),
		@Validate(field = "size"),
		@Validate(field = "order")
	})
	public PageHelper<LinkOrder> page = new PageHelper<LinkOrder>(this, DEFAULT_PAGE_SIZE, DEFAULT_ORDER).
			saveSizeUnder(Settings.Key.LINK_PAGE_SIZE).
			saveOrderUnder(Settings.Key.LINK_ORDER);

	public DaoPage<Link> getLinks()
	{
		return getLinks(format, TagList.EMPTY);
	}

	public DaoPage<Tag> getRelatedTags()
	{
		return getRelatedTags(format, TagList.EMPTY);
	}

	/**
	 * Provide all {@code Format}-s whenever asked (instead of setting it
	 * only in a specific event).
	 *
	 * @return root a list of all the {@code Format}-s.
	 */
	// TODO: this could be refactored somewhere (outside of the ActionBean,
	// use composition, call a static function, etc.).
	public List<Format> getAllFormats()
	{
		if (this.allFormats == null)
		{
			try
			{
				this.allFormats = (List<Format>) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getFormatDao().readAll();
					}
				});
			}
			catch (DaoException e)
			{
			}
		}

		return this.allFormats;
	}

	protected DaoPage<Link> getLinks(final Format format, final TagList tags)
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
						return getDaoManager().getLinkDao().pageByFormatAndTags(format, tags, page.
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

	protected DaoPage<Tag> getRelatedTags(final Format format, final TagList tags)
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
						return getDaoManager().getTagDao().pageByLinkFormatAndTags(format, tags, 0, 50, TagOrder.COMMON);
					}
				});
			}
			catch (Exception e)
			{
			}
		}

		return relatedTags;
	}

	@DefaultHandler
	public Resolution view()
	{
		return new ForwardResolution(VIEW);
	}
}