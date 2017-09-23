package database.node.value.binary;

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
public class EqualCompareValueNode extends CompareValueNode {

  /** Left */
  private ValueNode left;
  /** Right */
  private ValueNode right;

  /**
   * @param left
   * @param right
   */
  public EqualCompareValueNode(ValueNode left, ValueNode right) {
    this.left = left;
    this.right = right;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void head(ContextNode context) {
    this.left.head(context);
    this.right.head(context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void body(ContextNode context) {
    this.left.body(context);
    this.right.body(context);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(DbOpcodeOutputStream output) throws IOException {
    this.left.build(output);
    this.right.build(output);
    output.writeIntEqual();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token getToken() {
    return left.getToken().join(" = ").join(right.getToken());
  }

}
