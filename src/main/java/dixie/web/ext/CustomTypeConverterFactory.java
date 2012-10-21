package dixie.web.ext;

import dixie.dao.order.CommentOrder;
import dixie.dao.order.LinkOrder;
import dixie.dao.order.TagOrder;
import dixie.lang.DemotionReason;
import dixie.lang.TagList;
import dixie.model.Category;
import dixie.model.Comment;
import dixie.model.Format;
import dixie.model.Link;
import dixie.model.Tag;
import dixie.model.User;
import dixie.web.nonext.converter.EnumOptionTypeConverterFormatter;
import dixie.web.nonext.converter.NotNullBaseModelIdTypeConverterFormatter;
import dixie.web.nonext.converter.TagListTypeConverterFormatter;
import dixie.web.nonext.converter.UserTypeConverterFormatter;
import dixie.web.ext.di.IocInterceptor;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.validation.DefaultTypeConverterFactory;
import net.sourceforge.stripes.validation.TypeConverter;
import org.apache.log4j.Logger;

/**
 * 
 * @author jferland
 */
public class CustomTypeConverterFactory extends DefaultTypeConverterFactory
{
	private static final Logger logger = Logger.getLogger(CustomTypeConverterFactory.class.getName());

	@Override
	public TypeConverter getInstance(Class clazz, Locale locale) throws Exception
	{
		TypeConverter typeConverter = super.getInstance(clazz, locale);

		IocInterceptor.getInjector().injectMembers(typeConverter);

		return typeConverter;
	}

	@Override
	public void init(Configuration configuration)
	{
		super.init(configuration);

		SortedMap<Class, Class> typeToConverter = new TreeMap<Class, Class>()
		{

			{
				add(Comment.class, NotNullBaseModelIdTypeConverterFormatter.class);
				add(Link.class, NotNullBaseModelIdTypeConverterFormatter.class);
				add(Tag.class, NotNullBaseModelIdTypeConverterFormatter.class);
				add(Format.class, NotNullBaseModelIdTypeConverterFormatter.class);
				add(Category.class, NotNullBaseModelIdTypeConverterFormatter.class);

				add(User.class, UserTypeConverterFormatter.class);

				add(TagList.class, TagListTypeConverterFormatter.class);

				add(DemotionReason.class, EnumOptionTypeConverterFormatter.class);
				add(CommentOrder.class, EnumOptionTypeConverterFormatter.class);
				add(LinkOrder.class, EnumOptionTypeConverterFormatter.class);
				add(TagOrder.class, EnumOptionTypeConverterFormatter.class);
			}
		};

		for (Class type : typeToConverter.keySet())
		{
			Class converter = typeToConverter.get(type);
			logger.debug("Binding Type " + converter.getSimpleName() +
						 " to TypeConverter " + type.getSimpleName());
			this.add(type, converter);
		}
	}
}