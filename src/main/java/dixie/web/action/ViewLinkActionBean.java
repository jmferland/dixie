package dixie.web.action;

import dixie.dao.DaoCommand;
import dixie.dao.DaoPage;
import dixie.dao.exception.DaoException;
import dixie.dao.order.CommentOrder;
import dixie.model.Comment;
import dixie.model.Link;
import dixie.model.Settings;
import dixie.web.action.helper.CaptchaHelper;
import dixie.web.action.helper.PageHelper;
import dixie.web.nonext.converter.NullBaseModelIdTypeConverterFormatter;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
@UrlBinding("/link/{link}/{title}/page{page.index}")
public class ViewLinkActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(ViewLinkActionBean.class);
	private static final int DEFAULT_PAGE_SIZE = 50;
	private static final CommentOrder DEFAULT_PAGE_ORDER = CommentOrder.OLDEST;
	private static final String VIEW = "/WEB-INF/ftl/link/view.ftl";
	private DaoPage<Comment> comments;
	@Validate
	public Link link;
	@ValidateNestedProperties(
	{
		@Validate(field = "text", required = true, minlength = 2, maxlength = 5000)
	})
	public Comment.Builder comment;
	@Validate(converter = NullBaseModelIdTypeConverterFormatter.class)
	public Comment replyTo;
	@ValidateNestedProperties(
	{
		@Validate(field = "index"),
		@Validate(field = "size"),
		@Validate(field = "order")
	})
	public PageHelper<CommentOrder> page = new PageHelper<CommentOrder>(this, DEFAULT_PAGE_SIZE, DEFAULT_PAGE_ORDER).
			saveSizeUnder(Settings.Key.COMMENT_PAGE_SIZE).
			saveOrderUnder(Settings.Key.COMMENT_ORDER);
	@ValidateNestedProperties(
	{
		@Validate(field = "uuid"),
		@Validate(field = "answer")
	})
	public CaptchaHelper captcha = new CaptchaHelper(this);

	public DaoPage<Comment> getComments()
	{
		if (comments == null)
		{
			try
			{
				comments = (DaoPage<Comment>) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getCommentDao().page(link,
																	page.getOffset(),
																	page.getSize(),
																	page.getOrder());
					}
				});

				page.setMax(comments);
			}
			catch (Exception e)
			{
				logger.fatal("Comments page error: " + e);
			}
		}

		return comments;
	}

	@ValidationMethod(on = "addComment", priority = 100)
	public void validateCaptcha(ValidationErrors errors)
	{
		captcha.validate();
	}

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		if (link == null)
		{
			return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
		}

		// TODO: only if we didn't just come from this link?
		// TODO: track ip or session id?
		try
		{
			getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					getDaoManager().getLinkDao().incrementViews(link);
					return null;
				}
			});
		}
		catch (DaoException e)
		{
		}

		return new ForwardResolution(VIEW);
	}

	@DontValidate
	public Resolution redirect()
	{
		return redirect(1);
	}

	public Resolution addComment()
	{
		if (link == null)
		{
			return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
		}

		if (replyTo != null)
		{
			comment.parentId = replyTo.getId();
			comment.topId = replyTo.getTopId();
		}

		comment.linkId = link.getId();
		comment.user = getLoggedInUser();

		int newCommentPage = 1;

		try
		{
			Integer offset = (Integer) getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					Comment newComment = getDaoManager().getCommentDao().create(comment.build());
					return getDaoManager().getCommentDao().getOffset(newComment, page.getOrder());
				}
			});

			if (offset != null &&
				offset != -1)
			{
				newCommentPage = offset / page.getSize() + 1;
			}
		}
		catch (Exception e)
		{
			ValidationErrors errors = new ValidationErrors();
			errors.addGlobalError(new ScopedLocalizableError("validation", "daoException"));

			getContext().setValidationErrors(errors);
			return getContext().getSourcePageResolution();
		}

		return redirect(newCommentPage);
	}

	/**
	 * See return.
	 *
	 * @param page which page to go to.
	 * @return a resolution that redirects to the current url with the
	 * given page.
	 */
	private Resolution redirect(int page)
	{
		RedirectResolution resolution = new RedirectResolution(getClass());

		resolution.addParameter("link", link.getId());
		resolution.addParameter("title", link.getUrlSafeTitle());
		resolution.addParameter("page.index", page);

		return resolution;
	}
}
