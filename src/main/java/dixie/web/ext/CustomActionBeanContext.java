package dixie.web.ext;

import com.google.inject.Inject;
import dixie.dao.DaoCommand;
import dixie.dao.DaoManager;
import dixie.dao.exception.DaoException;
import dixie.model.Settings;
import dixie.model.User;
import java.math.BigInteger;
import javax.servlet.http.HttpSession;
import net.sourceforge.stripes.action.ActionBeanContext;
import org.apache.log4j.Logger;

public class CustomActionBeanContext extends ActionBeanContext
{
	private static final Logger logger = Logger.getLogger(CustomTypeConverterFactory.class.getName());
	private static final String USER_ID_KEY = "user_id";
	protected User cachedUser;
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	public DaoManager getDaoManager()
	{
		return daoManager;
	}

	/**
	 * Note that the User id is saved and the User is pulled from the database
	 * instead of storing the User. This is more costly but should help to
	 * keep things more consistent.
	 * 
	 * @param user User that is "logged in"
	 */
	public void setUser(User user)
	{
		// TODO: not sure this actually gets a new clean session or just
		// breaks the current one.
		// Start with a clean slate.
		HttpSession session = getRequest().getSession();
		session.invalidate();

		if (user != null)
		{
			setSessionAttr(USER_ID_KEY, BigInteger.valueOf(user.getId()));
		}
		else
		{
			setSessionAttr(USER_ID_KEY, null);
		}
	}

	public User getUser()
	{
		final BigInteger userId = getSessionAttr(USER_ID_KEY, null);

		if (userId == null)
		{
			return null;
		}

		// Only lookup the User until it is done once, successfully.
		if (this.cachedUser != null)
		{
			return this.cachedUser;
		}

		try
		{
			this.cachedUser = (User) this.daoManager.transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return daoManager.getUserDao().read(userId.longValue());
				}
			});
		}
		catch (Exception e)
		{
			logger.fatal(e);
		}

		return this.cachedUser;
	}

	public void logout()
	{
		setUser(null);

		HttpSession session = getRequest().getSession();

		if (session != null)
		{
			session.invalidate();
		}
	}

	protected void setSessionAttr(String key, Object value)
	{
		getRequest().getSession().setAttribute(key, value);
	}

	protected <T> T getSessionAttr(String key, T defaultValue)
	{
		T value = (T) getRequest().getSession().getAttribute(key);
		if (value == null)
		{
			value = defaultValue;
			setSessionAttr(key, value);
		}
		return value;
	}

	public void setSetting(Settings.Key key, Object value)
	{
		setSessionAttr(key.getSessionKey(), value);
	}

	public <T> T getSetting(Settings.Key key, T defaultValue)
	{
		return getSessionAttr(key.getSessionKey(), defaultValue);
	}
}