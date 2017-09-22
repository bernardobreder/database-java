package org.oonsql.lng.exception;

/**
 * Erro de parser tanto sintático quanto lexical
 * 
 * @author bernardobreder
 * 
 */
public abstract class ParserException extends Exception {

  /**
   * Construtor
   */
  public ParserException() {
    super();
  }

  /**
   * Construtor
   * 
   * @param message
   * @param e
   */
  public ParserException(String message, Throwable e) {
    super(message, e);
  }

  /**
   * Construtor
   * 
   * @param message
   */
  public ParserException(String message) {
    super(message);
  }

}
