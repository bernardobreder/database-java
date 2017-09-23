package database.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class BTree {

  private static final int t = 2;
  private Model model;
  private Node root;

  public BTree(Model model) throws IOException {
    this.model = model;
    Node x = allocNode();
    x.leaf = true;
    x.len = 0;
    this.write(x);
    this.root = x;
  }

  /**
   * @return
   */
  public Node allocNode() {
    int seq = this.model.sequence();
    Node x = new Node();
    return x;
  }

  private void write(Node x) throws IOException {
    OutputStream out = model.getOutputStream(x.id);
    try {
      ObjectOutputStream o = new ObjectOutputStream(out);
      o.writeObject(x);
    }
    finally {
      out.close();
    }
  }

  public Object find(int key) throws IOException {
    return find(root, key);
  }

  protected Object find(Node node, int key) throws IOException {
    int i = 0;
    while (i < node.len && key > node.keys[i]) {
      i++;
    }
    if (i < node.len && key == node.keys[i]) {
      return key;
    }
    if (node.leaf) {
      return -1;
    }
    else {
      Node child = read(node.child[i]);
      return find(child, key);
    }
  }

  public void add(int key, Object value) throws IOException {
    Node r = this.root;
    if (r.len == 2 * t - 1) {
      Node s = this.allocNode();
      this.root = s;
      s.leaf = false;
      s.len = 0;
      s.child[0] = r.id;
      splitNode(s, 0, r);
      addNotFull(s, key, value);
    }
    else {
      addNotFull(r, key, value);
    }
  }

  private void addNotFull(Node x, int k, Object v) throws IOException {
    int i = x.len - 1;
    if (x.leaf) {
      while (i >= 0 && k < x.keys[i]) {
        i--;
      }
      System.arraycopy(x.keys, i + 1, x.keys, i + 2, x.len - i - 1);
      x.keys[i + 1] = k;
      x.len++;
      write(x);
    }
    else {
      while (i >= 0 && k < x.keys[i]) {
        i--;
      }
      i++;
      Node z = read(x.child[i]);
      if (z.len == 2 * t - 1) {
        splitNode(x, i, z);
        if (k > x.keys[i]) {
          i++;
        }
      }
      addNotFull(z, k, v);
    }
  }

  public void splitNode(Node x, int i, Node y) throws IOException {
    Node z = this.allocNode();
    z.leaf = y.leaf;
    z.len = t - 1;
    for (int j = 0; j <= t - 1; j++) {
      z.keys[j] = y.keys[j + t];
    }
    if (!y.leaf) {
      for (int j = 0; j <= t; j++) {
        z.child[j] = y.child[j + t];
      }
    }
    y.len = t - 1;
    for (int j = x.len; j >= i; j--) {
      x.child[j + 1] = x.child[j];
    }
    x.child[i + 1] = z.id;
    for (int j = x.len - 1; j >= i; i--) {
      x.keys[j + 1] = x.keys[j];
    }
    x.keys[i] = y.keys[t];
    x.len++;
    write(y);
    write(z);
    write(x);
  }

  private Node read(int sequence) throws IOException {
    InputStream in = model.getInputStream(sequence);
    try {
      ObjectInputStream o = new ObjectInputStream(in);
      return (Node) o.readObject();
    }
    catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
    finally {
      in.close();
    }
  }

  public static class Node implements Serializable {

    public int id;

    private int len;

    private boolean leaf;

    private int[] keys = new int[2 * t];
    private int[] child = new int[2 * t + 1];

  }

  public static class Entry {
    Node node;
    int index;
  }

  public static interface Model {

    public InputStream getInputStream(int sequence) throws IOException;

    public OutputStream getOutputStream(int sequence) throws IOException;

    public int sequence();

  }

}
