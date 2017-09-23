package database.lexical;

/**
 * 
 * 
 * @author Tecgraf
 */
public class Token {

  /** Tipo de Token */
  public static final int DOC = 1;
  /** Tipo de Token */
  public static final int ID = 2;
  /** Tipo de Token */
  public static final int STRING = 3;
  /** Tipo de Token */
  public static final int NUMBER = 4;
  /** Tipo de Token */
  public static final int EOF = 5;
  /** Tipo */
  private int type;
  /** Palavra */
  private String word;
  /** Linha */
  private int lin;
  /** Coluna */
  private int col;

  /**
   * @param type
   * @param word
   * @param lin
   * @param col
   */
  public Token(int type, String word, int lin, int col) {
    this.type = type;
    this.word = word;
    this.lin = lin;
    this.col = col;
  }

  /**
   * @return tipo
   */
  public int getType() {
    return type;
  }

  /**
   * @return word
   */
  public String getWord() {
    return word;
  }

  /**
   * @return linha
   */
  public int getLine() {
    return lin;
  }

  /**
   * @return coluna
   */
  public int getColumn() {
    return col;
  }

  /**
   * Une dois tokens
   * 
   * @param t
   * @return tokens
   */
  public Token join(Token t) {
    String text = t == null ? "null" : t.word;
    return new Token(-1, this.word + text, lin, col);
  }

  /**
   * Une dois tokens
   * 
   * @param t
   * @return tokens
   */
  public Token join(String t) {
    String text = t == null ? "null" : t;
    return new Token(-1, this.word + text, lin, col);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + word.hashCode();
    result = prime * result + type;
    result = prime * result + lin;
    result = prime * result + col;
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Token other = (Token) obj;
    if (col != other.col) {
      return false;
    }
    if (lin != other.lin) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    if (word == null) {
      if (other.word != null) {
        return false;
      }
    }
    else if (!word.equals(other.word)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "[" + type + "," + word + "," + lin + "," + col + "]";
  }

}
