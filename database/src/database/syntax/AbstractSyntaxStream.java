package database.syntax;

import java.io.IOException;

import database.lexical.LexicalStream;
import database.lexical.LexicalStream.LexicalException;
import database.lexical.Token;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class AbstractSyntaxStream {

  /** Stream de tokens */
  private LexicalStream input;

  /**
   * @param input
   */
  public AbstractSyntaxStream(LexicalStream input) {
    this.input = input;
  }

  /**
   * @param type
   * @return verifica o tipo
   * @throws IOException
   * @throws LexicalException
   */
  public boolean is(int type) throws IOException, LexicalException {
    return input.is(type);
  }

  /**
   * @param type
   * @return é do tipo
   * @throws IOException
   * @throws LexicalException
   */
  public boolean can(int type) throws IOException, LexicalException {
    return input.can(type);
  }

  /**
   * @param type
   * @return token
   * @throws LexicalException
   * @throws IOException
   */
  public Token read(int type) throws LexicalException, IOException {
    return input.read(type);
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class SyntaxException extends Exception {

    /**
     * @param message
     */
    public SyntaxException(String message) {
      super(message);
    }

  }

}
