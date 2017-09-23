package database.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class EmptyOutputStream extends OutputStream {

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(int b) throws IOException {
  }

}
