package org.oonsql.lng.node.value.primitive;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de number
 * 
 * @author Bernardo Breder
 */
public class NumberNode extends PrimitiveNode {

  /** Valor */
  private double value;

  /**
   * @param token
   */
  public NumberNode(NumberToken token) {
    this.value = token.value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    output.opStackDouble(value);
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new NumberNode(new NumberToken(input.readDouble()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(NUMBER_VALUE);
    output.writeDouble(this.value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
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
    NumberNode other = (NumberNode) obj;
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
      return false;
    }
    return true;
  }

}
