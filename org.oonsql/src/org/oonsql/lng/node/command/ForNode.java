package org.oonsql.lng.node.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.util.AbstractDataInputStream;
import org.oonsql.lng.util.AbstractDataOutputStream;

/**
 * Node de for
 * 
 * @author Bernardo Breder
 */
public class ForNode extends CommandNode {

  /** Inicialização */
  private final List<CommandNode> initValues;
  /** Condição */
  private final ValueNode condValue;
  /** Valor pos inicialização */
  private final List<CommandNode> posValues;
  /** Comando */
  private final CommandNode command;

  /**
   * @param initValues
   * @param condValue
   * @param posValues
   * @param command
   */
  public ForNode(List<CommandNode> initValues, ValueNode condValue,
    List<CommandNode> posValues, CommandNode command) {
    this.initValues = initValues;
    this.condValue = condValue;
    this.posValues = posValues;
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
    int initValueSize = input.readIndex();
    List<CommandNode> initValues = new ArrayList<CommandNode>();
    for (int n = 0; n < initValueSize; n++) {
      initValues.add(AbstractNode.<CommandNode> read(input));
    }
    ValueNode condValue = AbstractNode.<ValueNode> read(input);
    int posValueSize = input.readIndex();
    List<CommandNode> posValues = new ArrayList<CommandNode>();
    for (int n = 0; n < posValueSize; n++) {
      posValues.add(AbstractNode.<CommandNode> read(input));
    }
    CommandNode command = AbstractNode.<CommandNode> read(input);
    return (E) new ForNode(initValues, condValue, posValues, command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(FOR_CMD);
    output.writeIndex(this.initValues.size());
    for (int n = 0; n < this.initValues.size(); n++) {
      this.initValues.get(n).write(output);
    }
    this.condValue.write(output);
    output.writeIndex(this.posValues.size());
    for (int n = 0; n < this.posValues.size(); n++) {
      this.posValues.get(n).write(output);
    }
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
    result = prime * result + initValues.hashCode();
    result = prime * result + posValues.hashCode();
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
    ForNode other = (ForNode) obj;
    if (!command.equals(other.command)) {
      return false;
    }
    if (!condValue.equals(other.condValue)) {
      return false;
    }
    if (!initValues.equals(other.initValues)) {
      return false;
    }
    if (!posValues.equals(other.posValues)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "for " + this.initValues + " ; " + this.condValue + " ; "
      + this.posValues + " " + this.command;
  }

}
