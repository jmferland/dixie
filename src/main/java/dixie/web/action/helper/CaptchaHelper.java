package dixie.web.action.helper;

import dixie.dao.DaoCommand;
import dixie.dao.exception.DaoException;
import dixie.model.Captcha;
import dixie.web.action.BaseActionBean;
import java.util.Date;
import java.util.UUID;
import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.log4j.Logger;

/**
 *
 * @author jferland
 */
public class CaptchaHelper
{
	private static final Logger logger = Logger.getLogger(CaptchaHelper.class);
	protected static final String CAPTCHA_UUID_FIELD_NAME = "captcha.uuid";
	protected static final String CAPTCHA_ANSWER_FIELD_NAME = "captcha.answer";
	private final BaseActionBean actionBean;
	public String uuid;
	public String answer;

	public CaptchaHelper(BaseActionBean actionBean)
	{
		this.actionBean = actionBean;
	}

	/**
	 * Called whenever the Captcha information should be validated. Will write
	 * any errors into the actionBean's ValidationErrors.
	 */
	public void validate()
	{
		ValidationErrors errors = actionBean.getContext().getValidationErrors();
		Captcha captcha = null;

		if (uuid == null)
		{
			errors.add(CAPTCHA_UUID_FIELD_NAME, new ScopedLocalizableError("validation.required", "valueNotPresent"));
			return;
		}

		if (answer == null)
		{
			errors.add(CAPTCHA_ANSWER_FIELD_NAME, new ScopedLocalizableError("validation.required", "valueNotPresent"));
			return;
		}

		try
		{
			captcha = (Captcha) actionBean.getDaoManager().transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return actionBean.getDaoManager().getCaptchaDao().readByUUID(UUID.fromString(uuid));
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			logger.debug("Captcha validation error: " + e);
			errors.add(CAPTCHA_ANSWER_FIELD_NAME, new ScopedLocalizableError("validation", "captchaExpired"));
			return;
		}
		catch (Exception e)
		{
			logger.fatal("Captcha validation error: " + e);
			errors.add(CAPTCHA_ANSWER_FIELD_NAME, new ScopedLocalizableError("validation", "daoException"));
			return;
		}

		if (captcha == null ||
			captcha.getCreatedOn().getTime() + Captcha.DEFAULT_LIFETIME < new Date().getTime())
		{
			errors.add(CAPTCHA_ANSWER_FIELD_NAME, new ScopedLocalizableError("validation", "captchaExpired"));
		}
		else if (captcha.getAnswer().equals(answer) == false)
		{
			errors.add(CAPTCHA_ANSWER_FIELD_NAME, new ScopedLocalizableError("validation", "captchaWrong"));
		}

		if (captcha != null)
		{
			// Remove the expired or now-used Captcha.
			try
			{
				actionBean.getDaoManager().transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						actionBean.getDaoManager().getCaptchaDao().deleteByUUID(UUID.fromString(uuid));
						return null;
					}
				});
			}
			catch (Exception e)
			{
				logger.fatal("Captcha deletion error: " + e);
			}
		}
	}
}
