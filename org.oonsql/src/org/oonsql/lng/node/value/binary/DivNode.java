package org.oonsql.lng.node.value.binary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de divisao
 * 
 * @author Bernardo Breder
 */
public class DivNode extends BinaryNode {

  /**
   * Construtor
   * 
   * @param left
   * @param right
   */
  public DivNode(ValueNode left, ValueNode right) {
    super(left, right);
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new DivNode(AbstractNode.<ValueNode> read(input), AbstractNode
      .<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    this.right.build(output);
    output.opDoubleDiv();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(DIV_VALUE);
    super.write(output);
  }

}
