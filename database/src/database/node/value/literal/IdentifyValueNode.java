package database.node.value.literal;

import java.io.IOException;

import database.lexical.Token;
import database.node.ContextNode;
import database.node.ContextNode.TableEntry;
import database.vm.DbContext.DbColumn;
import database.vm.DbContext.DbTable;
import database.vm.DbOpcodeOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class IdentifyValueNode extends LiteralValueNode {

  /** Left identify */
  private Token left;
  /** Right identify */
  private Token right;
  /** Indice da pilha */
  private int stackIndex;
  /** Indice do array */
  private int arrayIndex;

  /**
   * @param left
   * @param right
   */
  public IdentifyValueNode(Token left, Token right) {
    this.left = left;
    this.right = right;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void head(ContextNode context) {
    boolean found = false;
    int tableSize = context.tables.size();
    if (right == null) {
      String name = left.getWord();
      for (int n = 0; n < tableSize; n++) {
        String tableName = context.tables.get(n).table;
        DbTable table = context.context.getTable(tableName);
        for (int m = 0; m < table.head.size(); m++) {
          DbColumn column = table.head.get(m);
          if (column.name.equalsIgnoreCase(name)) {
            this.stackIndex = -n - 1;
            this.arrayIndex = m;
            found = true;
            break;
          }
        }
        if (found) {
          break;
        }
      }
    }
    else {
      String left = this.left.getWord();
      String right = this.right.getWord();
      for (int n = 0; n < tableSize; n++) {
        TableEntry tableEntry = context.tables.get(tableSize - 1 - n);
        String tableName = tableEntry.alias;
        if (left.equalsIgnoreCase(tableName)) {
          DbTable table = context.context.getTable(tableEntry.table);
          for (int m = 0; m < table.head.size(); m++) {
            DbColumn column = table.head.get(m);
            if (column.name.equalsIgnoreCase(right)) {
              this.stackIndex = n - tableSize;
              this.arrayIndex = m;
              found = true;
              break;
            }
          }
          if (found) {
            break;
          }
        }
      }
    }
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
    output.writeStackArray(stackIndex, arrayIndex);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    if (right != null) {
      return left.join(".").join(right);
    }
    else {
      return left;
    }
  }

}
