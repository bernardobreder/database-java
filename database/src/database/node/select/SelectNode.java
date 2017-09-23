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
public class SelectNode extends Node {

  /** Token */
  private Token token;
  /** Colunas */
  private SelectColumnNode columns;
  /** Froms */
  private List<SelectFromNode> froms;
  /** Where */
  private ValueNode where;
  /** Pc dos TableNexts */
  private int[] nextIndexs;
  /** Pc dos TableNexts */
  private int[] closeIndexs;
  /** Indice da condição Falsa */
  private int falseIndex;

  /**
   * @param token
   * @param columns
   * @param froms
   * @param where
   */
  public SelectNode(Token token, SelectColumnNode columns,
    List<SelectFromNode> froms, ValueNode where) {
    this.token = token;
    this.columns = columns;
    this.froms = froms;
    this.where = where;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void head(ContextNode context) {
    for (SelectFromNode node : froms) {
      node.head(context);
    }
    this.columns.head(context);
    if (this.where != null) {
      this.where.head(context);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void body(ContextNode context) {
    this.columns.body(context);
    for (SelectFromNode node : froms) {
      node.body(context);
    }
    if (this.where != null) {
      this.where.body(context);
    }
    int fromSize = this.froms.size();
    this.nextIndexs = new int[fromSize];
    this.closeIndexs = new int[fromSize];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(DbOpcodeOutputStream output) throws IOException {
    int fromSize = this.froms.size();
    for (int n = 0; n < fromSize; n++) {
      this.froms.get(n).buildTableOpen(output);
      this.nextIndexs[n] = output.getProgramCounter();
      output.writeTableNext(this.closeIndexs[fromSize - n - 1]);
    }
    if (this.where != null) {
      this.where.build(output);
      output.writeJumpFalse(this.falseIndex);
    }
    this.columns.build(output);
    output.writeJump(this.nextIndexs[fromSize - 1]);
    if (this.where != null) {
      this.falseIndex = output.getProgramCounter();
      output.writeStackPop(1);
      output.writeJump(this.nextIndexs[fromSize - 1]);
    }
    for (int n = 0; n < fromSize; n++) {
      SelectFromNode node = this.froms.get(n);
      this.closeIndexs[n] = output.getProgramCounter();
      output.writeStackPop(2);
      node.buildTableClose(output);
      if (n != fromSize - 1) {
        output.writeJump(this.nextIndexs[fromSize - n - 2]);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return token;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return this.token.join(" ").join(columns.toString()).join(" from ").join(
      froms.toString()).join(" where ").join(where.getToken()).getWord();
  }

}
