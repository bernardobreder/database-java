package org.oonsql.lng.token;

/**
 * Token
 * 
 * @author Bernardo Breder
 */
public class Token {

  /** Tag */
  public final int tag;

  /**
   * @param t
   */
  public Token(int t) {
    tag = t;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return tag;
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
    if (tag != other.tag) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "" + (char) tag;
  }

}
