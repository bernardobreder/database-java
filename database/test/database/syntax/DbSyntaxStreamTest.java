package database.syntax;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import database.lexical.DbLexicalStream;
import database.lexical.LexicalStream.LexicalException;
import database.node.Node;
import database.syntax.AbstractSyntaxStream.SyntaxException;

public class DbSyntaxStreamTest {

  @Test
  public void test() throws Exception {
    createNode("select * from dual");
    createNode("select * from dual where true");
    createNode("select * from dual where 1 = 1");
    createNode("select * from dual where 1 != 1");
    createNode("select * from dual where true");
    createNode("select * from dual where false");
    createNode("select * from dual where 1");
    createNode("select * from dual where \"a\"");
    createNode("select a.a, b.b from a, b");
    createNode("select a.a, b.b from a, b where 1 = 1");
    createNode("select a.a, b.b from a, b where id = 1");
    createNode("select a.a, b.b from a, b where a.id = 1");
  }

  /**
   * @param code
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   * @throws UnsupportedEncodingException
   */
  public Node createNode(String code) throws IOException, LexicalException,
    SyntaxException {
    return new DbSyntaxStream(new DbLexicalStream(new ByteArrayInputStream(code
      .getBytes("utf-8")))).read();
  }

}
