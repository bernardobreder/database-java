package org.oonsql.lng.lexical;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.oonsql.lng.exception.ParserException;
import org.oonsql.lng.token.WordToken;
import org.oonsql.lng.token.NumberToken;
import org.oonsql.lng.token.StringToken;
import org.oonsql.lng.token.Token;

/**
 * Classe abstrata de lexical
 * 
 * @author Bernardo Breder
 */
public abstract class AbstractLexicalStream {

  /** Stream */
  private InputStream input;
  /** Próximo caracter */
  protected int[] look;
  /** Máximo de look a head */
  protected int max = 5;
  /** Indice da fila */
  protected int index;
  /** Indice da fila */
  private int count;

  /**
   * Construtor
   * 
   * @param input
   * @throws IOException
   */
  public AbstractLexicalStream(InputStream input) throws IOException {
    this.input = input;
    this.look = new int[max];
  }

  /**
   * Realiza a leitura de um Token
   * 
   * @return Token
   * @throws IOException
   * @throws ParserException
   */
  public Token readToken() throws IOException, ParserException {
    if (this.look[index] == -1) {
      return null;
    }
    while (this.isDocument()) {
      this.readDocument();
    }
    if (this.isWord()) {
      return this.readWord();
    }
    else if (this.isNumber()) {
      return this.readNumber();
    }
    else if (this.isString()) {
      return this.readString();
    }
    else {
      return this.readSymbol();
    }
  }

  /**
   * Realiza a leitura de um número
   * 
   * @param c
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public abstract StringToken readDocument(int c) throws IOException,
    ParserException;

  /**
   * Realiza a leitura de um número
   * 
   * @param c
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public abstract NumberToken readNumber(int c) throws IOException,
    ParserException;

  /**
   * Realiza a leitura de uma String
   * 
   * @param c
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public abstract StringToken readString(int c) throws IOException,
    ParserException;

  /**
   * Realiza a leitura de um simbolo
   * 
   * @param c
   * @return token
   * @throws IOException
   */
  public abstract Token readSymbol(int c) throws IOException;

  /**
   * Realiza a leitura de uma palavra iniciando com uma letra
   * 
   * @param c
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public abstract WordToken readWord(int c) throws IOException, ParserException;

  /**
   * Realiza a leitura de um número
   * 
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public StringToken readDocument() throws IOException, ParserException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return null;
      }
      c = this.next();
    }
    return this.readDocument(c);
  }

  /**
   * Realiza a leitura de um número
   * 
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public StringToken readString() throws IOException, ParserException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return null;
      }
      c = this.next();
    }
    return this.readString(c);
  }

  /**
   * Realiza a leitura de um número
   * 
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public NumberToken readNumber() throws IOException, ParserException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return null;
      }
      c = this.next();
    }
    return this.readNumber(c);
  }

  /**
   * Realiza a leitura de um número
   * 
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public WordToken readWord() throws IOException, ParserException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return null;
      }
      c = this.next();
    }
    return this.readWord(c);
  }

  /**
   * Realiza a leitura de um número
   * 
   * @return token
   * @throws IOException
   * @throws ParserException
   */
  public Token readSymbol() throws IOException, ParserException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return null;
      }
      c = this.next();
    }
    return this.readSymbol(c);
  }

  /**
   * Verifica se o próximo token é um token do tipo número
   * 
   * @return indica se é do tipo número
   * @throws IOException
   */
  public boolean isDocument() throws IOException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return false;
      }
      this.next();
      c = this.look();
    }
    return isDocument(c);
  }

  /**
   * Verifica se o próximo token é um token do tipo número
   * 
   * @return indica se é do tipo número
   * @throws IOException
   */
  public boolean isNumber() throws IOException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return false;
      }
      this.next();
      c = this.look();
    }
    return isNumber(c);
  }

  /**
   * Verifica se o próximo token é um token do tipo string
   * 
   * @return indica se é do tipo string
   * @throws IOException
   */
  public boolean isString() throws IOException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return false;
      }
      this.next();
      c = this.look();
    }
    return isString(c);
  }

  /**
   * Verifica se o próximo token é um token do tipo palavra
   * 
   * @return indica se é do tipo palavra
   * @throws IOException
   */
  public boolean isWord() throws IOException {
    int c = this.look();
    while (c <= 32) {
      if (c < 0) {
        return false;
      }
      this.next();
      c = this.look();
    }
    return isWordStart(c);
  }

  /**
   * Indica se é um número
   * 
   * @param c
   * @return é numero
   * @throws IOException
   */
  public abstract boolean isDocument(int c) throws IOException;

  /**
   * Indica se é um número
   * 
   * @param c
   * @return é numero
   * @throws IOException
   */
  public abstract boolean isNumber(int c) throws IOException;

  /**
   * Indica se é uma string
   * 
   * @param c
   * @return é string
   * @throws IOException
   */
  public abstract boolean isString(int c) throws IOException;

  /**
   * Indica se é uma palavra
   * 
   * @param c
   * @return é word
   * @throws IOException
   */
  public abstract boolean isWordStart(int c) throws IOException;

  /**
   * Indica se é uma palavra
   * 
   * @param c
   * @return é word
   * @throws IOException
   */
  public abstract boolean isWordPart(int c) throws IOException;

  /**
   * Realiza a leitura de somente um byte
   * 
   * @return leitura de um byte
   * @throws IOException
   */
  protected int look() throws IOException {
    if (count == 0) {
      this.look[index] = this.read();
      this.count = 1;
    }
    return this.look[index];
  }

  /**
   * Realiza a leitura de somente um byte
   * 
   * @param next
   * @return leitura de um byte
   * @throws IOException
   */
  protected int look(int next) throws IOException {
    if (next >= max) {
      int[] data = new int[max * 2];
      for (int n = 0; n < max; n++) {
        data[index + n] = this.look[(index + n) % max];
      }
      this.look = data;
      max *= 2;
    }
    if (next >= count) {
      int size = next + 1 - count;
      for (int n = 0; n < size; n++) {
        this.look[(index + n + count) % max] = this.read();
      }
      this.count += size;
    }
    return this.look[(index + next) % max];
  }

  /**
   * Anda para o próximo byte
   * 
   * @return o byte seguinte
   * 
   * @throws IOException
   */
  public int next() throws IOException {
    if (count > 0) {
      this.count--;
    }
    index = (index + 1) % max;
    if (count <= 0) {
      count = 1;
      this.look[index] = this.read();
    }
    return this.look[index];
  }

  /**
   * Realiza a leitura
   * 
   * @return leitura
   * @throws IOException
   */
  private int read() throws IOException {
    int c = this.input.read();
    if (c <= 0x7F) {
      return c;
    }
    else if ((c >> 5) == 0x6) {
      int i2 = this.input.read();
      if (i2 < 0) {
        throw new EOFException();
      }
      return (((c & 0x1F) << 6) + (i2 & 0x3F));
    }
    else {
      int i2 = this.input.read();
      if (i2 < 0) {
        throw new EOFException();
      }
      int i3 = this.input.read();
      if (i3 < 0) {
        throw new EOFException();
      }
      return (((c & 0xF) << 12) + ((i2 & 0x3F) << 6) + (i3 & 0x3F));
    }
  }

}
