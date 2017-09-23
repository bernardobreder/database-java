package database.vm;

/**
 * Banco de Dados
 * 
 * @author Tecgraf
 */
public class DbOpcode {

  /** Group */
  public static final int OPCODE_GROUP_LOAD = 1;
  /** Group */
  public static final int OPCODE_GROUP_STACK = 2;
  /** Group */
  public static final int OPCODE_GROUP_JUMP = 3;
  /** Group */
  public static final int OPCODE_GROUP_TABLE = 4;
  /** Group */
  public static final int OPCODE_GROUP_INT = 5;
  /** Group */
  public static final int OPCODE_GROUP_BOOL = 6;
  /** Group */
  public static final int OPCODE_GROUP_SYS = 7;

  /** Opcode */
  public static final int OPCODE_LOAD_INT = 1;
  /** Opcode */
  public static final int OPCODE_LOAD_TRUE = 2;
  /** Opcode */
  public static final int OPCODE_LOAD_FALSE = 3;
  /** Opcode */
  public static final int OPCODE_LOAD_STR = 4;

  /** Opcode */
  public static final int OPCODE_TABLE_OPEN = 55;
  /** Opcode */
  public static final int OPCODE_TABLE_CLOSE = 56;
  /** Opcode */
  public static final int OPCODE_TABLE_NEXT = 60;
  /** Opcode */
  public static final int OPCODE_TABLE_FIND = 50;

  /** Opcode */
  public static final int OPCODE_STACK_SEND = 70;
  /** Opcode */
  public static final int OPCODE_STACK_SEND_TRUE = 71;
  /** Opcode */
  public static final int OPCODE_STACK_ARRAY = 72;
  /** Opcode */
  public static final int OPCODE_STACK_PUSH = 73;
  /** Opcode */
  public static final int OPCODE_STACK_POP = 74;
  /** Opcode */
  public static final int OPCODE_STACK_JOIN = 110;

  /** Opcode */
  public static final int OPCODE_JUMP_NULL = 100;
  /** Opcode */
  public static final int OPCODE_JUMP_FALSE = 5;
  /** Opcode */
  public static final int OPCODE_JUMP_PC = 104;

  /** Opcode */
  public static final int OPCODE_INT_EQUAL = 1;
  /** Opcode */
  public static final int OPCODE_INT_NOT_EQUAL = 2;

  /** Opcode */
  public static final int OPCODE_BOOL_OR = 1;
  /** Opcode */
  public static final int OPCODE_BOOL_AND = 2;
  /** Opcode */
  public static final int OPCODE_BOOL_NOT = 3;

  /** Opcode */
  public static final int OPCODE_SYS_HALF = 1;

}
