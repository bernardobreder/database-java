package database.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class StringInputStream extends InputStream {

  /** Conteúdo */
  private String content;
  /** Indice */
  private int index;
  /** Limite */
  private int mask;

  /**
   * @param content
   */
  public StringInputStream(String content) {
    this.content = content;
    this.index = this.mask = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    return this.content.charAt(index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int n;
    for (n = 0; n < len; n++) {
      int i = this.index + n;
      if (i >= this.content.length()) {
        break;
      }
      b[n] = (byte) this.content.charAt(i);
    }
    if (n == 0) {
      return -1;
    }
    this.index += n;
    return n;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long skip(long n) throws IOException {
    this.index += n;
    return n;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int available() throws IOException {
    return this.content.length() - this.index;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void mark(int readlimit) {
    this.mask = this.index;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void reset() throws IOException {
    this.index = this.mask;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean markSupported() {
    return true;
  }

}
