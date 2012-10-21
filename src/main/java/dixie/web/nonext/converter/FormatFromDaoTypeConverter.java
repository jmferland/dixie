package dixie.web.nonext.converter;

import com.google.inject.Inject;
import dixie.dao.DaoCommand;
import dixie.dao.DaoManager;
import dixie.dao.exception.DaoException;
import dixie.model.Format;
import java.util.Collection;
import java.util.Locale;

import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Convert a {@code Format} folder name into a {@code Format}. If the folder name
 * does not exist then default to {@code Format.ALL}
 *
 * @author jferland
 */
public class FormatFromDaoTypeConverter implements TypeConverter<Format>
{
	private static final Logger logger = Logger.getLogger(FormatFromDaoTypeConverter.class);
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@Override
	public Format convert(final String input, Class<? extends Format> type, Collection<ValidationError> errors)
	{
		Format format = null;

		try
		{
			format = (Format) this.daoManager.transaction(new DaoCommand()
			{
				@Override
				public Object execute() throws DaoException
				{
					return daoManager.getFormatDao().readByFolder(input);
				}
			});
		}
		catch (Exception e)
		{
			logger.fatal(e);
		}

		return format != null ? format : Format.ALL;
	}

	@Override
	public void setLocale(Locale locale)
	{
	}
}