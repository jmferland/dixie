package dixie.dao.db.vocab;

/**
 *
 * @author jferland
 */
public class LinkVocab
{
	public static final String TABLE_NAME = "link";
	public static final String ID = TABLE_NAME + ".id";
	public static final String USER_ID = TABLE_NAME + ".user_id";
	public static final String URL = TABLE_NAME + ".url";
	public static final String URL_HASH = TABLE_NAME + ".url_hash";
	public static final String SOURCE_THUMB_URL = TABLE_NAME + ".thumb_url";
	public static final String FORMAT_ID = TABLE_NAME + ".format_id";
	public static final String CATEGORY_ID = TABLE_NAME + ".category_id";
	public static final String TITLE = TABLE_NAME + ".title";
	public static final String NOTES = TABLE_NAME + ".notes";
	public static final String CREATED_ON = TABLE_NAME + ".created_on";
	public static final String TAGS = TABLE_NAME + "_tags";
	public static final String COMMENTS = TABLE_NAME + ".comments";
	public static final String VIEWS = TABLE_NAME + ".views";
	public static final String PROMOTIONS = TABLE_NAME + ".promotions";
	public static final String DEMOTIONS = TABLE_NAME + ".demotions";
}
