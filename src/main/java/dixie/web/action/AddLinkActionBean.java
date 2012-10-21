package dixie.web.action;

import com.google.inject.Inject;
import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.dao.util.LinkThumbsUtil;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.web.action.helper.CaptchaHelper;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.security.PermitAll;
import javax.imageio.ImageIO;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.action.Wizard;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import net.sourceforge.stripes.validation.ValidationState;
import org.apache.log4j.Logger;

/**
 * Link submission wizard.
 *
 * @author jferland
 */
@PermitAll
@UrlBinding("/submit")
@Wizard(startEvents =
{
	"url", "done"
})
public class AddLinkActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(AddLinkActionBean.class);
	private static final int TAGLIST_MAX_SIZE = 10;
	private static final int TAGLIST_MIN_SIZE = 2;
	private static final int THUMB_MAX_BYTES = 1 * 1024 * 1024;
	private static final int URL_READ_BUFFER_SIZE = 16384;
	private static final String URL_FIELD = "link.url";
	private static final String THUMB_URL_FIELD = "link.thumbUrl";
	private static final String TAGS_FIELD = "link.tags";
	private static final Pattern INVALID_HOST = Pattern.compile("^[\\d.]*$");
	private static final String URL = "/WEB-INF/ftl/link/add_url.ftl";
	private static final String DETAILS = "/WEB-INF/ftl/link/add_details.ftl";
	private List<Format> allFormats;
	private BufferedImage thumb;
	private LinkThumbsUtil linkThumbsUtil;
	@ValidateNestedProperties(
	{
		@Validate(field = "url", required = true, maxlength = 255),
		@Validate(field = "format", required = true),
		@Validate(field = "sourceThumbUrl", maxlength = 255),
		@Validate(field = "title", required = true, minlength = 5, maxlength = 65),
		@Validate(field = "notes", required = true, minlength = 15, maxlength = 1000),
		@Validate(field = "tags", required = true)
	})
	public Link.Builder link;
	@ValidateNestedProperties(
	{
		@Validate(field = "uuid"),
		@Validate(field = "answer")
	})
	public CaptchaHelper captcha = new CaptchaHelper(this);

	@Inject
	public void setLinkThumbsUtil(LinkThumbsUtil linkThumbsUtil)
	{
		this.linkThumbsUtil = linkThumbsUtil;
	}

	/**
	 * Provide all {@code Format}-s whenever asked (instead of setting it
	 * only in a specific event).
	 *
	 * @return root a list of all the {@code Format}-s.
	 */
	public List<Format> getAllFormats()
	{
		if (this.allFormats == null)
		{
			try
			{
				this.allFormats = (List<Format>) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getFormatDao().readAll();
					}
				});
			}
			catch (DaoException e)
			{
			}
		}

		return this.allFormats;
	}

	/**
	 * Check that the url is: (1) new (2) exists and (3) is not part of this
	 * website (bad for at least two reasons: recursion and it's just lame).
	 *
	 * @param errors what errors, if any, are added to.
	 */
	@ValidationMethod(priority = 1)
	public void validateUrl(ValidationErrors errors)
	{
		// Ensure HTTP protocol link.

		if (link.url.startsWith("http://") == false)
		{
			errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlBadProtocol"));
			return;
		}

		// Check that we are not checking ourselves before we check to see
		// if the link exists to avoid potential recursive calls.

		// TODO: not sure the client cannot alter this.
		String serverName = this.getContext().getRequest().getServerName();

		try
		{
			URL url = new URL(link.url);
			String lcUrlHost = url.getHost().toLowerCase();

			if (INVALID_HOST.matcher(lcUrlHost).matches())
			{
				errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlInvalidDomain"));
				return;
			}

			if (lcUrlHost.contains(serverName))
			{
				errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlLocal"));
				return;
			}

			// TODO: can this follow cyclic redirects forever?
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(1500);
			conn.setConnectTimeout(1500);
			conn.setRequestMethod("HEAD");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlNotFound"));
				return;
			}
		}
		catch (MalformedURLException e)
		{
			logger.debug("Malformed URL: " + e);
			errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlMalformed"));
			return;
		}
		catch (IOException e)
		{
			logger.debug("URL not found: " + e);
			errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlNotFound"));
			return;
		}

		try
		{
			// Check if this URL has already been submitted.
			Link originalLink = (Link) getDaoManager().autoCommit(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return getDaoManager().getLinkDao().readByUrl(link.url);
				}
			});

			if (originalLink != null)
			{
				errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlDuplicate"));
				return;
			}
		}
		catch (DaoException e)
		{
			logger.error("Failure checking link duplication: " + e);
			errors.add(URL_FIELD, new ScopedLocalizableError("validation", "urlUnableToCheck"));
			return;
		}
	}

	/**
	 * Ensure an appropriate number of tags.
	 *
	 * @param errors what errors, if any, are added to.
	 */
	@ValidationMethod(on = "save", when = ValidationState.ALWAYS, priority = 2)
	public void validateTags(ValidationErrors errors)
	{
		List<Tag> uniqueTags = new ArrayList<Tag>();

		for (Tag tag : link.tags.getList())
		{
			if (tag.getName().length() > Tag.NAME_MAX_LENGTH)
			{
				errors.add(TAGS_FIELD, new ScopedLocalizableError("validation", "tagsTooLong", tag.getName(), Tag.NAME_MAX_LENGTH));
			}
			else if (tag.getName().length() < Tag.NAME_MIN_LENGTH)
			{
				errors.add(TAGS_FIELD, new ScopedLocalizableError("validation", "tagsTooShort", tag.getName(), Tag.NAME_MIN_LENGTH));
			}
			else if (uniqueTags.contains(tag) == true)
			{
				// Give an error instead of automatically removing incase this
				// was a spelling mistake/ accident.
				errors.add(TAGS_FIELD, new ScopedLocalizableError("validation", "tagsNotUnique", tag.getName()));
			}
			else
			{
				// No error.
				uniqueTags.add(tag);
			}
		}

		if (this.link.tags.getList().size() > TAGLIST_MAX_SIZE)
		{
			logger.debug("Too many tags");
			errors.add(TAGS_FIELD, new ScopedLocalizableError("validation", "tagsTooMany", TAGLIST_MAX_SIZE));
			return;
		}
		else if (this.link.tags.getList().size() < TAGLIST_MIN_SIZE)
		{
			logger.debug("Too few tags");
			errors.add(TAGS_FIELD, new ScopedLocalizableError("validation", "tagsTooFew", TAGLIST_MIN_SIZE));
			return;
		}
	}

	@ValidationMethod(on = "save", priority = 3)
	public void validateCaptcha(ValidationErrors errors)
	{
		captcha.validate();
	}

	@ValidationMethod(on = "save", priority = 4) // Most expensive .: done last and if no errors.
	public void validateThumbUrl(ValidationErrors errors)
	{
		if (link.sourceThumbUrl == null)
		{
			// This is optional, so validation should occur only if there is a
			// value; however, we must set __some__ value for when this Link is
			// created.
			link.sourceThumbUrl = "";
			return;
		}

		ByteArrayOutputStream outputStream = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;

		try
		{
			URL url = new URL(link.sourceThumbUrl);

			// TODO: can this follow cyclic redirects forever?
			HttpURLConnection.setFollowRedirects(true);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(1500);
			conn.setConnectTimeout(1500);
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "urlNotFound"));
				return;
			}

			if (!conn.getContentType().startsWith("image/"))
			{
				errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "imageInvalidType"));
				return;
			}

			if (conn.getContentLength() > THUMB_MAX_BYTES)
			{
				errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "imageTooLarge"));
				return;
			}

			inputStream = conn.getInputStream();
			outputStream = new ByteArrayOutputStream();

			int totalBytesRead = 0;
			byte[] buffer = new byte[URL_READ_BUFFER_SIZE];
			while (totalBytesRead < THUMB_MAX_BYTES)
			{
				int bytesToRead = Math.min(buffer.length, THUMB_MAX_BYTES - totalBytesRead);
				int bytesRead = inputStream.read(buffer, 0, bytesToRead);

				if (bytesRead > 0)
				{
					outputStream.write(buffer, 0, bytesRead);
					totalBytesRead += bytesRead;
				}
				else if (bytesRead == -1)
				{
					break;
				}
			}

			if (inputStream.read() != -1)
			{
				errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "imageTooLarge"));
				return;
			}
		}
		catch (MalformedURLException e)
		{
			logger.debug("Malformed URL: " + e);
			errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "urlMalformed"));
			return;
		}
		catch (IOException e)
		{
			logger.debug("URL not found: " + e);
			errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "urlNotFound"));
			return;
		}
		finally
		{
			// Make sure we clean up any InputStream-s and/or connections.
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException ex)
				{
				}
			}
			if (conn != null)
			{
				conn.disconnect();
			}
		}

		try
		{
			thumb = ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray()));
		}
		catch (IOException e)
		{
			logger.debug("Thumbnail cannot be interpretted as image: " + e);
			errors.add(THUMB_URL_FIELD, new ScopedLocalizableError("validation", "imageInvalid"));
			return;
		}
	}

	@DontValidate
	@DefaultHandler
	public Resolution url()
	{
		return new ForwardResolution(URL);
	}

	public Resolution details() throws DaoException
	{
		return new ForwardResolution(DETAILS);
	}

	public Resolution save() throws DaoException
	{
		Link newLink = null;

		// Attach link to logged in user.
		link.user = getLoggedInUser();

		try
		{
			newLink = (Link) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					Link newLink = getDaoManager().getLinkDao().create(link.build(getDaoManager().
							getLinkThumbsUtil()));

					if (newLink != null && thumb != null)
					{
						// Create thumbnails. Unfortunately a connection will
						// remain open when this happens, but we do need to roll
						// back if something bad happens.
						// TODO: this could be done inside the LinkDao; however,
						// we would need to pass the BufferedImage to it (in the
						// Link.Builder?). 
						linkThumbsUtil.create(newLink, thumb);
					}

					return newLink;
				}
			});
		}
		catch (Exception e)
		{
			if (newLink != null && thumb != null)
			{
				// Something went wrong, clean up after ourselves.
				linkThumbsUtil.delete(newLink);
			}

			ValidationErrors errors = new ValidationErrors();

			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));

			// NOTE: There are a couple of options how to do this, one:
			//return new ForwardResolution(this.getClass(), "details");
			// or two:
			getContext().setValidationErrors(errors);
			return getContext().getSourcePageResolution();
		}

		return new RedirectResolution(ViewLinkActionBean.class).addParameter("link", newLink.getId()).
				addParameter("title", newLink.getUrlSafeTitle());
	}

	@DontValidate
	public Resolution cancel()
	{
		return new RedirectResolution(HomeActionBean.class);
	}
}