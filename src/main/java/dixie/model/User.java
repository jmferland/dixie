package dixie.model;

import dixie.util.BCrypt;
import dixie.util.Digest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class User extends BaseModel
{
	private static final long serialVersionUID = -7776863251954299926L;
	public static final User DEFAULT_USER = new User.Builder().build();
	private final String username;
	private final String passwordHash;
	private final String email;
	private final long birthDate;
	private final String firstName;
	private final String lastName;
	private final int accountStatus;
	private final long createdOn;
	private String md5Email;

	private User(Builder builder)
	{
		super(builder.id);
		this.username = builder.username;
		this.passwordHash = builder.passwordHash;
		this.email = builder.email;
		this.birthDate = builder.birthDate;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.accountStatus = builder.accountStatus;
		this.createdOn = builder.createdOn;
	}

	/**
	 * Instead of having a constructor with a very large number of parameters
	 * have an intermediate builder class. This does result in a sick amount
	 * of code duplication but it lets us be more clear and flexible with
	 * how things are set and still allow an immutable model class.
	 *
	 * I chose to use setX() for member variables X instead of public access
	 * for E.L. purposes. Also, it allows for tricks like setting password
	 * and turning it into a hash internally.
	 * 
	 * @author jferland
	 */
	public static class Builder
	{
		public long id = 0;
		public String username = BaseModel.EMPTY_STRING;
		public String passwordHash = BaseModel.EMPTY_STRING;
		public String email = BaseModel.EMPTY_STRING;
		public long birthDate = 0;
		public String firstName = BaseModel.EMPTY_STRING;
		public String lastName = BaseModel.EMPTY_STRING;
		public int accountStatus = 0;
		public long createdOn = new Date().getTime();

		/**
		 * Get a password hash for the given password using BCrypt. Only the DAO
		 * should ever read/ write to passwordHash since it is a hash, not the
		 * password.
		 *
		 * @param password to set.
		 */
		public Builder setPassword(String password)
		{
			this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
			return this;
		}

		public User build()
		{
			return new User(this);
		}
	}

	public int getAccountStatus()
	{
		return accountStatus;
	}

	public Date getBirthDate()
	{
		return new Date(birthDate);
	}

	public Date getCreatedOn()
	{
		return new Date(createdOn);
	}

	public String getEmail()
	{
		return email;
	}

	public String getMd5email()
	{
		if (md5Email == null)
		{
			try
			{
				md5Email = ""; // If anything goes wrong don't keep trying.
				md5Email = Digest.hex(Digest.md5(email));
			}
			catch (NoSuchAlgorithmException e)
			{
			}
			catch (UnsupportedEncodingException e)
			{
			}
		}

		return md5Email;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}

	public String getUsername()
	{
		return username;
	}

	/**
	 * Checks if the given password is this User's password or not.
	 *
	 * @param password to check.
	 * @return true if password matches this User's password, otherwise false.
	 */
	public boolean checkPassword(String password)
	{
		return BCrypt.checkpw(password, this.passwordHash);
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
		final User other = (User) obj;
		if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username))
		{
			return false;
		}
		if ((this.passwordHash == null) ? (other.passwordHash != null) : !this.passwordHash.equals(other.passwordHash))
		{
			return false;
		}
		if ((this.email == null) ? (other.email != null) : !this.email.equals(other.email))
		{
			return false;
		}
		if (this.birthDate != other.birthDate)
		{
			return false;
		}
		if ((this.firstName == null) ? (other.firstName != null) : !this.firstName.equals(other.firstName))
		{
			return false;
		}
		if ((this.lastName == null) ? (other.lastName != null) : !this.lastName.equals(other.lastName))
		{
			return false;
		}
		if (this.accountStatus != other.accountStatus)
		{
			return false;
		}
		if (this.createdOn != other.createdOn)
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = super.hashCode();
		hash = 67 * hash + (this.username != null ? this.username.hashCode() : 0);
		hash = 67 * hash + (this.passwordHash != null ? this.passwordHash.hashCode() : 0);
		hash = 67 * hash + (this.email != null ? this.email.hashCode() : 0);
		hash = 67 * hash + (int) (this.birthDate ^ (this.birthDate >>> 32));
		hash = 67 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
		hash = 67 * hash + (this.lastName != null ? this.lastName.hashCode() : 0);
		hash = 67 * hash + (int) (this.accountStatus ^ (this.accountStatus >>> 32));
		hash = 67 * hash + (int) (this.createdOn ^ (this.createdOn >>> 32));
		return hash;
	}

	@Override
	public String toString()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", this.getId());
		map.put("username", this.username);
		map.put("password", this.passwordHash);
		map.put("email", this.email);
		map.put("birthDate", new Date(this.birthDate));
		map.put("firstName", this.firstName);
		map.put("lastName", this.lastName);
		map.put("accountStatus", this.accountStatus);
		map.put("createdOn", new Date(this.createdOn));
		return map.toString();
	}

	public Builder getBuilder()
	{
		Builder builder = new Builder();

		builder.id = this.getId();
		builder.accountStatus = this.accountStatus;
		builder.birthDate = this.birthDate;
		builder.createdOn = this.createdOn;
		builder.email = this.email;
		builder.firstName = this.firstName;
		builder.lastName = this.lastName;
		builder.passwordHash = this.passwordHash;
		builder.username = this.username;

		return builder;

	}

	/**
	 * Values for a bit vector representing account status (ex: new, email
	 * confirmed, etc.).
	 */
	public class AccountStatus
	{
		public static final int BRAND_NEW = (1 << 0);
		public static final int EMAIL_CONFIRMED = (1 << 1);
	}
}
