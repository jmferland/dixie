package dixie.dao.db.vocab;

/**
 *
 * @author jferland
 */
public class CaptchaVocab
{

	public static final String TABLE_NAME = "captcha";
	public static final String UUID_MOST_SIG = TABLE_NAME + ".uuid_most_sig";
	public static final String UUID_LEAST_SIG = TABLE_NAME + ".uuid_least_sig";
	public static final String ANSWER = TABLE_NAME + ".answer";
	public static final String SEED = TABLE_NAME + ".seed";
	public static final String CREATED_ON = TABLE_NAME + ".created_on";
}
