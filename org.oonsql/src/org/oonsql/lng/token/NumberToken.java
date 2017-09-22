package org.oonsql.lng.token;

/**
 * Token de numero
 * 
 * @author Bernardo Breder
 */
public class NumberToken extends Token {

  /** Valor */
  public final double value;

  /**
   * @param v
   */
  public NumberToken(double v) {
    super(WordToken.NUM);
    value = v;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "" + value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(value);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    NumberToken other = (NumberToken) obj;
    if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
      return false;
    }
    return true;
  }

}
