package database.lexical;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A classe LexicalStream È responsavel pela leitura de uma InputStream e
 * retornar tokens da stream.
 * 
 * @author Tecgraf
 */
public class LexicalStream {

  /** Stram */
  private InputStream input;
  /** Atual caracter */
  private int current;
  /** Pr√≥ximo caracter */
  private int next;
  /** Linha corrente */
  private int lin;
  /** Coluna corrente */
  private int col;
  /** Bytes a serem lidos */
  private byte[] bytes;
  /** Bytes a serem lidos */
  private final Queue<Token> looks = new ArrayDeque<Token>();
  /** Indice corrente */
  private int index;
  /** Quantidade de bytes para ser lido */
  private int count;
  /** Quantidade de bytes para ser lido */
  private boolean closed;
  /** Fim de arquivo */
  private Token lastReadedToken;
  /** Fim de arquivo */
  private static Token eofToken;

  /**
   * @param input
   * @throws IOException
   * @throws LexicalException
   */
  public LexicalStream(InputStream input) throws IOException, LexicalException {
    this.input = input;
    this.bytes = new byte[1024];
    this.current = readUtf();
    this.next = readUtf();
    this.lin = 1;
    this.col = 1;
  }

  /**
   * @param type
   * @return È do tipo
   * @throws IOException
   * @throws LexicalException
   */
  public boolean is(int type) throws IOException, LexicalException {
    if (lastReadedToken == null) {
      this.lastReadedToken = this.readToken();
    }
    return lastReadedToken.getType() == type;
  }

  /**
   * @param type
   * @return È do tipo
   * @throws IOException
   * @throws LexicalException
   */
  public boolean can(int type) throws IOException, LexicalException {
    if (is(type)) {
      this.readToken();
      return true;
    }
    return false;
  }

  /**
   * @param type
   * @return token
   * @throws LexicalException
   * @throws IOException
   */
  public Token read(int type) throws LexicalException, IOException {
    if (!is(type)) {
      throw new LexicalException("expected type: " + type);
    }
    Token result = this.lastReadedToken;
    this.readToken();
    return result;
  }

  public Token look(int next) throws IOException, LexicalException {
    if (next == 0) {
      return this.lastReadedToken;
    }
    for (int n = 0; n < next; n++) {
      this.looks.add(this.readToken());
    }
    return this.looks.peek();
  }

  /**
   * @param token
   * @return tipo de id
   */
  protected int getIdType(String token) {
    return Token.ID;
  }

