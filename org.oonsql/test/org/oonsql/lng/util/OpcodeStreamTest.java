package org.oonsql.lng.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.oonsql.lng.exception.LexicalException;
import org.oonsql.lng.exception.ParserException;
import org.oonsql.lng.lexical.LexicalStream;
import org.oonsql.lng.node.AbstractNode;
import org.oonsql.lng.node.command.CommandNode;
import org.oonsql.lng.syntax.SyntaxStream;

public class OpcodeStreamTest {

  @SuppressWarnings("resource")
  @Test
  public void testIndex() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    StreamOpcodeOutputStream op = new StreamOpcodeOutputStream(out);
    op.writeIndex(0x7F);
    op.writeIndex(0x7FF);
    op.writeIndex(0xFFFF);
    op.writeIndex(0x1FFFFF);
    op.writeIndex(0x3FFFFFF);
    op.writeIndex(0x7FFFFFFF);
    StreamOpcodeInputStream in =
      new StreamOpcodeInputStream(new ByteArrayInputStream(out.toByteArray()));
    Assert.assertEquals(0x7F, in.readIndex());
    Assert.assertEquals(0x7FF, in.readIndex());
    Assert.assertEquals(0xFFFF, in.readIndex());
    Assert.assertEquals(0x1FFFFF, in.readIndex());
    Assert.assertEquals(0x3FFFFFF, in.readIndex());
    Assert.assertEquals(0x7FFFFFFF, in.readIndex());
  }

  @Test
  public void testInteger() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    StreamOpcodeOutputStream op = new StreamOpcodeOutputStream(out);
    op.writeInt(0);
    op.writeInt(63);
    op.writeInt(-63);
    op.writeInt(8191);
    op.writeInt(-8191);
    op.writeInt(1048575);
    op.writeInt(-1048575);
    op.writeInt(134217727);
    op.writeInt(-134217727);
    op.writeInt(2147483647);
    op.writeInt(-2147483647);
    StreamOpcodeInputStream in =
      new StreamOpcodeInputStream(new ByteArrayInputStream(out.toByteArray()));
    Assert.assertEquals(0, in.readInteger());
    Assert.assertEquals(63, in.readInteger());
    Assert.assertEquals(-63, in.readInteger());
    Assert.assertEquals(8191, in.readInteger());
    Assert.assertEquals(-8191, in.readInteger());
    Assert.assertEquals(1048575, in.readInteger());
    Assert.assertEquals(-1048575, in.readInteger());
    Assert.assertEquals(134217727, in.readInteger());
    Assert.assertEquals(-134217727, in.readInteger());
    Assert.assertEquals(2147483647, in.readInteger());
    Assert.assertEquals(-2147483647, in.readInteger());
    in.close();
    op.close();
  }

  public void testWriteNumber() throws IOException {
    double[] values =
      { 50.2, -20.4532, 1, 0, -0, 0., 4321341, Double.MAX_VALUE,
          Double.MIN_VALUE, 8473.3212343124132, Double.NaN };
    for (double value : values) {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      StreamOpcodeOutputStream out = new StreamOpcodeOutputStream(bytes);
      out.writeDouble(value);
      out.close();
      StreamOpcodeInputStream in =
        new StreamOpcodeInputStream(new ByteArrayInputStream(bytes
          .toByteArray()));
      Assert.assertEquals(value, in.readDouble());
      in.close();
    }
  }

  @Test
  public void parser() throws Exception {
    ex("1");
    ex("true");
    ex("false");
    ex("\"a\"");
    ex("a");
    ex("-1");
    ex("!true");
    ex("true?1:2");
    ex("true or false");
    ex("true and false");
    ex("1 > 1");
    ex("1 >= 1");
    ex("1 < 1");
    ex("1 <= 1");
    ex("1 == 1");
    ex("1 != 1");
    ex("1 > 1");
    ex("1 + 1");
    ex("1 - 1");
    ex("1 * 1");
    ex("1 / 1");
    ex("a = 1");
    ex("a++");
    ex("a--");
    ex("++a");
    ex("--a");
    ex("if true do end");
    ex("while true do end");
    ex("repeat do end true");
    ex("for (true,true;true;true,true) do end");
    ex("do do end end");
  }

  private void ex(String code) throws IOException, ParserException,
    LexicalException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    StreamOpcodeOutputStream out = new StreamOpcodeOutputStream(bytes);
    CommandNode node =
      new SyntaxStream(new LexicalStream(new BufferedInputStream(
        new ByteArrayInputStream(code.getBytes("utf-8"))))).readCommand();
    node.write(out);
    AbstractNode.read(new StreamOpcodeInputStream(new BufferedInputStream(
      new ByteArrayInputStream(bytes.toByteArray()))));
  }

}
