package dixie.dao.db;

import dixie.dao.DaoPage;
import dixie.dao.TagDao;
import dixie.dao.db.vocab.TagVocab;
import dixie.dao.exception.DaoException;
import dixie.dao.order.TagOrder;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Format;
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
public class TagDbDao extends BaseDbDao<Tag> implements TagDao
{
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());

	@Override
	public Tag create(Tag tag) throws DaoException
	{
		logger.debug("create: Tag=" + tag);

		Tag result = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call createTag(?,?)");
			stmt.setString(1, tag.getName());
			stmt.registerOutParameter(2, Types.INTEGER);

			this.db.executeUpdate(stmt);

			long id = stmt.getInt(2);

			result = new Tag(id, tag.getName(), tag.getCount());
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Tag readByName(String name) throws DaoException
	{
		logger.debug("readByName: name=" + name);

		Tag tag = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call readTagByName(?)");
			stmt.setString(1, name);

			ResultSet rs = this.db.executeQuery(stmt);

			if (rs.next())
			{
				tag = build(rs);
				logger.debug("found: Tag=" + tag);
			}
			else
			{
				logger.debug("Tag not found");
			}
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return tag;
	}

	@Override
	public void update(Tag tag) throws DaoException
	{
		logger.debug("update: Tag=" + tag);

		try
		{
			CallableStatement stmt = this.db.prepareCall("call updateTag(?,?)");
			stmt.setLong(1, tag.getId());
			stmt.setString(2, tag.getName());
			this.db.executeUpdate(stmt);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}
	}

	@Override
	public DaoPage<Tag> pageByLinkFormatAndTags(Format format, TagList tags, int offset, int count, TagOrder order) throws DaoException
	{
		logger.debug("pageByLinkTags: format=" + format + ",tags=" + tags + ",offset=" + offset + ",count=" + count + ",order=" + order.
				name());

		DaoPage<Tag> result = null;
		List<Tag> links = new ArrayList<Tag>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageTagsByLinkFormatAndTags(?,?,?,?,?,?)");
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
				Tag aTag = build(rs);
				logger.debug("found: Tag=" + aTag);

				links.add(aTag);
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
	public DaoPage<Tag> pageByLinkFormatAndTagsWithUserRelation(Format format, TagList tags, User user, UserLinkRelation relation, int offset, int count, TagOrder order) throws DaoException
	{
		logger.debug("pageByLinkFormatAndTagsWithUserRelation: format=" + format + ",tags=" + tags + ",user=" + user +
					 ",relation=" + relation + ",offset=" + offset + ",count=" + count +
					 ",order=" + order.name());

		DaoPage<Tag> result = null;
		List<Tag> links = new ArrayList<Tag>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageTagsByLinkFormatAndTagsWithUserRelation(?,?,?,?,?,?,?,?)");
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
				Tag aTag = build(rs);
				logger.debug("found: Tag=" + aTag);

				links.add(aTag);
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
	public Tag build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		long id = resultSet.getLong(TagVocab.ID);
		String name = resultSet.getString(TagVocab.NAME);
		long count = 0;

		// TODO: Make an "optional()" wrapper to hide away the details of how
		// we make some data optional?
		//
		// Currently optional - don't croak if the information is missing. This
		// may change in the future.
		try
		{
			count = resultSet.getLong(TagVocab.COUNT);
		}
		catch (SQLException e)
		{
		}

		return new Tag(id, name, count);
	}
}
