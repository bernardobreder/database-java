import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class BTreeTest implements BTree.DbIO {

  private static final long ELEM_MAX = 1024;
  private static final int SLOT_MAX = 32;

  @BeforeClass
  public static void beforeClassTreeTest() {
    for (File file : new File(".").listFiles()) {
      if (file.getName().endsWith(".db")) {
        file.delete();
      }
    }
  }

  @After
  public void afterTreeTest() {
    for (File file : new File(".").listFiles()) {
      if (file.getName().endsWith(".db")) {
        file.delete();
      }
    }
  }

  @Test
  public void testAdd() throws IOException {
    List<Long> list = new ArrayList<Long>();
    for (long n = 1; n <= ELEM_MAX; n++) {
      list.add(n);
    }
    Collections.shuffle(list, new Random(0));
    for (int n = 1; n <= SLOT_MAX; n++) {
      BTree<Long> tree = new BTree<Long>(this, "test", n);
      for (int m = 1; m <= ELEM_MAX; m++) {
        Long value = list.get(m - 1);
        long key = value.longValue();
        tree.add(key, value);
      }
      for (int m = 1; m <= ELEM_MAX; m++) {
        Long value = list.get(m - 1);
        long key = value.longValue();
        Assert.assertEquals(value, tree.get(key));
      }
      System.out.println("Step " + n);
    }
  }

  @Test
  public void test() throws IOException {
    BTree<String> tree = new BTree<String>(this, "test", 2);
    Assert.assertNull(tree.get(44));
    Assert.assertNull(tree.get(33));
    Assert.assertNull(tree.get(55));
    Assert.assertNull(tree.get(66));
    Assert.assertNull(tree.get(50));
    Assert.assertNull(tree.get(57));
    tree.add(44, "44");
    tree.add(33, "33");
    tree.add(55, "55");
    tree.add(66, "66");
    tree.add(50, "50");
    tree.add(57, "57");
    Assert.assertNotNull(tree.get(44));
    Assert.assertNotNull(tree.get(33));
    Assert.assertNotNull(tree.get(55));
    Assert.assertNotNull(tree.get(66));
    Assert.assertNotNull(tree.get(50));
    Assert.assertNotNull(tree.get(57));
    tree.remove(44);
    tree.remove(33);
    tree.remove(55);
    tree.remove(66);
    tree.remove(50);
    tree.remove(57);
    Assert.assertNull(tree.get(44));
    Assert.assertNull(tree.get(33));
    Assert.assertNull(tree.get(55));
    Assert.assertNull(tree.get(66));
    Assert.assertNull(tree.get(50));
    Assert.assertNull(tree.get(57));
  }

  @Test
  public void iterator() throws IOException {
    BTree<String> tree = new BTree<String>(this, "test", 2);
    tree.add(44, "44");
    tree.add(33, "33");
    tree.add(55, "55");
    tree.add(66, "66");
    tree.add(50, "50");
    tree.add(57, "57");
    Iterator<String> iterator = tree.iterator(44);
    Assert.assertTrue(iterator.hasNext());
    Assert.assertEquals("44", iterator.next());
    Assert.assertTrue(iterator.hasNext());
    Assert.assertEquals("50", iterator.next());
    Assert.assertTrue(iterator.hasNext());
    Assert.assertEquals("55", iterator.next());
    Assert.assertTrue(iterator.hasNext());
    Assert.assertEquals("57", iterator.next());
    Assert.assertTrue(iterator.hasNext());
    Assert.assertEquals("66", iterator.next());
    Assert.assertFalse(iterator.hasNext());
  }

  @Test
  public void iteratorStress() throws IOException {
    BTree<String> tree = new BTree<String>(this, "test", 2);
    for (Integer n = 0; n <= 1024; n++) {
      tree.add(n.intValue(), n.toString());
    }
    Iterator<String> iterator = tree.iterator(10);
    for (Integer n = 10; n <= 1024; n++) {
      Assert.assertTrue(iterator.hasNext());
      Assert.assertEquals(n.toString(), iterator.next());
    }
    Assert.assertFalse(iterator.hasNext());
  }

  /** Bytes */
  public static final Map<String, byte[]> io = new HashMap<String, byte[]>();

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readData(String name, long id) throws IOException {
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
  public void writeData(String name, long id, byte[] bytes) throws IOException {
    String path = name + "_" + id;
    io.put(path, bytes);
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
  public boolean exist(String name) throws IOException {
    return io.containsKey(name);
  }

}
