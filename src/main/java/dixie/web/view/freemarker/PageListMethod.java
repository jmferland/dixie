package dixie.web.view.freemarker;

import freemarker.ext.beans.NumberModel;
import java.util.List;

import org.apache.log4j.Logger;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import java.util.ArrayList;

/**
 * A FreeMarker function for returning a list of pages that should be accessible from the current page.
 *
 * @author jferland
 */
public class PageListMethod implements TemplateMethodModelEx
{

	private static final Logger logger = Logger.getLogger(PageListMethod.class);

	/**
	 * @param currentPage The current page.
	 * @param lastPage The maximum page number allowed.
	 * @param numEndPages The number of pages to list at the extreme (minimum and maximum) ends (optional).
	 * @param numEitherSidePages The number of pages to list on either side of the current page (optional).
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object exec(List args) throws TemplateModelException
	{
		if (args.size() < 2 || args.size() > 4)
		{
			logger.error("Received wrong number of arguments.");
			throw new TemplateModelException("Wrong number of arguments. Expected args size of 2 to 4, but received " + args.size());
		}

		int currentPage = popInt(args, 1);
		int lastPage = popInt(args, 1);
		int numEndPages = popInt(args, 2);
		int numEitherSidePages = popInt(args, 4);

		// Ensure the input ranges are within range and consistent.  Note that we do not throw errors here since
		// these values could be passed directly from user input.
		lastPage = Math.max(1, lastPage);
		currentPage = Math.max(1, Math.min(currentPage, lastPage));
		numEndPages = Math.max(1, numEndPages);
		numEitherSidePages = Math.max(1, numEitherSidePages);

		List<Integer> pageList = new ArrayList<Integer>();

		// Left end
		for (int i = 1; i <= numEndPages; i++)
		{
			addIfLegal(i, pageList, lastPage);
		}

		// Middle
		for (int i = -numEitherSidePages; i <= numEitherSidePages; i++)
		{
			addIfLegal(currentPage + i, pageList, lastPage);
		}

		// Right end
		for (int i = 1; i <= numEndPages; i++)
		{
			addIfLegal(lastPage - numEndPages + i, pageList, lastPage);
		}

		return pageList;
	}

	private boolean addIfLegal(int page, List<Integer> pageList, int lastPage)
	{
		if (page > 0 && page <= lastPage &&
				(pageList == null || pageList.size() == 0 || page > pageList.get(pageList.size() - 1)))
		{
			pageList.add(page);
			return true;
		}

		return false;
	}

	private int popInt(List args, int defaultValue) throws TemplateModelException
	{
		int result = defaultValue;

		if (args.size() > 0)
		{
			Object arg = args.remove(0);

			if (arg != null && arg instanceof SimpleNumber)
			{
				result = ((SimpleNumber) arg).getAsNumber().intValue();
			}
			else if (arg != null && arg instanceof NumberModel)
			{
				result = ((NumberModel) arg).getAsNumber().intValue();
			}
			else
			{
				String error = "Argument \"" + arg + "\" is a \"" + arg.getClass() + "\", not a SimpleNumber or NumberModel";
				logger.error(error);
				throw new TemplateModelException(error);
			}
		}

		return result;
	}
}
