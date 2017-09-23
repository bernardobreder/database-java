package database.util;

import java.util.ArrayList;

/**
 * Pilha
 * 
 * @author Tecgraf
 * @param <E>
 */
public class StackArrayList<E> extends ArrayList<E> {

  /**
   * 
   */
  public StackArrayList() {
    super();
  }

  /**
   * @param initialCapacity
   */
  public StackArrayList(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * Empilha
   * 
   * @param e
   */
  public void push(E e) {
    this.add(e);
  }

  /**
   * Desempilha
   */
  public void pop() {
    this.remove(this.size() - 1);
  }

  /**
   * @return elemento do topo
   */
  public E peek() {
    return this.get(this.size() - 1);
  }

}
