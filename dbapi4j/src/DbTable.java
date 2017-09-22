import java.io.IOException;
import java.util.Arrays;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbTable {

	/** Entrada e Saída */
	protected final DbIO io;
	/** Nome da tabela */
	protected final String name;
	/** Número de elementos */
	protected int size;
	/** Quantidade máxima de elementos */
	protected int max;
	/** Quantidade de páginas */
	protected int page;
	/** Quantidade de elementos por página */
	protected int slot;
	/** Tabela */
	protected Entry[] entrys;
	/** Estrutura mudou */
	protected boolean dataChanged;
	/** Estrutura mudou */
	protected boolean structureChanged;

	/**
	 * @param io
	 * @param name
	 * @param page
	 * @param slot
	 */
	protected DbTable(DbIO io, String name, int page, int slot) {
		if (io.exist(name + ".db")) {
			throw new IllegalArgumentException("table already exist");
		}
		this.io = io;
		this.name = name;
		this.max = page * slot;
		this.page = page;
		this.slot = slot;
		this.entrys = new Entry[max];
		this.structureChanged = true;
	}

	/**
	 * @param io
	 * @param name
	 * @throws IOException
	 */
	protected DbTable(DbIO io, String name) throws IOException {
		if (!io.exist(name + ".db")) {
			throw new IllegalArgumentException("table not exist");
		}
		this.io = io;
		this.name = name;
		DbInput in = this.io.read(name + ".db");
		this.size = in.readUInt32();
		this.page = in.readUInt32();
		this.slot = in.readUInt32();
		this.max = page * slot;
		this.entrys = new Entry[max];
	}

	/**
	 * @param key
	 * @return item da página
	 * @throws IOException
	 */
	protected Entry getEntry(int key) throws IOException {
		if (key == 0 || key > size) {
			return null;
		}
		int pageIndex = --key % page;
		Entry entry = entrys[pageIndex];
		String filename = name + "_" + pageIndex + ".db";
		if (entry == null && io.exist(filename)) {
			DbInput in = io.read(filename);
			int size = in.readUInt32();
			for (int n = 0; n < size; n++) {
				int cellIndex = n * page + pageIndex;
				int bytesLength = in.readUInt32();
				byte[] bytes = in.readBytes(bytesLength);
				entrys[cellIndex] = entry = new Entry(bytes, bytesLength, false);
			}
		}
		return entrys[key];
	}

	/**
	 * @param key
	 * @return bytes
	 * @throws IOException
	 */
	public DbInput get(int key) throws IOException {
		Entry entry = getEntry(key);
		if (entry == null) {
			return null;
		}
		return new DbInput(entry.data);
	}

	/**
	 * @param input
	 * @return código do item criado
	 * @throws IOException
	 */
	public int add(DbInput input) throws IOException {
		if (size >= max) {
			entrys = Arrays.copyOf(entrys, max * 2);
			max *= 2;
			page *= 2;
			structureChanged = true;
		}
		int index = size++;
		if (entrys[index % page] == null) {
			getEntry(index + 1);
		}
		Entry entry = entrys[index];
		if (entry == null) {
			entrys[index] = entry = new Entry();
		}
		entry.size = input.size();
		entry.data = input.toBytes();
		entry.changed = true;
		dataChanged = true;
		return index + 1;
	}

	/**
	 * @param id
	 * @param input
	 * @throws IOException
	 */
	public void put(int id, DbInput input) throws IOException {
		if (id > max) {
			entrys = Arrays.copyOf(entrys, max * 2 > id ? max * 2 : id + (id >> 1));
			max = max * 2 > id ? max * 2 : id + (id >> 1);
			if (page * slot < max) {
				page++;
			}
			structureChanged = true;
		}
		if (entrys[(id - 1) % page] == null) {
			getEntry(id);
		}
		size = Math.max(size, id);
		Entry entry = entrys[id - 1];
		if (entry == null) {
			entrys[id - 1] = entry = new Entry();
		}
		entry.size = input.size();
		entry.data = input.toBytes();
		entry.changed = true;
		dataChanged = true;
	}

	/**
	 * @param key
	 * @param input
	 * @return bytes
	 * @throws IOException
	 */
	public boolean set(int key, DbInput input) throws IOException {
		Entry entry = getEntry(key);
		if (entry == null) {
			return false;
		}
		entry.size = input.size();
		entry.data = input.toBytes();
		entry.changed = true;
		dataChanged = true;
		return true;
	}

	/**
	 * @param key
	 * @return bytes
	 * @throws IOException
	 */
	public boolean remove(int key) throws IOException {
		Entry entry = getEntry(key);
		if (entry == null) {
			return false;
		}
		entry.size = 0;
		entry.data = null;
		entry.changed = true;
		dataChanged = true;
		return true;
	}

	/**
	 * Remove a tabela
	 */
	public void drop() {
		io.remove(name + ".db");
		for (int n = 0; n < page; n++) {
			io.remove(name + "_" + n + ".db");
		}
	}

	/**
	 * @throws IOException
	 */
	public void commit() throws IOException {
		if (dataChanged) {
			for (int pageIndex = 0; pageIndex < page; pageIndex++) {
				int slotIndex, cellIndex, length = 0;
				boolean changed = false;
				for (slotIndex = 0, cellIndex = pageIndex; cellIndex < size; slotIndex++, cellIndex += page) {
					length += entrys[cellIndex].size;
					changed |= entrys[cellIndex].changed;
				}
				if (changed) {
					DbOutput out = new DbOutput(4 + slotIndex * 4 + length);
					out.writeUint32(slotIndex);
					for (slotIndex = 0, cellIndex = pageIndex; slotIndex * page + pageIndex < size; slotIndex++, cellIndex += page) {
						out.writeUint32(entrys[cellIndex].size);
						out.writeBytes(entrys[cellIndex].data);
						entrys[cellIndex].changed = false;
					}
					io.write(name + "_" + pageIndex + ".db", out);
				}
			}
		}
		if (structureChanged) {
			DbOutput out = new DbOutput(12);
			out.writeUint32(size);
			out.writeUint32(page);
			out.writeUint32(slot);
			io.write(name + ".db", out);
		}
	}

	/**
	 * Voltar ao estado original
	 */
	public void rollback() {
		for (int pageIndex = 0; pageIndex < page; pageIndex++) {
			if (entrys[pageIndex] != null) {
				int cellIndex;
				boolean changed = false;
				for (cellIndex = pageIndex; !changed && cellIndex < size; cellIndex += page) {
					changed |= entrys[cellIndex].changed;
				}
				if (changed) {
					for (cellIndex = pageIndex; cellIndex < size; cellIndex += page) {
						entrys[cellIndex] = null;
					}
				}
			}
		}
	}

	/**
	 * @return tamanho
	 */
	public int size() {
		return size;
	}

	/**
	 * @return está vazio
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @param array
	 * @param low
	 * @param high
	 * @param key
	 * @return indice da busca
	 */
	protected static int binarySearch(int[] array, int low, int high, int key) {
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = array[mid];
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -low;
	}

	/**
	 * Pagina
	 * 
	 * @author Tecgraf
	 */
	protected static class Entry {

		/** Chave */
		protected byte[] data;
		/** Tamanho */
		protected int size;
		/** Indica que mudou */
		protected boolean changed;

		/**
		 * Construtor
		 */
		public Entry() {
		}

		/**
		 * @param data
		 * @param size
		 * @param changed
		 */
		public Entry(byte[] data, int size, boolean changed) {
			super();
			this.data = data;
			this.size = size;
			this.changed = changed;
		}

	}

}
