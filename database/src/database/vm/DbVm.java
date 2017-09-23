package database.vm;

import static database.vm.DbOpcode.OPCODE_BOOL_AND;
import static database.vm.DbOpcode.OPCODE_BOOL_NOT;
import static database.vm.DbOpcode.OPCODE_BOOL_OR;
import static database.vm.DbOpcode.OPCODE_GROUP_BOOL;
import static database.vm.DbOpcode.OPCODE_GROUP_INT;
import static database.vm.DbOpcode.OPCODE_GROUP_JUMP;
import static database.vm.DbOpcode.OPCODE_GROUP_LOAD;
import static database.vm.DbOpcode.OPCODE_GROUP_STACK;
import static database.vm.DbOpcode.OPCODE_GROUP_SYS;
import static database.vm.DbOpcode.OPCODE_GROUP_TABLE;
import static database.vm.DbOpcode.OPCODE_INT_EQUAL;
import static database.vm.DbOpcode.OPCODE_INT_NOT_EQUAL;
import static database.vm.DbOpcode.OPCODE_JUMP_FALSE;
import static database.vm.DbOpcode.OPCODE_JUMP_NULL;
import static database.vm.DbOpcode.OPCODE_JUMP_PC;
import static database.vm.DbOpcode.OPCODE_LOAD_FALSE;
import static database.vm.DbOpcode.OPCODE_LOAD_INT;
import static database.vm.DbOpcode.OPCODE_LOAD_STR;
import static database.vm.DbOpcode.OPCODE_LOAD_TRUE;
import static database.vm.DbOpcode.OPCODE_STACK_ARRAY;
import static database.vm.DbOpcode.OPCODE_STACK_JOIN;
import static database.vm.DbOpcode.OPCODE_STACK_POP;
import static database.vm.DbOpcode.OPCODE_STACK_PUSH;
import static database.vm.DbOpcode.OPCODE_STACK_SEND;
import static database.vm.DbOpcode.OPCODE_STACK_SEND_TRUE;
import static database.vm.DbOpcode.OPCODE_SYS_HALF;
import static database.vm.DbOpcode.OPCODE_TABLE_CLOSE;
import static database.vm.DbOpcode.OPCODE_TABLE_FIND;
import static database.vm.DbOpcode.OPCODE_TABLE_NEXT;
import static database.vm.DbOpcode.OPCODE_TABLE_OPEN;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import database.compile.DbAssembly;
import database.compile.DbAssembly.Int;
import database.compile.DbAssembly.Int2d;
import database.compile.DbAssembly.Opcode;
import database.compile.DbAssembly.Str;
import database.object.DbObject;
import database.util.SimpleTreeMap;

/**
 * Banco de Dados
 * 
 * @author Tecgraf
 */
public class DbVm {

