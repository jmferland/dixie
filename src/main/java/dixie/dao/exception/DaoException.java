package dixie.dao.exception;

import java.sql.SQLException;

/**
 * A class to generalize DAO exceptions.
 * 
 * @author jferland
 */
public class DaoException extends Exception
{
	private DaoError problem;
	// Error codes are currently MySQL specific.  This could get replaced by
	// an interface and DI, but we'll do this if we ever switch to a different
	// database vendor.
	private static final int ERROR_CODE_DUPLICATE_ENTRY = 1062;
	private static final int ERROR_CODE_STORED_PROCEDURE_NOT_FOUND = 1305;

	public DaoException(String message)
	{
		super(message);
	}

	public DaoException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DaoException(Throwable cause)
	{
		super(cause);
	}

	public DaoException(SQLException e)
	{
	}

	public DaoError getDaoError()
	{
		if (this.problem == null)
		{
			// TODO: this seems sooooo ugly.
			Throwable cause = this.getCause();

			if (cause instanceof SQLException)
			{
				switch (((SQLException) cause).getErrorCode())
				{
					case DaoException.ERROR_CODE_DUPLICATE_ENTRY:
						this.problem = DaoError.DUPLICATE_ENTRY;
						break;
					case DaoException.ERROR_CODE_STORED_PROCEDURE_NOT_FOUND:
						this.problem = DaoError.STORED_PROCEDURE_NOT_FOUND;
						break;
					default:
						this.problem = DaoError.UNKNOWN;
						break;
				}
			}
		}

		return this.problem;
	}

	public enum DaoError
	{
		UNKNOWN,
		DUPLICATE_ENTRY,
		STORED_PROCEDURE_NOT_FOUND
	}
}
