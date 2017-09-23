package database.vm.out;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class UTFOutputStream extends OutputStream {

  /** Saída */
  private final OutputStream output;

  /**
   * @param output
   */
  public UTFOutputStream(OutputStream output) {
    this.output = output;
  }

  /**
   * @param text
   * @throws IOException
   */
  public void write(String text) throws IOException {
    write(this, text);
  }

  /**
   * @param output
   * @param text
   * @throws IOException
   */
  public static void write(OutputStream output, String text) throws IOException {
    int size = text.length();
    for (int n = 0; n < size; n++) {
      char c = text.charAt(n);
      if (c <= 0x7F) {
        output.write(c);
      }
      else if (c <= 0x7FF) {
        output.write(((c >> 6) & 0x1F) + 0xC0);
        output.write((c & 0x3F) + 0x80);
      }
      else {
        output.write(((c >> 12) & 0xF) + 0xE0);
        output.write(((c >> 6) & 0x3F) + 0x80);
        output.write((c & 0x3F) + 0x80);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(int n) throws IOException {
    output.write(n);
  }

}
