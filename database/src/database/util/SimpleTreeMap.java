package database.util;

import java.util.Iterator;

/**
 * 
 * 
 * @author Tecgraf
 * @param <V>
 */
public class SimpleTreeMap<V> implements Iterable<V> {

  /** Root */
  private transient Entry<V> root = null;
  /** The number of entries in the tree */
  private transient int size = 0;
  /** Red Color */
  private static final boolean RED = false;
  /** Black Color */
  private static final boolean BLACK = true;

  /**
   * @param key
   * @return valor da chave
   */
  public V get(long key) {
    Entry<V> p = getEntry(key);
    return (p == null ? null : p.value);
  }

  /**
   * @param key
   * @return indica se tem
   */
  public boolean has(long key) {
    return getEntry(key) != null;
  }

  /**
   * @return first key
   */
  public long first() {
    return firstEntry().key;
  }

  /**
   * @return last key
   */
  public long last() {
    return lastEntry().key;
  }

  /**
   * @param key
   * @param value
   * @return valor antigo
   */
  public V put(long key, V value) {
    Entry<V> t = root;
    if (t == null) {
      root = new Entry<>(key, value, null);
      size = 1;
      return null;
    }
    Entry<V> parent;
    long cmp;
    do {
      parent = t;
      cmp = key - t.key;
      if (cmp < 0) {
        t = t.left;
      }
      else if (cmp > 0) {
        t = t.right;
      }
      else {
        V result = t.value;
        t.value = value;
        return result;
      }
    } while (t != null);
    Entry<V> e = new Entry<V>(key, value, parent);
    if (cmp < 0) {
      parent.left = e;
    }
    else {
      parent.right = e;
    }
    fixAfterInsertion(e);
    size++;
    return null;
  }

  /**
   * Remover uma chave
   * 
   * @param key
   * @return valor
   */
  public V remove(long key) {
    Entry<V> p = getEntry(key);
    if (p == null) {
      return null;
    }

    V oldValue = p.value;
    deleteEntry(p);
    return oldValue;
  }

  /**
   * Returns the number of key-value mappings in this map.
   * 
   * @return the number of key-value mappings in this map
   */
  public int size() {
    return size;
  }

  /**
   * Removes all of the mappings from this map. The map will be empty after this
   * call returns.
   */
  public void clear() {
    size = 0;
    root = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    if (this.size == 0) {
      return "{}";
    }
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    Entry<V> entry = this.firstEntry();
    for (;;) {
      sb.append(entry.key);
      sb.append('=');
      sb.append(entry.value);
      Entry<V> successor = entry.successor();
      if (successor == null) {
        break;
      }
      sb.append(',').append(' ');
      entry = successor;
    }
    sb.append('}');
    return sb.toString();
  }

  /**
   * Node in the Tree. Doubles as a means to pass key-value pairs back to user
   * (see Entry).
   * 
   * @param <V>
   */
  public static class Entry<V> {

    /** Chave */
    public long sequence;
    /** Chave */
    public long key;
    /** Value */
    public V value;
    /** Left */
    public Entry<V> left;
    /** Right */
    public Entry<V> right;
    /** Parent */
    public Entry<V> parent;
    /** Color */
    public boolean color = BLACK;

    /**
     * Make a new cell with given key, value, and parent, and with {@code null}
     * child links, and BLACK color.
     * 
     * @param key
     * @param value
     * @param parent
     */
    Entry(long key, V value, Entry<V> parent) {
      this.key = key;
      this.value = value;
      this.parent = parent;
    }

    /**
     * Returns the successor of the specified Entry, or null if no such.
     * 
     * @return sucessor
     */
    public Entry<V> successor() {
      if (this.right != null) {
        Entry<V> p = this.right;
        while (p.left != null) {
          p = p.left;
        }
        return p;
      }
      else {
        Entry<V> p = this.parent;
        Entry<V> ch = this;
        while (p != null && ch == p.right) {
          ch = p;
          p = p.parent;
        }
        return p;
      }
    }

