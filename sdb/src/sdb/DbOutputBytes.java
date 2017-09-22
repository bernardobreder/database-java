package utils;

import java.util.Arrays;

/**
 * Classe que realiza a leitura dos bytes
 * 
 * @author bernardobreder
 */
public class DbOutputBytes {

	/** Type */
	public static final int LONG_TYPE = 1;
	/** Type */
	public static final int LONG_COMPRESSED_TYPE = 2;
	/** Type */
	public static final int INT_TYPE = 3;
	/** Type */
	public static final int BOOLEAN_TYPE = 4;
	/** Type */
	public static final int STRING_UTF8_TYPE = 5;
	/** Type */
	public static final int UBYTE_TYPE = 6;
	/** Type */
	public static final int SHORT_TYPE = 7;
	/** Type */
	public static final int LONG_ARRAY_TYPE = 8;
	/** Type */
	public static final int MAP_TYPE = 9;
	/** Bytes */
	protected byte[] bytes;
	/** Contador de bytes */
	protected int count;
	/** Maior valor do long comprimido */
	public static long LONG_COMPRESSED_MAX = 0xFFFFFFFFFFFFFFl;

	/**
	 * Construtor
	 */
	public DbOutputBytes() {
		this(32);
	}

	/**
	 * Construtor
	 * 
	 * @param size
	 *            tamanho inicial
	 */
	public DbOutputBytes(int size) {
		bytes = new byte[size];
	}

	/**
	 * @param v
	 *            valor em byte
	 */
	public void writeUByte(int v) {
		if (v < 0 || v >= 256) {
			throw new IllegalArgumentException();
		}
		write(v & 0xFF);
	}

	/**
	 * @param v
	 *            valor em byte
	 */
	public void writeByte(int v) {
		if (v < -128 || v > 128) {
			throw new IllegalArgumentException();
		}
		write(v);
	}

	/**
	 * @param v
	 *            valor em byte
	 */
	public void writeBoolean(boolean v) {
		write(v ? 1 : 0);
	}

	/**
	 * @param v
	 */
	public void writeUShort(int v) {
		write((v >> 8) & 0xFF);
		write(v & 0xFF);
	}

	/**
	 * @param v
	 */
	public void writeShort(int v) {
		if (v < 0) {
			write(((-v >> 8) & 0xFF) + 0x80);
		} else {
			write((v >> 8) & 0xFF);
		}
		write(v & 0xFF);
	}

	/**
	 * @param v
	 */
	public void writeInt(int v) {
		if (v < 0) {
			write(((-v >> 24) & 0xFF) + 0x80);
			v = -v;
		} else {
			write((v >> 24) & 0xFF);
		}
		write((v >> 16) & 0xFF);
		write((v >> 8) & 0xFF);
		write(v & 0xFF);
	}

	/**
	 * @param v
	 */
	public void writeLong(long v) {
		if (v < 0) {
			write((int) (((-v >> 56) & 0xFF) + 0x80));
			v = -v;
		} else {
			write((int) ((v >> 56) & 0xFF));
		}
		write((int) ((v >> 48) & 0xFF));
		write((int) ((v >> 40) & 0xFF));
		write((int) ((v >> 32) & 0xFF));
		write((int) ((v >> 24) & 0xFF));
		write((int) ((v >> 16) & 0xFF));
		write((int) ((v >> 8) & 0xFF));
		write((int) (v & 0xFF));
	}

