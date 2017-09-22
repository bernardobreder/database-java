package org.oonsql.lng.lexical;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.oonsql.lng.token.IdentifyToken;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.token.Token;
import org.oonsql.lng.token.WordToken;

public class LexicalStreamTest {

  @Test
  public void testNumToken() throws Exception {
    eq(new NumberToken(0), exNumber("0"));
    eq(new NumberToken(1), exNumber("0x1"));
    eq(new NumberToken(16), exNumber("0x10"));
    eq(new NumberToken(255), exNumber("0xFF"));
    eq(new NumberToken(90), exNumber("0x5A"));
    eq(new NumberToken(1), exNumber("1"));
    eq(new NumberToken(12), exNumber("12"));
    eq(new NumberToken(123), exNumber("123"));
    eq(new NumberToken(0.), exNumber("0."));
    eq(new NumberToken(0.1), exNumber("0.1"));
    eq(new NumberToken(0.12), exNumber("0.12"));
    eq(new NumberToken(0.123), exNumber("0.123"));
    eq(new NumberToken(123456789), exNumber("123456789"));
    eq(new NumberToken(1), exNumber(" 1"));
    eq(new NumberToken(1), exNumber("1 "));
    eq(new NumberToken(1), exNumber(" 1 "));
  }

  @Test
  public void testStringToken() throws Exception {
    eq(new StringToken(""), exString("\"\""));
    eq(new StringToken("a"), exString("\"a\""));
    eq(new StringToken("ab"), exString("\"ab\""));
    eq(new StringToken("abc"), exString("\"abc\""));
    eq(new StringToken("ação"), exString("\"ação\""));
    eq(new StringToken("\\"), exString("\"\\\\\""));
    eq(new StringToken("\n"), exString("\"\\n\""));
    eq(new StringToken("\r"), exString("\"\\r\""));
    eq(new StringToken("\t"), exString("\"\\t\""));
    eq(new StringToken("\b"), exString("\"\\b\""));
    eq(new StringToken("\f"), exString("\"\\f\""));
    eq(new StringToken("\r\n"), exString("\"\\r\\n\""));
    eq(new StringToken("\r\n\t"), exString("\"\\r\\n\\t\""));
    eq(new StringToken("a"), exString(" \"a\""));
    eq(new StringToken("a"), exString("\"a\" "));
    eq(new StringToken("a"), exString(" \"a\" "));
  }

  @Test
  public void testWordToken() throws Exception {
    eq(WordToken.build("!="), exSymbol("!="));
    eq(new Token('!'), exSymbol("!"));
    eq(new Token('!'), exSymbol(" ! "));
    eq(WordToken.build("!="), exSymbol(" != "));
    eq(WordToken.build("if"), exWord("if"));
    eq(WordToken.build("do"), exWord("do"));
    eq(WordToken.build("end"), exWord("end"));
    eq(WordToken.build("for"), exWord("for"));
    eq(WordToken.build("and"), exWord("and"));
    eq(WordToken.build("or"), exWord("or"));
    eq(WordToken.build("if"), exWord(" if"));
    eq(WordToken.build("if"), exWord("if "));
    eq(WordToken.build("if"), exWord(" if "));
  }

  @Test
  public void testMultiple() throws Exception {
    LexicalStream ex = ex("a=1;");
    eq(new IdentifyToken("a"), ex.readToken());
    eq(new Token('='), ex.readToken());
    eq(new NumberToken(1), ex.readToken());
    eq(new Token(';'), ex.readToken());
    eq(null, ex.readToken());
    eq(null, ex.readToken());
  }

  @Test
  public void testIdentifyToken() throws Exception {
  }

  @Test
  public void testDoc() throws Exception {
    LexicalStream lexer = ex("/*b*/");
    Assert.assertEquals('/', lexer.look(0));
    Assert.assertEquals('*', lexer.look(1));
    Assert.assertEquals('b', lexer.look(2));
    Assert.assertEquals('*', lexer.look(3));
    Assert.assertEquals('/', lexer.look(4));
  }

  @Test
  public void testDocToken() throws Exception {
    LexicalStream lexer = ex("a/*b*/c");
    Assert.assertEquals(new IdentifyToken("a"), lexer.readToken());
    Assert.assertEquals(new IdentifyToken("c"), lexer.readToken());
    Assert.assertEquals(null, lexer.readToken());
  }

  @Test
  public void testDocLineToken() throws Exception {
    LexicalStream lexer = ex("a//b\r\nc");
    Assert.assertEquals(new IdentifyToken("a"), lexer.readToken());
    Assert.assertEquals(new IdentifyToken("c"), lexer.readToken());
    Assert.assertEquals(null, lexer.readToken());
  }

  @Test
  public void testReadDocToken() throws Exception {
    LexicalStream lexer = ex("a/*b*/c");
    Assert.assertEquals(new IdentifyToken("a"), lexer.readToken());
    Assert.assertEquals(new StringToken("b"), lexer.readDocument());
    Assert.assertEquals(new IdentifyToken("c"), lexer.readToken());
    Assert.assertEquals(null, lexer.readToken());
  }

