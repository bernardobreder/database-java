package database.node;

import java.io.IOException;

import database.lexical.Token;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class Node {

  /**
   * @return token
   */
  public abstract Token getToken();

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.getToken().getWord();
  }

  /**
   * @param context
   */
  public abstract void head(ContextNode context);

  /**
   * @param context
   */
  public abstract void body(ContextNode context);

  /**
   * @param output
   * @throws IOException
   */
  public abstract void build(DbOpcodeOutputStream output) throws IOException;

}
