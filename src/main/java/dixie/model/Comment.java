package dixie.model;

import com.google.inject.internal.ImmutableList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Comment extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	// The parent Comment, top Comment and associated subject could all be objects but
	// we will leave them as ids until a view actually needs to access their details.
	private final long parentId;
	private final long topId;
	private final long linkId;
	private final User user;
	private final String text;
	private final long ups;
	private final long downs;
	private final long createdOn;
	private final List<Comment> replies;

	private Comment(Builder builder)
	{
		super(builder.id);
		this.parentId = builder.parentId;
		this.topId = builder.topId;
		this.user = builder.user;
		this.text = builder.text;
		this.linkId = builder.linkId;
		this.ups = builder.ups;
		this.downs = builder.downs;
		this.createdOn = builder.createdOn;
		this.replies = ImmutableList.copyOf(builder.replies);
	}

	public static class Builder
	{
		public long id = 0;
		public long parentId = 0;
		public long topId = 0;
		public long linkId = 0;
		public User user = User.DEFAULT_USER;
		public String text = BaseModel.EMPTY_STRING;
		public long ups = 0;
		public long downs = 0;
		public long createdOn = new Date().getTime();
		public List<Comment> replies = new ArrayList<Comment>();

		public Comment build()
		{
			return new Comment(this);
		}
	}

	public Date getCreatedOn()
	{
		return new Date(createdOn);
	}

	public long getDowns()
	{
		return downs;
	}

	public long getLinkId()
	{
		return linkId;
	}

	public long getParentId()
	{
		return parentId;
	}

	public List<Comment> getReplies()
	{
		return replies;
	}

	public String getText()
	{
		return text;
	}

	public long getTopId()
	{
		return topId;
	}

	public long getUps()
	{
		return ups;
	}

	public User getUser()
	{
		return user;
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
		final Comment other = (Comment) obj;
		if (this.parentId != other.parentId)
		{
			return false;
		}
		if (this.topId != other.topId)
		{
			return false;
		}
		if (this.linkId != other.linkId)
		{
			return false;
		}
		if (this.user != other.user && (this.user == null || !this.user.equals(other.user)))
		{
			return false;
		}
		if ((this.text == null) ? (other.text != null) : !this.text.equals(other.text))
		{
			return false;
		}
		if (this.ups != other.ups)
		{
			return false;
		}
		if (this.downs != other.downs)
		{
			return false;
		}
		if (this.createdOn != other.createdOn)
		{
			return false;
		}
		if (this.replies != other.replies && (this.replies == null || !this.replies.equals(other.replies)))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = this.hashCode();
		hash = 83 * hash + (int) (this.parentId ^ (this.parentId >>> 32));
		hash = 83 * hash + (int) (this.topId ^ (this.topId >>> 32));
		hash = 83 * hash + (int) (this.linkId ^ (this.linkId >>> 32));
		hash = 83 * hash + (this.user != null ? this.user.hashCode() : 0);
		hash = 83 * hash + (this.text != null ? this.text.hashCode() : 0);
		hash = 83 * hash + (int) (this.ups ^ (this.ups >>> 32));
		hash = 83 * hash + (int) (this.downs ^ (this.downs >>> 32));
		hash = 83 * hash + (int) (this.createdOn ^ (this.createdOn >>> 32));
		hash = 83 * hash + (this.replies != null ? this.replies.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("parentId", this.parentId);
		map.put("topId", this.topId);
		map.put("linkId", this.linkId);
		map.put("user", this.user);
		map.put("text", this.text);
		map.put("ups", this.ups);
		map.put("downs", this.downs);
		map.put("createdOn", new Date(this.createdOn));
		map.put("replies", this.replies.size());
		return map.toString();
	}

	public Builder getBuilder()
	{
		Builder builder = new Builder();

		builder.id = this.getId();
		builder.createdOn = this.createdOn;
		builder.downs = this.downs;
		builder.linkId = this.linkId;
		builder.parentId = this.parentId;
		
		// The Builder's replies should be mutable.
		builder.replies = new ArrayList<Comment>();
		builder.replies.addAll(this.replies);

		builder.text = this.text;
		builder.topId = this.topId;
		builder.ups = this.ups;
		builder.user = this.user;

		return builder;
	}
}