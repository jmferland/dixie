package dixie.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author jferland
 */
public final class Captcha extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	private static final int DEFAULT_ANSWER_LENGTH = 5;
	public static final int DEFAULT_LIFETIME = 120000; // In milliseconds
	public static final char[] DEFAULT_ANSWER_CHAR_SET = "abcde2345678gfynmnpwx".toCharArray();
	private final UUID uuid;
	private final String answer;
	private final long seed;
	private final long createdOn;

	/**
	 * Probably most useful when reading from a database.
	 * 
	 * @param uuid universally unique identifier.
	 * @param answer captcha answer (given by user?).
	 * @param seed the seed used for (reproducible) "Random" number generation.
	 */
	public Captcha(UUID uuid, String answer, long seed, long createdOn)
	{
		super(BaseModel.NULL_ID); // Id is not used at all.
		this.uuid = uuid;
		this.answer = answer;
		this.seed = seed;
		this.createdOn = createdOn;
	}

	/**
	 * Generate a random Captcha and return it. Note that it is not
	 * automatically put in any persistant store.
	 *
	 * @return randomly generated Captcha.
	 */
	public static Captcha randomCaptcha()
	{
		return new Captcha(UUID.randomUUID(),
						   Captcha.randomAnswer(Captcha.DEFAULT_ANSWER_LENGTH),
						   new Random().nextLong(),
						   new Date().getTime());
	}

	/**
	 * Generate a random answer using the default answer character set.
	 * 
	 * @param length the number of characters in the answer.
	 * @return a random answer.
	 */
	public static String randomAnswer(int length)
	{
		return Captcha.randomAnswer(Captcha.DEFAULT_ANSWER_CHAR_SET, length);
	}

	/**
	 * Generate a random answer using the specified answer character set.
	 *
	 * @param charSet an array of legal characters to chose from.
	 * @param length the number of characters in the answer.
	 * @return a random answer.
	 */
	public static String randomAnswer(char[] charSet, int length)
	{
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; i++)
		{
			buffer.append(charSet[random.nextInt(charSet.length)]);
		}

		return buffer.toString();
	}

	public String getAnswer()
	{
		return answer;
	}

	public Date getCreatedOn()
	{
		return new Date(createdOn);
	}

	/**
	 * This Object does not support the use of regular Ids, just UUIDs.
	 * 
	 * @return exception.
	 */
	@Override
	public long getId()
	{
		throw new UnsupportedOperationException();
	}

	public long getSeed()
	{
		return seed;
	}

	public UUID getUuid()
	{
		return uuid;
	}

	public String getUuidString()
	{
		return uuid.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		if (super.equals(obj) == false)
		{
			return false;
		}
		final Captcha other = (Captcha) obj;
		if (this.uuid != other.uuid && (this.uuid == null || !this.uuid.equals(other.uuid)))
		{
			return false;
		}
		if ((this.answer == null) ? (other.answer != null) : !this.answer.equals(other.answer))
		{
			return false;
		}
		if (this.seed != other.seed)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 37 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
		hash = 37 * hash + (this.answer != null ? this.answer.hashCode() : 0);
		hash = 37 * hash + (int) (this.seed ^ (this.seed >>> 32));
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", this.uuid.toString());
		map.put("answer", this.answer);
		map.put("seed", this.seed);
		map.put("createdOn", new Date(this.createdOn));
		return map.toString();
	}
}
