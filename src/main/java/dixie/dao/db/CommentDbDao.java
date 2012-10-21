package dixie.dao.db;

import dixie.dao.CommentDao;
import dixie.dao.DaoUtil;
import dixie.dao.DaoPage;
import dixie.dao.db.vocab.CommentVocab;
import dixie.dao.exception.DaoException;
import dixie.dao.order.CommentOrder;
import dixie.model.Comment;
import dixie.model.Link;
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
public class CommentDbDao extends BaseDbDao<Comment> implements CommentDao
{
	private static final long serialVersionUID = -4984150946474083985L;
	private static final Logger logger = Logger.getLogger(CommentDbDao.class.getName());

	@Override
	public DaoPage<Comment> page(Link link, int offset, int count, CommentOrder order) throws DaoException
	{
		logger.debug("page: Link= " + link + ",offset=" + offset + ",count=" + count + ",order=" + order.
				name());

		DaoPage<Comment> result = null;
		List<Comment.Builder> comments = new ArrayList<Comment.Builder>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageComments(?,?,?,?,?)");
			stmt.setLong(1, link.getId());
			stmt.setInt(2, offset);
			stmt.setInt(3, count);
			stmt.setInt(4, order.getIntValue());
			stmt.registerOutParameter(5, Types.INTEGER);

			ResultSet resultSet = this.db.executeQuery(stmt);

			int size = Math.max(0, stmt.getInt(5));

			while (resultSet.next())
			{
				Comment aComment = build(resultSet);
				logger.debug("found: Comment=" + aComment);

				comments.add(aComment.getBuilder());
			}

			result = new DaoPage(DaoUtil.buildCommentTree(comments), size);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public int getOffset(Comment comment, CommentOrder order) throws DaoException
	{
		logger.debug("getOffset: Comment= " + comment + ",order=" + order.name());

		int result = -1;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call getCommentOffset(?,?,?)");
			stmt.setLong(1, comment.getId());
			stmt.setInt(2, order.getIntValue());
			stmt.registerOutParameter(3, Types.INTEGER);

			this.db.executeUpdate(stmt);

			result = stmt.getInt(3);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Comment create(Comment comment) throws DaoException
	{
		logger.debug("create: Comment= " + comment);

		Comment result = null;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call createComment(?,?,?,?,?,?,?)");
			stmt.setLong(1, comment.getUser().getId());
			stmt.setLong(2, comment.getLinkId());
			stmt.setLong(3, comment.getParentId());
			stmt.setLong(4, comment.getTopId());
			stmt.setString(5, comment.getText());
			stmt.setLong(6, comment.getCreatedOn().getTime());
			stmt.registerOutParameter(4, Types.INTEGER);
			stmt.registerOutParameter(7, Types.INTEGER);

			this.db.executeUpdate(stmt);

			long id = stmt.getInt(7);

			// The topId we give might not be the topId actuall stored.  An
			// example is for top-level Comments: Comments with a parentId of
			// zero have a topId value equal to their id.  However, we do not
			// know the id until we insert the comment.  We could do this
			// check ourselves, but I'd rather let the database take care of
			// details like this and we can just maintain what it dictates
			// (maybe other cases/ conditions will arise).
			long topId = stmt.getInt(4);

			Comment.Builder builder = comment.getBuilder();
			builder.id = id;
			builder.topId = topId;
			result = builder.build();
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public void update(Comment model)
	{
		throw new UnsupportedOperationException("Not supported. Probably won't get supported either.");
	}

	@Override
	public int rate(Comment comment, User user, int rating, long when) throws DaoException
	{
		logger.debug("rate: Comment= " + comment + ",User=" + user + ",rating=" + rating + ",when=" + when);

		int deltaRating = 0;

		try
		{
			CallableStatement stmt = this.db.prepareCall("call rateComment(?,?,?,?,?)");
			stmt.setLong(1, comment.getId());
			stmt.setLong(2, user.getId());
			stmt.setInt(3, rating);
			stmt.setLong(4, when);
			stmt.registerOutParameter(5, Types.INTEGER);

			this.db.executeUpdate(stmt);

			deltaRating = stmt.getInt(4);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return deltaRating;
	}

	@Override
	public DaoPage<Comment> pageByUser(User user, int offset, int count, CommentOrder order) throws DaoException
	{
		logger.debug("page: User= " + user + ",offset=" + offset + ",count=" + count +
					 ",order=" + order.name());

		DaoPage<Comment> result = null;
		List<Comment> comments = new ArrayList<Comment>();

		try
		{
			CallableStatement stmt = this.db.prepareCall("call pageCommentsByUser(?,?,?,?,?)");
			stmt.setLong(1, user.getId());
			stmt.setInt(2, offset);
			stmt.setInt(3, count);
			stmt.setInt(4, order.getIntValue());
			stmt.registerOutParameter(5, Types.INTEGER);

			ResultSet resultSet = this.db.executeQuery(stmt);

			int size = Math.max(0, stmt.getInt(5));

			while (resultSet.next())
			{
				Comment aComment = build(resultSet);
				logger.debug("found: Comment=" + aComment);

				comments.add(aComment);
			}

			result = new DaoPage(comments, size);
		}
		catch (SQLException e)
		{
			logger.fatal(e);
			throw new DaoException(e);
		}

		return result;
	}

	@Override
	public Comment build(ResultSet resultSet) throws SQLException, DaoException
	{
		if (resultSet == null)
		{
			return null;
		}

		Comment.Builder builder = new Comment.Builder();

		builder.id = resultSet.getLong(CommentVocab.ID);

		builder.parentId = resultSet.getLong(CommentVocab.PARENT_ID);
		builder.topId = resultSet.getLong(CommentVocab.TOP_ID);
		builder.linkId = resultSet.getLong(CommentVocab.LINK_ID);

		// Rating details (ups & downs) are optional.
		try
		{
			builder.ups = resultSet.getLong(CommentVocab.UPS);
			builder.downs = resultSet.getLong(CommentVocab.DOWNS);
		}
		catch (SQLException e)
		{
		}

		builder.text = resultSet.getString(CommentVocab.TEXT);
		builder.createdOn = resultSet.getLong(CommentVocab.CREATED_ON);

		User.Builder userBuilder = new User.Builder();
		userBuilder.id = resultSet.getLong(CommentVocab.USER_ID);
		builder.user = this.build(resultSet, userBuilder.build());

		return builder.build();
	}
}
