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
import java.io.OutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbOpcodeOutputStream {

  /** Saída */
  private final DataOutputStream out;
  /** Program Counter */
  private int pc;
  /** Pilha */
  private int stackCounter;

  /**
   * @param out
   */
  public DbOpcodeOutputStream(OutputStream out) {
    this.out = new DataOutputStream(out);
  }

  /**
   * Incrementa a pilha
   */
  public void incStack() {
    this.stackCounter++;
  }

  /**
   * Decrementa a pilha
   */
  public void decStack() {
    this.stackCounter--;
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeLoadInt(int value) throws IOException {
    this.out.writeByte(OPCODE_GROUP_LOAD);
    this.out.writeByte(OPCODE_LOAD_INT);
    this.out.writeInt(value);
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeLoadBoolean(boolean value) throws IOException {
    this.out.writeByte(OPCODE_GROUP_LOAD);
    if (value) {
      this.out.writeByte(OPCODE_LOAD_TRUE);
    }
    else {
      this.out.writeByte(OPCODE_LOAD_FALSE);
    }
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeLoadString(String value) throws IOException {
    this.out.writeByte(OPCODE_GROUP_LOAD);
    this.out.writeByte(OPCODE_LOAD_STR);
    this.out.writeInt(value.length());
    for (int n = 0; n < value.length(); n++) {
      this.out.write(value.charAt(n));
    }
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @param value
   * @throws IOException
   */
  public void writeTableOpen(String value) throws IOException {
    this.out.writeByte(OPCODE_GROUP_TABLE);
    this.out.writeByte(OPCODE_TABLE_OPEN);
    this.out.writeShort(value.length());
    for (int n = 0; n < value.length(); n++) {
      this.out.write(value.charAt(n));
    }
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeTableClose() throws IOException {
    this.out.writeByte(OPCODE_GROUP_TABLE);
    this.out.writeByte(OPCODE_TABLE_CLOSE);
    this.pc++;
  }

  /**
   * @param index
   * @throws IOException
   */
  public void writeTableNext(short index) throws IOException {
    this.out.writeByte(OPCODE_GROUP_TABLE);
    this.out.writeByte(OPCODE_TABLE_NEXT);
    this.out.writeShort(index);
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @param table
   * @param id
   * @throws IOException
   */
  public void writeTableFind(String table, long id) throws IOException {
    this.out.writeByte(OPCODE_GROUP_TABLE);
    this.out.writeByte(OPCODE_TABLE_FIND);
    this.out.writeShort(table.length());
    for (int n = 0; n < table.length(); n++) {
      this.out.write(table.charAt(n));
    }
    this.out.writeLong(id);
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @param address
   * @throws IOException
   */
  public void writeJump(int address) throws IOException {
    this.out.writeByte(OPCODE_GROUP_JUMP);
    this.out.writeByte(OPCODE_JUMP_PC);
    this.out.writeInt(address);
    this.pc++;
  }

  /**
   * @param address
   * @throws IOException
   */
  public void writeJumpNull(int address) throws IOException {
    this.out.writeByte(OPCODE_GROUP_JUMP);
    this.out.writeByte(OPCODE_JUMP_NULL);
    this.out.writeInt(address);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @param address
   * @throws IOException
   */
  public void writeJumpFalse(int address) throws IOException {
    this.out.writeByte(OPCODE_GROUP_JUMP);
    this.out.writeByte(OPCODE_JUMP_FALSE);
    this.out.writeInt(address);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @param count
   * @throws IOException
   */
  public void writeJoin(int count) throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_JOIN);
    this.out.writeShort(count);
    this.stackCounter -= count - 1;
    this.pc++;
  }

  /**
   * @param stackIndex
   * @param arrayIndex
   * @throws IOException
   * 
   */
  public void writeStackArray(int stackIndex, int arrayIndex)
    throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_ARRAY);
    this.out.writeShort(stackIndex + this.stackCounter);
    this.out.writeShort(arrayIndex);
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeIntEqual() throws IOException {
    this.out.writeByte(OPCODE_GROUP_INT);
    this.out.writeByte(OPCODE_INT_EQUAL);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeIntNotEqual() throws IOException {
    this.out.writeByte(OPCODE_GROUP_INT);
    this.out.writeByte(OPCODE_INT_NOT_EQUAL);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @param jumpPc
   * @throws IOException
   */
  public void writeTableNext(int jumpPc) throws IOException {
    this.out.writeByte(OPCODE_GROUP_TABLE);
    this.out.writeByte(OPCODE_TABLE_NEXT);
    this.out.writeInt(jumpPc);
    this.stackCounter++;
    this.pc++;
  }

  /**
   * @return pc
   */
  public int getProgramCounter() {
    return this.pc;
  }

  /**
   * @throws IOException
   */
  public void sendTrue() throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_SEND_TRUE);
    this.stackCounter -= 2;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void send() throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_SEND);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @param n
   * @param i
   * @throws IOException
   */
  public void writeJumpNull(int n, int i) throws IOException {
    this.out.writeByte(OPCODE_GROUP_JUMP);
    this.out.writeByte(OPCODE_JUMP_NULL);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeHalf() throws IOException {
    this.out.writeByte(OPCODE_GROUP_SYS);
    this.out.writeByte(OPCODE_SYS_HALF);
    this.pc++;
  }

  /**
   * @param count
   * @throws IOException
   */
  public void writeStackPush(int count) throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_PUSH);
    this.out.writeShort(count);
    stackCounter += count;
    this.pc++;
  }

  /**
   * @param count
   * @throws IOException
   */
  public void writeStackPop(int count) throws IOException {
    this.out.writeByte(OPCODE_GROUP_STACK);
    this.out.writeByte(OPCODE_STACK_POP);
    this.out.writeShort(count);
    stackCounter -= count;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeBoolAnd() throws IOException {
    this.out.writeByte(OPCODE_GROUP_BOOL);
    this.out.writeByte(OPCODE_BOOL_AND);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeBoolOr() throws IOException {
    this.out.writeByte(OPCODE_GROUP_BOOL);
    this.out.writeByte(OPCODE_BOOL_OR);
    this.stackCounter--;
    this.pc++;
  }

  /**
   * @throws IOException
   */
  public void writeBoolNot() throws IOException {
    this.out.writeByte(OPCODE_GROUP_BOOL);
    this.out.writeByte(OPCODE_BOOL_NOT);
    this.pc++;
  }

}
