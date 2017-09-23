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
public class StringValueNode extends LiteralValueNode {

  /** Token */
  public Token token;

  /**
   * @param token
   */
  public StringValueNode(Token token) {
    this.token = token;
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
    output.writeLoadString(token.getWord());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return token;
  }

}
