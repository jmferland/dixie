package dixie.web.view;

import dixie.web.view.freemarker.PageListMethod;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class StripesFreemarkerServlet extends FreemarkerServlet
{
	private static final Logger logger = Logger.getLogger(StripesFreemarkerServlet.class.getName());

	/**
	 * This method exposes fields so that you have the option to use public properties in your
	 * action beans instead of private properties with getters and setters, if you wish.
	 */
	@Override
	protected ObjectWrapper createObjectWrapper()
	{
		ObjectWrapper result = super.createObjectWrapper();

		if (result instanceof BeansWrapper)
		{
			BeansWrapper beansWrapper = (BeansWrapper) result;
			beansWrapper.setExposeFields(true);
		}
		return result;
	}

	/**
	 * This method puts the context path under the key "contextPath" so that you can use
	 * ${contextPath} in your templates.
	 */
	@Override
	protected boolean preTemplateProcess(HttpServletRequest req, HttpServletResponse resp,
										 Template template, TemplateModel data)
			throws ServletException, IOException
	{
		SimpleHash hash = (SimpleHash) data;

		hash.put("contextPath", req.getContextPath());
		hash.put("requestURI", getRequestURI(req));
		hash.put("pageList", new PageListMethod());

		// Beautiful way to expose Enum-s. Can now access like:
		//   ${enums["java.math.RoundingMode"].UP}
		hash.put("enums", BeansWrapper.getDefaultInstance().getEnumModels());

		// TODO: consider switching this to the way Enum-s work (above).
		BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
		TemplateHashModel staticModels = wrapper.getStaticModels();
		try
		{
			TemplateHashModel calendarStatics = (TemplateHashModel) staticModels.get("dixie.util.CalendarUtil");
			hash.put("Calendar", calendarStatics);
		}
		catch (TemplateModelException e)
		{
			logger.fatal("Error mapping static functions: " + e);
		}

		return true;
	}

	protected String getRequestURI(HttpServletRequest req)
	{
		StringBuilder sb = new StringBuilder();

		// Start with the URI and the path.
		String uri = (String) req.getAttribute("javax.servlet.forward.request_uri");
		String path = (String) req.getAttribute("javax.servlet.forward.path_info");

		if (uri == null)
		{
			uri = req.getRequestURI();
			path = req.getPathInfo();
		}

		if (uri != null)
		{
			// Remove context path from uri
			if (uri.startsWith(req.getContextPath()))
			{
				uri = uri.substring(req.getContextPath().length());
			}

			sb.append(uri);

			if (path != null)
			{
				sb.append(path);
			}

			// Now the request parameters, if it was a GET request.
			if (req.getMethod().equalsIgnoreCase("GET"))
			{
				// TODO: can we remove request parameters that are already
				// embedded in the URI?

				sb.append('?');
				Map<String, String[]> map = new HashMap<String, String[]>(req.getParameterMap());

				// Append the parameters to the URL.
				for (String key : map.keySet())
				{
					String[] values = map.get(key);
					for (String value : values)
					{
						sb.append(key).append('=').append(value).append('&');
					}
				}
				// Remove the last ampersand (or the question mark).
				sb.deleteCharAt(sb.length() - 1);
			}
		}

		return sb.toString();
	}
}