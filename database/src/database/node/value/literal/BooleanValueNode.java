package database.node.value.literal;

import java.io.IOException;

import database.lexical.Token;
import database.node.ContextNode;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class BooleanValueNode extends LiteralValueNode {

  /** Token */
  public Token token;
  /** Valor */
  public boolean value;

  /**
   * @param token
   * @param value
   */
  public BooleanValueNode(Token token, boolean value) {
    this.token = token;
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void head(ContextNode context) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void body(ContextNode context) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(DbOpcodeOutputStream output) throws IOException {
    output.writeLoadBoolean(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return token;
  }

}
