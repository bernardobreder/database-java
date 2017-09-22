package org.oonsql.lng.node.value.unary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de inverter
 * 
 * @author Bernardo Breder
 */
public class InvertNode extends UnaryNode {

  /**
   * @param left
   */
  public InvertNode(ValueNode left) {
    super(left);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    output.opBooleanNot();
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new InvertNode(AbstractNode.<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(INVERT_VALUE);
    super.write(output);
  }

}
