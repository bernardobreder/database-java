package org.oonsql.lng.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public abstract class AbstractDataInputStream extends InputStream {

  /**
   * Realiza a leitura de um inteiro positivo compactado pela charset utf8
   * 
   * @return inteiro positivo
   * @throws IOException
   */
  public int readIndex() throws IOException {
    int i1 = this.read();
    if (i1 <= 0x7F) {
      return i1;
    }
    else if ((i1 >> 5) == 0x6) {
      int i2 = this.read();
      return ((i1 & 0x1F) << 6) + (i2 & 0x3F);
    }
    else if ((i1 >> 4) == 0xE) {
      int i2 = this.read();
      int i3 = this.read();
      return ((i1 & 0xF) << 12) + ((i2 & 0x3F) << 6) + (i3 & 0x3F);
    }
    else if ((i1 >> 3) == 0x1E) {
      int i2 = this.read();
      int i3 = this.read();
      int i4 = this.read();
      return ((i1 & 0x7) << 18) + ((i2 & 0x3F) << 12) + ((i3 & 0x3F) << 6)
        + (i4 & 0x3F);
    }
    else if ((i1 >> 2) == 0x3E) {
      int i2 = this.read();
      int i3 = this.read();
      int i4 = this.read();
      int i5 = this.read();
      return ((i1 & 0x3) << 24) + ((i2 & 0x3F) << 18) + ((i3 & 0x3F) << 12)
        + ((i4 & 0x3F) << 6) + (i5 & 0x3F);
    }
    else if ((i1 >> 1) == 0x7E) {
      int i2 = this.read();
      int i3 = this.read();
      int i4 = this.read();
      int i5 = this.read();
      int i6 = this.read();
      return ((i1 & 0x1) << 30) + ((i2 & 0x3F) << 24) + ((i3 & 0x3F) << 18)
        + ((i4 & 0x3F) << 12) + ((i5 & 0x3F) << 6) + (i6 & 0x3F);
    }
    else {
      throw new IllegalStateException();
    }
  }

  /**
   * Realiza a leitura de uma String
   * 
   * @return string
   * @throws IOException
   */
  public String readString() throws IOException {
    int size = this.readIndex();
    char[] chars = new char[size];
    for (int n = 0; n < size; n++) {
      chars[n] = (char) this.readIndex();
    }
    return new String(chars);
  }

  /**
   * Realiza a leitura de um número com ponto flutuante
   * 
   * @return ponto flutuante
   * @throws IOException
   */
  public double readDouble() throws IOException {
    int ebits = 11, fbits = 52;
    boolean[] bits = new boolean[64];
    int index = 0;
    for (int i = 0; i < 8; i++) {
      int b = this.read();
      for (int j = 0; j < 8; j++) {
        bits[index++] = b % 2 == 1 ? true : false;
        b = b >> 1;
      }
    }
    int bias = (1 << (ebits - 1)) - 1;
    int s = bits[63] ? -1 : 1;
    int e = 0;
    for (int n = 62; n >= 64 - ebits - 1; n--) {
      if (bits[n]) {
        e += Math.pow(2, ebits - 1 - 62 + n);
      }
    }
    long f = 0;
    int imax = 64 - ebits - 2;
    for (int n = imax; n >= 0; n--) {
      if (bits[n]) {
        f += Math.pow(2, n);
      }
    }
    if (e == (1 << ebits) - 1) {
      return f != 0 ? Double.NaN : s * Double.POSITIVE_INFINITY;
    }
    else if (e > 0) {
      return s * Math.pow(2, e - bias) * (1 + f / Math.pow(2, fbits));
    }
    else if (f != 0) {
      return s * Math.pow(2, -(bias - 1)) * (f / Math.pow(2, fbits));
    }
    else {
      return s * 0;
    }
  }

  /**
   * Escreve um inteiro longo que segue o intervalo de -9223372036854780000 até
   * +9223372036854780000.<br/>
   * A escrita segue o seguinte critério:<br/>
   * 1. 0+xxxxxx <br/>
   * 2. 10+xxxxx xxxxxxxx <br/>
   * 3. 110+xxxx xxxxxxxx xxxxxxxx <br/>
   * 4. 1110+xxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * 5. 11110000 +xxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * Sendo que o bit + significa um bit de positivo para 0 e negativo para 1. <br/>
   * Para leitura: <br/>
   * Se o primeiro byte >> 7 == 0, então é o item 1. <br/>
   * Se o primeiro byte >> 6 == 2, então é o item 2. <br/>
   * Se o primeiro byte >> 5 == 6, então é o item 3. <br/>
   * Se o primeiro byte >> 4 == 14, então é o item 4. <br/>
   * Se o primeiro byte >> 3 == 30, então é o item 5. <br/>
   * Para escrita: <br/>
   * Se o valor for <= +-63, então é o item 1. <br/>
   * Se o valor for <= +-8191, então é o item 2. <br/>
   * Se o valor for <= +-1048575, então é o item 3. <br/>
   * Se o valor for <= +-134217727, então é o item 4. <br/>
   * Se o valor for <= +-2147483647, então é o item 5. <br/>
   * 
   * @return int
   * @throws IOException
   */
  public int readInteger() throws IOException {
    int i1 = this.read();
    if (i1 >> 7 == 0) {
      if (i1 > 63) {
        return -(i1 & 63);
      }
      else {
        return +(i1 & 63);
      }
    }
    else if (i1 >> 6 == 2) {
      int i2 = this.read();
      if ((i1 & 32) == 32) {
        return -(((i1 & 31) << 8) + i2);
      }
      else {
        return +(((i1 & 31) << 8) + i2);
      }
    }
    else if (i1 >> 5 == 6) {
      int i2 = this.read();
      int i3 = this.read();
      if ((i1 & 16) == 16) {
        return -(((i1 & 15) << 16) + (i2 << 8) + i3);
      }
      else {
        return +(((i1 & 15) << 16) + (i2 << 8) + i3);
      }
    }
    else if (i1 >> 4 == 14) {
      int i2 = this.read();
      int i3 = this.read();
      int i4 = this.read();
      if ((i1 & 8) == 8) {
        return -(((i1 & 7) << 24) + (i2 << 16) + (i3 << 8) + i4);
      }
      else {
        return +(((i1 & 7) << 24) + (i2 << 16) + (i3 << 8) + i4);
      }
    }
    else if (i1 >> 3 == 30) {
      i1 = this.read();
      int i2 = this.read();
      int i3 = this.read();
      int i4 = this.read();
      if (i1 > 127) {
        return -(((i1 - 128) << 24) + (i2 << 16) + (i3 << 8) + i4);
      }
      else {
        return +((i1 << 24) + (i2 << 16) + (i3 << 8) + i4);
      }
    }
    else {
      throw new IllegalArgumentException();
    }
  }

}
