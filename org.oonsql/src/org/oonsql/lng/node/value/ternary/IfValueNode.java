package org.oonsql.lng.node.value.ternary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de Ternary If
 * 
 * @author Bernardo Breder
 */
public class IfValueNode extends TernaryNode {

  /**
   * @param left
   * @param right
   * @param center
   */
  public IfValueNode(ValueNode left, ValueNode right, ValueNode center) {
    super(left, right, center);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    this.center.build(output);
    this.right.build(output);
    output.opStackTernary();
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new IfValueNode(AbstractNode.<ValueNode> read(input),
      AbstractNode.<ValueNode> read(input), AbstractNode
        .<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(IF_VALUE);
    super.write(output);
  }

}
