package org.oonsql.lng.token;

/**
 * Token de String
 * 
 * @author Bernardo Breder
 */
public class StringToken extends Token {

  /** Valor */
  public final String value;

  /**
   * @param v
   */
  public StringToken(String v) {
    super(WordToken.STR);
    this.value = v;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    StringToken other = (StringToken) obj;
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    }
    else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

}
