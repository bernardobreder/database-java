package sdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import sdb.BXTree.DbTreeNode;

public class BXTreeTest {

	@Test
	public void testMerge() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		Assert.assertEquals("{{1->2}2{2->3}3{3|4}}", tree.root.toString());
		new BXTree(3).removeMerge(tree.root, 0, tree.root.children[0], tree.root.children[1]);
		Assert.assertEquals("{{1|2->3}3{3|4}}", tree.root.toString());
	}

	@Test
	public void testMergeLeftEmpty() throws IOException {
		DbTreeNode root = new DbTreeNode(null, 0, 3);
		DbTreeNode left = new DbTreeNode(null, 0, 3);
		DbTreeNode right = new DbTreeNode(null, 0, 3);
		root.leaf = false;
		Object[] array = new Object[1];
		array[0] = 40;
		root.keys[0] = 40;
		root.children[0] = left;
		root.children[1] = right;
		root.length++;
		left.next = right;
		right.keys[0] = 40;
		right.length++;
		Assert.assertEquals("{{->40}40{40}}", root.toString());
		new BXTree(3).removeMerge(root, 0, left, right);
		Assert.assertEquals("{{40}}", root.toString());
	}

	@Test
	public void testMergeRightEmpty() throws IOException {
		DbTreeNode root = new DbTreeNode(null, 0, 3);
		DbTreeNode left = new DbTreeNode(null, 0, 3);
		DbTreeNode right = new DbTreeNode(null, 0, 3);
		root.leaf = false;
		root.keys[0] = 40;
		root.children[0] = left;
		root.children[1] = right;
		root.length++;
		left.next = right;
		left.keys[0] = 30;
		left.length++;
		Assert.assertEquals("{{30->}40{}}", root.toString());
		new BXTree(3).removeMerge(root, 0, left, right);
		Assert.assertEquals("{{30}}", root.toString());
	}

	@Test
	public void testAddSimple10_70() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(10, 10);
		Assert.assertEquals("{10}", tree.root.toString());
		tree.add(20, 20);
		Assert.assertEquals("{10|20}", tree.root.toString());
		tree.add(30, 30);
		Assert.assertEquals("{{10->20}20{20|30}}", tree.root.toString());
		tree.add(40, 40);
		Assert.assertEquals("{{10->20}20{20->30}30{30|40}}", tree.root.toString());
		tree.add(50, 50);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40|50}}}", tree.root.toString());
		tree.add(60, 60);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}50{50|60}}}", tree.root.toString());
		tree.add(70, 70);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
	}

	@Test
	public void testGetStress() throws IOException {
		BXTree tree = new BXTree(3);
		int max = 1024;
		for (Integer n = 1; n <= max; n++) {
			tree.add(n, n);
		}
		for (int n = 1; n <= max; n++) {
			Assert.assertEquals(n, tree.get(n));
		}
	}

	@Test
	public void testAddStress32() throws IOException {
		BXTree tree = new BXTree(7);
		for (Integer n = 1; n <= 32; n++) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{1|2|3->4}4{4|5|6->7}7{7|8|9->10}10{10|11|12->13}}" + "13" + "{{13|14|15->16}16{16|17|18->19}19{19|20|21->22}22{22|23|24->25}25{25|26|27->28}28{28|29|30|31|32}}}", tree.root.toString());
	}

	@Test
	@Ignore
	public void testAddStressMany() throws IOException {
		int max = 16 * 1024;
		for (int o = 3; o <= 101; o += 2) {
			BXTree tree = new BXTree(o);
			for (Integer n = 1; n <= max; n++) {
				tree.add(n, n);
			}
			for (int n = 1; n <= max; n++) {
				Assert.assertEquals(n, tree.get(n));
			}
			Set<Long> set = new TreeSet<Long>();
			for (Long n = 1l; n <= max; n++) {
				set.add(n);
			}
			DbTreeNode node = tree.get(tree.root, 1);
			while (node != null) {
				for (Integer n = 0; n < node.length; n++) {
					Assert.assertTrue(set.remove(node.keys[n]));
				}
				node = node.next;
			}
			Assert.assertTrue(set.isEmpty());
		}
	}

	@Test
	@Ignore
	public void testRandomAddStressMany() throws IOException {
		int max = 16 * 1024;
		Random random = new Random(1);
		List<Long> list = new ArrayList<Long>(max);
		for (Long n = 1l; n <= max; n++) {
			list.add(n);
		}
		Collections.shuffle(list, random);
		for (int o = 3; o <= 101; o += 2) {
			BXTree tree = new BXTree(o);
			for (Integer n = 1; n <= max; n++) {
				tree.add(n, n);
			}
			for (int n = 1; n <= max; n++) {
				Assert.assertEquals(n, tree.get(n));
			}
			Set<Long> set = new TreeSet<Long>(list);
			DbTreeNode node = tree.get(tree.root, 1);
			while (node != null) {
				for (Integer n = 0; n < node.length; n++) {
					Assert.assertTrue(set.remove(node.keys[n]));
				}
				node = node.next;
			}
			Assert.assertTrue(set.isEmpty());
		}
	}

	@Test
	@Ignore
	public void testRandomAddRemoveStressMany() throws IOException {
		int max = 1000;
		Random random = new Random(1);
		for (int m = 1; m <= max; m++) {
			List<Integer> list = new ArrayList<Integer>(m);
			for (Integer n = 1; n <= m; n++) {
				list.add(n);
			}
			Collections.shuffle(list, random);
			for (int o = 3; o <= 101; o += 2) {
				BXTree tree = new BXTree(o);
				for (Integer n = 1; n <= m; n++) {
					tree.add(n, n);
				}
				List<Integer> myList = new ArrayList<Integer>(list);
				Collections.shuffle(myList, random);
				for (int n = 0; n < myList.size(); n++) {
					Integer value = myList.get(myList.size() - 1);
					System.out.println("m:" + m + " o:" + o + " n:" + n + " v:" + value);
					tree.remove(value);
					myList.remove(myList.size() - 1);
					for (int i = 0; i < myList.size(); i++) {
						int item = myList.get(i);
						Assert.assertEquals(item, tree.get(item));
					}
				}
			}
		}
	}

	@Test
	public void testTree70Remove10() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(10);
		Assert.assertEquals("{{{20->30}30{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove20() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(20);
		Assert.assertEquals("{{{10->30}30{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove30() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(30);
		Assert.assertEquals("{{{10->20}20{20->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove40() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(40);
		Assert.assertEquals("{{{10->20}20{20->30}30{30->50}}50{{50->60}60{60|70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove50() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(50);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->60}}60{{60->70}70{70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove60() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(60);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->70}70{70}}}", tree.root.toString());
	}

	@Test
	public void testTree70Remove70() throws IOException {
		BXTree tree = new BXTree(3);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60|70}}}", tree.root.toString());
		tree.remove(70);
		Assert.assertEquals("{{{10->20}20{20->30}}30{{30->40}40{40->50}}50{{50->60}60{60}}}", tree.root.toString());
	}

	@Test
	public void test7Tree70Remove30() throws IOException {
		BXTree tree = new BXTree(7);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{10|20|30->40}40{40|50|60|70}}", tree.root.toString());
		tree.remove(30);
		Assert.assertEquals("{{10|20|40->50}50{50|60|70}}", tree.root.toString());
	}

	@Test
	public void test7Tree70Remove20() throws IOException {
		BXTree tree = new BXTree(7);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{10|20|30->40}40{40|50|60|70}}", tree.root.toString());
		tree.remove(20);
		Assert.assertEquals("{{10|30|40->50}50{50|60|70}}", tree.root.toString());
	}

	@Test
	public void test7Tree70Remove10() throws IOException {
		BXTree tree = new BXTree(7);
		for (Integer n = 10; n <= 70; n += 10) {
			tree.add(n, n);
		}
		Assert.assertEquals("{{10|20|30->40}40{40|50|60|70}}", tree.root.toString());
		tree.remove(10);
		Assert.assertEquals("{{20|30|40->50}50{50|60|70}}", tree.root.toString());
	}

	@Test
	public void test3Tree123Remove1() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		Assert.assertEquals("{{1->2}2{2|3}}", tree.root.toString());
		tree.remove(1);
		Assert.assertEquals("{{2->3}3{3}}", tree.root.toString());
	}

	@Test
	public void test3Tree123Remove2() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		Assert.assertEquals("{{1->2}2{2|3}}", tree.root.toString());
		tree.remove(2);
		Assert.assertEquals("{{1->3}3{3}}", tree.root.toString());
	}

	@Test
	public void test3Tree123Remove3() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		Assert.assertEquals("{{1->2}2{2|3}}", tree.root.toString());
		tree.remove(3);
		Assert.assertEquals("{{1->2}2{2}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234Remove1() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		Assert.assertEquals("{{1->2}2{2->3}3{3|4}}", tree.root.toString());
		tree.remove(1);
		Assert.assertEquals("{{2->3}3{3|4}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234Remove2() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		Assert.assertEquals("{{1->2}2{2->3}3{3|4}}", tree.root.toString());
		tree.remove(2);
		Assert.assertEquals("{{1->3}3{3->4}4{4}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234Remove3() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		Assert.assertEquals("{{1->2}2{2->3}3{3|4}}", tree.root.toString());
		tree.remove(3);
		Assert.assertEquals("{{1->2}2{2->4}4{4}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234Remove4() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		Assert.assertEquals("{{1->2}2{2->3}3{3|4}}", tree.root.toString());
		tree.remove(4);
		Assert.assertEquals("{{1->2}2{2->3}3{3}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234Remove42() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		tree.remove(4);
		Assert.assertEquals("{{1->2}2{2->3}3{3}}", tree.root.toString());
		tree.remove(2);
		Assert.assertEquals("{{1->3}3{3}}", tree.root.toString());
	}

	@Test
	public void test3Tree1234567Remove432() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		tree.add(5, 5);
		tree.add(6, 6);
		tree.add(7, 7);
		tree.remove(4);
		tree.remove(3);
		Assert.assertEquals("{{{1->2}2{2->5}}5{{5->6}6{6|7}}}", tree.root.toString());
		tree.remove(2);
		Assert.assertEquals("{{1->5}5{5->6}6{6|7}}", tree.root.toString());
	}

	@Test
	public void test3Tree123456789Remove175() throws IOException {
		BXTree tree = new BXTree(3);
		tree.add(1, 1);
		tree.add(2, 2);
		tree.add(3, 3);
		tree.add(4, 4);
		tree.add(5, 5);
		tree.add(6, 6);
		tree.add(7, 7);
		tree.add(8, 8);
		tree.add(9, 9);
		Assert.assertEquals("{{{{1->2}2{2->3}}3{{3->4}4{4->5}}}5{{{5->6}6{6->7}}7{{7->8}8{8|9}}}}", tree.root.toString());
		tree.remove(1);
		Assert.assertEquals("{{{2->3}3{3->4}4{4->5}}5{{5->6}6{6->7}}7{{7->8}8{8|9}}}", tree.root.toString());
		tree.remove(7);
		Assert.assertEquals("{{{2->3}3{3->4}4{4->5}}5{{5->6}6{6->8}}8{{8->9}9{9}}}", tree.root.toString());
		tree.remove(5);
		Assert.assertEquals("{{{2->3}3{3->4}}4{{4->6}6{6->8}}8{{8->9}9{9}}}", tree.root.toString());
	}

	@Test
	public void testAddRemoveStress() throws IOException {
		BXTree tree = new BXTree(3);
		// List<Integer> list = new ArrayList<Integer>();
		// for (Integer n = 1; n < 25; n++) {
		// list.add(n);
		// }
		// Collections.shuffle(list, new Random(1));
		// System.out.println(list);
		Assert.assertEquals("{}", tree.root.toString());
		tree.add(4, 0);
		Assert.assertEquals("{4}", tree.root.toString());
		tree.add(18, 0);
		Assert.assertEquals("{4|18}", tree.root.toString());
		tree.add(24, 0);
		Assert.assertEquals("{{4->18}18{18|24}}", tree.root.toString());
		tree.add(12, 0);
		Assert.assertEquals("{{4|12->18}18{18|24}}", tree.root.toString());
		tree.add(5, 0);
		Assert.assertEquals("{{4->5}5{5|12->18}18{18|24}}", tree.root.toString());
		tree.add(17, 0);
		Assert.assertEquals("{{{4->5}5{5->12}}12{{12|17->18}18{18|24}}}", tree.root.toString());
		tree.add(21, 0);
		Assert.assertEquals("{{{4->5}5{5->12}}12{{12|17->18}18{18->21}21{21|24}}}", tree.root.toString());
		tree.add(9, 0);
		Assert.assertEquals("{{{4->5}5{5|9->12}}12{{12|17->18}18{18->21}21{21|24}}}", tree.root.toString());
		tree.add(8, 0);
		Assert.assertEquals("{{{4->5}5{5->8}8{8|9->12}}12{{12|17->18}18{18->21}21{21|24}}}", tree.root.toString());
		tree.add(13, 0);
		Assert.assertEquals("{{{4->5}5{5->8}8{8|9->12}}12{{12->13}13{13|17->18}}18{{18->21}21{21|24}}}", tree.root.toString());
		tree.add(3, 0);
		Assert.assertEquals("{{{3|4->5}5{5->8}8{8|9->12}}12{{12->13}13{13|17->18}}18{{18->21}21{21|24}}}", tree.root.toString());
		tree.add(23, 0);
		Assert.assertEquals("{{{3|4->5}5{5->8}8{8|9->12}}12{{12->13}13{13|17->18}}18{{18->21}21{21->23}23{23|24}}}", tree.root.toString());
		tree.add(11, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13|17->18}}18{{18->21}21{21->23}23{23|24}}}}", tree.root.toString());
		tree.add(19, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13|17->18}}18{{18|19->21}21{21->23}23{23|24}}}}", tree.root.toString());
		tree.add(14, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}14{14|17->18}}18{{18|19->21}21{21->23}23{23|24}}}}", tree.root.toString());
		tree.add(16, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14->16}16{16|17->18}}18{{18|19->21}21{21->23}23{23|24}}}}", tree.root.toString());
		tree.add(7, 0);
		Assert.assertEquals("{{{{3|4->5}5{5|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14->16}16{16|17->18}}18{{18|19->21}21{21->23}23{23|24}}}}", tree.root.toString());
		tree.add(22, 0);
		Assert.assertEquals("{{{{3|4->5}5{5|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14->16}16{16|17->18}}18{{18|19->21}21{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(6, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->6}6{6|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14->16}16{16|17->18}}18{{18|19->21}21{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(15, 0);
		Assert.assertEquals("{{{{3|4->5}5{5->6}6{6|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14|15->16}16{16|17->18}}18{{18|19->21}21{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(1, 0);
		Assert.assertEquals("{{{{1->3}3{3|4->5}}5{{5->6}6{6|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14|15->16}16{16|17->18}}18{{18|19->21}21{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(20, 0);
		Assert.assertEquals("{{{{1->3}3{3|4->5}}5{{5->6}6{6|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14|15->16}16{16|17->18}}}18{{{18->19}19{19|20->21}}21{{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(2, 0);
		Assert.assertEquals("{{{{1|2->3}3{3|4->5}}5{{5->6}6{6|7->8}}8{{8->9}9{9|11->12}}}12{{{12->13}13{13->14}}14{{14|15->16}16{16|17->18}}}18{{{18->19}19{19|20->21}}21{{21|22->23}23{23|24}}}}", tree.root.toString());
		tree.add(10, 0);
		Assert.assertEquals("{{{{1|2->3}3{3|4->5}}5{{5->6}6{6|7->8}}8{{8->9}9{9->10}10{10|11->12}}}12{{{12->13}13{13->14}}14{{14|15->16}16{16|17->18}}}18{{{18->19}19{19|20->21}}21{{21|22->23}23{23|24}}}}", tree.root.toString());
	}

}
