package dixie.dao.db.vocab;

/**
 *
 * @author jferland
 */
public class CommentVocab
{

	public static final String TABLE_NAME = "comment";
	public static final String ID = TABLE_NAME + ".id";
	public static final String PARENT_ID = TABLE_NAME + ".parent_id";
	public static final String TOP_ID = TABLE_NAME + ".top_id";
	public static final String USER_ID = TABLE_NAME + ".user_id";
	public static final String LINK_ID = TABLE_NAME + ".link_id";
	public static final String RATING = TABLE_NAME + "_rating";
	public static final String UPS = TABLE_NAME + "_ups";
	public static final String DOWNS = TABLE_NAME + "_downs";
	public static final String TEXT = TABLE_NAME + ".text";
	public static final String CREATED_ON = TABLE_NAME + ".created_on";
}
