package dixie.web.nonext.converter;

import com.google.inject.Inject;
import dixie.dao.DaoCommand;
import dixie.dao.DaoManager;
import dixie.dao.exception.DaoException;
import dixie.lang.TagList;
import dixie.model.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import net.sourceforge.stripes.validation.ScopedLocalizableError;
import net.sourceforge.stripes.validation.TypeConverter;
import net.sourceforge.stripes.validation.ValidationError;
import org.apache.log4j.Logger;

/**
 * Convert a list of Tag names into a list of Tags. The Tags must be able
 * to be read from the DAO.
 *
 * @author jferland
 */
public class TagListFromDaoTypeConverter implements TypeConverter<TagList>
{
	private static final Logger logger = Logger.getLogger(TagListFromDaoTypeConverter.class);
	protected DaoManager daoManager;

	@Inject
	public void setDaoManager(DaoManager daoManager)
	{
		this.daoManager = daoManager;
	}

	@Override
	public TagList convert(String input, Class<? extends TagList> type, Collection<ValidationError> errors)
	{
		TagList tags = TagList.split(input);
		List<Tag> result = new ArrayList<Tag>();

		for (final Tag tag : tags.getList())
		{
			Tag foundTag = null;

			try
			{
				foundTag = (Tag) this.daoManager.transaction(new DaoCommand()
				{
					@Override
					public Object execute() throws DaoException
					{
						return daoManager.getTagDao().readByName(tag.getName());
					}
				});
			}
			catch (Exception e)
			{
				logger.fatal(e);
			}

			if (foundTag == null)
			{
				errors.add(new ScopedLocalizableError("converter.tag", "notFound", tag.getName()));
				continue;
			}

			if (result.contains(foundTag))
			{
				errors.add(new ScopedLocalizableError("converter.tag", "notUnique", tag.getName()));
				continue;
			}

			result.add(foundTag);
		}

		if (errors.size() > 0 ||
			result.size() == 0)
		{
			return null;
		}

		return new TagList(result);
	}

	@Override
	public void setLocale(Locale locale)
	{
	}
}