package org.oonsql.lng.node.value.primitive;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de string
 * 
 * @author Bernardo Breder
 */
public class StringNode extends PrimitiveNode {

  /** Valor */
  private String value;

  /**
   * @param token
   */
  public StringNode(StringToken token) {
    this.value = token.value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    output.opStackString(value);
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new StringNode(new StringToken(input.readString()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(STRING_VALUE);
    output.writeString(this.value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return value.hashCode();
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
    StringNode other = (StringNode) obj;
    if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
