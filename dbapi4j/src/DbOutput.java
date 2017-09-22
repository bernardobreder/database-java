import java.util.Arrays;

/**
 * 
 * 
 * @author Bernardo Breder
 */
public class DbOutput {

	/** Bytes */
	protected byte[] bytes;
	/** Tamanho atual */
	protected int size;
	/** Tamanho atual */
	protected int max;

	/**
	 * Construtor
	 */
	public DbOutput() {
		this(32);
	}

	/**
	 * @param size
	 */
	public DbOutput(int size) {
		this.bytes = new byte[size];
		this.max = size;
	}

	public DbOutput reset() {
		size = 0;
		return this;
	}

	/**
	 * @param value
	 * @return this
	 */
	public DbOutput writeUInt8(int value) {
		grow(1);
		bytes[size++] = (byte) value;
		return this;
	}

	/**
	 * @param value
	 * @return this
	 */
	public DbOutput writeUint16(int value) {
		grow(2);
		bytes[size++] = (byte) ((value >> 8) & 0xFF);
		bytes[size++] = (byte) (value & 0xFF);
		return this;
	}

	/**
	 * @param value
	 * @return this
	 */
	public DbOutput writeUint32(int value) {
		grow(4);
		bytes[size++] = (byte) ((value >> 24) & 0xFF);
		bytes[size++] = (byte) ((value >> 16) & 0xFF);
		bytes[size++] = (byte) ((value >> 8) & 0xFF);
		bytes[size++] = (byte) (value & 0xFF);
		return this;
	}
	
	/**
	 * @param value
	 * @return this
	 */
	public DbOutput writeUint64(long value) {
		grow(8);
		bytes[size++] = (byte) ((value >> 56) & 0xFF);
		bytes[size++] = (byte) ((value >> 48) & 0xFF);
		bytes[size++] = (byte) ((value >> 40) & 0xFF);
		bytes[size++] = (byte) ((value >> 32) & 0xFF);
		bytes[size++] = (byte) ((value >> 24) & 0xFF);
		bytes[size++] = (byte) ((value >> 16) & 0xFF);
		bytes[size++] = (byte) ((value >> 8) & 0xFF);
		bytes[size++] = (byte) (value & 0xFF);
		return this;
	}

	/**
	 * @param value
	 * @return this
	 */
	public DbOutput writeBytes(byte[] value) {
		grow(value.length);
		System.arraycopy(value, 0, bytes, size, value.length);
		size += value.length;
		return this;
	}

	/**
	 * @param i
	 */
	public void grow(int i) {
		if (max - size < i) {
			bytes = Arrays.copyOf(bytes, (size + i) + ((size + i) >> 1));
			max = (size + i) + ((size + i) >> 1);
		}
	}

	/**
	 * @return bytes
	 */
	public byte[] toBytes() {
		if (size == bytes.length) {
			return bytes;
		} else {
			return Arrays.copyOf(bytes, max);
		}
	}

	/**
	 * @return bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @return tamanho
	 */
	public int size() {
		return size;
	}

}
