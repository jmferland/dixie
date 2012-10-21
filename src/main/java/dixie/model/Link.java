package dixie.model;

import dixie.dao.util.LinkThumbsUtil;
import dixie.dao.util.LinkThumbsUtil.Size;
import dixie.dao.util.ThumbSrc;
import dixie.lang.TagList;
import dixie.util.Digest;
import dixie.util.StringUtil;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
public final class Link extends BaseModel
{
	private static final Logger logger = Logger.getLogger(Link.class);
	private static final long serialVersionUID = -7776863251954299926L;
	private String urlSafeTitle;
	private String urlHost;
	private Map<Size, ThumbSrc> thumbUrls;
	private final LinkThumbsUtil linkThumbsUtil;
	private final User user;
	private final String url;
	private final String sourceThumbUrl;
	private final String title;
	private final String notes;
	private final Format format;
	private final Category category;
	private final long views;
	private final long comments;
	private final long promotions;
	private final long demotions;
	private final TagList tags;
	private final long createdOn;

	private Link(Builder builder, LinkThumbsUtil linkThumbsUtil)
	{
		super(builder.id);
		this.user = builder.user;
		this.url = builder.url;
		this.sourceThumbUrl = builder.sourceThumbUrl;
		this.title = builder.title;
		this.notes = builder.notes;
		this.format = builder.format;
		this.category = builder.category;
		this.views = builder.views;
		this.comments = builder.comments;
		this.promotions = builder.promotions;
		this.demotions = builder.demotions;
		this.tags = builder.tags;
		this.createdOn = builder.createdOn;

		this.linkThumbsUtil = linkThumbsUtil;
	}

	public static class Builder
	{
		public long id = 0;
		public User user = User.DEFAULT_USER;
		public String url = BaseModel.EMPTY_STRING;
		public String sourceThumbUrl = BaseModel.EMPTY_STRING;
		public String title = BaseModel.EMPTY_STRING;
		public String notes = BaseModel.EMPTY_STRING;
		public Format format = Format.NONE;
		public Category category = Category.DEFAULT_CATEGORY;
		public long views = 0;
		public long comments = 0;
		public long promotions = 0;
		public long demotions = 0;
		public TagList tags = TagList.EMPTY;
		public long createdOn = new Date().getTime();

		/**
		 * A {@code LinkThumbsUtil} instance is vital to the functionality of
		 * a {@code Link}. However, we cannot require it in a {@code Builder}
		 * constructor since some frameworks (ex: Stripes) must create a
		 * {@code Builder} instance with zero arguments as a part of validation
		 * and binding. Therefore, we require it here.
		 *
		 * @param linkThumbsUtil
		 * @return
		 */
		public Link build(LinkThumbsUtil linkThumbsUtil)
		{
			return new Link(this, linkThumbsUtil);
		}
	}

	public Category getCategory()
	{
		return category;
	}

	public long getComments()
	{
		return comments;
	}

	public Date getCreatedOn()
	{
		return new Date(createdOn);
	}

	public long getDemotions()
	{
		return demotions;
	}

	public Format getFormat()
	{
		return format;
	}

	private ThumbSrc getThumb(Size size)
	{
		if (thumbUrls == null)
		{
			thumbUrls = new HashMap<Size, ThumbSrc>();
		}

		if (!thumbUrls.containsKey(size))
		{
			thumbUrls.put(size, linkThumbsUtil.getThumbSrc(this, size));
		}

		return thumbUrls.get(size);
	}

	public ThumbSrc getLargeThumb()
	{
		return getThumb(Size.LARGE);
	}

	public ThumbSrc getMediumThumb()
	{
		return getThumb(Size.MEDIUM);
	}

	public String getNotes()
	{
		return notes;
	}

	public long getPromotions()
	{
		return promotions;
	}

	public ThumbSrc getSmallThumb()
	{
		return getThumb(Size.SMALL);
	}

	public String getSourceThumbUrl()
	{
		return sourceThumbUrl;
	}

	public TagList getTags()
	{
		return tags;
	}

	public ThumbSrc getTinyThumb()
	{
		return getThumb(Size.TINY);
	}

	public String getTitle()
	{
		return title;
	}

	public String getUrl()
	{
		return url;
	}

	public String getHost()
	{
		if (urlHost == null)
		{
			urlHost = ""; // Ensure we won't try again.

			try
			{
				URI uri = new URI(url);
				urlHost = uri.getHost();
			}
			catch (URISyntaxException e)
			{
				logger.error("URI Syntax wrong: " + e);
			}
		}

		return urlHost;
	}

