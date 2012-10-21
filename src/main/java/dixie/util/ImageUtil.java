package dixie.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
public class ImageUtil
{
	private static final Logger logger = Logger.getLogger(ImageUtil.class);

	/**
	 * Get the largest possible clipped instance of the given {@code BufferedImage} that
	 * has the given aspect ratio.
	 * 
	 * @param img the image to get a clip of.
	 * @param aspectRatio width divided by height.
	 * @return
	 */
	public static BufferedImage getClippedInstance(BufferedImage img, float aspectRatio)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		int w, h;

		w = img.getWidth();
		h = img.getHeight();

		if (aspectRatio > (float) w / h)
		{
			// Adjust height.
			h = (int) Math.ceil(w / aspectRatio);
		}
		else if (aspectRatio < (float) w / h)
		{
			// Adjust width.
			w = (int) Math.ceil(h / aspectRatio);
		}

		BufferedImage ret = new BufferedImage(w, h, type);
		Graphics2D g2 = ret.createGraphics();
		
		// This signature of drawImage crops instead of scales.
		g2.drawImage(img, 0, 0, w, h, 0, 0, w, h, null);

		return ret;
	}

	/**
	 * Convenience method that returns a scaled instance of the provided {@code BufferedImage}.
	 * This method will use a multi-step scaling technique that provides higher quality than
	 * the usual one-step technique (only useful in downscaling cases, where {@code targetWidth}
	 * or {@code targetHeight} is smaller than the original dimensions, and generally only when
	 * the {@code BILINEAR} hint is specified). This code has been adapted from
	 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	 *
	 * This really seems to be the fastest and best result I've come across.
	 *
	 * @param img the original image to be scaled
	 * @param targetWidth the desired width of the scaled instance, in pixels
	 * @param targetHeight the desired height of the scaled instance, in pixels
	 * @return a scaled version of the original {@code BufferedImage}
	 */
	public static BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage) img;
		int w, h;

		// Start with original size, then scale down in multiple passes with drawImage()
		// until the target size is reached.
		w = img.getWidth();
		h = img.getHeight();

		do
		{
			if (w > targetWidth)
			{
				w /= 2;
				if (w < targetWidth)
				{
					w = targetWidth;
				}
			}
			else
			{
				w = targetWidth;
			}

			if (h > targetHeight)
			{
				h /= 2;
				if (h < targetHeight)
				{
					h = targetHeight;
				}
			}
			else
			{
				h = targetHeight;
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		}
		while (w != targetWidth || h != targetHeight);

		return ret;
	}
}
