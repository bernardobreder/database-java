package database.object;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbBoolean extends DbObject {

  /** True */
  public static final DbBoolean TRUE = new DbBoolean(true);
  /** False */
  public static final DbBoolean FALSE = new DbBoolean(false);
  /** Valor */
  public boolean value;

  /**
   * @param value
   */
  private DbBoolean(boolean value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(DataOutputStream output) throws IOException {
    output.writeByte(DbObject.BOOL_CLASSID);
    output.writeByte(value ? 1 : 0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "" + value;
  }

}
