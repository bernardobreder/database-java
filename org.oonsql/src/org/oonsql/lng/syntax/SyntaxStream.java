package org.oonsql.lng.syntax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.oonsql.lng.exception.ParserException;
import org.oonsql.lng.exception.SyntaxException;
import org.oonsql.lng.lexical.AbstractLexicalStream;
import org.oonsql.lng.node.command.BlockNode;
import org.oonsql.lng.node.command.CommandNode;
import org.oonsql.lng.node.command.ExpressionNode;
import org.oonsql.lng.node.command.ForNode;
import org.oonsql.lng.node.command.IfNode;
import org.oonsql.lng.node.command.NullNode;
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
import org.oonsql.lng.node.value.unary.InvertNode;
import org.oonsql.lng.node.value.unary.NegativeNode;
import org.oonsql.lng.node.value.unary.PosDecNode;
import org.oonsql.lng.node.value.unary.PosIncNode;
import org.oonsql.lng.node.value.unary.PreDecNode;
import org.oonsql.lng.node.value.unary.PreIncNode;
import org.oonsql.lng.token.IdentifyToken;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.token.WordToken;

/**
 * Classe responsável por fazer um parser em cima de uma stream
 * 
 * @author bernardobreder
 * 
 */
public class SyntaxStream extends AbstractParser {

  /**
   * Construtor
   * 
   * @param lexer
   */
  public SyntaxStream(AbstractLexicalStream lexer) {
    super(lexer);
  }

