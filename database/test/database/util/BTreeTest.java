package database.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class BTreeTest {

  /**
   * @throws IOException
   */
  @Test
  public void test() throws IOException {
    BTree t = new BTree(new MyBTreeModel());
    Random r = new Random(System.currentTimeMillis());
    Set<Integer> set = new HashSet<Integer>();
    for (int n = 0; n < 1024 * 1024; n++) {
      Integer value = r.nextInt(1024);
      while (set.contains(value)) {
        value = r.nextInt(1024);
      }
      set.add(value);
      t.add(value, value);
      System.out.println(value);
      for (Integer i : set) {
        Assert.assertEquals(i, t.find(i));
      }
    }
  }

  public static class MyBTreeModel implements BTree.Model {

    private int sequence;

    private Map<Integer, byte[]> bytes = new HashMap<Integer, byte[]>();

    /**
     * {@inheritDoc}
     * 
     * @throws FileNotFoundException
     */
    @Override
    public InputStream getInputStream(int sequence) throws IOException {
      byte[] buf = bytes.get(bytes);
      if (buf == null) {
        throw new FileNotFoundException("" + sequence);
      }
      return new ByteArrayInputStream(buf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int sequence() {
      return ++sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getOutputStream(final int sequence) throws IOException {
      return new ByteArrayOutputStream() {
        @Override
        public void close() throws IOException {
          bytes.put(sequence, this.toByteArray());
        }
      };
    }

  }

}
