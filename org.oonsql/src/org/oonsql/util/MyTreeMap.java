package org.oonsql.util;

/**
 * Arvore na forma de Mapa
 * 
 * @author Tecgraf
 * @param <K>
 * @param <V>
 */
public class MyTreeMap<K extends Comparable<K>, V> {

  /** Vermelho False */
  private static final boolean RED = false;
  /** Black True */
  private static final boolean BLACK = true;
  /** Root */
  private transient Entry<K, V> root = null;
  /** Tamanho */
  private transient int size = 0;

  /**
   * Returns the number of key-value mappings in this map.
   * 
   * @return the number of key-value mappings in this map
   */
  public int size() {
    return size;
  }

  /**
   * Adiciona um elemento no mapa
   * 
   * @param key
   * @param value
   * @return elemento removido
   */
  public V put(K key, V value) {
    Entry<K, V> aux = root;
    if (aux == null) {
      root = new Entry<K, V>(key, value, null);
      size = 1;
      return null;
    }
    int cmp;
    Entry<K, V> parent;
    do {
      parent = aux;
      cmp = key.compareTo(aux.key);
      if (cmp < 0) {
        aux = aux.left;
      }
      else if (cmp > 0) {
        aux = aux.right;
      }
      else {
        V result = aux.value;
        aux.value = value;
        return result;
      }
    } while (aux != null);
    Entry<K, V> e = new Entry<K, V>(key, value, parent);
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
   * Returns the value to which the specified key is mapped, or {@code null} if
   * this map contains no mapping for the key.
   * 
   * <p>
   * More formally, if this map contains a mapping from a key {@code k} to a
   * value {@code v} such that {@code key} compares equal to {@code k} according
   * to the map's ordering, then this method returns {@code v}; otherwise it
   * returns {@code null}. (There can be at most one such mapping.)
   * 
   * <p>
   * A return value of {@code null} does not <i>necessarily</i> indicate that
   * the map contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to {@code null}. The containsKey operation may be
   * used to distinguish these two cases.
   * 
   * @param key
   * @return valor
   * @throws ClassCastException if the specified key cannot be compared with the
   *         keys currently in the map
   * @throws NullPointerException if the specified key is null and this map uses
   *         natural ordering, or its comparator does not permit null keys
   */
  public V get(K key) {
    Entry<K, V> p = getEntry(key);
    return (p == null ? null : p.value);
  }

  /**
   * Removes the mapping for this key from this TreeMap if present.
   * 
   * @param key key for which mapping should be removed
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
   *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
   *         can also indicate that the map previously associated <tt>null</tt>
   *         with <tt>key</tt>.)
   * @throws ClassCastException if the specified key cannot be compared with the
   *         keys currently in the map
   * @throws NullPointerException if the specified key is null and this map uses
   *         natural ordering, or its comparator does not permit null keys
   */
  public V remove(K key) {
    Entry<K, V> p = getEntry(key);
    if (p == null) {
      return null;
    }

    V oldValue = p.value;
    deleteEntry(p);
    return oldValue;
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
   * Returns this map's entry for the given key, or <tt>null</tt> if the map
   * does not contain an entry for the key.
   * 
   * @param k
   * @return this map's entry for the given key, or <tt>null</tt> if the map
   *         does not contain an entry for the key
   * @throws ClassCastException if the specified key cannot be compared with the
   *         keys currently in the map
   * @throws NullPointerException if the specified key is null and this map uses
   *         natural ordering, or its comparator does not permit null keys
   */
  private Entry<K, V> getEntry(K k) {
    Entry<K, V> p = root;
    while (p != null) {
      int cmp = k.compareTo(p.key);
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
   * Delete node p, and then rebalance the tree.
   * 
   * @param p
   */
  private void deleteEntry(Entry<K, V> p) {
    size--;
    if (p.left != null && p.right != null) {
      Entry<K, V> s = successor(p);
      p.key = s.key;
      p.value = s.value;
      p = s;
    }
    Entry<K, V> replacement = (p.left != null ? p.left : p.right);
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
   * Returns the successor of the specified Entry, or null if no such.
   * 
   * @param t
   * @return entidade
   */
  private static <K, V> Entry<K, V> successor(Entry<K, V> t) {
    if (t == null) {
      return null;
    }
    else if (t.right != null) {
      Entry<K, V> p = t.right;
      while (p.left != null) {
        p = p.left;
      }
      return p;
    }
    else {
      Entry<K, V> p = t.parent;
      Entry<K, V> ch = t;
      while (p != null && ch == p.right) {
        ch = p;
        p = p.parent;
      }
      return p;
    }
  }

  /**
   * From CLR
   * 
   * @param x
   */
  private void fixAfterInsertion(Entry<K, V> x) {
    x.color = RED;
    while (x != null && x != root && x.parent.color == RED) {
      if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
        Entry<K, V> y = rightOf(parentOf(parentOf(x)));
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
        Entry<K, V> y = leftOf(parentOf(parentOf(x)));
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
   * From CLR
   * 
   * @param x
   */
  private void fixAfterDeletion(Entry<K, V> x) {
    while (x != root && colorOf(x) == BLACK) {
      if (x == leftOf(parentOf(x))) {
        Entry<K, V> sib = rightOf(parentOf(x));

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
      else { // symmetric
        Entry<K, V> sib = leftOf(parentOf(x));

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
   * From CLR
   * 
   * @param p
   */
  private void rotateLeft(Entry<K, V> p) {
    if (p != null) {
      Entry<K, V> r = p.right;
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
  private void rotateRight(Entry<K, V> p) {
    if (p != null) {
      Entry<K, V> l = p.left;
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
   * @param p
   * @return cor
   */
  private static <K, V> boolean colorOf(Entry<K, V> p) {
    return p == null ? BLACK : p.color;
  }

  /**
   * @param p
   * @param c
   */
  private static <K, V> void setColor(Entry<K, V> p, boolean c) {
    if (p != null) {
      p.color = c;
    }
  }

  /**
   * @param p
   * @return pai
   */
  private static <K, V> Entry<K, V> parentOf(Entry<K, V> p) {
    return p == null ? null : p.parent;
  }

  /**
   * @param p
   * @return left
   */
  private static <K, V> Entry<K, V> leftOf(Entry<K, V> p) {
    return p == null ? null : p.left;
  }

  /**
   * @param p
   * @return right
   */
  private static <K, V> Entry<K, V> rightOf(Entry<K, V> p) {
    return p == null ? null : p.right;
  }

  /**
   * Node in the Tree. Doubles as a means to pass key-value pairs back to user
   * (see Map.Entry).
   * 
   * @param <K>
   * @param <V>
   */
  private static class Entry<K, V> {

    /** Chave */
    public K key;
    /** Valor */
    public V value;
    /** Left */
    public Entry<K, V> left;
    /** Right */
    public Entry<K, V> right;
    /** Pai */
    public Entry<K, V> parent;
    /** Cor */
    public boolean color;

    /**
     * Make a new cell with given key, value, and parent, and with <tt>null</tt>
     * child links, and BLACK color.
     * 
     * @param key
     * @param value
     * @param parent
     */
    public Entry(K key, V value, Entry<K, V> parent) {
      this.key = key;
      this.value = value;
      this.parent = parent;
      this.color = BLACK;
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
   * @param args
   */
  public static void main(String[] args) {
    MyTreeMap<Integer, String> map = new MyTreeMap<Integer, String>();
    for (int n = 0; n < 1024; n++) {
      map.put(n, "" + n);
    }
    for (int n = 0; n < 1024; n++) {
      System.out.println(map.get(n));
    }
    System.out.println(map.get(-1));
  }

}
