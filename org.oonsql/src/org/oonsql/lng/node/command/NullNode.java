package org.oonsql.lng.node.command;

import java.io.IOException;

import org.oonsql.lng.util.AbstractDataOutputStream;
import org.oonsql.lng.util.AbstractOpcodeOutputStream;

/**
 * Node de null
 * 
 * @author Bernardo Breder
 */
public class NullNode extends CommandNode {

  /** Valor unico */
  public static final NullNode INSTANCE = new NullNode();

  /**
   * Construtor privado
   */
  private NullNode() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void build(AbstractOpcodeOutputStream output) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(AbstractDataOutputStream output) throws IOException {
    output.writeIndex(NULL_CMD);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return ";";
  }

}
