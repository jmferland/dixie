package dixie.dao.util;

import com.google.inject.Singleton;
import dixie.dao.exception.DaoException;
import dixie.model.Link;
import dixie.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * (1) static URL (do not want a db connection for every piece of media)
 * (2) can change to a ISO/ descriptive filename LATER
 * (3) URL should be accessible from the model, so it can be done easily in the view (should be at least an object but maybe not a model [ie persist])
 * (4) For now the link DAO can delete thumbs whenever there is an error in a call to create (and delete in a delete call, but not re-create? sure.)
 * (5) Should the DAO or Model define the src/ path?
 * (6) Do I want a DB connection open while the images are being created?
 * 
 * @author jferland
 */
@Singleton
public class LocalLinkThumbsUtil implements LinkThumbsUtil
{
	private static final String THUMB_FORMAT = "jpg";
	private static final String THUMB_EXTENSION = "." + THUMB_FORMAT;
	private final File baseDir;
	private final String urlPrefix;

	public LocalLinkThumbsUtil(String basePath, String urlPrefix)
	{
		this.baseDir = new File(basePath);
		this.urlPrefix = urlPrefix;
	}

	@Override
	public void create(Link link, BufferedImage image) throws DaoException
	{
		for (Size size : Size.values())
		{
			// Need a clipping with the proper aspect ratio before we scale it
			// in order to maintain the appropriate aspect ratio.
			BufferedImage tmp = ImageUtil.getClippedInstance(image, size.width / size.height);
			tmp = ImageUtil.getScaledInstance(tmp, size.width, size.height);

			File thumb = new File(baseDir, new LocalThumbSrc(link, size).getPath());

			try
			{
				// Ensure parent directories exist.
				File parentDir = thumb.getParentFile();
				if (parentDir != null)
				{
					// Returns false if it already exists.
					parentDir.mkdirs();
				}

				if (!ImageIO.write(tmp, THUMB_FORMAT, thumb))
				{
					throw new IOException("Unable to create thumbnail.");
				}
			}
			catch (IOException e)
			{
				throw new DaoException(e);
			}
		}
	}

	@Override
	public void delete(Link link)
	{
		for (Size size : Size.values())
		{
			File thumb = new File(baseDir, new LocalThumbSrc(link, size).getPath());
			thumb.delete();
		}
	}

	@Override
	public ThumbSrc getThumbSrc(Link link, Size size)
	{
		return new LocalThumbSrc(link, size);
	}

	private void writeQualityJpeg(BufferedImage image, File file) throws DaoException
	{
		Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

		// Choose the first image writer available (unless you want to
		// choose a specific writer) and create an ImageWriter instance:
		ImageWriter writer = (ImageWriter) iter.next();

		// Instantiate an ImageWriteParam object with default compression options.
		ImageWriteParam iwp = writer.getDefaultWriteParam();

		// Now, we can set the compression quality:
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(1); // a float between 0 and 1, where 1 is minimum compression with best quality.

		FileImageOutputStream output = null;

		try
		{
			// Write the file
			output = new FileImageOutputStream(file);

			writer.setOutput(output);

			IIOImage iioImage = new IIOImage(image, null, null);
			
			writer.write(null, iioImage, iwp);
		}
		catch (Exception e)
		{
			throw new DaoException(e);
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
				}
			}

			writer.dispose();
		}
	}

	/**
	 * Build and return a path where each byte in the given {@code BigInteger id}
	 * represents a folder. The given {@code id} must be greater than zero. Leading
	 * zero bytes will not be included.
	 *
	 * @param id number to get path from.
	 * @return path representation of this number, or empty string if none.
	 */
	private static String getIdPath(BigInteger id)
	{
		StringBuffer path = new StringBuffer();

		if (id.compareTo(BigInteger.ZERO) == 1) // id > 0?
		{
			byte[] bytes = id.toByteArray();
			for (byte b : bytes)
			{
				path.append(File.separator);
				path.append(Byte.toString(b));
			}
		}

		return path.toString();
	}

	public class LocalThumbSrc implements ThumbSrc
	{
		private final Link link;
		private final Size size;
		private String path;
		private String source;
		private Boolean isValid;

		public LocalThumbSrc(Link link, Size size)
		{
			this.link = link;
			this.size = size;
		}

		public String getPath()
		{
			if (path == null)
			{
				String tmp = getIdPath(BigInteger.valueOf(link.getId()));

				if (!tmp.isEmpty())
				{
					tmp += File.separator + size.fileName + THUMB_EXTENSION;
				}

				path = tmp;
			}

			return path;
		}

		@Override
		public String getSrc()
		{
			if (source == null)
			{
				String tmp = getPath();

				if (!tmp.isEmpty())
				{
					tmp = urlPrefix + tmp;
				}
				source = tmp;
			}

			return source;
		}

		@Override
		public boolean getIsValid()
		{
			if (isValid == null)
			{
				File thumb = new File(baseDir, getPath());
				isValid = thumb.exists();
			}

			return isValid;
		}
	}
}
