package org.oonsql.lng.vm;

/**
 * Opcode da máquina virtual
 * 
 * @author Bernardo Breder
 */
public class OonsqlOpcode {

  /** Opcode de */
  public static final int STACK_INC = 1;
  /** Opcode de */
  public static final int STACK_DEC = 2;
  /** Opcode de */
  public static final int STACK_STRING = 3;
  /** Opcode de */
  public static final int STACK_DOUBLE = 4;
  /** Opcode de */
  public static final int STACK_INTEGER = 5;
  /** Opcode de */
  public static final int STACK_TRUE = 6;
  /** Opcode de */
  public static final int STACK_FALSE = 7;
  /** Opcode de */
  public static final int STACK_NULL = 8;
  /** Opcode de */
  public static final int STACK_TERNARY = 9;
  /** Opcode de */

  /** Opcode de */
  public static final int NUMBER_SUM = 20;
  /** Opcode de */
  public static final int NUMBER_SUB = 21;
  /** Opcode de */
  public static final int NUMBER_MUL = 22;
  /** Opcode de */
  public static final int NUMBER_DIV = 23;
  /** Opcode de */
  public static final int NUMBER_EQ = 24;
  /** Opcode de */
  public static final int NUMBER_NEQ = 25;
  /** Opcode de */
  public static final int NUMBER_COMPARE = 26;
  /** Opcode de */
  public static final int NUMBER_GT = 27;
  /** Opcode de */
  public static final int NUMBER_GE = 28;
  /** Opcode de */
  public static final int NUMBER_LT = 29;
  /** Opcode de */
  public static final int NUMBER_LE = 30;
  /** Opcode de */
  public static final int NUMBER_MOD = 31;
  /** Opcode de */
  public static final int NUMBER_INT_DIV = 32;
  /** Opcode de */
  public static final int NUMBER_TO_STRING = 33;
  /** Opcode de */
  public static final int NUMBER_IS_NAN = 34;
  /** Opcode de */
  public static final int NUMBER_IS_INFINITY = 35;
  /** Opcode de */
  public static final int NUMBER_HASH = 36;
  /** Opcode de */
  public static final int NUMBER_NEG = 37;
  /** Opcode de */
  public static final int NUMBER_INC = 38;
  /** Opcode de */
  public static final int NUMBER_DEC = 39;

  /** Opcode de */
  public static final int INTEGER_SUM = 60;
  /** Opcode de */
  public static final int INTEGER_SUB = 61;
  /** Opcode de */
  public static final int INTEGER_MUL = 62;
  /** Opcode de */
  public static final int INTEGER_DIV = 63;
  /** Opcode de */
  public static final int INTEGER_EQ = 64;
  /** Opcode de */
  public static final int INTEGER_NEQ = 65;
  /** Opcode de */
  public static final int INTEGER_COMPARE = 66;
  /** Opcode de */
  public static final int INTEGER_GT = 67;
  /** Opcode de */
  public static final int INTEGER_GE = 68;
  /** Opcode de */
  public static final int INTEGER_LT = 69;
  /** Opcode de */
  public static final int INTEGER_LE = 70;
  /** Opcode de */
  public static final int INTEGER_AND = 71;
  /** Opcode de */
  public static final int INTEGER_OR = 72;
  /** Opcode de */
  public static final int INTEGER_MOD = 73;
  /** Opcode de */
  public static final int INTEGER_TO_STRING = 74;
  /** Opcode de */
  public static final int INTEGER_HASH = 75;
  /** Opcode de */
  public static final int INTEGER_NEG = 76;
  /** Opcode de */
  public static final int INTEGER_INC = 77;
  /** Opcode de */
  public static final int INTEGER_DEC = 78;

  /** Opcode de */
  public static final int BOOLEAN_NOT = 90;
  /** Opcode de */
  public static final int BOOLEAN_AND = 91;
  /** Opcode de */
  public static final int BOOLEAN_OR = 92;

  /** Opcode de */
  public static final int STRING_SUM = 100;

  /** Opcode de */
  public static final int CONTROL_JUMP = 150;
  /** Opcode de */
  public static final int CONTROL_JUMP_TRUE = 151;
  /** Opcode de */
  public static final int CONTROL_JUMP_FALSE = 152;
  /** Opcode de */
  public static final int CONTROL_JUMP_INT = 153;
  /** Opcode de */
  public static final int CONTROL_RETURN = 154;

  /** Opcode de */
  public static final int HALF = 255;

}
