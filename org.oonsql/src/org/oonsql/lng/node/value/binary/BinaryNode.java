package org.oonsql.lng.node.value.binary;

import java.io.IOException;

import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.node.value.unary.UnaryNode;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Node de binary
 * 
 * @author Bernardo Breder
 */
public abstract class BinaryNode extends UnaryNode {

  /** Node de right */
  protected ValueNode right;

  /**
   * @param left
   * @param right
   */
  public BinaryNode(ValueNode left, ValueNode right) {
    super(left);
    this.right = right;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    super.write(output);
    this.right.write(output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * super.hashCode() + right.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    BinaryNode other = (BinaryNode) obj;
    if (!right.equals(other.right)) {
      return false;
    }
    return true;
  }

}
