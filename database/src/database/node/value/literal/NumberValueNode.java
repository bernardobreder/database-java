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
public class NumberValueNode extends LiteralValueNode {

  /** Token */
  public Token token;
  /** Valor */
  public int value;

  /**
   * @param token
   */
  public NumberValueNode(Token token) {
    this.token = token;
    this.value = Integer.valueOf(token.getWord());
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
    output.writeLoadInt(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return token;
  }

}
