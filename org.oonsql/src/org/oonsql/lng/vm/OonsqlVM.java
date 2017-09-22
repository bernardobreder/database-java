package org.oonsql.lng.vm;

import java.io.IOException;
import java.io.InputStream;

import org.oonsql.lng.util.AbstractOpcodeInputStream;
import org.oonsql.lng.util.StreamOpcodeInputStream;

/**
 * Máquina virtual
 * 
 * @author Bernardo Breder
 */
public class OonsqlVM {

  /**
   * Executa uma stream de bytes
   * 
   * @param input
   * @return valor
   * @throws IOException
   */
  public Object execute(InputStream input) throws IOException {
    AbstractOpcodeInputStream in = new StreamOpcodeInputStream(input);
    try {
      Object[] stack = new Object[1024];
      int stackIndex = -1;
      for (;;) {
        switch (in.readOpcode()) {
          case OonsqlOpcode.STACK_INC: {
            stackIndex += in.readIndex();
            break;
          }
          case OonsqlOpcode.STACK_DEC: {
            stackIndex += in.readIndex();
            break;
          }
          case OonsqlOpcode.STACK_INTEGER: {
            stack[++stackIndex] = in.readInteger();
            break;
          }
          case OonsqlOpcode.STACK_DOUBLE: {
            stack[++stackIndex] = in.readDouble();
            break;
          }
          case OonsqlOpcode.STACK_STRING: {
            stack[++stackIndex] = in.readString();
            break;
          }
          case OonsqlOpcode.STACK_TRUE: {
            stack[++stackIndex] = Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.STACK_FALSE: {
            stack[++stackIndex] = Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.STACK_TERNARY: {
            stackIndex -= 2;
            if (stack[stackIndex] == Boolean.TRUE) {
              stack[stackIndex] = stack[stackIndex + 1];
            }
            else {
              stack[stackIndex] = stack[stackIndex + 2];
            }
            break;
          }
          case OonsqlOpcode.NUMBER_SUM: {
            stackIndex--;
            stack[stackIndex] =
              (Double) stack[stackIndex] + (Double) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.NUMBER_SUB: {
            stackIndex--;
            stack[stackIndex] =
              (Double) stack[stackIndex] - (Double) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.NUMBER_MUL: {
            stackIndex--;
            stack[stackIndex] =
              (Double) stack[stackIndex] * (Double) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.NUMBER_DIV: {
            stackIndex--;
            stack[stackIndex] =
              (Double) stack[stackIndex] / (Double) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.NUMBER_EQ: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex]).equals(stack[stackIndex + 1]) ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.NUMBER_NEQ: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex]).equals(stack[stackIndex + 1]) ? Boolean.FALSE
                : Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.NUMBER_COMPARE: {
            stackIndex--;
            stack[stackIndex] =
              Double.valueOf(((Double) stack[stackIndex])
                .compareTo((Double) stack[stackIndex + 1]));
            break;
          }
          case OonsqlOpcode.NUMBER_GT: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex])
                .compareTo((Double) stack[stackIndex + 1]) > 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.NUMBER_GE: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex])
                .compareTo((Double) stack[stackIndex + 1]) >= 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.NUMBER_LT: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex])
                .compareTo((Double) stack[stackIndex + 1]) < 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.NUMBER_LE: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex])
                .compareTo((Double) stack[stackIndex + 1]) <= 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.NUMBER_NEG: {
            stack[stackIndex] = ((Double) stack[stackIndex]) * -1;
            break;
          }
          case OonsqlOpcode.NUMBER_MOD: {
            stackIndex--;
            stack[stackIndex] =
              ((Double) stack[stackIndex]) % ((Double) stack[stackIndex + 1]);
            break;
          }
          case OonsqlOpcode.NUMBER_INT_DIV: {
            stackIndex--;
            stack[stackIndex] =
              (int) ((Double) stack[stackIndex]).doubleValue()
                / ((Double) stack[stackIndex + 1]).doubleValue();
            break;
          }
          case OonsqlOpcode.NUMBER_TO_STRING: {
            stack[stackIndex] = ((Double) stack[stackIndex]).toString();
            break;
          }
          case OonsqlOpcode.NUMBER_IS_NAN: {
            stack[stackIndex] = ((Double) stack[stackIndex]).isNaN();
            break;
          }
          case OonsqlOpcode.NUMBER_IS_INFINITY: {
            stack[stackIndex] = ((Double) stack[stackIndex]).isInfinite();
            break;
          }
          case OonsqlOpcode.NUMBER_HASH: {
            stack[stackIndex] = ((Double) stack[stackIndex]).hashCode();
            break;
          }
          case OonsqlOpcode.INTEGER_SUM: {
            stackIndex--;
            stack[stackIndex] =
              (Integer) stack[stackIndex] + (Integer) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.INTEGER_SUB: {
            stackIndex--;
            stack[stackIndex] =
              (Integer) stack[stackIndex] - (Integer) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.INTEGER_MUL: {
            stackIndex--;
            stack[stackIndex] =
              (Integer) stack[stackIndex] * (Integer) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.INTEGER_DIV: {
            stackIndex--;
            stack[stackIndex] =
              (Integer) stack[stackIndex] / (Integer) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.INTEGER_EQ: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex]).equals(stack[stackIndex + 1]) ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.INTEGER_NEQ: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex]).equals(stack[stackIndex + 1]) ? Boolean.FALSE
                : Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.INTEGER_COMPARE: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex])
                .compareTo((Integer) stack[stackIndex + 1]);
            break;
          }
          case OonsqlOpcode.INTEGER_GT: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex])
                .compareTo((Integer) stack[stackIndex + 1]) > 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.INTEGER_GE: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex])
                .compareTo((Integer) stack[stackIndex + 1]) >= 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.INTEGER_LT: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex])
                .compareTo((Integer) stack[stackIndex + 1]) < 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.INTEGER_LE: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex])
                .compareTo((Integer) stack[stackIndex + 1]) <= 0 ? Boolean.TRUE
                : Boolean.FALSE;
            break;
          }
          case OonsqlOpcode.INTEGER_NEG: {
            stack[stackIndex] = -((Integer) stack[stackIndex]);
            break;
          }
          case OonsqlOpcode.INTEGER_AND: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex]) & ((Integer) stack[stackIndex + 1]);
            break;
          }
          case OonsqlOpcode.INTEGER_OR: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex]) | ((Integer) stack[stackIndex + 1]);
            break;
          }
          case OonsqlOpcode.INTEGER_MOD: {
            stackIndex--;
            stack[stackIndex] =
              ((Integer) stack[stackIndex]) % ((Integer) stack[stackIndex + 1]);
            break;
          }
          case OonsqlOpcode.INTEGER_TO_STRING: {
            stack[stackIndex] = ((Integer) stack[stackIndex]).toString();
            break;
          }
          case OonsqlOpcode.INTEGER_HASH: {
            break;
          }
          case OonsqlOpcode.STRING_SUM: {
            stackIndex--;
            stack[stackIndex] =
              (String) stack[stackIndex] + (String) stack[stackIndex + 1];
            break;
          }
          case OonsqlOpcode.CONTROL_JUMP: {
            in.goTo(in.readIndex());
            break;
          }
          case OonsqlOpcode.BOOLEAN_AND: {
            stackIndex--;
            stack[stackIndex] =
              stack[stackIndex] == Boolean.TRUE
                && (Boolean) stack[stackIndex + 1] == Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.BOOLEAN_OR: {
            stackIndex--;
            stack[stackIndex] =
              stack[stackIndex] == Boolean.TRUE
                || (Boolean) stack[stackIndex + 1] == Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.BOOLEAN_NOT: {
            stack[stackIndex] =
              stack[stackIndex] == Boolean.TRUE ? Boolean.FALSE : Boolean.TRUE;
            break;
          }
          case OonsqlOpcode.CONTROL_JUMP_TRUE: {
            if (stack[stackIndex] == Boolean.TRUE) {
              in.goTo(in.readIndex());
            }
            stackIndex--;
            break;
          }
          case OonsqlOpcode.CONTROL_JUMP_FALSE: {
            if (stack[stackIndex] == Boolean.FALSE) {
              in.goTo(in.readIndex());
            }
            stackIndex--;
            break;
          }
          case OonsqlOpcode.CONTROL_JUMP_INT: {
            if ((Integer) stack[stackIndex] == in.readIndex()) {
              in.goTo(in.readIndex());
            }
            stackIndex--;
            break;
          }
          case OonsqlOpcode.CONTROL_RETURN: {
            return stack[stackIndex];
          }
          case OonsqlOpcode.HALF: {
            return stackIndex < 0 ? null : stack[stackIndex];
          }
        }
      }
    }
    finally {
      in.close();
    }
  }
}
