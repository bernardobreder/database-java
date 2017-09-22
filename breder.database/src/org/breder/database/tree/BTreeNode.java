package org.breder.database.tree;

// Introduced in Chapter 17
import java.io.Serializable;
import java.util.List;

/** Node in a BTree. */
public class BTreeNode implements Serializable {

  /** Items stored in this node. */
  public List<Integer> data;

  /** Ids of children of this node. */
  public List<Integer> children;

  /** Number identifying this node. */
  public int id;

  public BTreeNode() {
  }

  /**
   * The new node has no data or children yet. The argument leaf specifies
   * whether it is a leaf.
   */
  public BTreeNode(boolean leaf) {
    this.id = IdGenerator.nextId();
    data = new java.util.ArrayList<Integer>((BTree.HALF_MAX * 2) - 1);
    if (!leaf) {
      children = new java.util.ArrayList<Integer>(BTree.HALF_MAX * 2);
    }
  }

  /**
   * Create a new node that has two children, each containing half of the items
   * from child. Write the children to disk.
   */
  public BTreeNode(BTreeNode child) {
    this(false);
    children.add(child.id);
    BTree.splitChild(this, 0, child);
  }

}