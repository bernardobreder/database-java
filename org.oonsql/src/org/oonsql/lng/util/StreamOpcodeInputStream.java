package org.oonsql.lng.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public class StreamOpcodeInputStream extends AbstractOpcodeInputStream {

  /** Stream */
  private InputStream in;

  /**
   * Construtor
   * 
   * @param in
   */
  public StreamOpcodeInputStream(InputStream in) {
    this.in = in;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    return this.in.read();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void goTo(int n) {
    throw new RuntimeException();
  }

}
