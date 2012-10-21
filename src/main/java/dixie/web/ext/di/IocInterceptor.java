package dixie.web.ext.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import java.util.Locale;
import java.util.TimeZone;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.config.ConfigurableComponent;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.controller.ExecutionContext;
import net.sourceforge.stripes.controller.Interceptor;
import net.sourceforge.stripes.controller.Intercepts;
import net.sourceforge.stripes.controller.LifecycleStage;
import org.apache.log4j.Logger;

/**
 * Dependency injection site. Stripes only creates one instance of any
 * injector.
 *
 * @author jferland
 */
@Intercepts(LifecycleStage.ActionBeanResolution)
public class IocInterceptor implements Interceptor, ConfigurableComponent
{
	private static final Logger logger = Logger.getLogger(IocInterceptor.class);
	private static Injector injector;

	@Override
	public void init(Configuration config) throws Exception
	{
		// Set default TimeZone and Locale.
		TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
		Locale.setDefault(Locale.US);

		// TODO: provide an appropriate stage depending on debug vs release.
		injector = Guice.createInjector(Stage.DEVELOPMENT, new ReleaseGuiceConfigModule(config));
	}

	@Override
	public Resolution intercept(ExecutionContext context) throws Exception
	{
		injector.injectMembers(context.getActionBeanContext());
		Resolution resolution = context.proceed();
		injector.injectMembers(context.getActionBean());

		return resolution;
	}

	public static Injector getInjector()
	{
		return injector;
	}
}

