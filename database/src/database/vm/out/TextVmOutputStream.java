package database.vm.out;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class TextVmOutputStream extends AbstractVmOutputStream {

  /** Saída */
  private UTFOutputStream output;

  /**
   * @param output
   */
  public TextVmOutputStream(OutputStream output) {
    this.output = new UTFOutputStream(output);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void next() throws IOException {
    output.write('\n');
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void eof() throws IOException {
    output.write('\n');
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(int value) throws IOException {
    output.write(Integer.toString(value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(boolean value) throws IOException {
    if (value) {
      this.output.write('t');
      this.output.write('r');
      this.output.write('u');
      this.output.write('e');
    }
    else {
      this.output.write('f');
      this.output.write('a');
      this.output.write('l');
      this.output.write('s');
      this.output.write('e');
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(String value) throws IOException {
    output.write(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column() throws IOException {
    output.write('\t');
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void open() throws IOException {
  }

}
