package dixie.dao;

import dixie.model.BaseModel;
import dixie.model.Comment;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collection of DAO utility/ helper classes.
 * 
 * @author jferland
 */
public class DaoUtil
{
	/**
	 * Build a hierarchical structure of Comments from a flat list. The order
	 * from the given list is maintained in the returned hierarchy.
	 *
	 * @param page a page's worth of Comments to build a tree from.
	 * @return a Comments tree
	 */
	public static List<Comment> buildCommentTree(List<Comment.Builder> comments)
	{
		List<Comment> result = new ArrayList<Comment>();
		List<TreeNode<Comment.Builder>> nodes = new ArrayList<TreeNode<Comment.Builder>>();
		List<TreeNode<Comment.Builder>> rootNodes = new ArrayList<TreeNode<Comment.Builder>>();
		Map<BigInteger, TreeNode<Comment.Builder>> map = new HashMap<BigInteger, TreeNode<Comment.Builder>>();

		// 1) Make a new list to iterate through that better facilitate a
		// tree structure.
		for (Comment.Builder comment : comments)
		{
			nodes.add(new TreeNode<Comment.Builder>(comment));
		}

		// 2) Make a map of all the comments since we cannot guarantee that
		// parents come before children. We want to maintain the order the
		// comments were given to us.
		for (TreeNode<Comment.Builder> node : nodes)
		{
			map.put(BigInteger.valueOf(node.object.id), node);
		}

		// 3) Make a second pass through the comments to build the tree.
		for (TreeNode<Comment.Builder> node : nodes)
		{
			// Need to explicitly make a BigInteger. Apparently something else
			// is made otherwise.
			BigInteger parentId = BigInteger.valueOf(node.object.parentId);
			if (node.object.parentId != BaseModel.NULL_ID &&
				map.containsKey(parentId))
			{
				TreeNode<Comment.Builder> parent = map.get(parentId);
				parent.children.add(node);
			}
			else
			{
				rootNodes.add(node);
			}
		}

		// 4) Build the tree of immutable Comment objects.
		result = DaoUtil.buildCommentChildren(rootNodes);

		return result;
	}

	/**
	 * Recursively build a tree of Comment's from a tree of Comment.Builder's.
	 * 
	 * @param nodes which Comment's to start with.
	 * @return a List of Comment's with children, with children, etc.
	 */
	private static List<Comment> buildCommentChildren(List<TreeNode<Comment.Builder>> nodes)
	{
		List<Comment> result = new ArrayList<Comment>();

		for (TreeNode<Comment.Builder> node : nodes)
		{
			node.object.replies.addAll(DaoUtil.buildCommentChildren(node.children));
			result.add(node.object.build());
		}

		return result;
	}

	/**
	 * Class to help build tree structures.
	 *
	 * @param <T> Object the tree is made up.
	 */
	private static class TreeNode<T>
	{
		public T object;
		public List<TreeNode<T>> children;

		public TreeNode(T object)
		{
			this.object = object;
			this.children = new ArrayList<TreeNode<T>>();
		}
	}
}
