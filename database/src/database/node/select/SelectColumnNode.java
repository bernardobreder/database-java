package database.node.select;

import java.io.IOException;
import java.util.List;

import database.lexical.Token;
import database.node.ContextNode;
import database.node.Node;
import database.node.value.ValueNode;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class SelectColumnNode extends Node {

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class All extends SelectColumnNode {

    /** Token */
    private Token token;

    /**
     * @param token
     */
    public All(Token token) {
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
      output.send();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
      return token;
    }

  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class ColumnSet extends SelectColumnNode {

    /** Columns */
    public final List<ValueNode> columns;

    /**
     * @param columns
     */
    public ColumnSet(List<ValueNode> columns) {
      super();
      this.columns = columns;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void head(ContextNode context) {
      for (ValueNode column : columns) {
        column.head(context);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body(ContextNode context) {
      for (ValueNode column : columns) {
        column.body(context);
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(DbOpcodeOutputStream output) throws IOException {
      for (ValueNode column : columns) {
        column.build(output);
      }
      output.writeJoin(columns.size());
      output.send();
      output.writeStackPop(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken() {
      Token token = this.columns.get(0).getToken();
      for (int n = 1; n < this.columns.size(); n++) {
        token = token.join(", ");
        token = token.join(this.columns.get(n).toString());
      }
      return token;
    }

  }

}
