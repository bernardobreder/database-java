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
public abstract class SelectFromNode extends Node {

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Table extends SelectFromNode {

    /** Table */
    public Token alias;
    /** Table */
    public Token table;

    /**
     * {@inheritDoc}
     */
    @Override
    public void head(ContextNode context) {
      context.addTable(alias.getWord(), table.getWord());
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
    public void buildTableOpen(DbOpcodeOutputStream output) throws IOException {
      output.writeTableOpen(table.getWord());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildTableClose(DbOpcodeOutputStream output) throws IOException {
      output.writeTableClose();
    }

    /**
     * @param alias
     * @param table
     */
    public Table(Token alias, Token table) {
      super();
      this.alias = alias;
      this.table = table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
      return alias;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(DbOpcodeOutputStream output) throws IOException {
  }

  /**
   * @param output
   * @throws IOException
   */
  public abstract void buildTableOpen(DbOpcodeOutputStream output)
    throws IOException;

  /**
   * @param output
   * @throws IOException
   */
  public abstract void buildTableClose(DbOpcodeOutputStream output)
    throws IOException;

}
