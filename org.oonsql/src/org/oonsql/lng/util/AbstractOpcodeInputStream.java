package org.oonsql.lng.util;

import java.io.IOException;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public abstract class AbstractOpcodeInputStream extends AbstractDataInputStream {

  /**
   * Realiza a leitura de um opcode
   * 
   * @return opcode
   * @throws IOException
   */
  public int readOpcode() throws IOException {
    return this.read();
  }

  /**
   * Anda com n bytes
   * 
   * @param n
   */
  public abstract void goTo(int n);

}
