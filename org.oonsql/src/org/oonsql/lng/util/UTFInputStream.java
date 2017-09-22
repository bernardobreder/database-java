package org.oonsql.lng.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Array de bytes
 * 
 * 
 * @author Bernardo Breder
 */
public class UTFInputStream extends InputStream implements Serializable {

  /** Stream */
  private final InputStream input;

  /**
   * @param input
   */
  public UTFInputStream(InputStream input) {
    this.input = input;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read() throws IOException {
    int c = this.input.read();
    if (c <= 0x7F) {
      return c;
    }
    else if ((c >> 5) == 0x6) {
      int i2 = this.input.read();
      return ((c & 0x1F) << 6) + (i2 & 0x3F);
    }
    else {
      int i2 = this.input.read();
      int i3 = this.input.read();
      return ((c & 0xF) << 12) + ((i2 & 0x3F) << 6) + (i3 & 0x3F);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(byte[] b) throws IOException {
    return this.input.read(b);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int size = 0;
    for (int n = off; n < len; n++) {
      int c = this.read();
      if (c < 0) {
        break;
      }
      size++;
      b[n] = (byte) c;
    }
    return size;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long skip(long n) throws IOException {
    return this.input.skip(n);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void mark(int readlimit) {
    this.input.mark(readlimit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void reset() throws IOException {
    this.input.reset();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean markSupported() {
    return this.input.markSupported();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int available() throws IOException {
    return this.input.available();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {
    this.input.close();
  }

}