  /**
   * @param symbol
   * @param next
   * @return tipo de id
   */
  protected int getSymbolType(int symbol, int next) {
    return symbol;
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  public Token readToken() throws IOException, LexicalException {
    for (;;) {
      int c = this.current;
      int n = this.next;
      if (c == -1) {
        if (eofToken == null) {
          eofToken = new Token(Token.EOF, "<eof>", lin, col);
        }
        return this.lastReadedToken = eofToken;
      }
      else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
        return this.lastReadedToken = readId();
      }
      else if (c >= '0' && c <= '9') {
        return this.lastReadedToken = readNumber();
      }
      else if (c == '\"') {
        return this.lastReadedToken = readString();
      }
      else if (c == '/' && n == '*') {
        return this.lastReadedToken = readDoc();
      }
      else if (c == '/' && n == '/') {
        return this.lastReadedToken = readDocLine();
      }
      else if (c == '\n') {
        this.current = this.next;
        this.next = this.readUtf();
        this.lin++;
        this.col = 1;
      }
      else if (c <= ' ') {
        this.current = this.next;
        this.next = this.readUtf();
        this.col++;
      }
      else {
        return this.lastReadedToken = readSymbol();
      }
    }
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readId() throws IOException, LexicalException {
    StringBuilder sb = new StringBuilder();
    int c = current;
    for (;;) {
      sb.append((char) c);
      c = this.current = this.next;
      this.next = this.readUtf();
      this.col++;
      if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_')) {
        break;
      }
    }
    int type = this.getIdType(sb.toString());
    return new Token(type, sb.toString(), lin, col - sb.length());
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readSymbol() throws IOException, LexicalException {
    int col = this.col;
    int c = current;
    int n = next;
    int type = this.getSymbolType(c, n);
    if (type != c) {
      this.current = this.readUtf();
      this.next = this.readUtf();
      this.col += 2;
      char[] cs = new char[2];
      cs[0] = (char) c;
      cs[1] = (char) n;
      return new Token(type, new String(cs), this.lin, col);
    }
    else {
      char[] cs = new char[1];
      cs[0] = (char) c;
      this.col++;
      this.current = next;
      this.next = this.readUtf();
      return new Token(c, new String(cs), lin, col);
    }
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readNumber() throws IOException, LexicalException {
    StringBuilder sb = new StringBuilder();
    boolean dot = false;
    int c = this.current;
    for (;;) {
      sb.append((char) c);
      c = this.current = this.next;
      this.next = this.readUtf();
      this.col++;
      if (!((c >= '0' && c <= '9') || (!dot && c == '.'))) {
        break;
      }
      if (c == '.') {
        dot = true;
      }
    }
    return new Token(Token.NUMBER, sb.toString(), lin, col - sb.length());
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readString() throws IOException, LexicalException {
    StringBuilder sb = new StringBuilder();
    for (;;) {
      int c = this.current = this.next;
      int n = this.next = this.readUtf();
      this.col++;
      if (c == '\\') {
        if (n == 'n') {
          sb.append('\n');
        }
        else if (n == 'r') {
          sb.append('\r');
        }
        else if (n == 't') {
          sb.append('\t');
        }
        else if (n == '\\') {
          sb.append('\\');
        }
        else {
          throw new LexicalException("expected: \\n, \\r, \\t, \\\\");
        }
        c = this.current = this.readUtf();
        n = this.next = this.readUtf();
        this.col++;
      }
      else if (c == '\"') {
        this.current = this.next;
        this.next = this.readUtf();
        this.col++;
        break;
      }
      else if (c == '\n') {
        throw new LexicalException("not expected: '\\n'");
      }
      else if (c == -1) {
        throw new LexicalException("<eof>");
      }
      else {
        sb.append((char) c);
      }
    }
    return new Token(Token.STRING, sb.toString(), this.lin, this.col
      - sb.length() - 2);
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readDoc() throws IOException, LexicalException {
    int lin = this.lin;
    int col = this.col;
    StringBuilder sb = new StringBuilder();
    int c = this.current = this.next;
    int n = this.next = this.readUtf();
    this.col++;
    for (;;) {
      c = this.current = this.next;
      n = this.next = this.readUtf();
      this.col++;
      if (c == '*' && n == '/') {
        this.col += 2;
        this.current = this.readUtf();
        this.next = this.readUtf();
        break;
      }
      else if (c == '\n') {
        this.lin++;
        this.col = 0;
      }
      else if (c == -1) {
        throw new LexicalException("<eof>");
      }
      sb.append((char) c);
    }
    return new Token(Token.DOC, sb.toString(), lin, col);
  }

  /**
   * @return token
   * @throws IOException
   * @throws LexicalException
   */
  protected Token readDocLine() throws IOException, LexicalException {
    int lin = this.lin;
    int col = this.col;
    StringBuilder sb = new StringBuilder();
    int c = this.current = this.next;
    this.next = this.readUtf();
    this.col++;
    for (;;) {
      c = this.current = this.next;
      this.next = this.readUtf();
      this.col++;
      if (c == '\n') {
        this.lin++;
        this.col = 1;
        this.current = next;
        this.next = this.readUtf();
        break;
      }
      else if (c < 0) {
        break;
      }
      sb.append((char) c);
    }
    return new Token(Token.DOC, sb.toString(), lin, col);
  }

  /**
   * @return character utf8
   * @throws IOException
   * @throws LexicalException
   */
  protected int readUtf() throws IOException, LexicalException {
    int c = this.read();
    if (c <= 0x7F) {
      return c;
    }
    else if ((c >> 5) == 0x6) {
      int i2 = this.read();
      if (i2 == -1) {
        throw new LexicalException("<eof>");
      }
      return ((c & 0x1F) << 6) + (i2 & 0x3F);
    }
    else {
      int i2 = this.read();
      int i3 = this.read();
      if (i2 == -1 || i3 == -1) {
        throw new LexicalException("<eof>");
      }
      return ((c & 0xF) << 12) + ((i2 & 0x3F) << 6) + (i3 & 0x3F);
    }
  }

  /**
   * @return caracter
   * @throws IOException
   */
  private int read() throws IOException {
    if (this.closed) {
      return -1;
    }
    while (count == 0) {
      this.index = 0;
      this.count = this.input.read(bytes);
      if (this.count == -1) {
        this.closed = true;
        return -1;
      }
    }
    count--;
    byte b = this.bytes[this.index++];
    return b >= 0 ? b : 2 * Byte.MAX_VALUE + 2 + b;
  }

  /**
   * Erro Lexical
   * 
   * @author Tecgraf
   */
  public static class LexicalException extends Exception {

    /**
     * @param message
     */
    public LexicalException(String message) {
      super(message);
    }

  }

}