  /**
   * Realiza uma leitura de um comando
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readCommand() throws IOException, ParserException {
    switch (this.lookTag()) {
      case ';':
        this.next();
        return NullNode.INSTANCE;
      case WordToken.IF:
        return this.readIf();
      case WordToken.FOR:
        return this.readFor();
      case WordToken.WHILE:
        return this.readWhile();
      case WordToken.REPEAT:
        return this.readRepeat();
      case WordToken.DO:
        return this.readBlock();
      default:
        return this.readExpression();
    }
  }

  /**
   * Realiza uma leitura do comando if
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readIf() throws IOException, ParserException {
    if (!this.match(WordToken.IF)) {
      throw new SyntaxException("expected: if");
    }
    this.next();
    ValueNode condValue = this.readValue();
    CommandNode command = this.readCommand();
    return new IfNode(condValue, command);
  }

  /**
   * Realiza uma leitura do comando for
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readFor() throws IOException, ParserException {
    if (!this.match(WordToken.FOR)) {
      throw new SyntaxException("expected: for");
    }
    this.next();
    if (!this.match('(')) {
      throw new SyntaxException("expected: for");
    }
    this.next();
    List<CommandNode> initValues;
    ValueNode condValue = BooleanNode.build(true);
    List<CommandNode> posValues;
    if (this.match(';')) {
      initValues = new ArrayList<CommandNode>(0);
    }
    else {
      initValues = new ArrayList<CommandNode>(1);
      initValues.add(this.readCommand());
      while (this.match(',')) {
        this.next();
        initValues.add(this.readCommand());
      }
      if (!this.match(';')) {
        throw new SyntaxException("expected: ;");
      }
    }
    this.next();
    if (!this.match(';')) {
      condValue = this.readValue();
      if (!this.match(';')) {
        throw new SyntaxException("expected: ;");
      }
    }
    this.next();
    if (this.match(')')) {
      posValues = new ArrayList<CommandNode>(0);
    }
    else {
      posValues = new ArrayList<CommandNode>(1);
      posValues.add(this.readCommand());
      while (this.match(',')) {
        this.next();
        posValues.add(this.readCommand());
      }
      if (!this.match(')')) {
        throw new SyntaxException("expected: )");
      }
    }
    this.next();
    CommandNode command = this.readCommand();
    return new ForNode(initValues, condValue, posValues, command);
  }

  /**
   * Realiza uma leitura do comando while
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readWhile() throws IOException, ParserException {
    if (!this.match(WordToken.WHILE)) {
      throw new SyntaxException("expected: while");
    }
    this.next();
    ValueNode condValue = this.readValue();
    CommandNode command = this.readCommand();
    return new WhileNode(condValue, command);
  }

  /**
   * Realiza uma leitura do comando repat
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readRepeat() throws IOException, ParserException {
    if (!this.match(WordToken.REPEAT)) {
      throw new SyntaxException("expected: repeat");
    }
    this.next();
    CommandNode command = this.readCommand();
    ValueNode condValue = this.readValue();
    return new RepeatNode(condValue, command);
  }

  /**
   * Realiza uma leitura de um bloco de comandos
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readBlock() throws IOException, ParserException {
    if (!this.match(WordToken.DO)) {
      throw new SyntaxException("expected: do");
    }
    this.next();
    if (this.match(WordToken.END)) {
      this.next();
      return new BlockNode(new ArrayList<CommandNode>(0));
    }
    List<CommandNode> list = new ArrayList<CommandNode>(10);
    while (!this.match(WordToken.END)) {
      list.add(this.readCommand());
    }
    this.next();
    return new BlockNode(list);
  }

  /**
   * Realiza uma leitura do comando if
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public CommandNode readExpression() throws IOException, ParserException {
    return new ExpressionNode(this.readValue());
  }

  /**
   * Realiza uma leitura de um valor de qualquer tipo
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readValue() throws IOException, ParserException {
    return this.readTernary();
  }

  /**
   * Realiza uma leitura de um valor de um valor ternário se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readTernary() throws IOException, ParserException {
    ValueNode left = this.readOr();
    while (this.match('?')) {
      this.next();
      ValueNode center = this.readOr();
      this.read(':');
      ValueNode right = this.readOr();
      left = new IfValueNode(left, right, center);
    }
    return left;
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readOr() throws IOException, ParserException {
    ValueNode left = this.readAnd();
    while (this.match(WordToken.OR)) {
      this.next();
      ValueNode right = this.readAnd();
      left = new OrNode(left, right);
    }
    return left;
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readAnd() throws IOException, ParserException {
    ValueNode left = this.readCompare();
    while (this.match(WordToken.AND)) {
      this.next();
      ValueNode right = this.readCompare();
      left = new AndNode(left, right);
    }
    return left;
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readCompare() throws IOException, ParserException {
    ValueNode left = this.readSumSub();
    for (;;) {
      switch (this.lookTag()) {
        case WordToken.EQ: {
          this.next();
          ValueNode right = this.readSumSub();
          left = new EqNode(left, right);
          break;
        }
        case WordToken.NE: {
          this.next();
          ValueNode right = this.readSumSub();
          left = new NeNode(left, right);
          break;
        }
        case WordToken.GE: {
          this.next();
          ValueNode right = this.readSumSub();
          left = new GeNode(left, right);
          break;
        }
        case WordToken.LE: {
          this.next();
          ValueNode right = this.readSumSub();
          left = new LeNode(left, right);
          break;
        }
        case '>': {
          this.next();
          ValueNode right = this.readSumSub();
          left = new GtNode(left, right);
          break;
        }
        case '<': {
          this.next();
          ValueNode right = this.readSumSub();
          left = new LtNode(left, right);
          break;
        }
        default: {
          return left;
        }
      }
    }
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readSumSub() throws IOException, ParserException {
    ValueNode left = this.readMulDiv();
    for (;;) {
      switch (this.lookTag()) {
        case '+': {
          this.next();
          ValueNode right = this.readMulDiv();
          left = new SumNode(left, right);
          break;
        }
        case '-': {
          this.next();
          ValueNode right = this.readMulDiv();
          left = new SubNode(left, right);
          break;
        }
        default: {
          return left;
        }
      }
    }
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readMulDiv() throws IOException, ParserException {
    ValueNode left = this.readUnary();
    for (;;) {
      switch (this.lookTag()) {
        case '*': {
          this.next();
          ValueNode right = this.readUnary();
          left = new MulNode(left, right);
          break;
        }
        case '/': {
          this.next();
          ValueNode right = this.readUnary();
          left = new DivNode(left, right);
          break;
        }
        default: {
          return left;
        }
      }
    }
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readUnary() throws IOException, ParserException {
    switch (this.lookTag()) {
      case '-': {
        this.next();
        ValueNode left = this.readNumber();
        return new NegativeNode(left);
      }
      case '!': {
        this.next();
        ValueNode left = this.readLiteral();
        return new InvertNode(left);
      }
      case WordToken.DEC: {
        this.next();
        ValueNode left = this.readLeftValue();
        return new PreDecNode(left);
      }
      case WordToken.INC: {
        this.next();
        ValueNode left = this.readLeftValue();
        return new PreIncNode(left);
      }
      default: {
        ValueNode left = this.readLiteral();
        switch (this.lookTag()) {
          case WordToken.DEC: {
            this.next();
            return new PosDecNode(left);
          }
          case WordToken.INC: {
            this.next();
            return new PosIncNode(left);
          }
          default: {
            return left;
          }
        }
      }
    }
  }

  /**
   * Realiza uma leitura de um valor de um valor lógico se possível
   * 
   * @return leitura de um valor
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readLiteral() throws IOException, ParserException {
    switch (this.lookTag()) {
      case WordToken.NUM:
        return this.readNumber();
      case WordToken.STR:
        return this.readString();
      case WordToken.TRUE:
        return this.readTrue();
      case WordToken.FALSE:
        return this.readFalse();
      case WordToken.THIS:
      case WordToken.ID:
        return this.readAssign();
      default:
        throw new SyntaxException("expected: <expression>");
    }
  }

  /**
   * Realiza uma leitura do valor lógico falso
   * 
   * @return leitura de uma associação
   * @throws IOException
   * @throws ParserException
   */
  public BooleanNode readFalse() throws IOException, ParserException {
    this.next();
    return BooleanNode.build(false);
  }

