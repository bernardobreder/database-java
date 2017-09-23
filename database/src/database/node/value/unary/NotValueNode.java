package database.node.value.unary;

import java.io.IOException;

import database.lexical.Token;
import database.node.ContextNode;
import database.node.value.ValueNode;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class NotValueNode extends UnaryValueNode {

  /** Valor */
  private ValueNode value;

  /**
   * @param value
   */
  public NotValueNode(ValueNode value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void head(ContextNode context) {
    this.value.head(context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void body(ContextNode context) {
    this.value.body(context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(DbOpcodeOutputStream output) throws IOException {
    this.value.build(output);
    output.writeBoolNot();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return value.getToken().join("!");
  }

}
