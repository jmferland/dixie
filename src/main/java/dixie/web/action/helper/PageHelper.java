package dixie.web.action.helper;

import dixie.dao.DaoPage;
import dixie.lang.EnumOption;
import dixie.model.Settings;
import dixie.web.action.BaseActionBean;

/**
 * 
 * @author jferland
 */
public class PageHelper<ORDER extends EnumOption>
{
	private int index = 1;
	private int max = 1;
	private final BaseActionBean actionBean;
	private final int defaultSize;
	private final ORDER defaultOrder;
	private Settings.Key sizeSettingKey = Settings.Key.DEFAULT_PAGE_SIZE;
	private Settings.Key orderSettingKey = Settings.Key.DEFAULT_ORDER;

	public PageHelper(BaseActionBean actionBean, int defaultSize, ORDER defaultOrder)
	{
		this.actionBean = actionBean;
		this.defaultSize = defaultSize;
		this.defaultOrder = defaultOrder;
	}

	/**
	 * If the page size should be saved, which key to save it under.
	 * 
	 * @param saveSizeUnder
	 * @return this Object, for chaining.
	 */
	public PageHelper saveSizeUnder(Settings.Key countSettingKey)
	{
		this.sizeSettingKey = countSettingKey;
		return this;
	}

	/**
	 * If the order should be saved, which key to save it under.
	 * 
	 * @param saveOrderUnder
	 * @return this Object, for chaining.
	 */
	public PageHelper saveOrderUnder(Settings.Key orderSettingKey)
	{
		this.orderSettingKey = orderSettingKey;
		return this;
	}

	public void setIndex(int page)
	{
		this.index = Math.max(page, 1);
	}

	public int getIndex()
	{
		return index;
	}

	public void setSize(int count)
	{
		count = Math.max(count, 1);
		count = Math.min(count, 200);

		actionBean.getContext().setSetting(sizeSettingKey, count);
	}

	public int getSize()
	{
		return actionBean.getContext().getSetting(sizeSettingKey, defaultSize);
	}

	public void setOrder(ORDER order)
	{
		actionBean.getContext().setSetting(orderSettingKey, order);
	}

	public ORDER getOrder()
	{
		return actionBean.getContext().getSetting(orderSettingKey, defaultOrder);
	}

	public int getOffset()
	{
		return (index - 1) * getSize();
	}

	/**
	 * This should be called so results can be properly paged. Sets the maximum
	 * number of pages according to/ calculated from the DaoPage given.
	 *
	 * @param daoPage the DaoPage which contains the total number of results.
	 */
	public void setMax(DaoPage daoPage)
	{
		int total = daoPage.getTotal();
		if (total == 0)
		{
			this.max = 1;
		}
		else
		{
			this.max = (total + getSize() - 1) / getSize();
		}
	}

	public int getMax()
	{
		return max;
	}
}
