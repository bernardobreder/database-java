package database.syntax;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import database.lexical.DbToken;
import database.lexical.LexicalStream;
import database.lexical.LexicalStream.LexicalException;
import database.lexical.Token;
import database.node.Node;
import database.node.select.SelectColumnNode;
import database.node.select.SelectFromNode;
import database.node.select.SelectNode;
import database.node.value.ValueNode;
import database.node.value.binary.AndValueNode;
import database.node.value.binary.EqualCompareValueNode;
import database.node.value.binary.NotEqualCompareValueNode;
import database.node.value.binary.OrValueNode;
import database.node.value.literal.BooleanValueNode;
import database.node.value.literal.IdentifyValueNode;
import database.node.value.literal.NumberValueNode;
import database.node.value.literal.StringValueNode;
import database.node.value.unary.NotValueNode;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbSyntaxStream extends AbstractSyntaxStream {

  /**
   * @param input
   */
  public DbSyntaxStream(LexicalStream input) {
    super(input);
  }

  /**
   * @return node
   * @throws LexicalException
   * @throws IOException
   * @throws SyntaxException
   */
  public Node read() throws IOException, LexicalException, SyntaxException {
    if (this.is(DbToken.SELECT)) {
      return this.readSelect();
    }
    else {
      throw new SyntaxException("expected: 'select'");
    }
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public Node readSelect() throws IOException, LexicalException,
    SyntaxException {
    Token token = this.read(DbToken.SELECT);
    SelectColumnNode columns = this.readSelectColumns();
    this.read(DbToken.FROM);
    List<SelectFromNode> froms = this.readSelectFrom();
    ValueNode where = null;
    if (this.can(DbToken.WHERE)) {
      where = this.readSelectWhere();
    }
    if (this.can(DbToken.GROUP)) {
      this.read(DbToken.BY);
      this.readSelectGroupBy();
    }
    if (this.can(DbToken.ORDER)) {
      this.readSelectOrder();
    }
    return new SelectNode(token, columns, froms, where);
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public SelectColumnNode readSelectColumns() throws IOException,
    LexicalException, SyntaxException {
    if (this.is('*')) {
      return new SelectColumnNode.All(this.read('*'));
    }
    List<ValueNode> columns = new ArrayList<ValueNode>();
    do {
      ValueNode valueNode = this.readValue();
      columns.add(valueNode);
    } while (this.can(','));
    return new SelectColumnNode.ColumnSet(columns);
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public List<SelectFromNode> readSelectFrom() throws IOException,
    LexicalException, SyntaxException {
    List<SelectFromNode> tables = new ArrayList<SelectFromNode>();
    do {
      Token table = this.read(DbToken.ID);
      Token alias = table;
      if (this.is(DbToken.ID)) {
        alias = this.read(DbToken.ID);
      }
      tables.add(new SelectFromNode.Table(alias, table));
    } while (this.can(','));
    return tables;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readSelectWhere() throws IOException, LexicalException,
    SyntaxException {
    return this.readValue();
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readValue() throws IOException, LexicalException,
    SyntaxException {
    return readTernaryValue();
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readTernaryValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readOrValue();
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readOrValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readAndValue();
    while (this.can(DbToken.OR)) {
      ValueNode right = readAndValue();
      left = new OrValueNode(left, right);
    }
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readAndValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readCompareValue();
    while (this.can(DbToken.AND)) {
      ValueNode right = readCompareValue();
      left = new AndValueNode(left, right);
    }
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readCompareValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readSumValue();
    while (this.is('=') || this.is(DbToken.NOT_EQUAL)) {
      if (this.can('=')) {
        ValueNode right = readSumValue();
        left = new EqualCompareValueNode(left, right);
      }
      else if (this.can(DbToken.NOT_EQUAL)) {
        ValueNode right = readSumValue();
        left = new NotEqualCompareValueNode(left, right);
      }
    }
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readSumValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readMulValue();
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readMulValue() throws IOException, LexicalException,
    SyntaxException {
    ValueNode left = readUnaryValue();
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readUnaryValue() throws IOException, LexicalException,
    SyntaxException {
    if (this.can('!')) {
      return new NotValueNode(readLiteralValue());
    }
    ValueNode left = readLiteralValue();
    return left;
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public ValueNode readLiteralValue() throws IOException, LexicalException,
    SyntaxException {
    if (this.is(Token.NUMBER)) {
      Token token = this.read(Token.NUMBER);
      return new NumberValueNode(token);
    }
    else if (this.is(Token.STRING)) {
      Token token = this.read(Token.STRING);
      return new StringValueNode(token);
    }
    else if (this.is(DbToken.TRUE)) {
      Token token = this.read(DbToken.TRUE);
      return new BooleanValueNode(token, true);
    }
    else if (this.is(DbToken.FALSE)) {
      Token token = this.read(DbToken.FALSE);
      return new BooleanValueNode(token, false);
    }
    else if (this.is(Token.ID)) {
      Token left = this.read(Token.ID);
      Token right = null;
      if (this.can('.')) {
        right = this.read(Token.ID);
      }
      return new IdentifyValueNode(left, right);
    }
    else {
      throw new SyntaxException("expected: <number>, <string>, <id>");
    }
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public Node readSelectGroupBy() throws IOException, LexicalException,
    SyntaxException {
    throw new SyntaxException("not implemented");
  }

  /**
   * @return node
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   */
  public Node readSelectOrder() throws IOException, LexicalException,
    SyntaxException {
    throw new SyntaxException("not implemented");
  }

}