  /**
   * Realiza uma leitura do valor lógico verdadeiro
   * 
   * @return leitura de uma associação
   * @throws IOException
   * @throws ParserException
   */
  public BooleanNode readTrue() throws IOException, ParserException {
    this.next();
    return BooleanNode.build(true);
  }

  /**
   * Realiza uma leitura de uma associação se possível
   * 
   * @return leitura de uma associação
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readAssign() throws IOException, ParserException {
    ValueNode left = this.readLeftValue();
    while (this.match('=')) {
      this.next();
      ValueNode right = this.readValue();
      left = new AssignNode(left, right);
    }
    return left;
  }

  /**
   * Realiza uma leitura de um left value
   * 
   * @return node
   * @throws IOException
   * @throws ParserException
   */
  public ValueNode readLeftValue() throws IOException, ParserException {
    ValueNode left;
    switch (this.lookTag()) {
      case WordToken.ID:
        left = this.readIdentify();
        break;
      default:
        throw new RuntimeException();
    }
    return left;
  }

  /**
   * Realiza a leitura de um número
   * 
   * @return leitura de um número
   * @throws IOException
   * @throws ParserException
   */
  public NumberNode readNumber() throws IOException, ParserException {
    NumberToken token = (NumberToken) this.look();
    this.next();
    return new NumberNode(token);
  }

  /**
   * Realiza a leitura de uma String
   * 
   * @return leitura de uma String
   * @throws IOException
   * @throws ParserException
   */
  public StringNode readString() throws IOException, ParserException {
    StringToken token = (StringToken) this.look();
    this.next();
    return new StringNode(token);
  }

  /**
   * Realiza uma leitura de um identificador
   * 
   * @return leitura de um identificador
   * @throws IOException
   * @throws ParserException
   */
  public IdentifyNode readIdentify() throws IOException, ParserException {
    IdentifyNode node = new IdentifyNode((IdentifyToken) this.look());
    this.next();
    return node;
  }

}
