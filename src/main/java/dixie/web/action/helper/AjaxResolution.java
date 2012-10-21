package dixie.web.action.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.ajax.JavaScriptResolution;
import net.sourceforge.stripes.validation.ValidationError;
import net.sourceforge.stripes.validation.ValidationErrors;

/**
 *
 * @author jferland
 */
public class AjaxResolution implements Resolution
{
	public static final String DATA_KEY = "data";
	public static final String ERRORS_KEY = "errors";
	public static final String GLOBAL_ERROR_KEY = "$global";
	private final ActionBean actionBean;
	private final Map<String, Object> rootObject;

	public AjaxResolution(ActionBean actionBean)
	{
		this.actionBean = actionBean;
		rootObject = new HashMap<String, Object>();

		addErrors();
	}

	/**
	 * Extract any errors we can from the ActionBean and put them in a map
	 * of field names that point to an array of corresponding errors.
	 */
	private void addErrors()
	{
		Locale locale = actionBean.getContext().getLocale();
		ValidationErrors validationErrors = actionBean.getContext().getValidationErrors();

		Map<String, List<String>> errors = new HashMap<String, List<String>>();

		for (List<ValidationError> validationErrorList : validationErrors.values())
		{
			if (validationErrorList.size() > 0)
			{
				String fieldName = validationErrorList.get(0).getFieldName();

				if (fieldName.equals(ValidationErrors.GLOBAL_ERROR))
				{
					fieldName = GLOBAL_ERROR_KEY;
				}

				List<String> fieldErrors = new ArrayList<String>();

				for (ValidationError validationError : validationErrorList)
				{
					fieldErrors.add(validationError.getMessage(locale));
				}

				errors.put(fieldName, fieldErrors);
			}
		}

		rootObject.put(ERRORS_KEY, errors);
	}

	public AjaxResolution setData(Object object)
	{
		rootObject.put(DATA_KEY, object);
		return this;
	}

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		new JavaScriptResolution(rootObject).execute(request, response);
	}
}
