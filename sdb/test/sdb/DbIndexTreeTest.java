package sdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import sdb.DbIndexTree.DbIndexTreeIODelegator;

public class DbIndexTreeTest implements DbIndexTreeIODelegator {

	@Test
	public void test() throws IOException {
		DbIndexTree tree = DbIndexTree.createStructure(this, "test", 3, 2);
		tree.add(1l, 1l, 2l);
		tree.add(2l, 1l, 3l);
		tree.add(3l, 1l, 4l);
		Assert.assertTrue(1l == tree.get(1l, 2l));
		Assert.assertTrue(2l == tree.get(1l, 3l));
		Assert.assertTrue(3l == tree.get(1l, 4l));
		tree.set(11l, 1l, 2l);
		tree.set(22l, 1l, 3l);
		tree.set(33l, 1l, 4l);
		Assert.assertTrue(11l == tree.get(1l, 2l));
		Assert.assertTrue(22l == tree.get(1l, 3l));
		Assert.assertTrue(33l == tree.get(1l, 4l));
	}

	@Test
	public void testManyAdd() throws IOException {
		DbIndexTree tree = DbIndexTree.createStructure(this, "test", 3, 2);
		int max = 20;
		long n;
		for (n = 1; n <= max; n++) {
			long left = n % 16;
			long right = (n / 16) + left;
			tree.add(n, left, right);
		}
		long left = n % 16;
		long right = (n / 16) + left;
		tree.add(n, left, right);
	}

	@Test
	public void testStress() throws IOException {
		int max = 16 * 1024;
		for (int o = 3; o <= 101; o += 2) {
			DbIndexTree tree = DbIndexTree.createStructure(this, "test", o, 2);
			for (long n = 1; n <= max; n++) {
				long left = n % 16;
				long right = (n / 16) + left;
				tree.add(n, left, right);
			}
			for (int m = 0; m < 10; m++) {
				for (long n = 1; n <= max; n++) {
					long left = n % 16;
					long right = (n / 16) + left;
					Assert.assertTrue(n == tree.get(left, right));
				}
			}
		}
	}

	/** Mapa de Bytes */
	private Map<String, byte[]> cache = new HashMap<String, byte[]>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readNode(String name, long id) throws IOException {
		return cache.get("node." + id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeNode(String name, long id, byte[] bytes) throws IOException {
		cache.put("node." + id, bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] readStructure(String name) throws IOException {
		return cache.get("dbtree");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeStructure(String name, byte[] bytes) throws IOException {
		cache.put("dbtree", bytes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasStructure(String name) throws IOException {
		return cache.containsKey("dbtree");
	}

}
