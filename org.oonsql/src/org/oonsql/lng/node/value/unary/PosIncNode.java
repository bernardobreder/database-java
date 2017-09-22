package org.oonsql.lng.node.value.unary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Node de PosInc
 * 
 * @author Bernardo Breder
 */
public class PosIncNode extends UnaryNode {

  /**
   * @param left
   */
  public PosIncNode(ValueNode left) {
    super(left);
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new PosIncNode(AbstractNode.<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(POS_INC_VALUE);
    super.write(output);
  }

}
