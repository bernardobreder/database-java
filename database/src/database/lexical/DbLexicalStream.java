package database.lexical;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A classe LexicalStream é responsável pela leitura de uma InputStream e
 * retornar tokens da stream.
 * 
 * @author Tecgraf
 */
public class DbLexicalStream extends LexicalStream {

  /** Keyword */
  public static final Map<String, Integer> keywords =
    new HashMap<String, Integer>();

  static {
    keywords.put("select", DbToken.SELECT);
    keywords.put("from", DbToken.FROM);
    keywords.put("where", DbToken.WHERE);
    keywords.put("order", DbToken.ORDER);
    keywords.put("group", DbToken.GROUP);
    keywords.put("by", DbToken.BY);
    keywords.put("true", DbToken.TRUE);
    keywords.put("false", DbToken.FALSE);
    keywords.put("like", DbToken.LIKE);
    keywords.put("and", DbToken.AND);
    keywords.put("or", DbToken.OR);
  }

  /**
   * @param input
   * @throws IOException
   * @throws LexicalException
   */
  public DbLexicalStream(InputStream input) throws IOException,
    LexicalException {
    super(input);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected int getIdType(String token) {
    Integer type = keywords.get(token.toLowerCase());
    if (type != null) {
      return type;
    }
    return super.getIdType(token);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected int getSymbolType(int symbol, int next) {
    if (symbol == '!' && next == '=') {
      return DbToken.NOT_EQUAL;
    }
    else if (symbol == '>' && next == '=') {
      return DbToken.GREATER_EQUAL;
    }
    else if (symbol == '<' && next == '=') {
      return DbToken.LOWER_EQUAL;
    }
    else {
      return super.getSymbolType(symbol, next);
    }
  }

}