  @Test
  public void testReadDocLineToken() throws Exception {
    LexicalStream lexer = ex("a//b\r\nc");
    Assert.assertEquals(new IdentifyToken("a"), lexer.readToken());
    Assert.assertEquals(new StringToken("b"), lexer.readDocument());
    Assert.assertEquals(new IdentifyToken("c"), lexer.readToken());
    Assert.assertEquals(null, lexer.readToken());
  }

  @Test
  public void testLookAHead() throws Exception {
    LexicalStream lexer = ex("abcdef");
    Assert.assertEquals('a', lexer.look());
    Assert.assertEquals('a', lexer.look(0));
    Assert.assertEquals('b', lexer.look(1));
    Assert.assertEquals('c', lexer.look(2));
    Assert.assertEquals('d', lexer.look(3));
    Assert.assertEquals('e', lexer.look(4));
    Assert.assertEquals('f', lexer.look(5));
    Assert.assertEquals(-1, lexer.look(6));
    Assert.assertEquals('a', lexer.look());
    lexer.next();
    Assert.assertEquals('b', lexer.look(0));
    Assert.assertEquals('b', lexer.look());
    Assert.assertEquals('c', lexer.look(1));
    Assert.assertEquals('d', lexer.look(2));
    Assert.assertEquals('e', lexer.look(3));
    Assert.assertEquals('f', lexer.look(4));
    Assert.assertEquals(-1, lexer.look(5));
    Assert.assertEquals('b', lexer.look());
    lexer.next();
    Assert.assertEquals('c', lexer.look());
    Assert.assertEquals('c', lexer.look(0));
    Assert.assertEquals('d', lexer.look(1));
    Assert.assertEquals('e', lexer.look(2));
    Assert.assertEquals('f', lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals('c', lexer.look());
    lexer.next();
    Assert.assertEquals('d', lexer.look(0));
    Assert.assertEquals('d', lexer.look());
    Assert.assertEquals('e', lexer.look(1));
    Assert.assertEquals('f', lexer.look(2));
    Assert.assertEquals(-1, lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals('d', lexer.look());
    lexer.next();
    Assert.assertEquals('e', lexer.look());
    Assert.assertEquals('e', lexer.look(0));
    Assert.assertEquals('f', lexer.look(1));
    Assert.assertEquals(-1, lexer.look(2));
    Assert.assertEquals(-1, lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals('e', lexer.look());
    lexer.next();
    Assert.assertEquals('f', lexer.look(0));
    Assert.assertEquals('f', lexer.look());
    Assert.assertEquals(-1, lexer.look(1));
    Assert.assertEquals(-1, lexer.look(2));
    Assert.assertEquals(-1, lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals('f', lexer.look());
    lexer.next();
    Assert.assertEquals(-1, lexer.look());
    Assert.assertEquals(-1, lexer.look(0));
    Assert.assertEquals(-1, lexer.look(1));
    Assert.assertEquals(-1, lexer.look(2));
    Assert.assertEquals(-1, lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals(-1, lexer.look());
    lexer.next();
    Assert.assertEquals(-1, lexer.look(0));
    Assert.assertEquals(-1, lexer.look());
    Assert.assertEquals(-1, lexer.look(1));
    Assert.assertEquals(-1, lexer.look(2));
    Assert.assertEquals(-1, lexer.look(3));
    Assert.assertEquals(-1, lexer.look(4));
    Assert.assertEquals(-1, lexer.look());
  }

  @Test
  public void testLookAHeadError() throws Exception {
    LexicalStream lexer = ex("abcdefghi");
    Assert.assertEquals('a', lexer.look(0));
    Assert.assertEquals('b', lexer.look(1));
    Assert.assertEquals('c', lexer.look(2));
    Assert.assertEquals('d', lexer.look(3));
    Assert.assertEquals('e', lexer.look(4));
    Assert.assertEquals('f', lexer.look(5));
    Assert.assertEquals('g', lexer.look(6));
    Assert.assertEquals('h', lexer.look(7));
    Assert.assertEquals('i', lexer.look(8));
    Assert.assertEquals(-1, lexer.look(9));
  }

  private NumberToken exNumber(String exp) throws Exception {
    return ex(exp).readNumber();
  }

  private StringToken exString(String exp) throws Exception {
    return ex(exp).readString();
  }

  private WordToken exWord(String exp) throws Exception {
    return ex(exp).readWord();
  }

  private Token exSymbol(String exp) throws Exception {
    return ex(exp).readSymbol();
  }

  private LexicalStream ex(String exp) throws IOException {
    return new LexicalStream(new BufferedInputStream(new ByteArrayInputStream(
      exp.getBytes("utf-8"))));
  }

  private void eq(NumberToken expected, NumberToken actual) {
    if (!expected.equals(actual)) {
      if (Math.abs(expected.value - actual.value) > 0.0000000001) {
        Assert.fail("expected:<" + expected.value + "> but was:<"
          + actual.value + ">");
      }
    }
  }

  private void eq(Object expected, Object actual) throws Exception {
    if (expected == null) {
      Assert.assertNull(actual);
    }
    else {
      expected.toString();
      actual.toString();
      expected.hashCode();
      actual.hashCode();
      Assert.assertEquals(expected, actual);
    }
  }

}
