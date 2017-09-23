package database.compile;

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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import database.vm.DbContext;
import database.vm.DbVm;
import database.vm.DbVm.DbRuntimeException;
import database.vm.out.UTFOutputStream;

/**
 * Classe que realiza a compilação de uma consulta
 * 
 * @author Tecgraf
 */
public class DbAssembly {

  /** Lista de opcodes */
  private List<Opcode> opcodes;

  /**
   * @param bytecodes
   */
  public DbAssembly(byte[] bytecodes) {
    ArrayList<Opcode> list = new ArrayList<Opcode>(bytecodes.length / 2);
    int pcSize = bytecodes.length;
    int pc = 0;
    StringBuilder sb = new StringBuilder();
    byte group, opcode;
    for (; pc < pcSize;) {
      switch (group = bytecodes[pc++]) {
        case OPCODE_GROUP_LOAD: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_LOAD_INT: {
              int value =
                (bytecodes[pc] << 24) + (bytecodes[pc + 1] << 16)
                  + (bytecodes[pc + 2] << 8) + bytecodes[pc + 3];
              pc += 4;
              list.add(new Int(group, opcode, value));
              break;
            }
            case OPCODE_LOAD_TRUE: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_LOAD_FALSE: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_LOAD_STR: {
              int size = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              for (int n = 0; n < size; n++) {
                sb.append((char) bytecodes[pc++]);
              }
              String value = sb.toString();
              sb.delete(0, sb.length());
              list.add(new Str(group, opcode, value));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_STACK: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_STACK_SEND: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_STACK_SEND_TRUE: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_STACK_JOIN: {
              int value = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              list.add(new Int(group, opcode, value));
              break;
            }
            case OPCODE_STACK_ARRAY: {
              int x = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              int y = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              list.add(new Int2d(group, opcode, x, y));
              break;
            }
            case OPCODE_STACK_PUSH: {
              int value = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              list.add(new Int(group, opcode, value));
              break;
            }
            case OPCODE_STACK_POP: {
              int value = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              list.add(new Int(group, opcode, value));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_JUMP: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_JUMP_PC: {
              int value =
                (bytecodes[pc] << 24) + (bytecodes[pc + 1] << 16)
                  + (bytecodes[pc + 2] << 8) + bytecodes[pc + 3];
              pc += 4;
              list.add(new Int(group, opcode, value));
              break;
            }
            case OPCODE_JUMP_NULL: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_JUMP_FALSE: {
              int value =
                (bytecodes[pc] << 24) + (bytecodes[pc + 1] << 16)
                  + (bytecodes[pc + 2] << 8) + bytecodes[pc + 3];
              pc += 4;
              list.add(new Int(group, opcode, value));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_INT: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_INT_EQUAL: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_INT_NOT_EQUAL: {
              list.add(new Opcode(group, opcode));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_BOOL: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_BOOL_AND: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_BOOL_OR: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_BOOL_NOT: {
              list.add(new Opcode(group, opcode));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_SYS: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_SYS_HALF: {
              list.add(new Opcode(group, opcode));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        case OPCODE_GROUP_TABLE: {
          switch (opcode = bytecodes[pc++]) {
            case OPCODE_TABLE_OPEN: {
              int size = (bytecodes[pc] << 8) + bytecodes[pc + 1];
              pc += 2;
              for (int n = 0; n < size; n++) {
                sb.append((char) bytecodes[pc++]);
              }
              String value = sb.toString();
              sb.delete(0, sb.length());
              list.add(new Str(group, opcode, value));
              break;
            }
            case OPCODE_TABLE_CLOSE: {
              list.add(new Opcode(group, opcode));
              break;
            }
            case OPCODE_TABLE_NEXT: {
              int value =
                (bytecodes[pc] << 24) + (bytecodes[pc + 1] << 16)
                  + (bytecodes[pc + 2] << 8) + bytecodes[pc + 3];
              pc += 4;
              list.add(new Int(group, opcode, value));
              break;
            }
            case OPCODE_TABLE_FIND: {
              list.add(new Opcode(group, opcode));
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: " + group + "."
                + opcode);
            }
          }
          break;
        }
        default: {
          throw new IllegalArgumentException("wrong group: " + group);
        }
      }
    }
    list.trimToSize();
    this.opcodes = list;
  }

  /**
   * @return lista de opcodes
   */
  public List<Opcode> getOpcodes() {
    return opcodes;
  }

  /**
   * @param context
   * @param output
   * @throws IOException
   * @throws DbRuntimeException
   */
  public void execute(DbContext context, DataOutputStream output)
    throws IOException, DbRuntimeException {
    new DbVm().vm(context, this, output);
  }

  /**
   * @param output
   * @throws IOException
   */
  public void disassembly(OutputStream output) throws IOException {
    UTFOutputStream out = new UTFOutputStream(output);
    int size = opcodes.size();
    for (int pc = 0; pc < size; pc++) {
      Opcode opcode = opcodes.get(pc);
      switch (opcode.group) {
        case OPCODE_GROUP_LOAD: {
          switch (opcode.opcode) {
            case OPCODE_LOAD_INT: {
              Int op = (Int) opcode;
              write(out, pc, "load.int " + op.value);
              break;
            }
            case OPCODE_LOAD_TRUE: {
              write(out, pc, "load.true");
              break;
            }
            case OPCODE_LOAD_FALSE: {
              write(out, pc, "load.false");
              break;
            }
            case OPCODE_LOAD_STR: {
              Str op = (Str) opcode;
              write(out, pc, "load.string '" + op.value + "'");
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
              write(out, pc, "stack.send");
              break;
            }
            case OPCODE_STACK_SEND_TRUE: {
              write(out, pc, "stack.send_true");
              break;
            }
            case OPCODE_STACK_JOIN: {
              Int op = (Int) opcode;
              write(out, pc, "stack.join " + op.value);
              break;
            }
            case OPCODE_STACK_ARRAY: {
              Int2d op = (Int2d) opcode;
              write(out, pc, "stack.array " + op.x + " " + op.y);
              break;
            }
            case OPCODE_STACK_PUSH: {
              Int op = (Int) opcode;
              write(out, pc, "stack.push " + op.value);
              break;
            }
            case OPCODE_STACK_POP: {
              Int op = (Int) opcode;
              write(out, pc, "stack.pop " + op.value);
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
              Int op = (Int) opcode;
              write(out, pc, "jump.pc " + op.value);
              break;
            }
            case OPCODE_JUMP_NULL: {
              Int op = (Int) opcode;
              write(out, pc, "jump.null " + op.value);
              break;
            }
            case OPCODE_JUMP_FALSE: {
              Int op = (Int) opcode;
              write(out, pc, "jump.false " + op.value);
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
              write(out, pc, "int.equal");
              break;
            }
            case OPCODE_INT_NOT_EQUAL: {
              write(out, pc, "int.notequal");
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
              write(out, pc, "bool.and");
              break;
            }
            case OPCODE_BOOL_OR: {
              write(out, pc, "bool.or");
              break;
            }
            case OPCODE_BOOL_NOT: {
              write(out, pc, "bool.not");
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
              Str op = (Str) opcode;
              write(out, pc, "table.open '" + op.value + "'");
              break;
            }
            case OPCODE_TABLE_CLOSE: {
              write(out, pc, "table.close");
              break;
            }
            case OPCODE_TABLE_NEXT: {
              Int op = (Int) opcode;
              write(out, pc, "table.next " + op.value);
              break;
            }
            case OPCODE_TABLE_FIND: {
              write(out, pc, "table.find");
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
              write(out, pc, "sys.half");
              break;
            }
            default: {
              throw new IllegalArgumentException("wrong opcode: "
                + opcode.group + "." + opcode.opcode);
            }
          }
          break;
        }
        default: {
          throw new IllegalArgumentException("wrong group: " + opcode.group);
        }
      }
      out.write('\n');
    }
  }

  /**
   * @param out
   * @param pc
   * @param text
   * @throws IOException
   */
  public void write(UTFOutputStream out, int pc, String text)
    throws IOException {
    out.write(pc + ":");
    for (int n = 0; n < 1; n++) {
      out.write('\t');
    }
    out.write(text);
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Int extends Opcode {
    /** Value */
    public int value;

    /**
     * @param group
     * @param opcode
     * @param value
     */
    public Int(byte group, byte opcode, int value) {
      super(group, opcode);
      this.value = value;
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Str extends Opcode {
    /** Value */
    public String value;

    /**
     * @param group
     * @param opcode
     * @param value
     */
    public Str(byte group, byte opcode, String value) {
      super(group, opcode);
      this.value = value;
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class StrInt extends Opcode {
    /** Value */
    public String value;
    /** Value */
    public int index;

    /**
     * @param group
     * @param opcode
     * @param value
     * @param index
     */
    public StrInt(byte group, byte opcode, String value, int index) {
      super(group, opcode);
      this.value = value;
      this.index = index;
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Bool extends Opcode {
    /** Value */
    public boolean value;

    /**
     * @param group
     * @param opcode
     * @param value
     */
    public Bool(byte group, byte opcode, boolean value) {
      super(group, opcode);
      this.value = value;
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Int2d extends Opcode {
    /** Value */
    public int x;
    /** Value */
    public int y;

    /**
     * @param group
     * @param opcode
     * @param x
     * @param y
     */
    public Int2d(byte group, byte opcode, int x, int y) {
      super(group, opcode);
      this.x = x;
      this.y = y;
    }
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class Opcode {
    /** Value */
    public byte group;
    /** Value */
    public byte opcode;

    /**
     * @param group
     * @param opcode
     */
    public Opcode(byte group, byte opcode) {
      super();
      this.group = group;
      this.opcode = opcode;
    }
  }

}
