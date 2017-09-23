package database.node.select;

import java.io.IOException;

import database.lexical.Token;
import database.node.ContextNode;
import database.node.Node;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class SelectOrderNode extends Node {

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Table extends SelectOrderNode {

    /** Table */
    public final Token name;

    /**
     * @param name
     */
    public Table(Token name) {
      super();
      this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
      return name;
    }

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
  }

}
