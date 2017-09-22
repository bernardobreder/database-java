package org.oonsql.lng.node.value.binary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;
import org.oonsql.lng.util.StreamOpcodeInputStream;

/**
 * Node de Le
 * 
 * @author Bernardo Breder
 */
public class LeNode extends BinaryNode {

  /**
   * Construtor
   * 
   * @param left
   * @param right
   */
  public LeNode(ValueNode left, ValueNode right) {
    super(left, right);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    this.right.build(output);
    output.opDoubleLowerEqual();
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(StreamOpcodeInputStream input)
    throws IOException {
    return (E) new LeNode(AbstractNode.<ValueNode> read(input), AbstractNode
      .<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(LE_VALUE);
    super.write(output);
  }

}