    /**
     * Returns the predecessor of the specified Entry, or null if no such.
     * 
     * @return predecessor
     */
    public Entry<V> predecessor() {
      if (this.left != null) {
        Entry<V> p = this.left;
        while (p.right != null) {
          p = p.right;
        }
        return p;
      }
      else {
        Entry<V> p = this.parent;
        Entry<V> ch = this;
        while (p != null && ch == p.left) {
          ch = p;
          p = p.parent;
        }
        return p;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Entry)) {
        return false;
      }
      Entry<?> e = (Entry<?>) o;
      return key == e.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      return (int) (key ^ (key >>> 32));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return key + "=" + value;
    }
  }

  /**
   * Returns this map's entry for the given key, or {@code null} if the map does
   * not contain an entry for the key.
   * 
   * @param k
   * @return this map's entry for the given key, or {@code null} if the map does
   *         not contain an entry for the key
   * @throws ClassCastException if the specified key cannot be compared with the
   *         keys currently in the map
   * @throws NullPointerException if the specified key is null and this map uses
   *         natural ordering, or its comparator does not permit null keys
   */
  public Entry<V> getEntry(long k) {
    Entry<V> p = root;
    while (p != null) {
      long cmp = k - p.key;
      if (cmp < 0) {
        p = p.left;
      }
      else if (cmp > 0) {
        p = p.right;
      }
      else {
        return p;
      }
    }
    return null;
  }

  /**
   * Returns the first Entry in the TreeMap (according to the TreeMap's key-sort
   * function). Returns null if the TreeMap is empty.
   * 
   * @return first entry
   */
  public Entry<V> firstEntry() {
    Entry<V> p = root;
    if (p != null) {
      while (p.left != null) {
        p = p.left;
      }
    }
    return p;
  }

  /**
   * Returns the last Entry in the TreeMap (according to the TreeMap's key-sort
   * function). Returns null if the TreeMap is empty.
   * 
   * @return last entry
   */
  public Entry<V> lastEntry() {
    Entry<V> p = root;
    if (p != null) {
      while (p.right != null) {
        p = p.right;
      }
    }
    return p;
  }

  /**
   * Balancing operations.
   * 
   * Implementations of rebalancings during insertion and deletion are slightly
   * different than the CLR version. Rather than using dummy nilnodes, we use a
   * set of accessors that deal properly with null. They are used to avoid
   * messiness surrounding nullness checks in the main algorithms.
   * 
   * @param p
   * @return cor
   */

  private static <K, V> boolean colorOf(Entry<V> p) {
    return (p == null ? BLACK : p.color);
  }

  /**
   * @param p
   * @return parent
   */
  private static <K, V> Entry<V> parentOf(Entry<V> p) {
    return (p == null ? null : p.parent);
  }

  /**
   * @param p
   * @param c
   */
  private static <K, V> void setColor(Entry<V> p, boolean c) {
    if (p != null) {
      p.color = c;
    }
  }

  /**
   * @param p
   * @return left
   */
  private static <K, V> Entry<V> leftOf(Entry<V> p) {
    return (p == null) ? null : p.left;
  }

  /**
   * @param p
   * @return right
   */
  private static <K, V> Entry<V> rightOf(Entry<V> p) {
    return (p == null) ? null : p.right;
  }

  /**
   * From CLR
   * 
   * @param p
   */
  private void rotateLeft(Entry<V> p) {
    if (p != null) {
      Entry<V> r = p.right;
      p.right = r.left;
      if (r.left != null) {
        r.left.parent = p;
      }
      r.parent = p.parent;
      if (p.parent == null) {
        root = r;
      }
      else if (p.parent.left == p) {
        p.parent.left = r;
      }
      else {
        p.parent.right = r;
      }
      r.left = p;
      p.parent = r;
    }
  }

  /**
   * From CLR
   * 
   * @param p
   */
  private void rotateRight(Entry<V> p) {
    if (p != null) {
      Entry<V> l = p.left;
      p.left = l.right;
      if (l.right != null) {
        l.right.parent = p;
      }
      l.parent = p.parent;
      if (p.parent == null) {
        root = l;
      }
      else if (p.parent.right == p) {
        p.parent.right = l;
      }
      else {
        p.parent.left = l;
      }
      l.right = p;
      p.parent = l;
    }
  }

  /**
   * From CLR
   * 
   * @param x
   */
  private void fixAfterInsertion(Entry<V> x) {
    x.color = RED;
    while (x != null && x != root && x.parent.color == RED) {
      if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
        Entry<V> y = rightOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED) {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        }
        else {
          if (x == rightOf(parentOf(x))) {
            x = parentOf(x);
            rotateLeft(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateRight(parentOf(parentOf(x)));
        }
      }
      else {
        Entry<V> y = leftOf(parentOf(parentOf(x)));
        if (colorOf(y) == RED) {
          setColor(parentOf(x), BLACK);
          setColor(y, BLACK);
          setColor(parentOf(parentOf(x)), RED);
          x = parentOf(parentOf(x));
        }
        else {
          if (x == leftOf(parentOf(x))) {
            x = parentOf(x);
            rotateRight(x);
          }
          setColor(parentOf(x), BLACK);
          setColor(parentOf(parentOf(x)), RED);
          rotateLeft(parentOf(parentOf(x)));
        }
      }
    }
    root.color = BLACK;
  }

  /**
   * Delete node p, and then rebalance the tree.
   * 
   * @param p
   */
  private void deleteEntry(Entry<V> p) {
    size--;
    if (p.left != null && p.right != null) {
      Entry<V> s = p.successor();
      p.key = s.key;
      p.value = s.value;
      p = s;
    }
    Entry<V> replacement = (p.left != null ? p.left : p.right);
    if (replacement != null) {
      replacement.parent = p.parent;
      if (p.parent == null) {
        root = replacement;
      }
      else if (p == p.parent.left) {
        p.parent.left = replacement;
      }
      else {
        p.parent.right = replacement;
      }
      p.left = p.right = p.parent = null;
      if (p.color == BLACK) {
        fixAfterDeletion(replacement);
      }
    }
    else if (p.parent == null) {
      root = null;
    }
    else {
      if (p.color == BLACK) {
        fixAfterDeletion(p);
      }
      if (p.parent != null) {
        if (p == p.parent.left) {
          p.parent.left = null;
        }
        else if (p == p.parent.right) {
          p.parent.right = null;
        }
        p.parent = null;
      }
    }
  }

  /**
   * From CLR
   * 
   * @param x
   */
  private void fixAfterDeletion(Entry<V> x) {
    while (x != root && colorOf(x) == BLACK) {
      if (x == leftOf(parentOf(x))) {
        Entry<V> sib = rightOf(parentOf(x));
        if (colorOf(sib) == RED) {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateLeft(parentOf(x));
          sib = rightOf(parentOf(x));
        }
        if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
          setColor(sib, RED);
          x = parentOf(x);
        }
        else {
          if (colorOf(rightOf(sib)) == BLACK) {
            setColor(leftOf(sib), BLACK);
            setColor(sib, RED);
            rotateRight(sib);
            sib = rightOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(rightOf(sib), BLACK);
          rotateLeft(parentOf(x));
          x = root;
        }
      }
      else {
        Entry<V> sib = leftOf(parentOf(x));
        if (colorOf(sib) == RED) {
          setColor(sib, BLACK);
          setColor(parentOf(x), RED);
          rotateRight(parentOf(x));
          sib = leftOf(parentOf(x));
        }
        if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
          setColor(sib, RED);
          x = parentOf(x);
        }
        else {
          if (colorOf(leftOf(sib)) == BLACK) {
            setColor(rightOf(sib), BLACK);
            setColor(sib, RED);
            rotateLeft(sib);
            sib = leftOf(parentOf(x));
          }
          setColor(sib, colorOf(parentOf(x)));
          setColor(parentOf(x), BLACK);
          setColor(leftOf(sib), BLACK);
          rotateRight(parentOf(x));
          x = root;
        }
      }
    }
    setColor(x, BLACK);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    SimpleTreeMap<byte[]> map = new SimpleTreeMap<byte[]>();
    long time = System.currentTimeMillis();
    for (long n = 0; n < 6 * 1024 * 1024; n++) {
      map.put(n + 1, new byte[64]);
      if ((n % (1024 * 1024)) == 0) {
        System.out.println(n / (1024 * 1024));
      }
    }
    time = (System.currentTimeMillis() - time) / 1000;
    System.out.println(((double) map.size) / time);
    System.out.println(map.size());
    System.out.println(time);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<V> iterator() {
    return new MyIterator<V>(this);
  }

  /**
   * 
   * 
   * @author Tecgraf
   * @param <V>
   */
  private static class MyIterator<V> implements Iterator<V> {

    /** Mapa */
    private SimpleTreeMap<V> map;
    /** Mapa */
    private SimpleTreeMap.Entry<V> entry;
    /** Iniciado */
    private boolean started = false;

    /**
     * @param map
     */
    public MyIterator(SimpleTreeMap<V> map) {
      this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
      if (!started) {
        started = true;
        this.entry = this.map.firstEntry();
      }
      return this.entry != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V next() {
      if (!started) {
        started = true;
        this.entry = this.map.firstEntry();
      }
      else {
        this.entry = this.entry.successor();
      }
      return this.entry == null ? null : this.entry.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
      throw new IllegalArgumentException();
    }

  }

}
