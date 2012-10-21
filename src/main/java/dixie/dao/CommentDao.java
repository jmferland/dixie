package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.dao.order.CommentOrder;
import dixie.model.Comment;
import dixie.model.Link;
import dixie.model.User;

public interface CommentDao extends Dao<Comment>
{
	/**
	 * Get a page of Comments on a given Link.  For Comments a page is defined by root-level
	 * comments only (not replies); however, the page includes ALL descendents.
	 *
	 * @param link ignore Comments not attached to this link.
	 * @param offset the top-level ancestor to start count-ing from.  Zero-indexed.
	 * @param count the number of top-level Comments per page.
	 * @param order how to order the results.
	 */
	public DaoPage<Comment> page(Link link, int offset, int count, CommentOrder order) throws DaoException;

	/**
	 * Get the offset of a Comment's top-level parent, given a certain ordering.
	 *
	 * @param comment the page will contain this comment.  Only the id is used.
	 * @param order how to order the results.
	 * @return the offset of the given Comment (-1 if it does not exist).
	 */
	public int getOffset(Comment comment, CommentOrder order) throws DaoException;

	/**
	 * Allows a User to rate a Comment.
	 *
	 * @param comment to be rated.
	 * @param user who is rating.
	 * @param rating value {-1,1}.
	 * @param when in millisecond since epoch.
	 * @return The change in the User's rating. For example, if a User first rates
	 * a comment -1 then re-rates it 1, a rating change of 2 would be returned.
	 */
	public int rate(Comment comment, User user, int rating, long when) throws DaoException;


	/**
	 * Get a page of Comments by a given {@code User}. This will return a simple list
	 * of {@code Comment}s, not a tree-like structure (that is, each {@code Comment}'s
	 * children list or parent will NOT be populated).
	 *
	 * @param user the {@code User} that made these {@code Comment}(s).
	 * @param offset the {@code Comment} to start the page on.  Zero-indexed.
	 * @param count the number of {@code Comment}s per page.
	 * @param order how to order the results.
	 */
	public DaoPage<Comment> pageByUser(User user, int offset, int count, CommentOrder order) throws DaoException;
}
