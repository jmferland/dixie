package dixie.web.action;

import dixie.dao.DaoManager;
import dixie.model.User;
import dixie.web.ext.CustomActionBeanContext;
import java.util.Map;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.StrictBinding;

@StrictBinding
public class BaseActionBean implements ActionBean
{
	private CustomActionBeanContext context;

	public DaoManager getDaoManager()
	{
		return this.getContext().getDaoManager();
	}

	@Override
	public CustomActionBeanContext getContext()
	{
		return context;
	}

	@Override
	public void setContext(ActionBeanContext context)
	{
		this.context = (CustomActionBeanContext) context;
	}

	/**
	 * This is not guaranteed to be correct and could easily be spoofed.
	 * 
	 * @return true if the request is an AJAX request, otherwise false.
	 */
	public boolean isAjaxRequest()
	{
		return isAjaxRequest(this);
	}

	/**
	 * This is not guaranteed to be correct and could easily be spoofed.
	 *
	 * @param bean the bean instance to check.
	 * @return true if the request is an AJAX request, otherwise false.
	 */
	public static boolean isAjaxRequest(ActionBean bean)
	{
		String header = bean.getContext().getRequest().getHeader("X-Requested-With");
		return header != null && header.equalsIgnoreCase("XMLHttpRequest");
	}

	public User getLoggedInUser()
	{
		return this.getContext().getUser();
	}

	public String getBeanclass()
	{
		return this.getClass().getName();
	}

	public Map getParams()
	{
		return this.getContext().getRequest().getParameterMap();
	}

	public boolean getHasErrors()
	{
		return this.getContext().getValidationErrors().size() > 0;
	}
}