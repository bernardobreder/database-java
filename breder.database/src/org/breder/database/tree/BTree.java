package org.breder.database.tree;

// Introduced in Chapter 17
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/** BTree storing many ints on disk. */
public class BTree implements Serializable {

  /** Directory where files are stored. */
  public static final String DIR = "data/";
  /** Minimum number of children. Max is twice this. */
  public static final int HALF_MAX = 10;

  static {
    new File(DIR).mkdirs();
  }

  /** Id number of the root node. */
  private int rootId;

  /** A new BTree is initially empty. */
  public BTree() {
    BTreeNode root = new BTreeNode(true);
    rootId = root.id;
    writeToDisk(root);
    writeToDisk();
  }

  /**
   * Add target to this BTree and write any modified nodes to disk.
   * 
   * @param target
   */
  public void add(int target) {
    BTreeNode root = readFromDisk(rootId);
    if (isFull(root)) {
      BTreeNode parent = new BTreeNode(root);
      rootId = parent.id;
      writeToDisk();
      add(parent, target);
    }
    else {
      add(root, target);
    }
  }

  /**
   * Return true if this BTree contains target.
   * 
   * @param target
   * @return
   */
  public boolean contains(int target) {
    BTreeNode node = readFromDisk(rootId);
    while (node != null) {
      double d = indexOf(node, target);
      int i = (int) d;
      if (i == d) {
        return true;
      }
      else {
        node = getChild(node, i);
      }
    }
    return false;
  }

