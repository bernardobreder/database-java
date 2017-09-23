package database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import database.lexical.LexicalStream;
import database.lexical.DbLexicalStream;
import database.lexical.DbToken;
import database.lexical.Token;
import database.lexical.LexicalStream.LexicalException;

/**
 * {@link LexicalStream}
 * 
 * @author Tecgraf
 */
public class LexicalStreamTest {

  /**
   * @throws IOException
   * @throws LexicalException
   */
  @Test
  public void test() throws IOException, LexicalException {
    LexicalStream stream = createStream("select * from a");
    Assert.assertEquals(new Token(DbToken.SELECT, "select", 1, 1), stream
      .readToken());
    Assert.assertEquals(new Token('*', "*", 1, 8), stream.readToken());
    Assert.assertEquals(new Token(DbToken.FROM, "from", 1, 10), stream
      .readToken());
    Assert.assertEquals(new Token(Token.ID, "a", 1, 15), stream.readToken());
    Assert.assertEquals(new Token(Token.EOF, "<eof>", 1, 16), stream
      .readToken());
  }

  /**
   * @param code
   * @return stream
   * @throws IOException
   * @throws LexicalException
   * @throws UnsupportedEncodingException
   */
  public LexicalStream createStream(String code) throws IOException,
    LexicalException {
    return new DbLexicalStream(new ByteArrayInputStream(code.getBytes("utf-8")));
  }

}
