package dixie.model;

/**
 *
 * @author jferland
 */
public class Settings
{
	public static enum Key
	{
		DEFAULT_ORDER("default_order"),
		DEFAULT_PAGE_SIZE("default_page_size"),
		COMMENT_ORDER("comment_order"),
		COMMENT_PAGE_SIZE("comment_page_size"),
		LINK_ORDER("link_order"),
		LINK_PAGE_SIZE("link_page_size");
		private final String storageKey;

		Key(String storageKey)
		{
			this.storageKey = storageKey;
		}

		public String getSessionKey()
		{
			return Settings.class.getCanonicalName() + ":" + this.name();
		}
	}
}
