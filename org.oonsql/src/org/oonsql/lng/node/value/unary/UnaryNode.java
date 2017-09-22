package org.oonsql.lng.node.value.unary;

import java.io.IOException;

import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Node de unary
 * 
 * @author Bernardo Breder
 */
public abstract class UnaryNode extends ValueNode {

  /** Node de left */
  protected ValueNode left;

  /**
   * @param left
   */
  public UnaryNode(ValueNode left) {
    this.left = left;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    this.left.write(output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return left.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UnaryNode other = (UnaryNode) obj;
    if (left == null) {
      if (other.left != null) {
        return false;
      }
    }
    else if (!left.equals(other.left)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.left.toString();
  }

}