  /**
   * @param context
   * @param assembly
   * @param output
   * @throws IOException
   * @throws DbRuntimeException
   */
  public void vm(DbContext context, DbAssembly assembly, DataOutputStream output)
    throws IOException, DbRuntimeException {
    Opcode[] opcodes =
      assembly.getOpcodes().toArray(new Opcode[assembly.getOpcodes().size()]);
    int pc = 0;
    Object[] stack = new Object[32];
    @SuppressWarnings("unchecked")
    Iterator<Object>[] nexts = new Iterator[32];
    short stackIndex = -1;
    short tableIndex = -1;
    for (;;) {
      Opcode opcode = opcodes[pc++];
      switch (opcode.group) {
        case OPCODE_GROUP_LOAD: {
          switch (opcode.opcode) {
            case OPCODE_LOAD_INT: {
              Int op = (Int) opcode;
              stack[++stackIndex] = op.value;
              break;
            }
            case OPCODE_LOAD_TRUE: {
              stack[++stackIndex] = Boolean.TRUE;
              break;
            }
            case OPCODE_LOAD_FALSE: {
              stack[++stackIndex] = Boolean.FALSE;
              break;
            }
            case OPCODE_LOAD_STR: {
              stack[++stackIndex] = ((Str) opcode).value;
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_STACK: {
          switch (opcode.opcode) {
            case OPCODE_STACK_SEND: {
              send(output, stack[stackIndex--]);
              break;
            }
            case OPCODE_STACK_SEND_TRUE: {
              if (stack[stackIndex] == Boolean.TRUE) {
                send(output, stack[stackIndex - 1]);
              }
              stackIndex -= 2;
              break;
            }
            case OPCODE_STACK_ARRAY: {
              Int2d op = (Int2d) opcode;
              stackIndex++;
              stack[stackIndex] =
                ((Object[]) stack[stackIndex - op.x - 1])[op.y];
              break;
            }
            case OPCODE_STACK_JOIN: {
              Int op = (Int) opcode;
              Object[] array = new Object[op.value];
              System.arraycopy(stack, stackIndex - op.value + 1, array, 0,
                op.value);
              stackIndex -= op.value - 1;
              stack[stackIndex] = array;
              break;
            }
            case OPCODE_STACK_PUSH: {
              stackIndex += ((Int) opcode).value;
              break;
            }
            case OPCODE_STACK_POP: {
              stackIndex -= ((Int) opcode).value;
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_JUMP: {
          switch (opcode.opcode) {
            case OPCODE_JUMP_PC: {
              pc = ((Int) opcode).value;
              break;
            }
            case OPCODE_JUMP_FALSE: {
              if (stack[stackIndex--] == Boolean.FALSE) {
                pc = ((Int) opcode).value;
              }
              break;
            }
            case OPCODE_JUMP_NULL: {
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_INT: {
          switch (opcode.opcode) {
            case OPCODE_INT_EQUAL: {
              Integer left = (Integer) stack[stackIndex - 1];
              Integer right = (Integer) stack[stackIndex];
              if (left.intValue() == right.intValue()) {
                stack[--stackIndex] = Boolean.TRUE;
              }
              else {
                stack[--stackIndex] = Boolean.FALSE;
              }
              break;
            }
            case OPCODE_INT_NOT_EQUAL: {
              Integer left = (Integer) stack[stackIndex - 1];
              Integer right = (Integer) stack[stackIndex];
              if (left.intValue() != right.intValue()) {
                stack[--stackIndex] = Boolean.TRUE;
              }
              else {
                stack[--stackIndex] = Boolean.FALSE;
              }
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_BOOL: {
          switch (opcode.opcode) {
            case OPCODE_BOOL_AND: {
              Boolean left = (Boolean) stack[stackIndex - 1];
              Boolean right = (Boolean) stack[stackIndex];
              stack[--stackIndex] =
                left == Boolean.TRUE && right == Boolean.TRUE ? Boolean.TRUE
                  : Boolean.FALSE;
              break;
            }
            case OPCODE_BOOL_OR: {
              Boolean left = (Boolean) stack[stackIndex - 1];
              Boolean right = (Boolean) stack[stackIndex];
              stack[--stackIndex] =
                left == Boolean.TRUE || right == Boolean.TRUE ? Boolean.TRUE
                  : Boolean.FALSE;
              break;
            }
            case OPCODE_BOOL_NOT: {
              Boolean left = (Boolean) stack[stackIndex];
              stack[stackIndex] =
                left == Boolean.TRUE ? Boolean.FALSE : Boolean.TRUE;
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_TABLE: {
          switch (opcode.opcode) {
            case OPCODE_TABLE_OPEN: {
              SimpleTreeMap<Object> table =
                context.getTable(((Str) opcode).value).data;
              if (table == null) {
                throw new DbRuntimeException("table '" + ((Str) opcode).value
                  + "' not found");
              }
              nexts[++tableIndex] = table.iterator();
              break;
            }
            case OPCODE_TABLE_CLOSE: {
              nexts[tableIndex--] = null;
              break;
            }
            case OPCODE_TABLE_NEXT: {
              Object next = nexts[tableIndex].next();
              stack[++stackIndex] = next;
              if (next == null) {
                pc = ((Int) opcode).value;
              }
              break;
            }
            case OPCODE_TABLE_FIND: {
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_SYS: {
          switch (opcode.opcode) {
            case OPCODE_SYS_HALF: {
              output.write('\n');
              return;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
        }
        default: {
          throw new IllegalArgumentException("wrong group: " + opcode.group);
        }
      }
    }
  }

  /**
   * @param output
   * @param value
   * @throws IOException
   */
  protected void send(DataOutputStream output, Object value) throws IOException {
    if (value instanceof Integer) {
      Integer integerValue = (Integer) value;
      output.writeByte(DbObject.INT_CLASSID);
      output.writeInt(integerValue);
    }
    else if (value instanceof Boolean) {
      Boolean booleanValue = (Boolean) value;
      output.writeByte(DbObject.BOOL_CLASSID);
      output.writeByte(booleanValue ? 1 : 0);
    }
    else if (value instanceof String) {
      String stringValue = (String) value;
      output.writeByte(DbObject.STR_CLASSID);
      int size = stringValue.length();
      output.writeShort(size);
      for (int n = 0; n < size; n++) {
        char c = stringValue.charAt(n);
        if (c == '\n') {
          output.write('\\');
          output.write('n');
        }
        else if (c == '\t') {
          output.write('\\');
          output.write('t');
        }
        else {
          output.write(c);
        }
      }
    }
    else if (value instanceof Object[]) {
      Object[] arrayValue = (Object[]) value;
      output.writeByte(DbObject.ARRAY_CLASSID);
      output.writeShort(arrayValue.length);
      for (int n = 0; n < arrayValue.length; n++) {
        send(output, arrayValue[n]);
      }
      output.write('\n');
    }
    else {
      throw new RuntimeException(value.getClass().getSimpleName());
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class DbRuntimeException extends Exception {

    /**
     * @param message
     * @param cause
     */
    public DbRuntimeException(String message, Throwable cause) {
      super(message, cause);
    }

    /**
     * @param message
     */
    public DbRuntimeException(String message) {
      super(message);
    }

    /**
     * @param cause
     */
    public DbRuntimeException(Throwable cause) {
      super(cause);
    }

  }

}
