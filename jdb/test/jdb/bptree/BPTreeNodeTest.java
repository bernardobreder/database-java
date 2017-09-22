package jdb.bptree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BPTreeNodeTest {

	private static final int MAX =  1024;

	private static List<Integer> list;

	@BeforeClass
	public static void beforeClass() {
		list = new ArrayList<Integer>();
		for (int n = 0; n < MAX; n++) {
			list.add(n);
		}
		Collections.shuffle(list);
	}

	@Test
	public void testInsertSame() {
		BPTreeNode root = new BPTreeNode();
		root = root.add(5, 5);
		Assert.assertEquals(5, root.find(5));
		root = root.add(2, 2);
		Assert.assertEquals(5, root.find(5));
		Assert.assertEquals(2, root.find(2));
		root = root.add(8, 8);
		Assert.assertEquals(5, root.find(5));
		Assert.assertEquals(2, root.find(2));
		Assert.assertEquals(8, root.find(8));
		root = root.add(1, 1);
		Assert.assertEquals(5, root.find(5));
		Assert.assertEquals(2, root.find(2));
		Assert.assertEquals(8, root.find(8));
		Assert.assertEquals(1, root.find(1));
		root = root.add(9,9);
		Assert.assertEquals(5, root.find(5));
		Assert.assertEquals(2, root.find(2));
		Assert.assertEquals(8, root.find(8));
		Assert.assertEquals(1, root.find(1));
		Assert.assertEquals(9, root.find(9));
	}

	@Test
	public void testInsert() {
		BPTreeNode root = new BPTreeNode();
		for (int n = 0; n < list.size(); n++) {
			{
				Integer value = list.get(n);
				int key = value.intValue();
				root = root.add(key, value);
			}
			for (int m = 0; m <= n; m++) {
				int key = list.get(m);
				Assert.assertEquals(key, root.find(key));
			}
		}
		Collections.shuffle(list);
		for (int n = 0; n < list.size(); n++) {
			int key = list.get(n);
			Assert.assertEquals(key, root.find(key));
		}
	}

	@Test
	public void testNextRandom() {
		BPTreeNode root = new BPTreeNode();
		for (int n = 0; n < list.size(); n++) {
			Integer value = list.get(n);
			int key = value.intValue();
			root = root.add(key, value);
		}
		BPTreeNode leaf = root;
		while (!leaf.leaf) {
			leaf = (BPTreeNode) leaf.nodes[0];
		}
		for (int k = 0, m = 0; k < list.size(); k++, m++) {
			if (m == leaf.length) {
				Assert.assertNotNull(leaf.nodes[root.order - 1]);
				leaf = (BPTreeNode) leaf.nodes[root.order - 1];
				m = 0;
			}
			Assert.assertEquals(k, leaf.nodes[m]);
		}
	}

	@Test
	public void testNextRandomIterator() {
		for (int i = 1; i <= list.size(); i++) {
			List<Integer> expectedList = new ArrayList<Integer>();
			BPTreeNode root = new BPTreeNode();
			for (int n = 0; n < i; n++) {
				Integer value = list.get(n);
				int key = value.intValue();
				expectedList.add(value);
				root = root.add(key, value);
			}
			Collections.sort(expectedList);
			BPTreeNode leaf = root;
			while (!leaf.leaf) {
				leaf = (BPTreeNode) leaf.nodes[0];
			}
			for (int k = 0, m = 0; k < i; k++, m++) {
				if (m == leaf.length) {
					Assert.assertNotNull(leaf.nodes[root.order - 1]);
					leaf = (BPTreeNode) leaf.nodes[root.order - 1];
					m = 0;
				}
				Assert.assertEquals(expectedList.get(k), leaf.nodes[m]);
			}
		}
	}

	@Test
	public void testRemove() {
		BPTreeNode root = new BPTreeNode();
		for (int n = 0; n < list.size(); n++) {
			Integer value = list.get(n);
			int key = value.intValue();
			root = root.add(key, value);
		}
		Collections.shuffle(list);
		for (int n = 0; n < list.size(); n++) {
			{
				int key = list.get(n);
				Assert.assertEquals(key, root.find(key));
				root = root.delete(key);
				if(n==list.size()-1){
					Assert.assertNull(root);
				}else{
					Assert.assertNull(root.find(key));
				}
			}
			for (int m = n + 1; m < list.size(); m++) {
				int key = list.get(m);
				Assert.assertEquals(key, root.find(key));
			}
		}
	}

}
