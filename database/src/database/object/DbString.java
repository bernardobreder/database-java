package database.object;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbString extends DbObject {

  /** Valor */
  public String value;

  /**
   * @param value
   */
  public DbString(String value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(DataOutputStream output) throws IOException {
    output.writeByte(DbObject.STR_CLASSID);
    int size = value.length();
    output.writeShort(size);
    for (int n = 0; n < size; n++) {
      char c = value.charAt(n);
      if (c == '\n') {
        output.write('\\');
        output.write('n');
      }
      else if (c == '\t') {
        output.write('\\');
        output.write('t');
      }
      else {
        output.write(c);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return value;
  }

}
