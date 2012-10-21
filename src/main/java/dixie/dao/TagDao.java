package dixie.dao;

import dixie.dao.exception.DaoException;
import dixie.dao.order.TagOrder;
import dixie.lang.TagList;
import dixie.lang.UserLinkRelation;
import dixie.model.Format;
import dixie.model.Tag;
import dixie.model.User;

/**
 *
 * @author jferland
 */
public interface TagDao extends Dao<Tag>
{
	/**
	 * Read and return a Tag with the given name. Tag names are unique, so
	 * there can only be one result returned.
	 *
	 * @param name the Tag must have this name.
	 * @return the Tag with the given name or null if none exists.
	 */
	public Tag readByName(String name) throws DaoException;

	/**
	 * Get a page of Tags where each Tag returned describes a Link that is
	 * tagged with all Tags in the given TagList.
	 *
	 * @param format
	 * @param tags
	 * @param offset
	 * @param count
	 * @param order
	 * @return
	 * @throws dixie.dao.exception.DaoException
	 */
	public DaoPage<Tag> pageByLinkFormatAndTags(Format format, TagList tags, int offset, int count, TagOrder order) throws DaoException;

	/**
	 * Get a page of {@code Tag}s where every {@code Tag} describes a {@code Link}
	 * that is tagged with all {@code Tag}s in the given {@code TagList}. Also, the
	 * {@code Link}s must be related to the user according to the
	 * {@code UserLinkRelation} given.
	 * 
	 * @param format {@code Link}s must be of this Format.
	 * @param tags {@code Link}s must have all these {@code Tag}s.
	 * @param user {@code Link}s must be related to this {@code User}.
	 * @param relation how the {@code Link}s and {@code User} must be related.
	 * @param offset the {@code Tag} to start count-ing from. Zero-indexed.
	 * @param count the number of {@code Tag}s per page.
	 * @param order how to order the results (before offset and count).
	 * @return a page of {@code Tag}s.
	 * @throws dixie.dao.exception.DaoException
	 */
	public DaoPage<Tag> pageByLinkFormatAndTagsWithUserRelation(Format format, TagList tags, User user, UserLinkRelation relation, int offset, int count, TagOrder order) throws DaoException;
}

