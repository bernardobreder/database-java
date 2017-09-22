package org.oonsql.lng.syntax;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.oonsql.lng.lexical.LexicalStream;
import org.oonsql.lng.node.command.BlockNode;
import org.oonsql.lng.node.command.CommandNode;
import org.oonsql.lng.node.command.ExpressionNode;
import org.oonsql.lng.node.command.ForNode;
import org.oonsql.lng.node.command.IfNode;
import org.oonsql.lng.node.command.RepeatNode;
import org.oonsql.lng.node.command.WhileNode;
import org.oonsql.lng.node.value.ValueNode;
import org.oonsql.lng.node.value.binary.AndNode;
import org.oonsql.lng.node.value.binary.AssignNode;
import org.oonsql.lng.node.value.binary.DivNode;
import org.oonsql.lng.node.value.binary.EqNode;
import org.oonsql.lng.node.value.binary.GeNode;
import org.oonsql.lng.node.value.binary.GtNode;
import org.oonsql.lng.node.value.binary.LeNode;
import org.oonsql.lng.node.value.binary.LtNode;
import org.oonsql.lng.node.value.binary.MulNode;
import org.oonsql.lng.node.value.binary.NeNode;
import org.oonsql.lng.node.value.binary.OrNode;
import org.oonsql.lng.node.value.binary.SubNode;
import org.oonsql.lng.node.value.binary.SumNode;
import org.oonsql.lng.node.value.primitive.BooleanNode;
import org.oonsql.lng.node.value.primitive.IdentifyNode;
import org.oonsql.lng.node.value.primitive.NumberNode;
import org.oonsql.lng.node.value.primitive.StringNode;
import org.oonsql.lng.node.value.ternary.IfValueNode;
import org.oonsql.lng.node.value.unary.NegativeNode;
import org.oonsql.lng.node.value.unary.PosDecNode;
import org.oonsql.lng.node.value.unary.PosIncNode;
import org.oonsql.lng.node.value.unary.PreDecNode;
import org.oonsql.lng.node.value.unary.PreIncNode;
import org.oonsql.lng.token.IdentifyToken;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.token.Token;
import org.oonsql.lng.token.WordToken;

public class SyntaxStreamTest {

  @Test
  public void testInternal() throws Exception {
    SyntaxStream p = ex("a=1");
    eq(new IdentifyToken("a"), p.look());
    eq(Id("a"), p.readIdentify());
    eq(new Token('='), p.look());
    eq(t('='), p.read('='));
    eq(new NumberToken(1), p.look());
    eq(Num(1), p.readNumber());
    eq(null, p.look());
    eq(null, p.look());
  }

  @Test
  public void testPrimitive() throws Exception {
    eq(Id("a"), ex("a").readIdentify());
    eq(Id("a"), ex(" a ").readIdentify());
    eq(Str("a"), ex(" \"a\" ").readString());
    eq(Num(12.3), ex(" 12.3 ").readNumber());
    eq(True(), ex("true").readLiteral());
    eq(False(), ex("false").readLiteral());
    eq(True(), ex(" true ").readLiteral());
    eq(False(), ex(" false ").readLiteral());
  }

  @Test
  public void testNegative() throws Exception {
    eq(new NegativeNode(Num(1)), ex("-1").readUnary());
    eq(new NegativeNode(Num(1)), ex("-1").readValue());
    eq(new NegativeNode(Num(1)), ex(" - 1 ").readUnary());
  }

  @Test
  public void testIncDec() throws Exception {
    eq(new PreDecNode(Id("a")), ex("--a").readUnary());
    eq(new PreIncNode(Id("a")), ex("++a").readUnary());
    eq(new PosDecNode(Id("a")), ex("a--").readUnary());
    eq(new PosIncNode(Id("a")), ex("a++").readUnary());
    eq(new PosIncNode(Id("a")), ex(" a ++ ").readUnary());
    eq(new PreDecNode(Id("a")), ex(" -- a ").readUnary());
  }

  @Test
  public void testOr() throws Exception {
    eq(new OrNode(True(), False()), ex("true or false").readValue());
  }

  @Test
  public void testAnd() throws Exception {
    eq(new AndNode(True(), False()), ex("true and false").readValue());
  }

