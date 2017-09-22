package org.oonsql.lng.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public class StreamOpcodeOutputStream extends AbstractOpcodeOutputStream {

  /** Stream */
  private OutputStream out;

  /**
   * Construtor
   * 
   * @param out
   */
  public StreamOpcodeOutputStream(OutputStream out) {
    this.out = out;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(int b) throws IOException {
    out.write(b);
  }

}
