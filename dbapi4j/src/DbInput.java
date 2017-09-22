import java.io.EOFException;
import java.util.Arrays;

/**
 * 
 * 
 * @author Bernardo Breder
 */
public class DbInput {

	/** Bytes */
	private final byte[] bytes;
	/** Bytes */
	private int offset;

	/**
	 * Construtor
	 * 
	 * @param bytes
	 */
	public DbInput(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * Quantidade de bytes a serem lidos
	 * 
	 * @return bytes a serem lidos
	 */
	public int size() {
		return bytes.length - offset;
	}

	/**
	 * @return inteiro positivo
	 * @throws EOFException
	 */
	public long readUInt64() throws EOFException {
		if (offset + 8 > bytes.length) {
			throw new EOFException();
		}
		long left = ((bytes[offset] & 0xFF) << 24) + ((bytes[offset + 1] & 0xFF) << 16) + ((bytes[offset + 2] & 0xFF) << 8) + (bytes[offset + 3] & 0xFF);
		int right = ((bytes[offset] & 0xFF) << 24) + ((bytes[offset + 1] & 0xFF) << 16) + ((bytes[offset + 2] & 0xFF) << 8) + (bytes[offset + 3] & 0xFF);
		offset += 8;
		return (left << 32) + right;
	}

	/**
	 * @return inteiro positivo
	 * @throws EOFException
	 */
	public int readUInt32() throws EOFException {
		if (offset + 4 > bytes.length) {
			throw new EOFException();
		}
		int result = ((bytes[offset] & 0xFF) << 24) + ((bytes[offset + 1] & 0xFF) << 16) + ((bytes[offset + 2] & 0xFF) << 8) + (bytes[offset + 3] & 0xFF);
		offset += 4;
		return result;
	}

	/**
	 * @return inteiro positivo
	 * @throws EOFException
	 */
	public int readUInt16() throws EOFException {
		if (offset + 2 > bytes.length) {
			throw new EOFException();
		}
		int result = ((bytes[offset] & 0xFF) << 8) + (bytes[offset + 1] & 0xFF);
		offset += 2;
		return result;
	}

	/**
	 * @return inteiro positivo
	 * @throws EOFException
	 */
	public int readUInt8() throws EOFException {
		if (offset + 1 > bytes.length) {
			throw new EOFException();
		}
		return bytes[offset++] & 0xFF;
	}

	/**
   * @param len
   * @return bytes
   */
  public byte[] readBytes(int len) {
  	byte[] result = Arrays.copyOfRange(bytes, offset, offset + len);
  	offset += len;
  	return result;
  }

  /**
	 * Verifica se chegou no final do arquivo
	 * 
	 * @throws EOFException
	 */
	public void readEof() throws EOFException {
		if (offset + 1 > bytes.length) {
			throw new EOFException();
		}
		if (bytes[offset++] != 0xFF) {
			throw new IllegalStateException();
		}
	}

	/**
	 * @return bytes
	 */
	public byte[] toBytes() {
		if (offset == 0) {
			offset = bytes.length;
			return bytes;
		} else {
			offset = bytes.length;
			return Arrays.copyOfRange(bytes, offset, bytes.length - offset);
		}
	}

}