  @Test
  public void testCompare() throws Exception {
    eq(eqn(True(), False()), ex("true == false").readValue());
    eq(new NeNode(True(), False()), ex("true != false").readValue());
    eq(new GeNode(True(), False()), ex("true >= false").readValue());
    eq(new LeNode(True(), False()), ex("true <= false").readValue());
    eq(new GtNode(True(), False()), ex("true > false").readValue());
    eq(new LtNode(True(), False()), ex("true < false").readValue());
  }

  @Test
  public void testSumSub() throws Exception {
    eq(new SumNode(Num(1), Num(2)), ex("1 + 2").readValue());
    eq(new SubNode(Num(1), Num(2)), ex("1 - 2").readValue());
  }

  @Test
  public void testMulDiv() throws Exception {
    eq(new MulNode(Num(1), Num(2)), ex("1 * 2").readValue());
    eq(new DivNode(Num(1), Num(2)), ex("1 / 2").readValue());
  }

  @Test
  public void testAssign() throws Exception {
    eq(new AssignNode(Id("a"), Num(1)), ex("a=1").readAssign());
  }

  @Test
  public void testIfTernary() throws Exception {
    eq(new IfValueNode(True(), Num(2), Num(1)), ex("true?1:2").readTernary());
  }

  @Test
  public void testBlock() throws Exception {
    eq(emptyBlock(), ex("do end").readCommand());
    eq(simpleBlock(), ex("do do end end").readCommand());
  }

  @Test
  public void testRepeat() throws Exception {
    eq(new RepeatNode(eqn(Num(1), Num(2)), emptyBlock()), ex(
      "repeat do end 1 == 2").readCommand());
  }

  @Test
  public void testWhile() throws Exception {
    eq(new WhileNode(eqn(Num(1), Num(2)), emptyBlock()), ex(
      "while 1 == 2 do end").readCommand());
  }

  @Test
  public void testIf() throws Exception {
    eq(new IfNode(eqn(Num(1), Num(2)), emptyBlock()), ex("if 1 == 2 do end")
      .readCommand());
  }

  @Test
  public void testFor() throws Exception {
    eq(new ForNode(cmds(), True(), cmds(), emptyBlock()), ex("for (;;) do end")
      .readCommand());
    eq(new ForNode(cmds(), eqn(Num(1), Num(2)), cmds(), emptyBlock()), ex(
      "for (; 1 == 2 ;) do end").readCommand());
    eq(new ForNode(cmds(), True(), cmds(new ExpressionNode(True())),
      emptyBlock()), ex("for (;;true) do end").readCommand());
    eq(new ForNode(cmds(new ExpressionNode(True())), True(), cmds(),
      emptyBlock()), ex("for (true;;) do end").readCommand());
    eq(new ForNode(cmds(), False(), cmds(), emptyBlock()), ex(
      "for (;false;) do end").readCommand());
    eq(new ForNode(cmds(new ExpressionNode(eqn(Num(1), Num(2)))), eqn(Num(3),
      Num(4)), cmds(new ExpressionNode(eqn(Num(5), Num(6)))), emptyBlock()),
      ex("for (1==2; 3 == 4 ;5==6) do end").readCommand());

  }

  private List<CommandNode> cmds(CommandNode... nodes) {
    List<CommandNode> list = new ArrayList<CommandNode>(nodes.length);
    for (CommandNode node : nodes) {
      list.add(node);
    }
    return list;
  }

  private EqNode eqn(ValueNode left, ValueNode right) {
    return new EqNode(left, right);
  }

  private BlockNode emptyBlock() {
    return new BlockNode(Arrays.<CommandNode> asList());
  }

  private BlockNode simpleBlock() {
    return new BlockNode(Arrays.<CommandNode> asList(new BlockNode(Arrays
      .<CommandNode> asList())));
  }

  private BooleanNode False() {
    return BooleanNode.build(false);
  }

  private BooleanNode True() {
    return BooleanNode.build(true);
  }

  private StringNode Str(String value) {
    return new StringNode(new StringToken(value));
  }

  private NumberNode Num(double value) {
    return new NumberNode(new NumberToken(value));
  }

  private Token t(int c) {
    return new Token(c);
  }

  private WordToken w(int c) {
    return WordToken.build(c);
  }

  private IdentifyNode Id(String name) {
    return new IdentifyNode(new IdentifyToken(name));
  }

  private SyntaxStream ex(String code) throws Exception {
    return new SyntaxStream(new LexicalStream(new BufferedInputStream(
      new ByteArrayInputStream(code.getBytes("utf-8"))))) {
    };
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
