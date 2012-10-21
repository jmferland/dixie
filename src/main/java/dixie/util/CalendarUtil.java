package dixie.util;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author jferland
 */
public class CalendarUtil
{
	public static List<String> months()
	{
		// 13 months are returned by default for cultures that support a 13
		// month lunar calendar, so we'll just limit it to 12.
		return Arrays.asList(Arrays.copyOf(DateFormatSymbols.getInstance().getMonths(), 12));
	}

	public static List<String> days()
	{
		String[] days = new String[31];
		int i = 0;
		while (i < days.length)
		{
			days[i] = new Integer(++i).toString();
		}
		return Arrays.asList(days);
	}

	public static List<String> years()
	{
		String[] years = new String[110];
		int i = 0;
		int yearNow = Calendar.getInstance().get(Calendar.YEAR);
		while (i < years.length)
		{
			years[i] = new Integer(yearNow - i++).toString();
		}
		return Arrays.asList(years);
	}
}
