package dixie.web.action;

import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.Captcha;
import java.util.Date;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

/**
 *
 * @author jferland
 */
@UrlBinding("/captcha/ask")
public class AskCaptchaActionBean extends BaseActionBean
{
	private static final String ASK = "/WEB-INF/ftl/captcha/ask.ftl";
	private static long lastPruneTime = 0;
	private Captcha captcha;

	public Captcha getCaptcha()
	{
		if (captcha == null)
		{
			try
			{
				// TODO: this could fail, and if it does then a big FTL error
				// will explode in our faces.
				captcha = (Captcha) getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return getDaoManager().getCaptchaDao().create(Captcha.randomCaptcha());
					}
				});
			}
			catch (Exception e)
			{
			}
		}

		return captcha;
	}

	/**
	 * Periodically delete expired Captcha objects.
	 */
	private synchronized void pruneCaptchas()
	{
		if (lastPruneTime + Captcha.DEFAULT_LIFETIME < new Date().getTime())
		{
			try
			{
				final long beforeTime = new Date().getTime() - Captcha.DEFAULT_LIFETIME;
				getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						getDaoManager().getCaptchaDao().deleteByCreatedBefore(beforeTime);
						return null;
					}
				});
			}
			catch (Exception e)
			{
			}

			lastPruneTime = new Date().getTime();
		}
	}

	@DontValidate
	@DefaultHandler
	public Resolution ask()
	{
		pruneCaptchas();

		return new ForwardResolution(ASK);
	}
}
