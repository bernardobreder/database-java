package database.object;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbArray extends DbObject {

  /** Valor */
  public DbObject[] value;

  /**
   * @param value
   */
  public DbArray(DbObject[] value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(DataOutputStream output) throws IOException {
    output.writeByte(DbObject.ARRAY_CLASSID);
    output.writeShort(this.value.length);
    for (int n = 0; n < value.length; n++) {
      value[n].write(output);
    }
    output.write('\n');
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return Arrays.toString(value);
  }

}
