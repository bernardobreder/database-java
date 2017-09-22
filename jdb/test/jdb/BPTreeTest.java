package jdb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class BPTreeTest extends StorageBPTree {

	private static final int MAX = 1024;

	private static final Random RND = new Random(1);

	@Test
	public void testInsert() {
		List<Integer> list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list);
		StorageBPTree tree = new StorageBPTree();
		for (int n = 0; n < list.size(); n++) {
			{
				int key = list.get(n);
				tree.add(key, key);
			}
			for (int m = 0; m <= n; m++) {
				int key = list.get(m);
				Assert.assertEquals(key, tree.find(key));
			}
		}
		Collections.shuffle(list);
		for (int n = 0; n < list.size(); n++) {
			int key = list.get(n);
			Assert.assertEquals(key, tree.find(key));
		}
	}

	@Test
	public void testNext() {
		List<Integer> list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		BPTreeTest tree = new BPTreeTest();
		for (int n = 0; n < list.size(); n++) {
			{
				int key = list.get(n);
				tree.add(key, key);
			}
			{
				BPTreeNode leaf = tree.root;
				while (!leaf.leaf) {
					leaf = (BPTreeNode) leaf.nodes[0];
				}
				for (int k = 0, m = 0; k <= n; k++, m++) {
					if (m == leaf.length) {
						Assert.assertNotNull(leaf.nodes[tree.order - 1]);
						leaf = (BPTreeNode) leaf.nodes[tree.order - 1];
						m = 0;
					}
					Assert.assertEquals(k, leaf.nodes[m]);
				}
			}
		}
	}

	@Test
	public void testNextRandom() {
		List<Integer> list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list);
		StorageBPTree tree = new StorageBPTree();
		for (int n = 0; n < list.size(); n++) {
			tree.add(list.get(n), list.get(n));
		}
		BPTreeNode leaf = tree.root;
		while (!leaf.leaf) {
			leaf = (BPTreeNode) leaf.nodes[0];
		}
		for (int k = 0, m = 0; k < list.size(); k++, m++) {
			if (m == leaf.length) {
				Assert.assertNotNull(leaf.nodes[tree.order - 1]);
				leaf = (BPTreeNode) leaf.nodes[tree.order - 1];
				m = 0;
			}
			Assert.assertEquals(k, leaf.nodes[m]);
		}
	}

	@Test
	public void testNextRandomIterator() {
		List<Integer> list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list, RND);
		for (int i = 1; i <= list.size(); i++) {
			List<Integer> expectedList = new ArrayList<Integer>();
			StorageBPTree tree = new StorageBPTree();
			for (int n = 0; n < i; n++) {
				Integer value = list.get(n);
				int key = value.intValue();
				expectedList.add(value);
				tree.add(key, value);
			}
			Collections.sort(expectedList);
			BPTreeNode leaf = tree.root;
			while (!leaf.leaf) {
				leaf = (BPTreeNode) leaf.nodes[0];
			}
			for (int k = 0, m = 0; k < i; k++, m++) {
				if (m == leaf.length) {
					Assert.assertNotNull(leaf.nodes[tree.order - 1]);
					leaf = (BPTreeNode) leaf.nodes[tree.order - 1];
					m = 0;
				}
				Assert.assertEquals(expectedList.get(k), leaf.nodes[m]);
			}
		}
	}

	@Test
	public void testRemove() {
		List<Integer> list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list);
		StorageBPTree tree = new StorageBPTree();
		for (int n = 0; n < list.size(); n++) {
			tree.add(list.get(n), list.get(n));
		}
		Collections.shuffle(list);
		for (int n = 0; n < list.size(); n++) {
			{
				int key = list.get(n);
				Assert.assertEquals(key, tree.find(key));
				tree.delete(key);
				Assert.assertNull(tree.find(key));
			}
			for (int m = n + 1; m < list.size(); m++) {
				int key = list.get(m);
				Assert.assertEquals(key, tree.find(key));
			}
		}
	}

}
