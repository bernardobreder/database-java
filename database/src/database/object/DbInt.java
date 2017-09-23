package database.object;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbInt extends DbObject {

  /** Valor */
  public int value;

  /**
   * @param value
   */
  public DbInt(int value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(DataOutputStream output) throws IOException {
    output.writeByte(DbObject.INT_CLASSID);
    output.writeInt(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "" + value;
  }

}
