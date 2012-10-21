package dixie.web.action;

import dixie.captcha.CaptchaProducer;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.Captcha;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import org.apache.log4j.Logger;

/**
 * Captcha viewing as a jpg.
 *
 * @author jferland
 */
@UrlBinding("/captcha/{key}.jpg")
public class ViewCaptchaJpgActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(ViewCaptchaJpgActionBean.class);
	private static final String CONTENT_TYPE = "image/jpeg";
	@Validate
	public String key;

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		Captcha captcha = null;

		// 1) Lookup captcha value.
		if (key != null)
		{
			try
			{
				captcha = (Captcha) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getCaptchaDao().readByUUID(UUID.fromString(key));
					}
				});
			}
			catch (Exception e)
			{
				logger.debug("Captcha lookup error: " + e);
			}
		}

		// 2) Generate output.
		if (captcha != null)
		{

			final BufferedImage image = new CaptchaProducer(captcha.getAnswer(), captcha.getSeed()).draw();

			return new StreamingResolution(ViewCaptchaJpgActionBean.CONTENT_TYPE)
			{
				@Override
				public void stream(HttpServletResponse response) throws Exception
				{
					// Set to expire far in the past.
					response.setDateHeader("Expires", 0);
					// Set standard HTTP/1.1 no-cache headers.
					response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
					// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
					response.addHeader("Cache-Control", "post-check=0, pre-check=0");
					// Set standard HTTP/1.0 no-cache header.
					response.setHeader("Pragma", "no-cache");
					// Return a jpeg.
					response.setContentType(ViewCaptchaJpgActionBean.CONTENT_TYPE);

					OutputStream out = response.getOutputStream();

					// Write out the image.
					ImageIO.write(image, "jpg", out);

					try
					{
						out.flush();
					}
					finally
					{
						out.close();
					}
				}
			}; //.setFilename("your-filename.jpg");
		}

		return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
	}
}