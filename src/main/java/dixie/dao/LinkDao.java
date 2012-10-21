package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.dao.order.LinkOrder;
import dixie.lang.DemotionReason;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.model.User;

public interface LinkDao extends Dao<Link>, Pageable<Link>
{
	/**
	 * Tries to find and return a Link by a URL.
	 *
	 * @param url URL.
	 * @throws dixie.dao.exception.DaoException
	 */
	public Link readByUrl(String url) throws DaoException;

	/**
	 * Increment the view counter by 1.
	 * 
	 * @param link what to increment the view counter for.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void incrementViews(Link link) throws DaoException;

	/**
	 * Have a User promote a Link.
	 *
	 * @param link what to promote.
	 * @param user who's promoting.
	 * @param when in millisecond since epoch.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void promote(Link link, User user, long when) throws DaoException;

	/**
	 * Undo a User's Link promotion.
	 *
	 * @param link what to un-promote.
	 * @param user who's un-promoting.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void unPromote(Link link, User user) throws DaoException;

	/**
	 * Have a User demote a Link with a reason.
	 * 
	 * @param link what to demote.
	 * @param user who's demoting.
	 * @param reason why the link deserves to be demoted.
	 * @param when in millisecond since epoch.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void demote(Link link, User user, DemotionReason reason, long when) throws DaoException;

	/**
	 * Undo a User's Link demotions.
	 * 
	 * @param link what to un-demote.
	 * @param user who's un-demoting.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void unDemote(Link link, User user) throws DaoException;

	/**
	 * Have a User favorite a Link.
	 *
	 * @param link what to favorite.
	 * @param user who's favoriting.
	 * @param when in millisecond since epoch.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void favorite(Link link, User user, long when) throws DaoException;

	/**
	 * Undo a User's Link favorite.
	 *
	 * @param link what to un-favorite.
	 * @param user who's un-favoriting.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void unFavorite(Link link, User user) throws DaoException;

	/**
	 * Add a Tag to a Link in a specific position.
	 *
	 * @param link the link.
	 * @param tag the tag to add.
	 * @param position the position to put the Tag in (zero-indexed).
	 * @throws dixie.dao.exception.DaoException
	 */
	public void tag(Link link, Tag tag, int position) throws DaoException;

	/**
	 * Remove every Tag on a Link. Typical before an update on a Link.
	 *
	 * @param link the link.
	 * @throws dixie.dao.exception.DaoException
	 */
	public void unTagAll(Link link) throws DaoException;

	/**
	 * Get a page of Link-s of a given format, tagged with the given Tag-s.
	 *
	 * @param format Link-s must be of this Format.
	 * @param tags Link-s must have all these Tag-s.
	 * @param offset the Link to start count-ing from. Zero-indexed.
	 * @param count the number of Link-s per page.
	 * @param order how to order the results (before offset and count).
	 * @return a page of Link-s.
	 * @throws dixie.dao.exception.DaoException
	 */
	public DaoPage<Link> pageByFormatAndTags(Format format, TagList tags, int offset, int count, LinkOrder order) throws DaoException;

	/**
	 * Get a page of {@code Link}s with the specified {@code Format} and
	 * {@code Tag}s given. Also, the {@code Link}s must be related to the
	 * user according to the {@code UserLinkRelation} given.
	 * 
	 * @param format {@code Link}s must be of this Format.
	 * @param tags {@code Link}s must have all these {@code Tag}s.
	 * @param user {@code Link}s must be related to this {@code User}.
	 * @param relation how the {@code Link}s and {@code User} must be related.
	 * @param offset the {@code Link} to start count-ing from. Zero-indexed.
	 * @param count the number of {@code Link}s per page.
	 * @param order how to order the results (before offset and count).
	 * @return a page of {@code Link}s.
	 * @throws dixie.dao.exception.DaoException
	 */
	public DaoPage<Link> pageByFormatAndTagsWithUserRelation(Format format, TagList tags, User user, UserLinkRelation relation, int offset, int count, LinkOrder order) throws DaoException;
}