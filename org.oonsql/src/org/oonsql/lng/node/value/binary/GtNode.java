package org.oonsql.lng.node.value.binary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;
import org.oonsql.lng.util.StreamOpcodeInputStream;

/**
 * Node de Gt
 * 
 * @author Bernardo Breder
 */
public class GtNode extends BinaryNode {

  /**
   * Construtor
   * 
   * @param left
   * @param right
   */
  public GtNode(ValueNode left, ValueNode right) {
    super(left, right);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    this.right.build(output);
    output.opDoubleGreaterThan();
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(StreamOpcodeInputStream input)
    throws IOException {
    return (E) new GtNode(AbstractNode.<ValueNode> read(input), AbstractNode
      .<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(GT_VALUE);
    super.write(output);
  }

}
