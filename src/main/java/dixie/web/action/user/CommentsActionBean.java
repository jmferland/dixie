package dixie.web.action.user;

import dixie.web.action.*;
import dixie.dao.DaoCommand;
import dixie.dao.DaoPage;
import dixie.dao.exception.DaoException;
import dixie.dao.order.CommentOrder;
import dixie.model.Comment;
import dixie.model.Settings;
import dixie.model.User;
import dixie.web.action.helper.PageHelper;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ErrorResolution;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
// TODO: look into why /user/comments/{user} works but /user/{user}/comments doesn't.
@UrlBinding("/user/comments/{user}/page{page.index}")
public class CommentsActionBean extends BaseActionBean
{
	private static final Logger logger = Logger.getLogger(CommentsActionBean.class);
	private static final String VIEW = "/WEB-INF/ftl/user/comments.ftl";
	protected static final int DEFAULT_PAGE_SIZE = 50;
	protected static final CommentOrder DEFAULT_ORDER = CommentOrder.NEWEST;
	private DaoPage<Comment> comments;
	@Validate
	public User user;
	@ValidateNestedProperties(
	{
		@Validate(field = "index"),
		@Validate(field = "size"),
		@Validate(field = "order")
	})
	public PageHelper<CommentOrder> page = new PageHelper<CommentOrder>(this, DEFAULT_PAGE_SIZE, DEFAULT_ORDER).
			saveSizeUnder(Settings.Key.COMMENT_PAGE_SIZE).
			saveOrderUnder(Settings.Key.COMMENT_ORDER);

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
						return getDaoManager().getCommentDao().pageByUser(user, page.getOffset(), page.
								getSize(), page.getOrder());
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

	@DefaultHandler
	@DontValidate
	public Resolution view()
	{
		if (user == null)
		{
			return new ErrorResolution(HttpServletResponse.SC_NOT_FOUND);
		}

		return new ForwardResolution(VIEW);
	}
}