	/**
	 * @param v
	 */
	public void writeLongCompressed(long v) {
		if (v < 0) {
			throw new IllegalArgumentException("number is negative");
		}
		if (v <= 0x7F) {
			write((int) (v & 0x7F));
		} else if (v <= 0x3FFF) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) (((v & 0x3F80) >> 7) & 0xFF));
		} else if (v <= 0x3FFFF) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) (((v & 0x1FC000) >> 14) & 0xFF));
		} else if (v <= 0x1FFFFF) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC000) >> 14) & 0xFF) + 0x80));
			write((int) (((v & 0xFE00000) >> 21) & 0xFF));
		} else if (v <= 0xFFFFFFF) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC000) >> 14) & 0xFF) + 0x80));
			write((int) ((((v & 0xFE00000) >> 21) & 0xFF) + 0x80));
			write((int) (((v & 0x7F0000000l) >> 28) & 0xFF));
		} else if (v <= 0x7FFFFFFFFl) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC000) >> 14) & 0xFF) + 0x80));
			write((int) ((((v & 0xFE00000) >> 21) & 0xFF) + 0x80));
			write((int) ((((v & 0x7F0000000l) >> 28) & 0xFF) + 0x80));
			write((int) (((v & 0x3F800000000l) >> 35) & 0xFF));
		} else if (v <= 0x3FFFFFFFFFFl) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC000) >> 14) & 0xFF) + 0x80));
			write((int) ((((v & 0xFE00000) >> 21) & 0xFF) + 0x80));
			write((int) ((((v & 0x7F0000000l) >> 28) & 0xFF) + 0x80));
			write((int) ((((v & 0x3F800000000l) >> 35) & 0xFF) + 0x80));
			write((int) (((v & 0x1FC0000000000l) >> 42) & 0xFF));
		} else if (v <= 0xFFFFFFFFFFFFFFl) {
			write((int) ((v & 0x7F) + 0x80));
			write((int) ((((v & 0x3F80) >> 7) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC000) >> 14) & 0xFF) + 0x80));
			write((int) ((((v & 0xFE00000) >> 21) & 0xFF) + 0x80));
			write((int) ((((v & 0x7F0000000l) >> 28) & 0xFF) + 0x80));
			write((int) ((((v & 0x3F800000000l) >> 35) & 0xFF) + 0x80));
			write((int) ((((v & 0x1FC0000000000l) >> 42) & 0xFF) + 0x80));
			write((int) (((v & 0xFE000000000000l) >> 49) & 0xFF));
		} else {
			throw new IllegalArgumentException("number too large");
		}
	}

	/**
	 * Realiza a escrita de um array de long
	 * 
	 * @param v
	 */
	public void writeLongArray(Long[] v) {
		int length = v.length;
		writeLongCompressed(length);
		for (int n = 0; n < length; n++) {
			long i = v[n];
			if (i > LONG_COMPRESSED_MAX || i < 0) {
				writeUByte(LONG_TYPE);
				writeLong(i);
			} else {
				writeUByte(LONG_COMPRESSED_TYPE);
				writeLongCompressed(i);
			}
		}
	}

	/**
	 * @param v
	 */
	public void writeStringUtf8(String v) {
		int length = v.length();
		if (length > 0xFFFF) {
			throw new IllegalArgumentException("string too large");
		}
		write((length >> 8) & 0xFF);
		write(length & 0xFF);
		for (int n = 0; n < length; n++) {
			char c = v.charAt(n);
			if (c <= 0x7F) {
				write(c);
			} else if (c <= 0x7FF) {
				write((((c >> 6) & 0x1F) + 0xC0));
				write(((c & 0x3F) + 0x80));
			} else {
				write((((c >> 12) & 0xF) + 0xE0));
				write((((c >> 6) & 0x3F) + 0x80));
				write(((c & 0x3F) + 0x80));
			}
		}
	}

	/**
	 * @param v
	 *            valor a ser escrito
	 */
	public void writeString11AZ_$(String v) {
		if (v.length() > 11) {
			throw new IllegalArgumentException("string too large");
		}
		long value = 0;
		for (int n = 0; n < v.length(); n++) {
			char c = v.charAt(n);
			long m;
			if (c >= 'A' && c <= 'Z') {
				m = (c - 'A') + 1;
			} else if (c >= 'a' && c <= 'z') {
				m = (c - 'a') + 1;
			} else if (c == '_') {
				m = 'Z' - 'A' + 2;
			} else if (c == '$') {
				m = 'Z' - 'A' + 3;
			} else {
				throw new IllegalArgumentException("string[" + n
						+ "] is not AZaz");
			}
			value += m << (5 * n);
		}
		writeLongCompressed(value);
	}

	/**
	 * @param v
	 */
	public void writeDbObject(DbObject v) {
		writeLongCompressed(v.size());
		sdb.DbTreeMap.Entry<String, Object> entry = v.values.first();
		while (entry != null) {
			writeStringUtf8(entry.getKey());
			writeObject(entry.getValue());
			entry = entry.successor();
		}
	}

	/**
	 * Realiza a escrita de um objeto
	 * 
	 * @param value
	 */
	public void writeObject(Object value) {
		if (value instanceof Long) {
			Long longObject = (Long) value;
			long longValue = longObject.longValue();
			if (longValue > LONG_COMPRESSED_MAX
					|| longValue < -LONG_COMPRESSED_MAX) {
				write(LONG_TYPE);
				writeLong(longValue);
			} else {
				write(LONG_COMPRESSED_TYPE);
				writeLongCompressed(longValue);
			}
		} else if (value instanceof Integer) {
			Integer integerObject = (Integer) value;
			int integerValue = integerObject.intValue();
			write(INT_TYPE);
			writeInt(integerValue);
		} else if (value instanceof Boolean) {
			Boolean booleanObject = (Boolean) value;
			boolean booleanValue = booleanObject.booleanValue();
			write(BOOLEAN_TYPE);
			writeBoolean(booleanValue);
		} else if (value instanceof String) {
			String stringValue = (String) value;
			write(STRING_UTF8_TYPE);
			writeStringUtf8(stringValue);
		} else if (value instanceof Long[]) {
			Long[] longArrayValue = (Long[]) value;
			write(LONG_ARRAY_TYPE);
			writeLongArray(longArrayValue);
		} else if (value instanceof Byte) {
			Byte byteObject = (Byte) value;
			int byteValue = byteObject.intValue() & 0xFF;
			write(UBYTE_TYPE);
			writeUByte(byteValue);
		} else if (value instanceof Short) {
			Short shortObject = (Short) value;
			int shortValue = shortObject.intValue();
			write(SHORT_TYPE);
			writeShort(shortValue);
		} else if (value instanceof DbObject) {
			DbObject dbMap = (DbObject) value;
			write(MAP_TYPE);
			writeDbObject(dbMap);
		} else {
			throw new IllegalArgumentException("value unknown");
		}
	}

	/**
	 * @param b
	 */
	public void write(int b) {
		if (count + 1 == bytes.length) {
			bytes = Arrays.copyOf(bytes, bytes.length << 1);
		}
		bytes[count++] = (byte) b;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param b
	 * @param off
	 * @param len
	 */
	public void write(byte b[], int off, int len) {
		if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > bytes.length) {
			bytes = Arrays.copyOf(bytes, Math.max(bytes.length << 1, newcount));
		}
		System.arraycopy(b, off, bytes, count, len);
		count = newcount;
	}

	/**
	 * @return bytes escrito
	 */
	public byte[] getBytes() {
		if (count != bytes.length) {
			bytes = Arrays.copyOf(bytes, count);
		}
		return bytes;
	}

}
