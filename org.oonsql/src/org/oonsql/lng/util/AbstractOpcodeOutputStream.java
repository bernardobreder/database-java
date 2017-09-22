package org.oonsql.lng.util;

import java.io.IOException;

import org.oonsql.lng.vm.OonsqlOpcode;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public abstract class AbstractOpcodeOutputStream extends
  AbstractDataOutputStream {

  /**
   * Adiciona o opcode de Push String
   * 
   * @param count
   * @throws IOException
   */
  public void opStackInc(int count) throws IOException {
    this.write(OonsqlOpcode.STACK_INC);
    this.writeIndex(count);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @param count
   * @throws IOException
   */
  public void opStackDec(int count) throws IOException {
    this.write(OonsqlOpcode.STACK_DEC);
    this.writeIndex(count);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @param text
   * @throws IOException
   */
  public void opStackString(String text) throws IOException {
    this.write(OonsqlOpcode.STACK_STRING);
    this.writeString(text);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @param value
   * @throws IOException
   */
  public void opStackDouble(double value) throws IOException {
    this.write(OonsqlOpcode.STACK_DOUBLE);
    this.writeDouble(value);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @throws IOException
   */
  public void opStackTrue() throws IOException {
    this.write(OonsqlOpcode.STACK_TRUE);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @throws IOException
   */
  public void opStackFalse() throws IOException {
    this.write(OonsqlOpcode.STACK_FALSE);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @throws IOException
   */
  public void opStackNull() throws IOException {
    this.write(OonsqlOpcode.STACK_NULL);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @throws IOException
   */
  public void opStackTernary() throws IOException {
    this.write(OonsqlOpcode.STACK_TERNARY);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleEqual() throws IOException {
    this.write(OonsqlOpcode.NUMBER_EQ);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleNotEqual() throws IOException {
    this.write(OonsqlOpcode.NUMBER_NEQ);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleGreaterThan() throws IOException {
    this.write(OonsqlOpcode.NUMBER_GT);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleGreaterEqual() throws IOException {
    this.write(OonsqlOpcode.NUMBER_GE);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleLowerThan() throws IOException {
    this.write(OonsqlOpcode.NUMBER_LT);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleLowerEqual() throws IOException {
    this.write(OonsqlOpcode.NUMBER_LE);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleSum() throws IOException {
    this.write(OonsqlOpcode.NUMBER_SUM);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleSub() throws IOException {
    this.write(OonsqlOpcode.NUMBER_SUB);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleMul() throws IOException {
    this.write(OonsqlOpcode.NUMBER_MUL);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleDiv() throws IOException {
    this.write(OonsqlOpcode.NUMBER_DIV);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opDoubleNeg() throws IOException {
    this.write(OonsqlOpcode.NUMBER_NEG);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opStringSum() throws IOException {
    this.write(OonsqlOpcode.STRING_SUM);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opBooleanAnd() throws IOException {
    this.write(OonsqlOpcode.BOOLEAN_AND);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opBooleanOr() throws IOException {
    this.write(OonsqlOpcode.BOOLEAN_OR);
  }

  /**
   * Realiza a soma de dois números
   * 
   * @throws IOException
   */
  public void opBooleanNot() throws IOException {
    this.write(OonsqlOpcode.BOOLEAN_NOT);
  }

  /**
   * Adiciona o opcode de half
   * 
   * @throws IOException
   */
  public void opControlHalf() throws IOException {
    this.write(OonsqlOpcode.HALF);
  }

  /**
   * Adiciona o opcode de Push String
   * 
   * @throws IOException
   */
  public void opControlReturn() throws IOException {
    this.write(OonsqlOpcode.CONTROL_RETURN);
  }

}
