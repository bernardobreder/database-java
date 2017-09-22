package org.oonsql.lng.node.value.binary;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.StreamOpcodeInputStream;

/**
 * Node de assign
 * 
 * @author Bernardo Breder
 */
public class AssignNode extends BinaryNode {

  /**
   * Construtor
   * 
   * @param left
   * @param right
   */
  public AssignNode(ValueNode left, ValueNode right) {
    super(left, right);
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(StreamOpcodeInputStream input)
    throws IOException {
    return (E) new AssignNode(AbstractNode.<ValueNode> read(input),
      AbstractNode.<ValueNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(ASSIGN_VALUE);
    super.write(output);
  }

}
