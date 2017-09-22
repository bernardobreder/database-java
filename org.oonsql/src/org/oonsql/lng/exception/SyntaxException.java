package org.oonsql.lng.exception;

/**
 * Erro sintático
 * 
 * @author bernardobreder
 * 
 */
public class SyntaxException extends ParserException {

  /**
   * Construtor
   */
  public SyntaxException() {
    super();
  }

  /**
   * Construtor
   * 
   * @param message
   * @param e
   */
  public SyntaxException(String message, Throwable e) {
    super(message, e);
  }

  /**
   * Construtor
   * 
   * @param message
   */
  public SyntaxException(String message) {
    super(message);
  }

}