	public User getUser()
	{
		return user;
	}

	public long getViews()
	{
		return views;
	}

	/**
	 * Get a url-safe version of this Link's title.
	 *
	 * @see dixie.util.StringUtil.urlSafe
	 * @return a url-safe version of the title.
	 */
	public String getUrlSafeTitle()
	{
		if (this.urlSafeTitle != null)
		{
			return this.urlSafeTitle;
		}

		String tmp = StringUtil.urlSafe(this.title);

		this.urlSafeTitle = tmp;

		return tmp;
	}

	public byte[] getUrlHash()
	{
		return Link.getUrlHash(this.url);
	}

	public static byte[] getUrlHash(String url)
	{
		try
		{
			return Digest.sha1(url);
		}
		catch (UnsupportedEncodingException e)
		{
		}
		catch (NoSuchAlgorithmException e)
		{
		}

		return null;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		if (super.equals(obj) == false)
		{
			return false;
		}
		final Link other = (Link) obj;
		if ((this.user == null) ? (other.user != null) : !this.user.equals(other.user))
		{
			return false;
		}
		if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url))
		{
			return false;
		}
		if ((this.sourceThumbUrl == null) ? (other.sourceThumbUrl != null) : !this.sourceThumbUrl.
				equals(other.sourceThumbUrl))
		{
			return false;
		}
		if ((this.title == null) ? (other.title != null) : !this.title.equals(other.title))
		{
			return false;
		}
		if ((this.notes == null) ? (other.notes != null) : !this.notes.equals(other.notes))
		{
			return false;
		}
		if ((this.format == null) ? (other.format != null) : !this.format.equals(other.format))
		{
			return false;
		}
		if ((this.category == null) ? (other.category != null) : !this.category.equals(other.category))
		{
			return false;
		}
		if (this.views != other.views)
		{
			return false;
		}
		if (this.comments != other.comments)
		{
			return false;
		}
		if (this.promotions != other.promotions)
		{
			return false;
		}
		if (this.demotions != other.demotions)
		{
			return false;
		}
		if (this.tags != other.tags && (this.tags == null || !this.tags.equals(other.tags)))
		{
			return false;
		}
		if (this.createdOn != other.createdOn)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 53 * hash + (this.user != null ? this.user.hashCode() : 0);
		hash = 53 * hash + (this.url != null ? this.url.hashCode() : 0);
		hash = 53 * hash + (this.sourceThumbUrl != null ? this.sourceThumbUrl.hashCode() : 0);
		hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
		hash = 53 * hash + (this.notes != null ? this.notes.hashCode() : 0);
		hash = 53 * hash + (this.format != null ? this.format.hashCode() : 0);
		hash = 53 * hash + (this.category != null ? this.category.hashCode() : 0);
		hash = 53 * hash + (int) (this.views ^ (this.views >>> 32));
		hash = 53 * hash + (int) (this.comments ^ (this.comments >>> 32));
		hash = 53 * hash + (int) (this.promotions ^ (this.promotions >>> 32));
		hash = 53 * hash + (int) (this.demotions ^ (this.demotions >>> 32));
		hash = 53 * hash + (this.tags != null ? this.tags.hashCode() : 0);
		hash = 53 * hash + (int) (this.createdOn ^ (this.createdOn >>> 32));
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("user", this.user);
		map.put("url", this.url);
		map.put("sourceThumbUrl", this.sourceThumbUrl);
		map.put("title", this.title);
		map.put("notes", this.notes);
		map.put("format", this.format);
		map.put("category", this.category);
		map.put("views", this.views);
		map.put("comments", this.comments);
		map.put("promotions", this.promotions);
		map.put("demotions", this.demotions);
		map.put("tags", this.tags);
		map.put("linkThumbsUtil", this.linkThumbsUtil);
		map.put("createdOn", new Date(this.createdOn));
		return map.toString();
	}

	public Builder getBuilder()
	{
		Builder builder = new Builder();

		builder.id = this.getId();
		builder.category = this.category;
		builder.comments = this.comments;
		builder.createdOn = this.createdOn;
		builder.demotions = this.demotions;
		builder.format = this.format;
		builder.notes = this.notes;
		builder.promotions = this.promotions;
		builder.tags = this.tags;
		builder.sourceThumbUrl = this.sourceThumbUrl;
		builder.title = this.title;
		builder.url = this.url;
		builder.user = this.user;
		builder.views = this.views;

		return builder;
	}
}
