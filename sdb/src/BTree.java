import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class BTree<E> {

  protected int sequence = 1;

  protected final String name;

  protected final int order;

  protected BTreeNode<E> root;

  protected boolean changed;

  protected int count;

  protected final DbIO io;

  public BTree(DbIO io, String name, int order) throws IOException {
    this.io = io;
    this.name = name;
    this.order = order;
    if (io.exist(name)) {
      DataInputStream in =
        new DataInputStream(new ByteArrayInputStream(io.readStructure(name)));
      sequence = in.readInt();
      int rootId = in.readInt();
      order = in.readInt();
      this.root = read(rootId);
    }
  }

  protected BTreeNode<E> search(BTreeNode<E> node, long key) throws IOException {
    if (node == null) {
      return null;
    }
    int i = 0;
    while (i < node.length && key > node.keys[i]) {
      i++;
    }
    if (node.keys[i] == key) {
      return node;
    }
    if (node.leaf) {
      return null;
    }
    BTreeNode<E> child = nodeChild(node, i);
    return search(child, key);
  }

  public boolean has(long key) throws IOException {
    return root != null ? search(root, key) != null : false;
  }

  protected E get(long key) throws IOException {
    BTreeNode<E> node = root != null ? search(root, key) : null;
    if (node == null) {
      return null;
    }
    int i = 0;
    while (i < node.length && key != node.keys[i]) {
      i++;
    }
    if (key != node.keys[i]) {
      return null;
    }
    return node.values[i];
  }

  // protected void splitChild(BTreeNode<E> x, int i, BTreeNode<E> y)
  // throws IOException {
  // BTreeNode<E> z = new BTreeNode<E>(sequence++, order);
  // z.leaf = y.leaf;
  // z.length = order - 1;
  // for (int j = 0; j < order - 1; j++) {
  // z.keys[j] = y.keys[j + order];
  // z.values[j] = y.values[j + order];
  // }
  // if (!y.leaf) {
  // for (int j = 0; j < order; j++) {
  // z.childrenId[j] = y.childrenId[j + order];
  // z.childrenNode[j] = y.childrenNode[j + order];
  // }
  // }
  // y.length = order - 1;
  // for (int j = x.length; j > i + 1; i--) {
  // x.childrenId[j + 1] = x.childrenId[j];
  // x.childrenNode[j + 1] = x.childrenNode[j];
  // }
  // x.childrenId[i + 1] = z.id;
  // x.childrenNode[i + 1] = z;
  // for (int j = x.length - 1; j > i; i--) {
  // x.keys[j + 1] = x.keys[j];
  // x.values[j + 1] = x.values[j];
  // }
  // x.keys[i] = y.keys[order - 1];
  // x.values[i] = y.values[order - 1];
  // x.length++;
  // y.write(name);
  // z.write(name);
  // x.write(name);
  // }

  public void add(long key, E value) throws IOException {
    if (root == null) {
      root = new BTreeNode<E>(sequence++, order);
      root.id = ++sequence;
      root.leaf = true;
      root.keys[0] = key;
      root.values[0] = value;
      root.length = 1;
      root.changed = true;
    }
    else {
      if (root.length == 2 * order - 1) {
        BTreeNode<E> s = new BTreeNode<E>(sequence++, order);
        s.id = ++sequence;
        s.changed = true;
        s.childrenId[0] = root.id;
        s.childrenNode[0] = root;
        splitChild(s, 0, root);
        int i = 0;
        if (s.keys[0] < key) {
          i++;
        }
        insertNonFull(nodeChild(s, i), key, value);
        root = s;
      }
      else {
        insertNonFull(root, key, value);
      }
      count++;
      changed = true;
    }
  }

  protected void splitChild(BTreeNode<E> x, int index, BTreeNode<E> y) {
    BTreeNode<E> z = new BTreeNode<E>(sequence++, order);
    z.id = ++sequence;
    z.leaf = y.leaf;
    z.length = order - 1;
    for (int j = 0; j < order - 1; j++) {
      z.keys[j] = y.keys[j + order];
      z.values[j] = y.values[j + order];
    }
    if (!y.leaf) {
      for (int j = 0; j < order; j++) {
        z.childrenNode[j] = y.childrenNode[j + order];
        z.childrenId[j] = y.childrenId[j + order];
      }
    }
    int len = y.length;
    y.length = order - 1;
    for (int j = x.length; j >= index + 1; j--) {
      x.childrenNode[j + 1] = x.childrenNode[j];
      x.childrenId[j + 1] = x.childrenId[j];
    }
    x.childrenNode[index + 1] = z;
    x.childrenId[index + 1] = z.id;
    for (int j = x.length - 1; j >= index; j--) {
      x.keys[j + 1] = x.keys[j];
      x.values[j + 1] = x.values[j];
    }
    x.keys[index] = y.keys[order - 1];
    x.values[index] = y.values[order - 1];
    x.length++;
    for (int j = order - 1; j < len; j++) {
      y.keys[j] = 0;
      y.values[j] = null;
      y.childrenNode[j + 1] = null;
      y.childrenId[j + 1] = 0;
    }
    z.changed = true;
    y.changed = true;
    x.changed = true;
  }

  protected void insertNonFull(BTreeNode<E> x, long key, E value)
    throws IOException {
    int i = x.length - 1;
    if (x.leaf) {
      while (i >= 0 && x.keys[i] > key) {
        x.keys[i + 1] = x.keys[i];
        x.values[i + 1] = x.values[i];
        i--;
      }
      x.keys[i + 1] = key;
      x.values[i + 1] = value;
      x.length++;
      x.changed = true;
    }
    else {
      while (i >= 0 && x.keys[i] > key) {
        i--;
      }
      BTreeNode<E> i1 = nodeChild(x, i + 1);
      if (i1.length == 2 * order - 1) {
        splitChild(x, i + 1, i1);
        if (x.keys[i + 1] < key) {
          i++;
        }
      }
      BTreeNode<E> i2 = nodeChild(x, i + 1);
      insertNonFull(i2, key, value);
    }
  }

  public boolean remove(long key) throws IOException {
    if (!has(key)) {
      return false;
    }
    remove(root, key);
    if (root.length == 0) {
      if (root.leaf) {
        root = null;
        changed = true;
      }
      else {
        root = nodeChild(root, 0);
      }
    }
    count--;
    return true;
  }

  protected boolean remove(BTreeNode<E> node, long key) throws IOException {
    int idx = findKey(node, key);
    if (idx < node.length && node.keys[idx] == key) {
      if (node.leaf) {
        removeFromLeaf(node, idx);
      }
      else {
        removeFromNonLeaf(node, idx);
      }
    }
    else {
      if (node.leaf) {
        return false;
      }
      boolean flag = ((idx == node.length) ? true : false);
      BTreeNode<E> child = nodeChild(node, idx);
      if (child.length < order) {
        fill(node, idx);
      }
      if (flag && idx > node.length) {
        child = nodeChild(node, idx - 1);
      }
      else {
        child = nodeChild(node, idx);
      }
      remove(child, key);
    }
    return true;
  }

  protected void removeFromLeaf(BTreeNode<E> node, int idx) {
    for (int i = idx + 1; i < node.length; ++i) {
      node.keys[i - 1] = node.keys[i];
      node.values[i - 1] = node.values[i];
    }
    {
      node.length--;
      node.keys[node.length] = 0;
      node.values[node.length] = null;
    }
    node.changed = true;
  }

  protected boolean removeFromNonLeaf(BTreeNode<E> node, int idx)
    throws IOException {
    long k = node.keys[idx];
    if (node.childrenNode[idx].length >= order) {
      BTreeNode<E> predNode = getPred(node, idx);
      if (predNode == null) {
        return false;
      }
      node.keys[idx] = predNode.keys[predNode.length - 1];
      node.values[idx] = predNode.values[predNode.length - 1];
      node.changed = true;
      BTreeNode<E> child = nodeChild(node, idx);
      if (!remove(child, node.keys[idx])) {
        return false;
      }
    }
    else if (node.childrenNode[idx + 1].length >= order) {
      BTreeNode<E> succNode = getSucc(node, idx);
      if (succNode == null) {
        return false;
      }
      node.keys[idx] = succNode.keys[0];
      node.values[idx] = succNode.values[0];
      node.changed = true;
      BTreeNode<E> child = nodeChild(node, idx + 1);
      if (!remove(child, node.keys[idx])) {
        return false;
      }
    }
    else {
      if (!merge(node, idx)) {
        return false;
      }
      BTreeNode<E> child = nodeChild(node, idx);
      if (!remove(child, k)) {
        return false;
      }
    }
    return true;
  }

  protected BTreeNode<E> getPred(BTreeNode<E> node, int idx) throws IOException {
    BTreeNode<E> cur = nodeChild(node, idx);
    while (!cur.leaf) {
      cur = nodeChild(cur, cur.length);
    }
    return cur;
  }

  protected BTreeNode<E> getSucc(BTreeNode<E> node, int idx) throws IOException {
    BTreeNode<E> cur = nodeChild(node, idx + 1);
    while (!cur.leaf) {
      cur = nodeChild(cur, 0);
    }
    return cur;
  }

  protected boolean fill(BTreeNode<E> node, int idx) throws IOException {
    if (idx != 0) {
      BTreeNode<E> child = nodeChild(node, idx - 1);
      if (child.length >= order) {
        borrowFromPrev(node, idx);
        return true;
      }
    }
    else if (idx != node.length) {
      BTreeNode<E> child = nodeChild(node, idx + 1);
      if (child.length >= order) {
        borrowFromPrev(node, idx);
        return true;
      }
    }
    if (idx != node.length) {
      if (!merge(node, idx)) {
        return false;
      }
    }
    else {
      if (!merge(node, idx - 1)) {
        return false;
      }
    }
    return true;
  }

  protected void borrowFromPrev(BTreeNode<E> node, int idx) throws IOException {
    BTreeNode<E> child = nodeChild(node, idx);
    BTreeNode<E> sibling = nodeChild(node, idx - 1);
    for (int i = child.length - 1; i >= 0; --i) {
      child.keys[i + 1] = child.keys[i];
      child.values[i + 1] = child.values[i];
    }
    if (!child.leaf) {
      for (int i = child.length; i >= 0; --i) {
        child.childrenNode[i + 1] = child.childrenNode[i];
        child.childrenId[i + 1] = child.childrenId[i];
      }
    }
    child.keys[0] = node.keys[idx - 1];
    child.values[0] = node.values[idx - 1];
    if (!node.leaf) {
      child.childrenNode[0] = sibling.childrenNode[sibling.length];
      child.childrenId[0] = sibling.childrenId[sibling.length];
    }
    node.keys[idx - 1] = sibling.keys[sibling.length - 1];
    node.values[idx - 1] = sibling.values[sibling.length - 1];
    child.length++;
    {
      sibling.childrenNode[sibling.length] = null;
      sibling.childrenId[sibling.length] = 0;
      sibling.length--;
      sibling.values[sibling.length] = null;
      sibling.keys[sibling.length] = 0;
    }
    child.changed = true;
    sibling.changed = true;
    changed = true;
  }

  protected void borrowFromNext(BTreeNode<E> node, int idx) throws IOException {
    BTreeNode<E> child = nodeChild(node, idx);
    BTreeNode<E> sibling = nodeChild(node, idx + 1);
    child.keys[(child.length)] = node.keys[idx];
    child.values[(child.length)] = node.values[idx];
    if (!(child.leaf)) {
      child.childrenNode[(child.length) + 1] = sibling.childrenNode[0];
      child.childrenId[(child.length) + 1] = sibling.childrenId[0];
    }
    node.keys[idx] = sibling.keys[0];
    node.values[idx] = sibling.values[0];
    for (int i = 1; i < sibling.length; ++i) {
      sibling.keys[i - 1] = sibling.keys[i];
      sibling.values[i - 1] = sibling.values[i];
    }
    if (!sibling.leaf) {
      for (int i = 1; i <= sibling.length; ++i) {
        sibling.childrenNode[i - 1] = sibling.childrenNode[i];
        sibling.childrenId[i - 1] = sibling.childrenId[i];
      }
    }
    child.length++;
    {
      sibling.childrenNode[sibling.length] = null;
      sibling.childrenId[sibling.length] = 0;
      sibling.length--;
      sibling.values[sibling.length] = null;
      sibling.keys[sibling.length] = 0;
    }
    child.changed = true;
    sibling.changed = true;
    changed = true;
  }

  protected boolean merge(BTreeNode<E> node, int idx) throws IOException {
    BTreeNode<E> child = nodeChild(node, idx);
    BTreeNode<E> sibling = nodeChild(node, idx + 1);
    child.keys[order - 1] = node.keys[idx];
    child.values[order - 1] = node.values[idx];
    for (int i = 0; i < sibling.length; ++i) {
      child.keys[i + order] = sibling.keys[i];
      child.values[i + order] = sibling.values[i];
    }
    if (!child.leaf) {
      for (int i = 0; i <= sibling.length; ++i) {
        child.childrenNode[i + order] = sibling.childrenNode[i];
        child.childrenId[i + order] = sibling.childrenId[i];
      }
    }
    for (int i = idx + 1; i < node.length; ++i) {
      node.keys[i - 1] = node.keys[i];
      node.values[i - 1] = node.values[i];
    }
    for (int i = idx + 2; i <= node.length; ++i) {
      node.childrenNode[i - 1] = node.childrenNode[i];
      node.childrenId[i - 1] = node.childrenId[i];
    }
    child.length += sibling.length + 1;
    for (int n = 0; n < 2 * order; n++) {
      sibling.childrenId[n] = 0;
      sibling.childrenNode[n] = null;
    }
    {
      node.childrenNode[node.length] = null;
      node.childrenId[node.length] = 0;
      node.length--;
      node.keys[node.length] = 0;
      node.values[node.length] = null;
    }
    child.changed = true;
    changed = true;
    return true;
  }

  protected int findKey(BTreeNode<E> node, long key) {
    int idx = 0;
    while (idx < node.length && node.keys[idx] < key) {
      idx++;
    }
    return idx;
  }

  protected BTreeNode<E> nodeChild(BTreeNode<E> node, int index)
    throws IOException {
    if (node.childrenNode[index] != null) {
      return node.childrenNode[index];
    }
    if (node.childrenId[index] == 0) {
      return null;
    }
    return node.childrenNode[index] = read(node.childrenId[index]);

  }

  protected BTreeNode<E> read(long id) throws IOException {
    DataInputStream in =
      new DataInputStream(new ByteArrayInputStream(io.readData(name, id)));
    try {
      BTreeNode<E> node = new BTreeNode<E>(id, order);
      node.leaf = in.readBoolean();
      node.length = in.readInt();
      for (int n = 0; n < node.length; n++) {
        node.keys[n] = in.readLong();
        node.values[n] = (E) in.readUTF();
      }
      for (int n = 0; n < node.length + 1; n++) {
        node.childrenId[n] = in.readLong();
      }
      return node;
    }
    finally {
      in.close();
    }
  }

  public Iterator<E> iterator(long key) throws IOException {
    return new MyIterator(key);
  }

  public void write() throws IOException {
    DataOutputStream out =
      new DataOutputStream(new FileOutputStream(name + ".db"));
    try {
      out.writeInt(sequence);
      out.writeLong(root.id);
      out.writeInt(order);
    }
    finally {
      out.close();
    }
  }

  public static class BTreeNode<E> {

    private long id;

    public long[] keys;

    public E[] values;

    public long[] childrenId;

    public BTreeNode<E>[] childrenNode;

    public int length;

    public boolean leaf;

    public boolean changed;

    public BTreeNode(long id, int order) {
      this.id = id;
      this.keys = new long[2 * order];
      this.values = (E[]) new Object[2 * order];
      this.childrenId = new long[2 * order + 1];
      this.childrenNode = new BTreeNode[2 * order + 1];
    }

    protected BTreeNode<E> read(int i, String name, int order)
      throws IOException {
      BTreeNode<E> node = childrenNode[i];
      if (node != null) {
        return node;
      }
      long id = childrenId[i];
      DataInputStream in =
        new DataInputStream(new FileInputStream(name + "." + id + ".db"));
      try {
        node = new BTreeNode<E>(id, order);
        node.leaf = in.readBoolean();
        node.length = in.readInt();
        for (int n = 0; n < node.length; n++) {
          node.keys[n] = in.readInt();
          node.values[n] = (E) in.readUTF();
        }
        for (int n = 0; n < node.length + 1; n++) {
          node.childrenId[n] = in.readInt();
        }
      }
      finally {
        in.close();
      }
      childrenNode[i] = node;
      return node;
    }

    public void write(String name) throws IOException {
      DataOutputStream out =
        new DataOutputStream(new FileOutputStream(name + "." + id + ".db"));
      try {
        out.writeBoolean(leaf);
        out.writeInt(length);
        for (int n = 0; n < length; n++) {
          out.writeLong(keys[n]);
          out.writeUTF(values[n].toString());
        }
        for (int n = 0; n < length + 1; n++) {
          out.writeLong(childrenId[n]);
        }
      }
      finally {
        out.close();
      }
    }

    @Override
    public String toString() {
      return "[id=" + id + ", keys=" + Arrays.toString(keys) + "]";
    }

  }

  public class MyIterator implements Iterator<E> {

    /** Chave */
    protected final long key;
    /** Pais */
    protected int[] idxs;
    /** Pais */
    protected BTreeNode<E>[] parents;
    /** Indice da pilha */
    protected int stackIndex;
    /** Node atual */
    protected BTreeNode<E> node;
    /** Indice do node */
    protected int idx;

    public MyIterator(long key) {
      this.key = key;
    }

    protected BTreeNode<E> iterator(BTreeNode<E> node) {
      int i = findKey(node, key);
      if (i > node.length) {
        return null;
      }
      if (node.keys[i] == key) {
        return node;
      }
      if (node.leaf) {
        return null;
      }
      parents[stackIndex] = node;
      idxs[stackIndex] = i;
      stackIndex++;
      BTreeNode<E> child;
      try {
        child = nodeChild(node, i);
      }
      catch (IOException e) {
        return null;
      }
      return iterator(child);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
      try {
        if (node == null) {
          parents = new BTreeNode[65];
          idxs = new int[65];
          node = root != null ? iterator(root) : null;
          if (node == null) {
            return false;
          }
          idx = findKey(node, key);
          if (idx > node.length) {
            node = null;
            return false;
          }
          return true;
        }
        else {
          if (node.leaf) {
            while (idx >= node.length) {
              node = parents[stackIndex - 1];
              idx = idxs[stackIndex - 1];
              stackIndex--;
              parents[stackIndex] = null;
              idxs[stackIndex] = 0;
            }
            if (idx >= node.length) {
              return false;
            }
          }
          else {
            do {
              parents[stackIndex] = node;
              idxs[stackIndex] = idx;
              stackIndex++;
              node = nodeChild(node, idx);
              idx = 0;
            } while (!node.leaf);
          }
          return true;
        }
      }
      catch (IOException e) {
        return false;
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E next() {
      if (idx >= node.length) {
        return null;
      }
      E value = node.values[idx++];
      return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
      // TODO Auto-generated method stub

    }

  }

  public static interface DbIO {

    public byte[] readData(String name, long id) throws IOException;

    public byte[] readStructure(String name) throws IOException;

    public void writeData(String name, long id, byte[] bytes)
      throws IOException;

    public void writeStructure(String name, byte[] bytes) throws IOException;

    public boolean exist(String name) throws IOException;

  }

}
