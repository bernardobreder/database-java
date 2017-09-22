package org.oonsql.lng.syntax;

import java.io.ByteArrayInputStream;

import junit.framework.Assert;

import org.junit.Test;
import org.oonsql.lng.lexical.LexicalStream;
import org.oonsql.lng.token.WordToken;

public class AbstractParserTest {

  @Test
  public void test() throws Exception {
    Assert.assertTrue(ex(" = ").match('='));
    Assert.assertTrue(ex(" ! ").match('!'));
    Assert.assertTrue(ex(" == ").match(WordToken.EQ));
    Assert.assertTrue(ex(" a ").match(WordToken.ID));
    Assert.assertTrue(ex("a").match(WordToken.ID));
    Assert.assertTrue(ex("or").match(WordToken.OR));
    Assert.assertTrue(ex(" or ").match(WordToken.OR));
    Assert.assertTrue(ex("1.2").match(WordToken.NUM));
    Assert.assertTrue(ex("\"ab\"").match(WordToken.STR));
  }

  @Test
  public void testLookAHead() throws Exception {
    AbstractParser ex = ex("a = 1 or b == \"a\" and true");
    Assert.assertTrue(ex.match(WordToken.ID, 0));
    Assert.assertTrue(ex.match('=', 1));
    Assert.assertTrue(ex.match(WordToken.NUM, 2));
    Assert.assertTrue(ex.match(WordToken.OR, 3));
    Assert.assertTrue(ex.match(WordToken.ID, 4));
    Assert.assertTrue(ex.match(WordToken.EQ, 5));
    Assert.assertTrue(ex.match(WordToken.STR, 6));
    Assert.assertTrue(ex.match(WordToken.AND, 7));
    Assert.assertTrue(ex.match(WordToken.TRUE, 8));
  }

  private AbstractParser ex(String code) throws Exception {
    return new AbstractParser(new LexicalStream(new ByteArrayInputStream(code
      .getBytes("utf-8")))) {
    };
  }

}
