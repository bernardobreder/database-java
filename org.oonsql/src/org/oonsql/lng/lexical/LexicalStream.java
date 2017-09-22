package org.oonsql.lng.lexical;

import java.io.IOException;
import java.io.InputStream;

import org.oonsql.lng.exception.LexicalException;
import org.oonsql.lng.exception.ParserException;
import org.oonsql.lng.token.WordToken;
import org.oonsql.lng.token.IdentifyToken;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.token.Token;

/**
 * Lexical da Linguagem
 * 
 * @author Bernardo Breder
 */
public class LexicalStream extends AbstractLexicalStream {

  /**
   * Construtor
   * 
   * @param input
   * @throws IOException
   */
  public LexicalStream(InputStream input) throws IOException {
    super(input);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StringToken readDocument(int c) throws IOException, ParserException {
    c = this.next();
    this.next();
    StringBuilder sb = new StringBuilder();
    if (c == '*') {
      c = this.look();
      for (; c != '*' || this.look(1) != '/';) {
        sb.append((char) c);
        c = this.next();
      }
      this.next();
      this.next();
    }
    else {
      c = this.look();
      for (; c != '\n';) {
        if (c != '\r') {
          sb.append((char) c);
        }
        c = this.next();
      }
      this.next();
    }
    return new StringToken(sb.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NumberToken readNumber(int c) throws IOException, ParserException {
    double value = 0.;
    int dot = 10;
    if (!this.isNumber(c)) {
      throw new LexicalException();
    }
    if (c == '0' && this.look(1) == 'x') {
      c = this.next();
      if (c != 'x') {
        throw new LexicalException();
      }
      c = Character.toLowerCase(this.next());
      value = c >= '0' && c <= '9' ? c - '0' : c - 'a' + 10;
      c = Character.toLowerCase(this.next());
      while ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')) {
        value = 16 * value + (c >= '0' && c <= '9' ? c - '0' : c - 'a' + 10);
        c = Character.toLowerCase(this.next());
      }
      return new NumberToken(value);
    }
    else {
      value = c - '0';
      c = this.next();
      while (this.isNumber(c)) {
        value = 10 * value + (c - '0');
        c = this.next();
      }
      if (c == '.') {
        c = this.next();
        while (this.isNumber(c)) {
          value += (double) (c - '0') / dot;
          dot *= 10;
          c = this.next();
        }
      }
      return new NumberToken(value);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public StringToken readString(int c) throws IOException, ParserException {
    StringBuilder sb = new StringBuilder();
    if (!this.isString(c)) {
      throw new LexicalException();
    }
    c = this.next();
    while (!this.isString(c)) {
      if (c == '\\') {
        switch (this.next()) {
          case 'n': {
            sb.append('\n');
            break;
          }
          case 'r': {
            sb.append('\r');
            break;
          }
          case 't': {
            sb.append('\t');
            break;
          }
          case 'f': {
            sb.append('\f');
            break;
          }
          case 'b': {
            sb.append('\b');
            break;
          }
          case '\\': {
            sb.append('\\');
            break;
          }
          default: {
            throw new LexicalException();
          }
        }
      }
      else {
        sb.append((char) c);
      }
      c = this.next();
    }
    this.next();
    return new StringToken(sb.toString());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public WordToken readWord(int c) throws IOException, ParserException {
    StringBuilder sb = new StringBuilder();
    if (this.isWordStart(c)) {
      sb.append((char) c);
    }
    c = this.next();
    while (this.isWordPart(c)) {
      sb.append((char) c);
      c = this.next();
    }
    String value = sb.toString();
    WordToken word = WordToken.build(value);
    if (word != null) {
      return word;
    }
    return new IdentifyToken(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Token readSymbol(int c) throws IOException {
    switch (c) {
      case '!': {
        c = this.next();
        if (c == '=') {
          this.next();
          return WordToken.NOT_EQUAL_TOKEN;
        }
        else {
          return new Token('!');
        }
      }
      case '=': {
        c = this.next();
        if (c == '=') {
          this.next();
          return WordToken.EQ_TOKEN;
        }
        else {
          return new Token('=');
        }
      }
      case '-': {
        c = this.next();
        if (c == '-') {
          this.next();
          return WordToken.DEC_TOKEN;
        }
        else {
          return new Token('-');
        }
      }
      case '+': {
        c = this.next();
        if (c == '+') {
          this.next();
          return WordToken.INC_TOKEN;
        }
        else {
          return new Token('+');
        }
      }
      case '>': {
        c = this.next();
        if (c == '=') {
          this.next();
          return WordToken.GE_TOKEN;
        }
        else {
          return new Token('>');
        }
      }
      case '<': {
        c = this.next();
        if (c == '=') {
          this.next();
          return WordToken.LE_TOKEN;
        }
        else {
          return new Token('<');
        }
      }
      default: {
        this.next();
        return new Token(c);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDocument(int c) throws IOException {
    if (c != '/') {
      return false;
    }
    c = this.look(1);
    return c == '*' || c == '/';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNumber(int c) {
    return c >= '0' && c <= '9';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isString(int c) {
    return c == '\"';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWordStart(int c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_'
      || c == '$';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWordPart(int c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
      || (c >= '0' && c <= '9') || c == '_' || c == '$';
  }

}
