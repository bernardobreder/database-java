package sdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import sdb.DbTableTree.DbTableTreeIODelegator;

public class DbTableTreeTest implements DbTableTreeIODelegator {

  @Test
  public void test() throws IOException {
    DbTableTree tree = DbTableTree.createStructure(this, "test", 3);
    tree.add(new DbObject().putAsInteger("id", 1), 2l);
    tree.add(new DbObject().putAsInteger("id", 2), 3l);
    tree.add(new DbObject().putAsInteger("id", 3), 4l);
    Assert.assertEquals(new DbObject().putAsInteger("id", 1), tree.get(2l));
    Assert.assertEquals(new DbObject().putAsInteger("id", 2), tree.get(3l));
    Assert.assertEquals(new DbObject().putAsInteger("id", 3), tree.get(4l));
    tree.set(new DbObject().putAsInteger("id", 11), 2l);
    tree.set(new DbObject().putAsInteger("id", 22), 3l);
    tree.set(new DbObject().putAsInteger("id", 33), 4l);
    Assert.assertEquals(new DbObject().putAsInteger("id", 11), tree.get(2l));
    Assert.assertEquals(new DbObject().putAsInteger("id", 22), tree.get(3l));
    Assert.assertEquals(new DbObject().putAsInteger("id", 33), tree.get(4l));
  }

  @Test
  public void testStress() throws IOException {
    int max = 16 * 1024;
    for (int o = 3; o <= 101; o += 2) {
      DbTableTree tree = DbTableTree.createStructure(this, "test", o);
      for (long n = 1; n <= max; n++) {
        tree.add(new DbObject().putAsLong("id", n), n);
      }
      for (int m = 0; m < 10; m++) {
        for (long n = 1; n <= max; n++) {
          Assert.assertEquals(new DbObject().putAsLong("id", n), tree.get(n));
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
