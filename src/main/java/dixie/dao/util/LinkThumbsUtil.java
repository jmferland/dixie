package dixie.dao.util;

import dixie.dao.exception.DaoException;
import dixie.model.Link;
import java.awt.image.BufferedImage;

/**
 *
 * @author jferland
 */
public interface LinkThumbsUtil
{
	/**
	 * Create thumbnails for the given {@code Link} using the given {@code BufferedImage}.
	 * 
	 * @param link
	 * @param image
	 * @throws dixie.dao.exception.DaoException
	 */
	public void create(Link link, BufferedImage image) throws DaoException;

	/**
	 * Delete the thumbnails of the given {@code Link}.
	 * 
	 * @param link
	 */
	public void delete(Link link);

	/**
	 * Get a {@code ThumbSrc} for the given {@code Link} and {@code Size}.
	 * @param link
	 * @param size
	 * @return
	 */
	public ThumbSrc getThumbSrc(Link link, Size size);

	/**
	 * Defines the size and name for {@code Link} thumbnails.
	 *
	 * @author jferland
	 */
	public static enum Size
	{
		TINY("t", 48, 48),
		SMALL("s", 80, 80),
		MEDIUM("m", 120, 120),
		LARGE("l", 160, 160);
		public final String fileName;
		public final int width;
		public final int height;

		private Size(String fileName, int width, int height)
		{
			this.fileName = fileName;
			this.width = width;
			this.height = height;
		}
	}
}
