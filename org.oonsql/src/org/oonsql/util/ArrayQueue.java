package org.oonsql.util;

/**
 * Filha implementado com array
 * 
 * @author Bernardo Breder
 * @param <E>
 */
public class ArrayQueue<E> {

  /** Array */
  private E[] elements;
  /** Indice do cabeçalho */
  private int head;
  /** Indice do rabo */
  private int tail;

  /**
   * Constructs an empty array deque with an initial capacity sufficient to hold
   * 16 elements.
   */
  @SuppressWarnings("unchecked")
  public ArrayQueue() {
    elements = (E[]) new Object[16];
  }

  /**
   * Constructs an empty array deque with an initial capacity sufficient to hold
   * the specified number of elements.
   * 
   * @param numElements lower bound on initial capacity of the deque
   */
  @SuppressWarnings("unchecked")
  public ArrayQueue(int numElements) {
    int initialCapacity = 8;
    if (numElements >= initialCapacity) {
      initialCapacity = numElements;
      initialCapacity |= (initialCapacity >>> 1);
      initialCapacity |= (initialCapacity >>> 2);
      initialCapacity |= (initialCapacity >>> 4);
      initialCapacity |= (initialCapacity >>> 8);
      initialCapacity |= (initialCapacity >>> 16);
      initialCapacity++;
      if (initialCapacity < 0) {
        initialCapacity >>>= 1;
      }
    }
    elements = (E[]) new Object[initialCapacity];
  }

  /**
   * Inserts the specified element at the end of this deque.
   * 
   * @param e the element to add
   */
  @SuppressWarnings("unchecked")
  public void add(E e) {
    elements[tail] = e;
    if ((tail = (tail + 1) & (elements.length - 1)) == head) {
      int p = head;
      int n = elements.length;
      int r = n - p; // number of elements to the right of p
      int newCapacity = n << 1;
      if (newCapacity < 0) {
        throw new IllegalStateException("Sorry, deque too big");
      }
      Object[] a = new Object[newCapacity];
      System.arraycopy(elements, p, a, 0, r);
      System.arraycopy(elements, 0, a, r, p);
      elements = (E[]) a;
      head = 0;
      tail = n;
    }
  }

  /**
   * Retrieves and removes the head of the queue represented by this deque (in
   * other words, the first element of this deque), or returns <tt>null</tt> if
   * this deque is empty.
   * 
   * @return the head of the queue represented by this deque, or <tt>null</tt>
   *         if this deque is empty
   */
  public E remove() {
    int h = head;
    E result = elements[h];
    if (result == null) {
      return null;
    }
    elements[h] = null;
    head = (h + 1) & (elements.length - 1);
    return result;
  }

  /**
   * Retrieves, but does not remove, the head of the queue represented by this
   * deque, or returns <tt>null</tt> if this deque is empty.
   * 
   * @return the head of the queue represented by this deque, or <tt>null</tt>
   *         if this deque is empty
   */
  public E get() {
    return elements[head];
  }

  /**
   * Returns the number of elements in this deque.
   * 
   * @return the number of elements in this deque
   */
  public int size() {
    return (tail - head) & (elements.length - 1);
  }

  /**
   * Returns <tt>true</tt> if this deque contains no elements.
   * 
   * @return <tt>true</tt> if this deque contains no elements
   */
  public boolean isEmpty() {
    return head == tail;
  }

  /**
   * Returns <tt>true</tt> if this deque contains the specified element. More
   * formally, returns <tt>true</tt> if and only if this deque contains at least
   * one element <tt>e</tt> such that <tt>o.equals(e)</tt>.
   * 
   * @param o object to be checked for containment in this deque
   * @return <tt>true</tt> if this deque contains the specified element
   */
  public boolean contains(Object o) {
    if (o == null) {
      return false;
    }
    int mask = elements.length - 1;
    int i = head;
    E x;
    while ((x = elements[i]) != null) {
      if (o.equals(x)) {
        return true;
      }
      i = (i + 1) & mask;
    }
    return false;
  }

  /**
   * Removes all of the elements from this deque. The deque will be empty after
   * this call returns.
   */
  public void clear() {
    int h = head;
    int t = tail;
    if (h != t) { // clear all cells
      head = tail = 0;
      int i = h;
      int mask = elements.length - 1;
      do {
        elements[i] = null;
        i = (i + 1) & mask;
      } while (i != t);
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    ArrayQueue<Integer> queue = new ArrayQueue<Integer>();
    for (int n = 0; n < 1024 * 1024; n++) {
      queue.add(n);
    }
    System.out.println(queue.size());
  }

}
