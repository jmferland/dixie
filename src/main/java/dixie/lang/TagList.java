package dixie.lang;

import com.google.inject.internal.ImmutableList;
import dixie.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An immutable list of tags.
 * 
 * @author jferland
 */
public class TagList
{
	private static final Pattern SPLIT_PATTERN = Pattern.compile("[^\\w\\-]+");
	public static final TagList EMPTY = new TagList(new ArrayList());
	private List<Tag> list;

	public TagList(List<Tag> list)
	{
		this.list = ImmutableList.copyOf(list);
	}

	public List<Tag> getList()
	{
		return this.list;
	}

	/**
	 * Build and return a String version of a delimited list of the names
	 * of the Tags that make up this list.
	 *
	 * @param delimiter the delimiter string to join the names with.
	 * @return a delimited list of the tags in this list.
	 */
	public String joinNames(String delimiter)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.list.size(); i++)
		{
			stringBuilder.append(this.list.get(i).getName());

			if (i != this.list.size() - 1)
			{
				stringBuilder.append(delimiter);
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Build and return a String version of a delimited list of the IDs
	 * of the Tags that make up this list.
	 *
	 * @param delimiter the delimiter string to join the ids with.
	 * @return a delimited list of the tags in this list.
	 */
	public String joinIds(String delimiter)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < this.list.size(); i++)
		{
			stringBuilder.append(this.list.get(i).getId());

			if (i != this.list.size() - 1)
			{
				stringBuilder.append(delimiter);
			}
		}

		return stringBuilder.toString();
	}

	/**
	 * Convert a delimited list of Tags into a TagList.
	 * 
	 * @param tags delimited list of Tags.
	 * @return a TagList.
	 */
	public static TagList split(String tags)
	{
		List<Tag> list = new ArrayList<Tag>();

		if (tags != null)
		{
			String[] array = TagList.SPLIT_PATTERN.split(tags.trim());

			for (int i = 0; i < array.length; i++)
			{
				list.add(new Tag(array[i]));
			}
		}

		return new TagList(list);
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
		final TagList other = (TagList) obj;
		if (this.list != other.list && (this.list == null || !this.list.equals(other.list)))
		{
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 97 * hash + (this.list != null ? this.list.hashCode() : 0);
		return hash;
	}

	@Override
	public String toString()
	{
		return this.list.toString();
	}
}
