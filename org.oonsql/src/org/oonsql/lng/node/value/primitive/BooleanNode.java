package org.oonsql.lng.node.value.primitive;

import java.io.IOException;

import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de boolean
 * 
 * @author Bernardo Breder
 */
public class BooleanNode extends PrimitiveNode {

  /** Node de True */
  private static final BooleanNode TRUE = new BooleanNode();
  /** Node de False */
  private static final BooleanNode FALSE = new BooleanNode();

  /**
   * Construtor
   */
  private BooleanNode() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) throws IOException {
    if (this == TRUE) {
      output.opStackTrue();
    }
    else {
      output.opStackFalse();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    if (this == TRUE) {
      output.writeIndex(TRUE_VALUE);
    }
    else {
      output.writeIndex(FALSE_VALUE);
    }
  }

  /**
   * Constroi um objeto
   * 
   * @param flag
   * @return objeto boolean
   */
  public static BooleanNode build(boolean flag) {
    if (flag) {
      return TRUE;
    }
    else {
      return FALSE;
    }
  }

}