  /**
   * Read a previously saved BTree from disk.
   * 
   * @return
   */
  public static BTree readFromDisk() {
    try {
      ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(DIR + "btree"));
      return (BTree) (in.readObject());
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  /**
   * Remove target from this BTree.
   * 
   * @param target
   */
  public void remove(int target) {
    BTreeNode root = readFromDisk(rootId);
    remove(root, target);
    if ((size(root) == 1) && (!(isLeaf(root)))) {
      BTreeNode child = getChild(root, 0);
      deleteFromDisk(root);
      rootId = child.id;
      writeToDisk();
    }
  }

  /** Write this BTree to disk. */
  public void writeToDisk() {
    try {
      ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(DIR + "btree"));
      out.writeObject(this);
      out.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  ////////////////////////////////

  /**
   * Add target to the subtree rooted at this node. Write nodes to disk as
   * necessary.
   */
  public static void add(BTreeNode nthis, int target) {
    BTreeNode node = nthis;
    while (!(isLeaf(node))) {
      double d = indexOf(node, target);
      int i = (int) d;
      if (i == d) {
        return;
      }
      else {
        BTreeNode child = getChild(node, i);
        if (isFull(child)) {
          splitChild(node, i, child);
        }
        else {
          writeToDisk(node);
          node = child;
        }
      }
    }
    addLocally(node, target);
    writeToDisk(node);
  }

  /**
   * Add target to this node, which is assumed to not be full. Make room for an
   * extra child to the right of target.
   */
  protected static void addLocally(BTreeNode node, int target) {
    double d = indexOf(node, target);
    int i = (int) d;
    if (i != d) {
      node.data.add(i, target);
      if (!isLeaf(node)) {
        node.children.add(i + 1, 0);
      }
    }
  }

  /**
   * Create and return a new node which will be a right sibling of this one.
   * Half of the items and children in this node are copied to the new one.
   */
  protected static BTreeNode createRightSibling(BTreeNode node) {
    BTreeNode sibling = new BTreeNode(isLeaf(node));
    for (int i = HALF_MAX; i < (HALF_MAX * 2) - 1; i++) {
      sibling.data.add(node.data.remove(HALF_MAX));
    }
    if (!isLeaf(node)) {
      for (int i = HALF_MAX; i < HALF_MAX * 2; i++) {
        sibling.children.add(node.children.remove(HALF_MAX));
      }
    }
    writeToDisk(sibling);
    return sibling;
  }

  /**
   * Read the ith child of this node from the disk and return it. If this node
   * is a leaf, return null.
   */
  public static BTreeNode getChild(BTreeNode node, int index) {
    if (isLeaf(node)) {
      return null;
    }
    else {
      return readFromDisk(node.children.get(index));
    }
  }

  /**
   * Return the index of target in this node if present. Otherwise, return the
   * index of the child that would contain target, plus 0.5.
   */
  public static double indexOf(BTreeNode node, int target) {
    for (int i = 0; i < node.data.size(); i++) {
      if (node.data.get(i) == target) {
        return i;
      }
      if (node.data.get(i) > target) {
        return i + 0.5;
      }
    }
    return size(node) - 0.5;
  }

  /** Return true if this node is full. */
  public static boolean isFull(BTreeNode node) {
    return size(node) == HALF_MAX * 2;
  }

  /** Return true if this node is a leaf. */
  public static boolean isLeaf(BTreeNode node) {
    return node.children == null;
  }

  /** Return true if this node is minimal. */
  public static boolean isMinimal(BTreeNode node) {
    return size(node) == HALF_MAX;
  }

  /**
   * Merge this node's ith and (i+1)th children (child and sibling, both
   * minimal), moving the ith item down from this node. Delete sibling from
   * disk.
   */
  protected static void mergeChildren(BTreeNode node, int i, BTreeNode child,
    BTreeNode sibling) {
    child.data.add(node.data.remove(i));
    node.children.remove(i + 1);
    if (!(isLeaf(child))) {
      child.children.add(sibling.children.remove(0));
    }
    for (int j = 0; j < HALF_MAX - 1; j++) {
      child.data.add(sibling.data.remove(0));
      if (!(isLeaf(child))) {
        child.children.add(sibling.children.remove(0));
      }
    }
    deleteFromDisk(sibling);
  }

  /**
   * Remove target from the subtree rooted at this node. Write any modified
   * nodes to disk.
   */
  public static void remove(BTreeNode node, int target) {
    double d = indexOf(node, target);
    int i = (int) d;
    if (isLeaf(node)) {
      if (i == d) {
        node.data.remove(i);
        writeToDisk(node);
      }
    }
    else if (i == d) {
      removeFromInternalNode(node, i, target);
    }
    else {
      removeFromChild(node, i, target);
    }
  }

  /**
   * Remove target from the subtree rooted at child i of this node. Write any
   * modified nodes to disk.
   */
  protected static void removeFromChild(BTreeNode node, int i, int target) {
    BTreeNode child = getChild(node, i);
    if (isMinimal(child)) {
      if (i == 0) { // Target in first child
        BTreeNode sibling = getChild(node, 1);
        if (isMinimal(sibling)) {
          mergeChildren(node, i, child, sibling);
        }
        else {
          rotateLeft(node, i, child, sibling);
        }
      }
      else if (i == size(node) - 1) { // Target in last child
        BTreeNode sibling = getChild(node, i - 1);
        if (isMinimal(sibling)) {
          mergeChildren(node, i - 1, sibling, child);
          child = sibling;
        }
        else {
          rotateRight(node, i - 1, sibling, child);
        }
      }
      else { // Target in middle child
        BTreeNode rightSibling = getChild(node, i + 1);
        BTreeNode leftSibling = getChild(node, i - 1);
        if (!(isMinimal(rightSibling))) {
          rotateLeft(node, i, child, rightSibling);
        }
        else if (!(isMinimal(leftSibling))) {
          rotateRight(node, i - 1, leftSibling, child);
        }
        else {
          mergeChildren(node, i, child, rightSibling);
        }
      }
    }
    writeToDisk(node);
    remove(child, target);
  }

  /**
   * Remove the ith item (target) from this node. Write any modified nodes to
   * disk.
   */
  protected static void removeFromInternalNode(BTreeNode node, int i, int target) {
    BTreeNode child = getChild(node, i);
    BTreeNode sibling = getChild(node, i + 1);
    if (!(isMinimal(child))) {
      node.data.set(i, removeRightmost(child));
      writeToDisk(node);
    }
    else if (!(isMinimal(sibling))) {
      node.data.set(i, removeLeftmost(sibling));
      writeToDisk(node);
    }
    else {
      mergeChildren(node, i, child, sibling);
      writeToDisk(node);
      remove(child, target);
    }
  }

  /**
   * Remove and return the leftmost element in the leftmost descendant of this
   * node. Write any modified nodes to disk.
   */
  protected static int removeLeftmost(BTreeNode nthis) {
    BTreeNode node = nthis;
    while (!(isLeaf(node))) {
      BTreeNode child = getChild(node, 0);
      if (isMinimal(child)) {
        BTreeNode sibling = getChild(node, 1);
        if (isMinimal(sibling)) {
          mergeChildren(node, 0, child, sibling);
        }
        else {
          rotateLeft(node, 0, child, sibling);
        }
      }
      writeToDisk(node);
      return removeLeftmost(child);
    }
    int result = node.data.remove(0);
    writeToDisk(node);
    return result;
  }

  /**
   * Remove and return the rightmost element in the rightmost descendant of this
   * node. Write any modified nodes to disk.
   */
  protected static int removeRightmost(BTreeNode nthis) {
    BTreeNode node = nthis;
    while (!(isLeaf(node))) {
      BTreeNode child = getChild(node, size(nthis) - 1);
      if (isMinimal(child)) {
        BTreeNode sibling = getChild(node, size(nthis) - 2);
        if (isMinimal(sibling)) {
          mergeChildren(node, size(nthis) - 2, sibling, child);
          child = sibling;
        }
        else {
          rotateRight(node, size(nthis) - 2, sibling, child);
        }
      }
      writeToDisk(node);
      return removeRightmost(child);
    }
    int result = node.data.remove(size(nthis) - 2);
    writeToDisk(node);
    return result;
  }

  /**
   * Child is the ith child of this node, sibling the (i+1)th. Move one item
   * from sibling up into this node, one from this node down into child. Pass
   * one child from sibling to node. Write sibling to disk.
   */
  protected static void rotateLeft(BTreeNode node, int i, BTreeNode child,
    BTreeNode sibling) {
    child.data.add(node.data.get(i));
    if (!(isLeaf(child))) {
      child.children.add(sibling.children.remove(0));
    }
    node.data.set(i, sibling.data.remove(0));
    writeToDisk(sibling);
  }

  /**
   * Sibling is the ith child of this node, child the (i+1)th. Move one item
   * from sibling up into this node, one from this node down into child. Pass
   * one child from sibling to node. Write sibling to disk.
   */
  protected static void rotateRight(BTreeNode node, int i, BTreeNode sibling,
    BTreeNode child) {
    child.data.add(0, node.data.get(i));
    if (!(isLeaf(child))) {
      child.children.add(0, sibling.children.remove(size(sibling) - 1));
    }
    node.data.set(i, sibling.data.remove(size(sibling) - 2));
    writeToDisk(sibling);
  }

  /** Make this node a leaf if value is true, not a leaf otherwise. */
  public static void setLeaf(BTreeNode node, boolean value) {
    if (value) {
      node.children = null;
    }
    else {
      node.children = new java.util.ArrayList<Integer>(HALF_MAX * 2);
    }
  }

  /** Return one plus the number of items in this node. */
  public static int size(BTreeNode node) {
    return node.data.size() + 1;
  }

  /**
   * Split child, which is the full ith child of this node, into two minimal
   * nodes, moving the middle item up into this node.
   */
  protected static void splitChild(BTreeNode node, int i, BTreeNode child) {
    BTreeNode sibling = createRightSibling(child);
    addLocally(node, child.data.remove(HALF_MAX - 1));
    writeToDisk(child);
    node.children.set(i + 1, sibling.id);
  }

  /** Read from disk and return the node with the specified id. */
  public static BTreeNode readFromDisk(int id) {
    try {
      ObjectInputStream in =
        new ObjectInputStream(new FileInputStream(BTree.DIR + "b" + id
          + ".node"));
      BTreeNode node = new BTreeNode();
      readObject(node, in);
      in.close();
      return node;
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  /** Write this node to disk. */
  public static void writeToDisk(BTreeNode node) {
    try {
      ObjectOutputStream out =
        new ObjectOutputStream(new FileOutputStream(BTree.DIR + "b" + node.id
          + ".node"));
      writeObject(node, out);
      out.close();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /** Delete the file containing this node from the disk. */
  public static void deleteFromDisk(BTreeNode node) {
    try {
      File file = new File(BTree.DIR + "b" + node.id + ".node");
      file.delete();
    }
    catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void writeObject(BTreeNode node, ObjectOutputStream output)
    throws IOException {
    output.writeBoolean(isLeaf(node));
    output.writeInt(node.id);
    output.writeInt(node.data.size());
    for (int n = 0; n < node.data.size(); n++) {
      output.writeInt(node.data.get(n));
    }
    if (!isLeaf(node)) {
      output.writeInt(node.children.size());
      for (int n = 0; n < node.children.size(); n++) {
        output.writeInt(node.children.get(n));
      }
    }
  }

  private static void readObject(BTreeNode node, ObjectInputStream input)
    throws IOException {
    boolean leaf = input.readBoolean();
    node.id = input.readInt();
    int dataSize = input.readInt();
    node.data = new ArrayList<Integer>(dataSize);
    for (int n = 0; n < dataSize; n++) {
      node.data.add(input.readInt());
    }
    if (!leaf) {
      int childrenSize = input.readInt();
      node.children = new ArrayList<Integer>(childrenSize);
      for (int n = 0; n < childrenSize; n++) {
        node.children.add(input.readInt());
      }
    }
  }

}