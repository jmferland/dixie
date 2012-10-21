package dixie.web.ext.di;

import com.google.inject.AbstractModule;
import dixie.dao.CaptchaDao;
import dixie.dao.CategoryDao;
import dixie.dao.CommentDao;
import dixie.dao.FormatDao;
import dixie.dao.LinkDao;
import dixie.dao.TagDao;
import dixie.dao.UserDao;
import dixie.dao.db.CaptchaDbDao;
import dixie.dao.db.CategoryDbDao;
import dixie.dao.db.CommentDbDao;
import dixie.dao.db.FormatDbDao;
import dixie.dao.db.LinkDbDao;
import dixie.dao.db.TagDbDao;
import dixie.dao.db.UserDbDao;
import dixie.dao.util.LinkThumbsUtil;
import dixie.dao.util.LocalLinkThumbsUtil;
import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import net.sourceforge.stripes.config.Configuration;
import org.apache.log4j.Logger;

/**
 * Release configuration for Guice dependency injection.
 * 
 * @author jferland
 */
public class ReleaseGuiceConfigModule extends AbstractModule
{
	private static final Logger logger = Logger.getLogger(ReleaseGuiceConfigModule.class);
	private Configuration config;

	public ReleaseGuiceConfigModule(Configuration config)
	{
		this.config = config;
	}

	/**
	 * Remember that this is only called once, not per injectMembers call.
	 */
	@Override
	protected void configure()
	{
		try
		{
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource dataSource = (DataSource) envContext.lookup("jdbc/dixie_sproc");
			bind(DataSource.class).toInstance(dataSource);
		}
		catch (NamingException e)
		{
			logger.fatal(e);
		}

		bind(CategoryDao.class).to(CategoryDbDao.class);
		bind(CaptchaDao.class).to(CaptchaDbDao.class);
		bind(FormatDao.class).to(FormatDbDao.class);
		bind(CommentDao.class).to(CommentDbDao.class);
		bind(UserDao.class).to(UserDbDao.class);
		bind(LinkDao.class).to(LinkDbDao.class);
		bind(TagDao.class).to(TagDbDao.class);

		String subDir = File.separator + "thumbs" + File.separator + "link";
		String baseDir = config.getServletContext().getRealPath("") + subDir;
		String contextPath = config.getServletContext().getContextPath();
		LinkThumbsUtil linkThumbsUtil = new LocalLinkThumbsUtil(baseDir, contextPath + subDir);
		bind(LinkThumbsUtil.class).toInstance(linkThumbsUtil);
	}
}
