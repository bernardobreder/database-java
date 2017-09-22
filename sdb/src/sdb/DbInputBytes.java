package utils;

/**
 * Classe que realiza a leitura dos bytes
 * 
 * @author bernardobreder
 */
public class DbInputBytes {

  /** Bytes */
  protected final byte[] bytes;
  /** Offset dos bytes */
  protected int offset;

  /**
   * Construtor
   * 
   * @param bytes
   */
  public DbInputBytes(byte[] bytes) {
    super();
    if (bytes == null) {
      throw new NullPointerException();
    }
    this.bytes = bytes;
    this.offset = 0;
  }

  /**
   * @return byte de 0 a 255
   */
  public int readUByte() {
    if (offset >= bytes.length) {
      throw new IllegalStateException("<eof>");
    }
    return bytes[offset++] & 0xFF;
  }

  /**
   * @return byte de -128 a 127
   */
  public int readByte() {
    if (offset >= bytes.length) {
      throw new IllegalStateException("<eof>");
    }
    return bytes[offset++];
  }

  /**
   * @return boolean
   */
  public boolean readBoolean() {
    return readUByte() != 0;
  }

  /**
   * @return short de 0 a 65536
   */
  public int readUShort() {
    int a = readUByte();
    int b = readUByte();
    return (a << 8) + b;
  }

  /**
   * @return short de 32768 a 32767
   */
  public int readShort() {
    int a = readUByte();
    int b = readUByte();
    if ((a & 0x80) == 0x80) {
      return -(((a - 0x80) << 8) + b);
    }
    else {
      return (a << 8) + b;
    }
  }

  /**
   * @return inteiro de -2147483648 a 2147483647
   */
  public int readInt() {
    int a = readUByte();
    int b = readUByte();
    int c = readUByte();
    int d = readUByte();
    if ((a & 0x80) == 0x80) {
      return -(((a - 0x80) << 24) + (b << 16) + (c << 8) + d);
    }
    else {
      return (a << 24) + (b << 16) + (c << 8) + d;
    }
  }

  /**
   * @return long de -9223372036854775808 a 9223372036854775807
   */
  public long readLong() {
    long a = readUByte();
    long b = readUByte();
    long c = readUByte();
    long d = readUByte();
    long e = readUByte();
    long f = readUByte();
    long g = readUByte();
    long h = readUByte();
    if ((a & 0x80) == 0x80) {
      return -(((a - 0x80) << 56) + (b << 48) + (c << 40) + (d << 32)
        + (e << 24) + (f << 16) + (g << 8) + h);
    }
    else {
      return (a << 56) + (b << 48) + (c << 40) + (d << 32) + (e << 24)
        + (f << 16) + (g << 8) + h;
    }
  }

  /**
   * @return long de 0 a 72057594037927935
   */
  public long readLongCompressed() {
    int a = readUByte();
    if ((a & 0x80) == 0) {
      return a;
    }
    a -= 0x80;
    int b = readUByte();
    if ((b & 0x80) == 0) {
      return a + (b << 7);
    }
    b -= 0x80;
    int c = readUByte();
    if ((c & 0x80) == 0) {
      return a + (b << 7) + (c << 14);
    }
    c -= 0x80;
    int d = readUByte();
    if ((d & 0x80) == 0) {
      return a + (b << 7) + (c << 14) + (d << 21);
    }
    d -= 0x80;
    long e = readUByte();
    if ((e & 0x80) == 0) {
      return a + (b << 7) + (c << 14) + (d << 21) + (e << 28);
    }
    e -= 0x80;
    long f = readUByte();
    if ((f & 0x80) == 0) {
      return a + (b << 7) + (c << 14) + (d << 21) + (e << 28) + (f << 35);
    }
    f -= 0x80;
    long g = readUByte();
    if ((g & 0x80) == 0) {
      return a + (b << 7) + (c << 14) + (d << 21) + (e << 28) + (f << 35)
        + (g << 42);
    }
    g -= 0x80;
    long h = readUByte();
    return a + (b << 7) + (c << 14) + (d << 21) + (e << 28) + (f << 35)
      + (g << 42) + (h << 49);
  }

  /**
   * @return array de long
   */
  public Long[] readLongArray() {
    int length = (int) readLongCompressed();
    Long[] v = new Long[length];
    for (int n = 0; n < length; n++) {
      int type = readUByte();
      switch (type) {
        case DbOutputBytes.LONG_TYPE: {
          v[n] = readLong();
          break;
        }
        case DbOutputBytes.LONG_COMPRESSED_TYPE: {
          v[n] = readLongCompressed();
          break;
        }
        default: {
          throw new IllegalStateException("item of array is unknown");
        }
      }
    }
    return v;
  }

  /**
   * @return string com comprimento máximo de 65565 e memória máxima de 131070
   *         bytes (132kB)
   */
  public String readStringUtf8() {
    int length = readUShort();
    StringBuilder sb = new StringBuilder(length);
    for (int n = 0; n < length; n++) {
      int c = readUByte();
      if (c <= 0x7F) {
      }
      else if ((c >> 5) == 0x6) {
        int i2 = readUByte();
        c = (((c & 0x1F) << 6) + (i2 & 0x3F));
      }
      else {
        int i2 = readUByte();
        int i3 = readUByte();
        c = (((c & 0xF) << 12) + ((i2 & 0x3F) << 6) + (i3 & 0x3F));
      }
      sb.append((char) c);
    }
    return sb.toString();
  }

  /**
   * @return string
   */
  public String readString11AZ_$() {
    long value = readLongCompressed();
    StringBuilder sb = new StringBuilder();
    int c = (int) (value & 0x1F);
    while (c > 0) {
      int n = 'A' + (c - 1);
      if (n <= 'Z') {
      }
      else if (c == ('Z' - 'A' + 2)) {
        n = '_';
      }
      else if (c == ('Z' - 'A' + 3)) {
        n = '$';
      }
      sb.append((char) n);
      value = value >> 5;
      c = (int) (value & 0x1F);
    }
    return sb.toString();
  }

  /**
   * Leitura do mapa
   * 
   * @return mapa
   */
  public DbObject readDbObject() {
    int length = (int) readLongCompressed();
    DbTreeMap<String, Object> map = new DbTreeMap<String, Object>();
    for (int n = 0; n < length; n++) {
      String key = readStringUtf8();
      Object value = readObject();
      map.put(key, value);
    }
    return new DbObject(map);
  }

  /**
   * Realiza a leitura do objeto
   * 
   * @return objeto
   */
  public Object readObject() {
    int type = readUByte();
    switch (type) {
      case DbOutputBytes.LONG_TYPE: {
        return readLong();
      }
      case DbOutputBytes.LONG_COMPRESSED_TYPE: {
        return readLongCompressed();
      }
      case DbOutputBytes.INT_TYPE: {
        return readInt();
      }
      case DbOutputBytes.BOOLEAN_TYPE: {
        return readBoolean();
      }
      case DbOutputBytes.STRING_UTF8_TYPE: {
        return readStringUtf8();
      }
      case DbOutputBytes.UBYTE_TYPE: {
        return readUByte();
      }
      case DbOutputBytes.SHORT_TYPE: {
        return readShort();
      }
      case DbOutputBytes.LONG_ARRAY_TYPE: {
        return readLongArray();
      }
      case DbOutputBytes.MAP_TYPE: {
        return readDbObject();
      }
      default: {
        throw new IllegalStateException("not found object");
      }
    }
  }

  /**
   * @return está no final do arquivo
   */
  public boolean readEof() {
    return offset == bytes.length;
  }

}
