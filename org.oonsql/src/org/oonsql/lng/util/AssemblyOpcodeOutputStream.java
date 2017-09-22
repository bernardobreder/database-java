package org.oonsql.lng.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 
 * @author Bernardo Breder
 */
public class AssemblyOpcodeOutputStream extends StreamOpcodeOutputStream {

  /**
   * @param out
   */
  public AssemblyOpcodeOutputStream(OutputStream out) {
    super(out);
  }

  /**
   * @param code
   * @throws IOException
   */
  public void write(String code) throws IOException {
    int index = code.indexOf(' ');
    if (index < 0) {
      index = code.length();
    }
    String opcode = code.substring(0, index);
    if (opcode.equals("stack.inc")) {
      this.opStackInc(Integer.parseInt(code.substring(index)));
    }
    else if (opcode.equals("stack.dec")) {
      this.opStackDec(Integer.parseInt(code.substring(index)));
    }
    else if (opcode.equals("stack.num")) {
      this.opStackDouble(Double.parseDouble(code.substring(index)));
    }
    else if (opcode.equals("stack.str")) {
      this.opStackString(code.substring(index));
    }
    else if (opcode.equals("stack.true")) {
      this.opStackTrue();
    }
    else if (opcode.equals("stack.false")) {
      this.opStackFalse();
    }
    else if (opcode.equals("stack.null")) {
      this.opStackNull();
    }
    else if (opcode.equals("half")) {
      this.opControlHalf();
    }
    else {
      throw new IllegalArgumentException("unknown opcode: " + opcode);
    }
  }

}
