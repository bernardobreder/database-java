package org.oonsql.util;

/**
 * 
 * 
 * @author Tecgraf
 * @param <K>
 * @param <V>
 */
public class MyHashMap<K, V> {

  /**
   * The table, resized as necessary. Length MUST Always be a power of two.
   */
  private Entry<K, V>[] table;

  /**
   * The number of key-value mappings contained in this map.
   */
  private int size;

  /**
   * The next size value at which to resize (capacity * load factor).
   * 
   * @serial
   */
  private int threshold;

  /**
   * The load factor for the hash table.
   * 
   * @serial
   */
  private final float loadFactor;

  /**
   * Capacidade inicial
   */
  private int initialCapacity;

  /**
   * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
   * and load factor.
   * 
   * @param initialCapacity the initial capacity
   * @param loadFactor the load factor
   * @throws IllegalArgumentException if the initial capacity is negative or the
   *         load factor is nonpositive
   */
  public MyHashMap(int initialCapacity, float loadFactor) {
    // Find a power of 2 >= initialCapacity
    int capacity = 1;
    while (capacity < initialCapacity) {
      capacity <<= 1;
    }
    this.initialCapacity = capacity;
    this.loadFactor = loadFactor;
    this.threshold = (int) (capacity * loadFactor);
    this.table = createEntry(capacity);
  }

  /**
   * Constructs an empty <tt>HashMap</tt> with the default initial capacity (16)
   * and the default load factor (0.75).
   */
  public MyHashMap() {
    this.initialCapacity = 16;
    this.loadFactor = 0.75f;
    this.threshold = 12;
    this.table = this.createEntry(16);
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if
   * this map contains no mapping for the key.
   * 
   * <p>
   * More formally, if this map contains a mapping from a key {@code k} to a
   * value {@code v} such that {@code (key==null ? k==null :
   * key.equals(k))}, then this method returns {@code v}; otherwise it returns
   * {@code null}. (There can be at most one such mapping.)
   * 
   * <p>
   * A return value of {@code null} does not <i>necessarily</i> indicate that
   * the map contains no mapping for the key; it's also possible that the map
   * explicitly maps the key to {@code null}. The containsKey operation may be
   * used to distinguish these two cases.
   * 
   * @param key
   * @return objeto
   * @see #put(Object, Object)
   */
  public V get(Object key) {
    int hash = hash(key.hashCode());
    int indexFor = indexFor(hash, table.length);
    for (Entry<K, V> e = table[indexFor]; e != null; e = e.next) {
      if (e.hash == hash) {
        Object k = e.key;
        if (k == key || key.equals(k)) {
          return e.value;
        }
      }
    }
    return null;
  }

  /**
   * Associates the specified value with the specified key in this map. If the
   * map previously contained a mapping for the key, the old value is replaced.
   * 
   * @param key key with which the specified value is to be associated
   * @param value value to be associated with the specified key
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
   *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
   *         can also indicate that the map previously associated <tt>null</tt>
   *         with <tt>key</tt>.)
   */
  public V put(K key, V value) {
    int hash = hash(key.hashCode());
    int indexFor = indexFor(hash, table.length);
    for (Entry<K, V> e = table[indexFor]; e != null; e = e.next) {
      if (e.hash == hash) {
        Object k = e.key;
        if (k == key || key.equals(k)) {
          V oldValue = e.value;
          e.value = value;
          return oldValue;
        }
      }
    }
    table[indexFor] = new Entry<K, V>(hash, key, value, table[indexFor]);
    if (size++ >= threshold) {
      this.resize(2 * table.length);
    }
    return null;
  }

  /**
   * Cria entidades
   * 
   * @param newCapacity
   * @return entry
   */
  @SuppressWarnings("unchecked")
  private Entry<K, V>[] createEntry(int newCapacity) {
    return new Entry[newCapacity];
  }

  /**
   * Removes the mapping for the specified key from this map if present.
   * 
   * @param key key whose mapping is to be removed from the map
   * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
   *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
   *         can also indicate that the map previously associated <tt>null</tt>
   *         with <tt>key</tt>.)
   */
  public V remove(Object key) {
    int hash = hash(key.hashCode());
    int indexFor = indexFor(hash, table.length);
    Entry<K, V> prev = table[indexFor];
    Entry<K, V> e = prev;
    while (e != null) {
      Entry<K, V> next = e.next;
      if (e.hash == hash) {
        Object k = e.key;
        if (k == key || key.equals(k)) {
          size--;
          if (prev == e) {
            table[indexFor] = next;
          }
          else {
            prev.next = next;
          }
          break;
        }
      }
      prev = e;
      e = next;
    }
    if (this.size <= this.threshold / 2
      && this.table.length > this.initialCapacity) {
      this.resize(table.length / 2);
    }
    return (e == null ? null : e.value);
  }

  /**
   * @param newCapacity
   */
  private void resize(int newCapacity) {
    Entry<K, V>[] newTable = createEntry(newCapacity);
    Entry<K, V>[] src = table;
    for (int j = 0; j < src.length; j++) {
      Entry<K, V> srcEntry = src[j];
      if (srcEntry != null) {
        src[j] = null;
        do {
          Entry<K, V> next = srcEntry.next;
          int i = indexFor(srcEntry.hash, newCapacity);
          srcEntry.next = newTable[i];
          newTable[i] = srcEntry;
          srcEntry = next;
        } while (srcEntry != null);
      }
    }
    table = newTable;
    threshold = (int) (newCapacity * loadFactor);
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
   * Returns <tt>true</tt> if this map contains no key-value mappings.
   * 
   * @return <tt>true</tt> if this map contains no key-value mappings
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Removes all of the mappings from this map. The map will be empty after this
   * call returns.
   */
  public void clear() {
    this.threshold = (int) (this.initialCapacity * this.loadFactor);
    this.table = this.createEntry(this.initialCapacity);
    this.size = 0;
  }

  /**
   * Applies a supplemental hash function to a given hashCode, which defends
   * against poor quality hash functions. This is critical because MyHashMap
   * uses power-of-two length hash tables, that otherwise encounter collisions
   * for hashCodes that do not differ in lower bits. Note: Null keys always map
   * to hash 0, thus index 0.
   * 
   * @param h
   * @return hash
   */
  private static int hash(int h) {
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
  }

  /**
   * Returns index for hash code h.
   * 
   * @param h
   * @param length
   * @return indice
   */
  private static int indexFor(int h, int length) {
    return h & (length - 1);
  }

  /**
   * Entidade
   * 
   * @author Tecgraf
   * @param <K>
   * @param <V>
   */
  private static class Entry<K, V> {

    /** Chave */
    private final K key;
    /** Valor */
    private V value;
    /** Próximo */
    private Entry<K, V> next;
    /** Hash da chave */
    private final int hash;

    /**
     * Creates new entry.
     * 
     * @param h
     * @param k
     * @param v
     * @param n
     */
    public Entry(int h, K k, V v, Entry<K, V> n) {
      value = v;
      next = n;
      key = k;
      hash = h;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
      return key + "=" + value;
    }

  }

}
