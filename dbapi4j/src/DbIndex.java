import java.io.IOException;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbIndex {

	/** Entrada e Saída */
	protected final DbIO io;
	/** Nome da tabela */
	protected final String name;
	/** Quantidade de páginas */
	protected int page;
	/** Quantidade de elementos por página */
	protected int slot;
	/** Tabela */
	protected Entry[] entrys;
	/** Tabela */
	protected int[] sizes;
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
	protected DbIndex(DbIO io, String name, int page, int slot) {
		if (io.exist(name + ".idb")) {
			throw new IllegalArgumentException("index already exist");
		}
		this.io = io;
		this.name = name;
		this.page = page;
		this.slot = slot;
		this.entrys = new Entry[page];
		this.sizes = new int[page];
		this.structureChanged = true;
	}

	/**
	 * @param io
	 * @param name
	 * @throws IOException
	 */
	protected DbIndex(DbIO io, String name) throws IOException {
		if (!io.exist(name + ".idb")) {
			throw new IllegalArgumentException("index not exist");
		}
		this.io = io;
		this.name = name;
		DbInput in = this.io.read(name + ".db");
		this.page = in.readUInt32();
		this.slot = in.readUInt32();
		this.entrys = new Entry[page];
		this.sizes = new int[page];
		for (int n = 0; n < page; n++) {
			this.sizes[n] = in.readUInt32();
		}
	}

	protected int binarySearch(Item[] array, int low, int high, long key) {
		if (low > high) {
			return -(low + 1);
		} else if (array[high].key == key) {
			return high;
		} else if (array[low].key == key) {
			return low;
		} else if (array[high].key < key) {
			return -(high + 2);
		} else if (array[low].key > key) {
			return -(low + 1);
		}
		while (low <= high) {
			int mid = (low + high) >> 1;
			long midVal = array[mid].key;
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -(low + 1);
	}

	protected void resizeEntry(int newPage) {
		Entry[] newEntrys = new Entry[newPage];
		int[] newSizes = new int[newPage];
		for (int e = 0; e < page; e++) {
			Entry entry = entrys[e];
			int size = sizes[e];
			for (int i = 0; i < size; i++) {
				Item item = entry.items[i];
				int newEntryIndex = (int) (entry.items[i].key & (newPage - 1));
				if (newSizes[newEntryIndex] == slot) {
					newEntrys = null;
					newSizes = null;
					resizeEntry(newPage + ((newPage >> 1) > 0 ? (newPage >> 1) : 1));
					return;
				}
				Entry newEntry = newEntrys[newEntryIndex];
				if (newEntry == null) {
					newEntrys[newEntryIndex] = newEntry = new Entry(new Item[slot]);
					newEntry.changed = true;
				}
				int newItemIndex = newSizes[newEntryIndex]++;
				newEntry.items[newItemIndex] = new Item(item.key, item.value, true);
			}
		}
		page = newPage;
		sizes = newSizes;
		entrys = newEntrys;
		structureChanged = true;
	}

	protected Entry loadEntry(int pageIndex) throws IOException {
		DbInput in = io.read(name + "_" + pageIndex + ".idb");
		int size = in.readUInt32();
		Item[] array = new Item[size];
		for (int n = 0; n < size; n++) {
			long key = in.readUInt64();
			int value = in.readUInt32();
			array[n] = new Item(key, value, false);
		}
		return new Entry(array);
	}

	/**
	 * @param key
	 * @return bytes
	 * @throws IOException
	 */
	public int get(long key) throws IOException {
		int pageIndex = (int) (key & (page - 1));
		int size = sizes[pageIndex];
		Entry entry = entrys[pageIndex];
		if (entry == null) {
			if (size > 0) {
				entrys[pageIndex] = entry = loadEntry(pageIndex);
			} else {
				return 0;
			}
		}
		int row = binarySearch(entry.items, 0, size - 1, key);
		if (row < 0) {
			return 0;
		}
		return entry.items[row].value;
	}

	/**
	 * @param input
	 * @return código do item criado
	 * @throws IOException
	 */
	public void add(long key, int value) throws IOException {
		int pageIndex = (int) (key & (page - 1));
		int size = sizes[pageIndex];
		Entry entry = entrys[pageIndex];
		if (entry == null) {
			if (size > 0) {
				entry = loadEntry(pageIndex);
			} else {
				entrys[pageIndex] = entry = new Entry(new Item[slot]);
			}
		}
		int row = binarySearch(entry.items, 0, size - 1, key);
		if (row >= 0) {
			row++;
		} else {
			row = -row - 1;
		}
		if (size == slot) {
			do {
				resizeEntry(page * 2);
				pageIndex = (int) (key & (page - 1));
				size = sizes[pageIndex];
				entry = entrys[pageIndex];
			} while (size == slot);
			row = binarySearch(entry.items, 0, size - 1, key);
			row = -row - 1;
		}
		int length = size - row;
		if (length > 0) {
			System.arraycopy(entry.items, row, entry.items, row + 1, length);
		}
		sizes[pageIndex]++;
		entry.items[row] = new Item(key, value, true);
	}

	/**
	 * @param key
	 * @return bytes
	 * @throws IOException
	 */
	public boolean remove(int key) throws IOException {
		int pageIndex = (int) (key & (page - 1));
		int size = sizes[pageIndex];
		Entry entry = entrys[pageIndex];
		if (entry == null) {
			if (size > 0) {
				entry = loadEntry(pageIndex);
			} else {
				entrys[pageIndex] = entry = new Entry(new Item[slot]);
			}
		}
		int row = binarySearch(entry.items, 0, size - 1, key);
		if (row < 0) {
			return false;
		}
		row++;
		int length = size - row - 1;
		if (length > 0) {
			System.arraycopy(entry.items, row, entry.items, row + 1, length);
		}
		sizes[pageIndex]--;
		dataChanged = true;
		return true;
	}

	/**
	 * Remove a tabela
	 */
	public void drop() {
		io.remove(name + ".idb");
		for (int n = 0; n < page; n++) {
			io.remove(name + "_" + n + ".idb");
		}
	}

	/**
	 * @throws IOException
	 */
	public void commit() throws IOException {
		if (dataChanged) {
			for (int pageIndex = 0; pageIndex < page; pageIndex++) {
				Entry entry = entrys[pageIndex];
				if (entry != null && entry.changed) {
					int size = sizes[pageIndex];
					DbOutput out = new DbOutput(4 + 2 * 5 * size);
					out.writeUint32(size);
					for (int slotIndex = 0; slotIndex < entry.items.length; slotIndex++) {
						Item item = entry.items[slotIndex];
						out.writeUint64(item.key);
						out.writeUint32(item.value);
						item.changed = false;
					}
					io.write(name + "_" + pageIndex + ".db", out);
					entry.changed = false;
				}
			}
		}
		if (structureChanged) {
			DbOutput out = new DbOutput(2 * 4 + page * 4);
			out.writeUint32(page);
			out.writeUint32(slot);
			for (int pageIndex = 0; pageIndex < page; pageIndex++) {
				out.writeUint32(sizes[pageIndex]);
			}
			io.write(name + ".idb", out);
		}
	}

	/**
	 * Voltar ao estado original
	 */
	public void rollback() {
		if (dataChanged || structureChanged) {
			for (int pageIndex = 0; pageIndex < page; pageIndex++) {
				Entry entry = entrys[pageIndex];
				if (entry != null && entry.changed) {
					entrys[pageIndex] = null;
				}
			}
		}
	}

	/**
	 * Pagina
	 * 
	 * @author Tecgraf
	 */
	protected static class Entry {

		/** Chave */
		protected Item[] items;
		/** Indica que mudou */
		protected boolean changed;

		/**
		 * Construtor
		 * 
		 * @param array
		 */
		public Entry(Item[] array) {
			this.items = array;
		}

	}

	/**
	 * Pagina
	 * 
	 * @author Tecgraf
	 */
	protected static class Item {

		/** Chave */
		protected long key;
		/** Tamanho */
		protected int value;
		/** Indica que mudou */
		protected boolean changed;

		/**
		 * Construtor
		 * 
		 * @param key
		 * @param value
		 * @param changed
		 */
		public Item(long key, int value, boolean changed) {
			this.key = key;
			this.value = value;
			this.changed = changed;
		}

	}

}
