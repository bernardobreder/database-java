package org.oonsql.lng.node.value.ternary;

import java.io.IOException;

import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.node.value.binary.BinaryNode;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Ternary
 * 
 * @author Bernardo Breder
 */
public abstract class TernaryNode extends BinaryNode {

  /** Node ternary */
  protected ValueNode center;

  /**
   * @param left
   * @param right
   * @param center
   */
  public TernaryNode(ValueNode left, ValueNode right, ValueNode center) {
    super(left, right);
    this.center = center;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    super.write(output);
    this.center.write(output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * super.hashCode() + center.hashCode();
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
    TernaryNode other = (TernaryNode) obj;
    if (!center.equals(other.center)) {
      return false;
    }
    return true;
  }

}
