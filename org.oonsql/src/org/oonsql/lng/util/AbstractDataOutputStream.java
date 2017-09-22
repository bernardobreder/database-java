package org.oonsql.lng.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Stream de Opcode
 * 
 * @author bernardobreder
 * 
 */
public abstract class AbstractDataOutputStream extends OutputStream {

  /**
   * Escreve um indice positivo
   * 
   * @param v
   * @throws IOException
   */
  public void writeIndex(int v) throws IOException {
    if (v < 0) {
      throw new IllegalArgumentException("value is negative: " + v);
    }
    if (v <= 0x7F) {
      this.write(v);
    }
    else if (v <= 0x7FF) {
      this.write(0xC0 + (v >> 6));
      this.write(0x80 + (v & 0x3F));
    }
    else if (v <= 0xFFFF) {
      this.write(0xE0 + (v >> 12));
      this.write(0x80 + ((v >> 6) & 0x3F));
      this.write(0x80 + (v & 0x3F));
    }
    else if (v <= 0x1FFFFF) {
      this.write(0xF0 + (v >> 18));
      this.write(0x80 + ((v >> 12) & 0x3F));
      this.write(0x80 + ((v >> 6) & 0x3F));
      this.write(0x80 + (v & 0x3F));
    }
    else if (v <= 0x3FFFFFF) {
      this.write(0xF8 + (v >> 24));
      this.write(0x80 + ((v >> 18) & 0x3F));
      this.write(0x80 + ((v >> 12) & 0x3F));
      this.write(0x80 + ((v >> 6) & 0x3F));
      this.write(0x80 + (v & 0x3F));
    }
    else if (v <= 0x7FFFFFFF) {
      this.write(0xFC + (v >> 30));
      this.write(0x80 + ((v >> 24) & 0x3F));
      this.write(0x80 + ((v >> 18) & 0x3F));
      this.write(0x80 + ((v >> 12) & 0x3F));
      this.write(0x80 + ((v >> 6) & 0x3F));
      this.write(0x80 + (v & 0x3F));
    }
    else {
      throw new IllegalArgumentException("value is to high: " + v);
    }
  }

  /**
   * Escreve uma String
   * 
   * @param text
   * @throws IOException
   */
  public void writeString(String text) throws IOException {
    int length = text.length();
    this.writeIndex(length);
    for (int n = 0; n < length; n++) {
      this.writeIndex(text.charAt(n));
    }
  }

  /**
   * Referencia
   * http://cautionsingularityahead.blogspot.com.br/2010/04/javascript
   * -and-ieee754-redux.html
   * 
   * @param v
   * @throws IOException
   */
  public void writeDouble(double v) throws IOException {
    int ebits = 11, fbits = 52;
    int bias = (1 << (ebits - 1)) - 1;
    boolean s;
    double f;
    int e;
    if (Double.isNaN(v)) {
      e = (1 << bias) - 1;
      f = 1;
      s = false;
    }
    else if (Double.isInfinite(v)) {
      e = (1 << bias) - 1;
      f = 0;
      s = (v < 0) ? true : false;
    }
    else if (v == 0) {
      e = 0;
      f = 0;
      s = (1 / v == Double.NEGATIVE_INFINITY) ? true : false;
    }
    else {
      s = v < 0;
      v = Math.abs(v);
      if (v >= Math.pow(2, 1 - bias)) {
        double ln2 = 0.6931471805599453;
        double ln = Math.min(Math.floor(Math.log(v) / ln2), bias);
        e = (int) (ln + bias);
        f = v * Math.pow(2, fbits - ln) - Math.pow(2, fbits);
      }
      else {
        e = 0;
        f = v / Math.pow(2, 1 - bias - fbits);
      }
    }
    // Pack sign, exponent, fraction
    boolean[] bits = new boolean[64];
    int index = 0;
    for (int i = 0; i < fbits; i++) {
      bits[index++] = f % 2 == 1 ? true : false;
      f = Math.floor(f / 2);
    }
    for (int i = 0; i < ebits; i++) {
      bits[index++] = e % 2 == 1 ? true : false;
      e = (int) Math.floor(e / 2);
    }
    bits[index] = s;
    for (int n = 0; n < 8; n++) {
      int value = 0;
      for (int m = 0; m < 8; m++) {
        if (bits[n * 8 + m]) {
          value += Math.pow(2, m);
        }
      }
      this.write(value);
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
   * 5. 11110+xx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * 6. 111110+x xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * 7. 1111110+ xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * 8. 11111110 +xxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx <br/>
   * 9. 11111111 +xxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx xxxxxxxx
   * xxxxxxxx <br/>
   * Sendo que o bit + significa um bit de positivo para 0 e negativo para 1. <br/>
   * 
   * @param v
   * @throws IOException
   */
  public void writeLong(long v) throws IOException {
    throw new RuntimeException();
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
   * @param v
   * @throws IOException
   */
  public void writeInt(int v) throws IOException {
    int i;
    if (v >= -63 && v <= 63) {
      i = v < 0 ? -v : v;
      if (v < 0) {
        i += 64;
      }
      this.write(i);
    }
    else if (v >= -8191 && v <= 8191) {
      i = v < 0 ? -v : v;
      this.write(128 + (v < 0 ? 32 : 0) + (i >> 8));
      this.write(i & 0xFF);
    }
    else if (v >= -1048575 && v <= 1048575) {
      i = v < 0 ? -v : v;
      this.write(192 + (v < 0 ? 16 : 0) + (i >> 16));
      this.write((i >> 8) & 0xFF);
      this.write(i & 0xFF);
    }
    else if (v >= -134217727 && v <= 134217727) {
      i = v < 0 ? -v : v;
      this.write(224 + (v < 0 ? 8 : 0) + (i >> 24));
      this.write((i >> 16) & 0xFF);
      this.write((i >> 8) & 0xFF);
      this.write(i & 0xFF);
    }
    else if (v >= -2147483647 && v <= 2147483647) {
      i = v < 0 ? -v : v;
      this.write(240);
      this.write((v < 0 ? 128 : 0) + (i >> 24));
      this.write((i >> 16) & 0xFF);
      this.write((i >> 8) & 0xFF);
      this.write(i & 0xFF);
    }
    else {
      throw new IllegalArgumentException();
    }
  }

}
