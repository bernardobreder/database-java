package org.oonsql.lng.node.command;

import java.io.IOException;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Node de repeat
 * 
 * @author Bernardo Breder
 */
public class RepeatNode extends CommandNode {

  /** Node de condição */
  private final ValueNode condValue;
  /** Node de comando */
  private final CommandNode command;

  /**
   * @param condValue
   * @param command
   */
  public RepeatNode(ValueNode condValue, CommandNode command) {
    this.condValue = condValue;
    this.command = command;
  }

  /**
   * @param input
   * @return node
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractNode> E read(AbstractDataInputStream input)
    throws IOException {
    return (E) new WhileNode(AbstractNode.<ValueNode> read(input), AbstractNode
      .<CommandNode> read(input));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(REPEAT_CMD);
    this.condValue.write(output);
    this.command.write(output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + command.hashCode();
    result = prime * result + condValue.hashCode();
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
    RepeatNode other = (RepeatNode) obj;
    if (!command.equals(other.command)) {
      return false;
    }
    if (!condValue.equals(other.condValue)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "repeat " + this.command + " " + this.condValue;
  }

}
