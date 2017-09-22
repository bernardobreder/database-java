package org.oonsql.lng.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.oonsql.util.MyHashMap;

public class MyHashMapTest {

  @Test
  public void test() {
    MyHashMap<Integer, String> map = new MyHashMap<Integer, String>(12, 0.75f);
    Set<Integer> keys = new HashSet<Integer>();
    Random r = new Random(System.currentTimeMillis());
    int size = 512 * 1024;
    for (int n = 0; n < size; n++) {
      int key;
      do {
        key = r.nextInt(Integer.MAX_VALUE);
      } while (map.get(key) != null);
      keys.add(key);
      map.put(key, "" + key);
    }
    Assert.assertEquals(size, map.size());
    for (Integer key : keys) {
      Assert.assertEquals(key.toString(), map.get(key));
    }
    for (Integer key : keys) {
      Assert.assertEquals(key.toString(), map.remove(key));
    }
    Assert.assertEquals(0, map.size());
  }

}
