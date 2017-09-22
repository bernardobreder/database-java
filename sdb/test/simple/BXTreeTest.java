package simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import simple.BXTree.DbTreeNode;

/**
 * Testador da classe
 * 
 * @author Tecgraf/PUC-Rio
 */
public class BXTreeTest implements BXTree.DbTreeIODelegator {

	/** Numero maximo de elementos */
	private static final int ELEM_MAX = 1024;
	/** Numero maximo de slot */
	private static final int SLOT_MAX = 128;

	/**
	 * @throws IOException
	 */
	@Test
	public void testAdd() throws IOException {
		List<Long> list = new ArrayList<Long>();
		for (long n = 1; n <= ELEM_MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list, new Random(0));
		for (int n = 3; n <= SLOT_MAX; n += 2) {
			BXTree<Long, Long> tree = new BXTree<Long, Long>(this, "test", n);
			for (int m = 1; m <= ELEM_MAX; m++) {
				Long value = list.get(m - 1);
				tree.add(value, value);
				List<DbTreeNode<Long, Long>> nodes = new ArrayList<DbTreeNode<Long, Long>>();
				nodes.add(tree.root);
				for (int x = 0; x < nodes.size(); x++) {
					DbTreeNode<Long, Long> node = nodes.get(x);
					for (int y = 0; y < node.childrenNode.length; y++) {
						if (node.childrenNode[y] != null) {
							nodes.add(node.childrenNode[y]);
						}
					}
					Assert.assertTrue(node.length <= n);
					for (int p = node.length; p < node.keys.length; p++) {
						Assert.assertNull(node.keys[p]);
						Assert.assertNull(node.values[p]);
					}
					for (int p = node.length + 1; p < node.childrenNode.length; p++) {
						Assert.assertEquals(0, node.childrenId[p]);
						Assert.assertNull(node.childrenNode[p]);
					}
				}
			}
			for (int m = 1; m <= ELEM_MAX; m++) {
				Long value = list.get(m - 1);
				long key = value.longValue();
				Assert.assertEquals(value, tree.get(key));
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testRemove() throws IOException {
		List<Long> list = new ArrayList<Long>();
		for (long n = 1; n <= ELEM_MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list, new Random(0));
		for (int n = 3; n <= SLOT_MAX; n += 2) {
			BXTree<Long, Long> tree = new BXTree<Long, Long>(this, "test", n);
			for (int m = 1; m <= ELEM_MAX; m++) {
				Long value = list.get(m - 1);
				tree.add(value, value);
			}
			for (int m = 1; m <= ELEM_MAX; m++) {
				Long key = list.get(m - 1);
				Assert.assertTrue(tree.remove(key));
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testAdd1234Remove() throws IOException {
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 4; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(1));
			Assert.assertEquals("[2, 3, 4]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 4; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(2));
			Assert.assertEquals("[1, 3, 4]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 4; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(3));
			Assert.assertEquals("[1, 2, 4]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 4; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(4));
			Assert.assertEquals("[1, 2, 3]", tree.toString());
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testAddAscManyRemove() throws IOException {
		int max = 1024;
		{
			Set<Integer> set = new TreeSet<Integer>();
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 5);
			for (Integer n = 1; n <= max; n++) {
				tree.add(n, n);
				set.add(n);
			}
			for (Integer n = 1; n <= max; n++) {
				Assert.assertTrue(tree.remove(n));
				set.remove(n);
				Assert.assertEquals(set.toString(), tree.toString());
			}
		}
		for (int s = 3; s <= SLOT_MAX; s += 2) {
			Set<Integer> set = new TreeSet<Integer>();
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", s);
			for (Integer n = 1; n <= ELEM_MAX; n++) {
				tree.add(n, n);
				set.add(n);
			}
			for (Integer n = 1; n <= ELEM_MAX; n++) {
				Assert.assertTrue(tree.remove(n));
				set.remove(n);
				Assert.assertEquals(set.toString(), tree.toString());
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testAddDescManyRemove() throws IOException {
		{
			int max = 4;
			Set<Integer> set = new TreeSet<Integer>();
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = max; n >= 1; n--) {
				tree.add(n, n);
				set.add(n);
			}
			for (Integer n = max; n >= 1; n--) {
				Assert.assertTrue(tree.remove(n));
				set.remove(n);
				Assert.assertEquals(set.toString(), tree.toString());
			}
		}
		for (int s = 3; s <= SLOT_MAX; s += 2) {
			Set<Integer> set = new TreeSet<Integer>();
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = ELEM_MAX; n >= 1; n--) {
				tree.add(n, n);
				set.add(n);
			}
			for (Integer n = ELEM_MAX; n >= 1; n--) {
				Assert.assertTrue(tree.remove(n));
				set.remove(n);
				Assert.assertEquals(set.toString(), tree.toString());
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testAdd1234567Remove() throws IOException {
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(1));
			Assert.assertEquals("[2, 3, 4, 5, 6, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(2));
			Assert.assertEquals("[1, 3, 4, 5, 6, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(3));
			Assert.assertEquals("[1, 2, 4, 5, 6, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(4));
			Assert.assertEquals("[1, 2, 3, 5, 6, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(5));
			Assert.assertEquals("[1, 2, 3, 4, 6, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(6));
			Assert.assertEquals("[1, 2, 3, 4, 5, 7]", tree.toString());
		}
		{
			BXTree<Integer, Integer> tree = new BXTree<Integer, Integer>(this,
					"test", 3);
			for (Integer n = 1; n <= 7; n++) {
				tree.add(n, n);
			}
			Assert.assertTrue(tree.remove(7));
			Assert.assertEquals("[1, 2, 3, 4, 5, 6]", tree.toString());
		}
	}

	/** Bytes */
	public static final Map<String, byte[]> io = new HashMap<String, byte[]>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readStructure(String name) throws IOException {
		if (io.get(name) == null) {
			throw new FileNotFoundException(name);
		}
		return io.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeStructure(String name, byte[] bytes) throws IOException {
		io.put(name, bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readNode(String name, long id) throws IOException {
		String path = name + "_" + id;
		if (io.get(path) == null) {
			throw new FileNotFoundException(path);
		}
		return io.get(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeNode(String name, long id, byte[] bytes)
			throws IOException {
		String path = name + "_" + id;
		io.put(path, bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasStructure(String name) throws IOException {
		return io.containsKey(name);
	}

}
