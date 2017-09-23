package database.syntax;

import java.io.IOException;

import database.lexical.LexicalStream;
import database.lexical.LexicalStream.LexicalException;
import database.node.Node;

/**
 * 
 * 
 * @author Tecgraf
 */
public class AsmSyntaxStream extends AbstractSyntaxStream {

  /**
   * @param input
   */
  public AsmSyntaxStream(LexicalStream input) {
    super(input);
  }

  /**
   * @return node
   * @throws LexicalException
   * @throws IOException
   * @throws SyntaxException
   */
  public Node read() throws IOException, LexicalException, SyntaxException {
    throw new SyntaxException("expected: <select>");
  }

}