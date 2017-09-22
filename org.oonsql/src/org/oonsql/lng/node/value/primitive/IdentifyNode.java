package org.oonsql.lng.node.value.primitive;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.token.IdentifyToken;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Construtor
 * 
 * @author Bernardo Breder
 */
public class IdentifyNode extends PrimitiveNode {

  /** Node de Id */
  private IdentifyToken token;

  /**
   * @param token
   */
  public IdentifyNode(IdentifyToken token) {
    this.token = token;
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new IdentifyNode(new IdentifyToken(input.readString()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(IDENTIFY_VALUE);
    output.writeString(this.token.lexeme);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return token.hashCode();
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
    IdentifyNode other = (IdentifyNode) obj;
    if (token == null) {
      if (other.token != null) {
        return false;
      }
    }
    else if (!token.equals(other.token)) {
      return false;
    }
    return true;
  }

}
