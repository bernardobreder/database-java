package database.object;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class DbObject {

  /** Class Id */
  public static final int INT_CLASSID = 1;
  /** Class Id */
  public static final int STR_CLASSID = 2;
  /** Class Id */
  public static final int BOOL_CLASSID = 3;
  /** Class Id */
  public static final int ARRAY_CLASSID = 4;

  /**
   * @param output
   * @throws IOException
   */
  public abstract void write(DataOutputStream output) throws IOException;

}
