package dixie.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author jferland
 */
public class Digest
{
	private static final long serialVersionUID = -7776863251954299926L;
	private static final String ALGORITHM_SHA1 = "SHA-1";
	private static final String ALGORITHM_MD5 = "MD5";
	public static final int ALGORITHM_SHA1_HASH_LENGTH = 20;
	// Specify charset name so the string to byte conversion is consistent
	// across all platforms.  This ensures the hash generated is consistent
	// across all platforms (that support the charset).
	private static final String CHARSET_NAME = "UTF-8";

	/**
	 * Get the SHA-1 digest of the given message.
	 *
	 * @param message what to digest/ hash.
	 * @return a 20 byte hash.
	 */
	public static byte[] sha1(String message) throws UnsupportedEncodingException, NoSuchAlgorithmException
	{
		MessageDigest messageDigest = MessageDigest.getInstance(Digest.ALGORITHM_SHA1);
		return messageDigest.digest(message.getBytes(Digest.CHARSET_NAME));
	}

	/**
	 * Get the MD5 digest of the given message.
	 *
	 * @param message what to digest/ hash.
	 * @return a 16 byte hash.
	 */
	public static byte[] md5(String message) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		MessageDigest md = MessageDigest.getInstance(Digest.ALGORITHM_MD5);
		return md.digest(message.getBytes(Digest.CHARSET_NAME));
	}

	/**
	 * Turn a byte array into a hex string.
	 * 
	 * @param array the array of bytes to transform into a hex string.
	 * @return a hex string.
	 */
	public static String hex(byte[] array)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i)
		{
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString();
	}
}
