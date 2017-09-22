package org.oonsql.lng.syntax;

import java.io.IOException;

import org.oonsql.lng.exception.ParserException;
import org.oonsql.lng.exception.SyntaxException;
import org.oonsql.lng.lexical.AbstractLexicalStream;
import org.oonsql.lng.token.Token;

/**
 * Classe base para criar uma parser
 * 
 * @author bernardobreder
 * 
 */
public abstract class AbstractParser {

  /** Lexical */
  protected final AbstractLexicalStream lexer;
  /** Próximo token */
  protected Token[] look;
  /** Máximo de look a head */
  protected int max = 5;
  /** Indice da fila */
  protected int index;
  /** Indice da fila */
  protected int count;

  /**
   * Construtor
   * 
   * @param lexer
   */
  public AbstractParser(AbstractLexicalStream lexer) {
    super();
    this.lexer = lexer;
    this.look = new Token[max];
  }

  /**
   * Indica se foi encontrado o token de uma tag especifica
   * 
   * @param type
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected boolean match(int type) throws IOException, ParserException {
    Token look = this.look();
    if (look == null) {
      return false;
    }
    return look.tag == type;
  }

  /**
   * Indica se foi encontrado o token de uma tag especifica
   * 
   * @param type
   * @param next
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected boolean match(int type, int next) throws IOException,
    ParserException {
    Token look = this.look(next);
    if (look == null) {
      return false;
    }
    return look.tag == type;
  }

  /**
   * Indica se foi encontrado o token de uma tag especifica
   * 
   * @param type
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected Token read(int type) throws IOException, ParserException {
    if (this.lookTag() != type) {
      throw new SyntaxException();
    }
    Token result = this.look[index];
    this.next();
    return result;
  }

  /**
   * Retorna o token corrente
   * 
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected Token look() throws IOException, ParserException {
    if (count == 0) {
      this.look[index] = this.lexer.readToken();
      this.count = 1;
    }
    return this.look[index];
  }

  /**
   * Retorna o token corrente
   * 
   * @param next
   * 
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected Token look(int next) throws IOException, ParserException {
    if (next >= max) {
      Token[] data = new Token[max * 2];
      for (int n = 0; n < max; n++) {
        data[index + n] = this.look[(index + n) % max];
      }
      this.look = data;
      max *= 2;
    }
    if (next >= count) {
      int size = next + 1 - count;
      for (int n = 0; n < size; n++) {
        this.look[(index + n + count) % max] = this.lexer.readToken();
      }
      this.count += size;
    }
    return this.look[(index + next) % max];
  }

  /**
   * Retorna o token corrente
   * 
   * @return token encontrado da tag
   * @throws ParserException
   * @throws IOException
   */
  protected int lookTag() throws IOException, ParserException {
    Token look = this.look();
    if (look == null) {
      return -1;
    }
    else {
      return look.tag;
    }
  }

  /**
   * Anda para o próximo token
   * 
   * @return próximo token
   * @throws IOException
   * @throws ParserException
   */
  protected Token next() throws IOException, ParserException {
    if (count > 0) {
      this.count--;
    }
    index = (index + 1) % max;
    if (count <= 0) {
      count = 1;
      this.look[index] = this.lexer.readToken();
    }
    return this.look[index];
  }

  /**
   * Reinicia o parser
   */
  public void reset() {
    this.look = null;
  }

}
