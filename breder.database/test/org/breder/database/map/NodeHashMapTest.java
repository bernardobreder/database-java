package org.breder.database.map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import org.breder.database.map.NodeHashMap.NodeHashMapInterface;
import org.junit.Test;

public class NodeHashMapTest {

  @Test
  public void test() throws IOException {
    int size = 1024;
    NodeHashMap map = new NodeHashMap(null);
    for (int n = 0; n < size; n++) {
      map.add(n, Integer.valueOf(n));
    }
    Assert.assertEquals(size, map.size());
    for (int n = 0; n < size; n++) {
      Integer value = (Integer) map.get(n);
      Assert.assertEquals(Integer.valueOf(n), value);
    }
    Assert.assertEquals(size, map.size());
    for (int n = 0; n < size; n++) {
      map.set(n, Integer.valueOf(-n));
    }
    Assert.assertEquals(size, map.size());
    for (int n = 0; n < size; n++) {
      Integer value = (Integer) map.get(n);
      Assert.assertEquals(Integer.valueOf(-n), value);
    }
    Assert.assertEquals(size, map.size());
    for (int n = 0; n < size; n++) {
      map.remove(n);
    }
    for (int n = 0; n < size; n++) {
      Assert.assertNull(map.get(n));
    }
    Assert.assertEquals(0, map.size());
    int count = 0;
    for (int n = 0; count != size; n++) {
      if (n % 5 != 0) {
        map.add(n, new Object());
        count++;
      }
    }
    Assert.assertEquals(size, map.size());
  }

  @Test
  public void testWithStream() throws IOException {
    int size = 32 * 1024;
    MemoryNodeHashMapInterface memory = new MemoryNodeHashMapInterface();
    NodeHashMap map = new NodeHashMap(memory);
    map.save();
    for (int n = 0; n < size; n++) {
      map.add(n, Integer.valueOf(n));
      map.save();
      map = new NodeHashMap(memory);
    }
    Assert.assertEquals(size, map.size());
    map.save();
    map = new NodeHashMap(memory);
    for (int n = 0; n < size; n++) {
      Integer value = (Integer) map.get(n);
      Assert.assertEquals(Integer.valueOf(n), value);
      map.save();
      map = new NodeHashMap(memory);
    }
    Assert.assertEquals(size, map.size());
    map.save();
    map = new NodeHashMap(memory);
    for (int n = 0; n < size; n++) {
      map.set(n, Integer.valueOf(-n));
      map.save();
      map = new NodeHashMap(memory);
    }
    Assert.assertEquals(size, map.size());
    map.save();
    map = new NodeHashMap(memory);
    for (int n = 0; n < size; n++) {
      Integer value = (Integer) map.get(n);
      Assert.assertEquals(Integer.valueOf(-n), value);
      map.save();
      map = new NodeHashMap(memory);
    }
    Assert.assertEquals(size, map.size());
    map.save();
    map = new NodeHashMap(memory);
    for (int n = 0; n < size; n++) {
      map.remove(n);
      map.save();
      map = new NodeHashMap(memory);
    }
    for (int n = 0; n < size; n++) {
      Assert.assertNull(map.get(n));
      map.save();
      map = new NodeHashMap(memory);
    }
    Assert.assertEquals(0, map.size());
    map.save();
    map = new NodeHashMap(memory);
    for (int n = 0; n < size; n++) {
      if ((n % 2) == 0) {
        map.add(n, Integer.valueOf(n));
        map.save();
        map = new NodeHashMap(memory);
      }
    }
    Assert.assertEquals(size / 2, map.size());
  }

  /**
   * Mapa em memoria
   * 
   * @author Tecgraf
   */
  private static class MemoryNodeHashMapInterface implements
    NodeHashMapInterface {

    /** Bytes */
    public byte[] bytes;
    /** Bytes */
    public byte[][] nodeBytes;

    /**
     * @throws IOException
     */
    public MemoryNodeHashMapInterface() throws IOException {
      {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeInt(0);
        output.writeInt(1);
        output.writeInt(16);
        output.close();
        this.bytes = bytes.toByteArray();
      }
      {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(bytes);
        output.writeInt(0);
        output.close();
        this.nodeBytes = new byte[1][];
        this.nodeBytes[0] = bytes.toByteArray();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(DataOutputStream output, Object value) throws IOException {
      output.writeInt((Integer) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getRootOutputStream() {
      return new ByteArrayOutputStream() {
        @Override
        public void close() throws IOException {
          bytes = this.toByteArray();
        }
      };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getRootInputStream() {
      return new ByteArrayInputStream(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeSave() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startSave() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object read(DataInputStream input) throws IOException {
      return input.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream getNodeOutputStream(final int index) {
      if (index >= this.nodeBytes.length) {
        byte[][] array = new byte[index + 1][];
        System.arraycopy(this.nodeBytes, 0, array, 0, this.nodeBytes.length);
        this.nodeBytes = array;
      }
      return new ByteArrayOutputStream() {
        @Override
        public void close() throws IOException {
          nodeBytes[index] = this.toByteArray();
        }
      };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getNodeInputStream(int index) {
      return new ByteArrayInputStream(this.nodeBytes[index]);
    }
  }

}
