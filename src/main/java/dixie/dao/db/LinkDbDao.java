package dixie.dao.db;

import dixie.dao.LinkDao;
import dixie.dao.DaoPage;
import dixie.dao.db.vocab.LinkVocab;
import dixie.dao.exception.DaoException;
import dixie.dao.order.LinkOrder;
import dixie.lang.DemotionReason;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.model.User;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
public class LinkDbDao extends BaseDbDao<Link> implements LinkDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class);

	@Override
	public Link create(Link link) throws DaoException
	{
		logger.debug("create: Link=" + link);

		Link result = null;

		try
		{
			this.db.checkTransaction();

			// Create link.
			CallableStatement stmt = this.db.prepareCall("call createLink(?,?,?,?,?,?,?,?,?,?)");
			stmt.setLong(1, link.getUser().getId());
			stmt.setString(2, link.getUrl());
			stmt.setBytes(3, link.getUrlHash());
			stmt.setString(4, link.getSourceThumbUrl());
			stmt.setLong(5, link.getFormat().getId());
			stmt.setLong(6, link.getCategory().getId());
			stmt.setString(7, link.getTitle());
			stmt.setString(8, link.getNotes());
			stmt.setLong(9, link.getCreatedOn().getTime());
			stmt.registerOutParameter(10, Types.INTEGER);

			this.db.executeUpdate(stmt);

			long id = stmt.getInt(10);

			// Need to build link before adding tags so we have an existing
			// Link to tag.
			Link.Builder builder = link.getBuilder();
			builder.id = id;
			result = builder.build(daoManager.getLinkThumbsUtil());

			// TODO: maybe a new TagList should be made (with correct ids).
			// Add tags.
			int position = 0;
			for (Tag tag : link.getTags().getList())
			{
				this.tag(result, tag, position++);
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Link readByUrl(String url) throws DaoException
	{
		logger.debug("readByUrlHash: url=" + url);

		Link link = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call readLinkByUrlHash(?)");
			stmt.setBytes(1, Link.getUrlHash(url));

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				link = build(rs);
				logger.debug("found: Link=" + link);
			}
			else
			{
				logger.debug("Link not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return link;
	}

	@Override
	public void update(Link link) throws DaoException
	{
		logger.debug("update: Link=" + link);

		try
		{
			this.db.checkTransaction();

			// Update the link.
			CallableStatement stmt = this.db.prepareCall("call updateLink(?,?,?,?,?,?,?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, link.getUser().getId());
			stmt.setString(3, link.getUrl());
			stmt.setBytes(4, link.getUrlHash());
			stmt.setString(5, link.getSourceThumbUrl());
			stmt.setLong(6, link.getFormat().getId());
			stmt.setLong(7, link.getCategory().getId());
			stmt.setString(8, link.getTitle());
			stmt.setString(9, link.getNotes());

			this.db.executeUpdate(stmt);

			// Remove all tags, then add the new ones.
			this.unTagAll(link);

			// Add new tags.
			int position = 0;
			for (Tag tag : link.getTags().getList())
			{
				this.tag(link, tag, position++);
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public DaoPage<Link> page(int offset, int count, int sortBy, int order) throws DaoException
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void incrementViews(Link link) throws DaoException
	{
		logger.debug("incrementViews: Link=" + link);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call incrementLinkViews(?)");
			stmt.setLong(1, link.getId());

			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void promote(Link link, User user, long when) throws DaoException
	{
		logger.debug("promote: Link=" + link + ",User=" + user + ",when=" + when);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call promoteLink(?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			stmt.setLong(3, when);
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void unPromote(Link link, User user) throws DaoException
	{
		logger.debug("unPromote: Link=" + link + ",User=" + user);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call unPromoteLink(?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void demote(Link link, User user, DemotionReason flag, long when) throws DaoException
	{
		logger.debug("demote: Link=" + link + ",User=" + user + ",LinkFlag=" + flag + ",when=" + when);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call demoteLink(?,?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			stmt.setInt(3, flag.getIntValue());
			stmt.setLong(4, when);
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void unDemote(Link link, User user) throws DaoException
	{
		logger.debug("unDemote: Link=" + link + ",User=" + user);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call unDemoteLink(?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void favorite(Link link, User user, long when) throws DaoException
	{
		logger.debug("favorite: Link=" + link + ",User=" + user + ",when=" + when);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call favoriteLink(?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			stmt.setLong(3, when);
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void unFavorite(Link link, User user) throws DaoException
	{
		logger.debug("unFavorite: Link=" + link + ",User=" + user);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call unFavoriteLink(?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, user.getId());
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void tag(Link link, Tag tag, int position) throws DaoException
	{
		logger.debug("tag: Link=" + link + ",Tag=" + tag + ",position=" + position);

		try
		{
			// Ensure the tag exists before we add it.
			tag = this.daoManager.getTagDao().create(tag);

			CallableStatement stmt = this.db.prepareCall("call tagLink(?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setLong(2, tag.getId());
			stmt.setInt(3, position);
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public void unTagAll(Link link) throws DaoException
	{
		logger.debug("unTagAll: Link=" + link);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call unTagAllLink(?)");
			stmt.setLong(1, link.getId());
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public DaoPage<Link> pageByFormatAndTags(Format format, TagList tags, int offset, int count, LinkOrder order) throws DaoException
	{
		logger.debug("pageByTags: format=" + format + ",tags=" + tags + ",offset=" + offset + ",count=" + count + ",order=" + order.
				name());

		DaoPage<Link> result = null;
		List<Link> links = new ArrayList<Link>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageLinksByFormatAndTags(?,?,?,?,?,?)");
			String ids = tags.joinIds(",");
			stmt.setLong(1, format.getId());
			stmt.setString(2, ids);
			stmt.setInt(3, offset);
			stmt.setInt(4, count);
			stmt.setInt(5, order.getIntValue());
			stmt.registerOutParameter(6, Types.INTEGER);

			ResultSet rs = this.db.executeQuery(stmt);

			int size = Math.max(0, stmt.getInt(6));

			while (rs.next())
			{
				Link aLink = build(rs);
				logger.debug("found: Link=" + aLink);

				links.add(aLink);
			}

			result = new DaoPage(links, size);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public DaoPage<Link> pageByFormatAndTagsWithUserRelation(Format format, TagList tags, User user, UserLinkRelation relation, int offset, int count, LinkOrder order) throws DaoException
	{
		logger.debug("pageByFormatAndTagsWithUserRelation: format=" + format + ",tags=" + tags + ",user=" + user +
					 ",relation=" + relation + ",offset=" + offset + ",count=" + count +
					 ",order=" + order.name());

		DaoPage<Link> result = null;
		List<Link> links = new ArrayList<Link>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageLinksByFormatAndTagsWithUserRelation(?,?,?,?,?,?,?,?)");
			String ids = tags.joinIds(",");
			stmt.setLong(1, format.getId());
			stmt.setString(2, ids);
			stmt.setLong(3, user.getId());
			stmt.setInt(4, relation.getIntValue());
			stmt.setInt(5, offset);
			stmt.setInt(6, count);
			stmt.setInt(7, order.getIntValue());
			stmt.registerOutParameter(8, Types.INTEGER);

			ResultSet rs = this.db.executeQuery(stmt);

			int size = Math.max(0, stmt.getInt(8));

			while (rs.next())
			{
				Link aLink = build(rs);
				logger.debug("found: Link=" + aLink);

				links.add(aLink);
			}

			result = new DaoPage(links, size);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Link build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		Link.Builder builder = new Link.Builder();

		builder.id = resultSet.getLong(LinkVocab.ID);

		builder.url = resultSet.getString(LinkVocab.URL);
		builder.sourceThumbUrl = resultSet.getString(LinkVocab.SOURCE_THUMB_URL);
		builder.title = resultSet.getString(LinkVocab.TITLE);
		builder.notes = resultSet.getString(LinkVocab.NOTES);
		builder.views = resultSet.getLong(LinkVocab.VIEWS);
		builder.comments = resultSet.getLong(LinkVocab.COMMENTS);
		builder.promotions = resultSet.getLong(LinkVocab.PROMOTIONS);
		builder.demotions = resultSet.getLong(LinkVocab.DEMOTIONS);
		builder.createdOn = resultSet.getLong(LinkVocab.CREATED_ON);

		// Resulting TagList will be missing getIntValue and count information, but
		// this should be fine since that information is not necessary in this
		// case.
		builder.tags = TagList.split(resultSet.getString(LinkVocab.TAGS));

		User.Builder userBuilder = new User.Builder();
		userBuilder.id = resultSet.getLong(LinkVocab.USER_ID);
		builder.user = this.build(resultSet, userBuilder.build());

		long formatId = resultSet.getLong(LinkVocab.FORMAT_ID);
		builder.format = this.daoManager.getFormatDao().read(formatId);

		long categoryId = resultSet.getLong(LinkVocab.CATEGORY_ID);
		builder.category = this.daoManager.getCategoryDao().read(categoryId);

		return builder.build(daoManager.getLinkThumbsUtil());
	}
}
