package dixie.web.ext;

import net.sourceforge.stripes.controller.NameBasedActionResolver;

public class CustomActionResolver extends NameBasedActionResolver
{
	@Override
	protected String getBindingSuffix()
	{
		return "";
	}

	@Override
	protected String getUrlBinding(String actionBeanName)
	{
		String result = super.getUrlBinding(actionBeanName);
		result = convertToLowerCaseWithUnderscores(result);
		return result;
	}

	private String convertToLowerCaseWithUnderscores(String string)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0, t = string.length(); i < t; i++)
		{
			char ch = string.charAt(i);
			if (Character.isUpperCase(ch))
			{
				ch = Character.toLowerCase(ch);
				if (i > 1)
				{
					builder.append('_');
				}
			}
			builder.append(ch);
		}
		return builder.toString();
	}
}