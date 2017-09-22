import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DbDatabase {

	protected DbIO io;

	protected int version;

	protected Map<String, DbTable> tables;

	protected Map<String, DbIndex> indexs;

	public DbDatabase(DbIO io) throws IOException {
		this.io = io;
		this.tables = new TreeMap<String, DbTable>();
		this.indexs = new TreeMap<String, DbIndex>();
		if (io.exist("database.db")) {
			DbInput in = new DbInput(io.read("database.db"));
			if (in.readInt16() != 0xDBFF) {
				throw new EOFException();
			}
			this.version = in.readInt32();
			int tableCount = in.readInt32();
			for (int n = 0; n < tableCount; n++) {
				String name = in.readUTF();
				tables.put(name, new DbTable(io, name));
			}
			int indexCount = in.readInt32();
			for (int n = 0; n < indexCount; n++) {
				String name = in.readUTF();
				indexs.put(name, new DbIndex(io, name));
			}
			if (in.readInt8() != 0xFF) {
				throw new IOException();
			}
		}
	}

	public DbTable createTable(String name, int slot) throws IOException {
		DbTable table = new DbTable(io, name, slot);
		tables.put(name, table);
		return table;
	}

	public DbTable openTable(String name) throws IOException {
		DbTable table = new DbTable(io, name);
		tables.put(name, table);
		return table;
	}

	public DbIndex createIndex(String name, int slot) throws IOException {
		DbIndex index = new DbIndex(io, name, slot);
		indexs.put(name, index);
		return index;
	}

	public DbIndex openIndex(String name) throws IOException {
		DbIndex index = new DbIndex(io, name);
		indexs.put(name, index);
		return index;
	}

	public void drop() throws IOException {
		io.remove("database.db");
		for (Entry<String, DbTable> entry : tables.entrySet()) {
			entry.getValue().drop();
		}
		for (Entry<String, DbIndex> entry : indexs.entrySet()) {
			entry.getValue().drop();
		}
	}

	public void commit() throws IOException {
		for (Entry<String, DbTable> entry : tables.entrySet()) {
			entry.getValue().commit();
		}
		for (Entry<String, DbIndex> entry : indexs.entrySet()) {
			entry.getValue().commit();
		}
		{
			DbOutput out = new DbOutput();
			out.writeInt16(0xDBFF);
			out.writeInt32(version);
			out.writeInt32(tables.size());
			for (Entry<String, DbTable> entry : tables.entrySet()) {
				entry.getValue().commit();
			}
			out.writeInt32(indexs.size());
			for (Entry<String, DbIndex> entry : indexs.entrySet()) {
				entry.getValue().commit();
			}
			out.writeInt8(0xFF);
			io.write("database.db", out.toBytes());
		}
		io.commit();
	}

	public void rollback() throws IOException {
		for (Entry<String, DbTable> entry : tables.entrySet()) {
			entry.getValue().rollback();
		}
		for (Entry<String, DbIndex> entry : indexs.entrySet()) {
			entry.getValue().rollback();
		}
		io.rollback();
	}

	public static class DbTable {

		protected DbIO io;

		protected String name;

		protected int size;

		protected int page;

		protected int slot;

		protected Map<Integer, DbTableEntry> entrys;

		protected Set<Integer> dataChanged;

		public DbTable(DbIO io, String name, int slot) throws IOException {
			this.io = io;
			if (io.exist(name)) {
				throw new IOException("table already exist");
			}
			this.name = name;
			this.slot = slot;
			this.entrys = new TreeMap<Integer, DbTableEntry>();
			this.dataChanged = new TreeSet<Integer>();
		}

		public DbTable(DbIO io, String name) throws IOException {
			this.io = io;
			if (!io.exist(name)) {
				throw new IOException("table not exist");
			}
			this.name = name;
			DbInput in = new DbInput(io.read(name));
			this.size = in.readInt32();
			this.page = in.readInt32();
			this.slot = in.readInt32();
			if (in.readInt8() != 0xFF) {
				throw new EOFException();
			}
			this.entrys = new TreeMap<Integer, DbTableEntry>();
			this.dataChanged = new TreeSet<Integer>();
		}

		public DbInput get(int id) throws IOException {
			if (id <= 0 || id > size) {
				return null;
			}
			loadPage((id - 1) / slot);
			DbTableEntry entry = entrys.get(id - 1);
			if (entry == null || entry.data == null) {
				return null;
			}
			return new DbInput(entry.data);
		}

		public boolean contain(int id) throws IOException {
			if (id <= 0 || id > size) {
				return false;
			}
			loadPage((id - 1) / slot);
			DbTableEntry entry = entrys.get(id - 1);
			if (entry == null || entry.data == null) {
				return false;
			}
			return true;
		}

		public void set(int id, DbOutput out) throws IOException {
			if (id <= 0 || id > size) {
				return;
			}
			loadPage((id - 1) / slot);
			DbTableEntry entry = entrys.get(id - 1);
			if (entry == null || entry.data == null) {
				return;
			}
			entry.data = out.toBytes();
			entry.changed = true;
		}

		public int add(DbOutput out) throws IOException {
			int index = size;
			int page = index / slot;
			loadPage(page);
			Integer key = index;
			entrys.put(key, new DbTableEntry(out.toBytes(), true));
			dataChanged.add(key);
			size++;
			page = (int) Math.ceil((float) size / slot);
			return index + 1;
		}

		public void remove(int id) throws IOException {
			int page = (id - 1) / slot;
			loadPage(page);
			entrys.put(id - 1, new DbTableEntry(null, true));
			dataChanged.add(page);
		}

		public int size() {
			return this.size;
		}

		public void drop() throws IOException {
			io.remove(name);
			if (size > 0) {
				for (int n = 0; n <= size / slot; n++) {
					io.remove(name, n);
				}
			}
		}

		public void commit() throws IOException {
			DbOutput out = new DbOutput();
			for (Integer page : dataChanged) {
				for (int n = 0, c = page * slot; n < slot; n++, c++) {
					DbTableEntry entry = entrys.get(c);
					if (entry != null) {
						if (entry.data == null) {
							out.writeInt32(0);
						} else {
							out.writeInt32(entry.data.length);
							out.writeBytes(entry.data, 0, entry.data.length);
						}
					}
				}
				out.writeInt8(0xFF);
				io.write(name, page, out.toBytes());
				out.reset();
			}
			{
				out.reset();
				out.writeInt32(size);
				out.writeInt32(page);
				out.writeInt32(slot);
				out.writeInt32(0xFF);
				io.write(name, out.toBytes());
			}
			dataChanged.clear();
		}

		public void rollback() {
			for (Integer page : dataChanged) {
				for (int n = 0, c = page * slot; n < slot; n++, c++) {
					entrys.remove(c);
				}
			}
			dataChanged.clear();
		}

		protected void loadPage(int page) throws IOException {
			if (!entrys.containsKey(page)) {
				if (io.exist(name, page)) {
					DbInput in = new DbInput(io.read(name, page));
					for (int n = 0, c = page * slot; n < slot; n++, c++) {
						int length = in.readInt32();
						byte[] bytes = in.readBytes(length);
						entrys.put(c, new DbTableEntry(bytes, false));
					}
				}
			}
		}

		public static class DbTableEntry {

			protected byte[] data;

			protected boolean changed;

			public DbTableEntry(byte[] data, boolean changed) {
				super();
				this.data = data;
				this.changed = changed;
			}

		}

	}

	public static class DbIndexSet {

		protected int[] array;

		protected int size;

		protected int max;

		public DbIndexSet(int size) {
			this.max = size + (size >> 2);
			this.array = new int[max];
		}

		public DbIndexSet add(int index) {
			if (size == max) {
				max = max + (max >> 1);
				array = Arrays.copyOf(array, max);
			}
			array[size++] = index;
			return this;
		}

		public int get(int index) {
			if (index < 0 || index >= size) {
				throw new ArrayIndexOutOfBoundsException(index);
			}
			return array[index];
		}

		public int size() {
			return size;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			for (int n = 0; n < size; n++) {
				sb.append(array[n]);
				if (n != size - 1) {
					sb.append(", ");
				}
			}
			sb.append(']');
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + size;
			for (int element : array) {
				result = prime * result + element;
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DbIndexSet other = (DbIndexSet) obj;
			if (size != other.size)
				return false;
			for (int i = 0; i < size; i++) {
				if (array[i] != other.array[i]) {
					return false;
				}
			}
			return true;
		}

	}

	public static class DbIndex {

		protected DbTable table;

		public DbIndex(DbIO io, String name, int slot) throws IOException {
			this.table = new DbTable(io, name, slot);
		}

		public DbIndex(DbIO io, String name) throws IOException {
			this.table = new DbTable(io, name);
		}

		public DbIndexSet get(int id) throws IOException {
			DbInput in = table.get(id);
			if (in == null) {
				return null;
			}
			int size = in.readInt32();
			DbIndexSet set = new DbIndexSet(size);
			for (int n = 0; n < size; n++) {
				set.add(in.readInt32());
			}
			if (in.readInt8() != 0xFF) {
				throw new EOFException();
			}
			return set;
		}

		public void set(int id, DbIndexSet set) throws IOException {
			DbInput in = table.get(id);
			if (in == null) {
				return;
			}
			DbOutput out = new DbOutput();
			out.writeInt32(set.size);
			for (int n = 0; n < set.size; n++) {
				out.writeInt32(set.array[n]);
			}
			out.writeInt8(0xFF);
			table.set(id, out);
		}

		public void add(int id, int index) throws IOException {
			DbIndexSet set = get(id);
			if (set == null) {
				set = new DbIndexSet(8);
			}
			set.add(index);
			DbOutput out = new DbOutput();
			out.writeInt32(set.size);
			for (int n = 0; n < set.size; n++) {
				out.writeInt32(set.array[n]);
			}
			out.writeInt8(0xFF);
			table.set(id, out);
		}

		public void remove(int id) throws IOException {
			table.remove(id);
		}

		public void drop() throws IOException {
			table.drop();
		}

		public void commit() throws IOException {
			table.commit();
		}

		public void rollback() throws IOException {
			table.rollback();
		}

		@Override
		public int hashCode() {
			return table.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return table.equals(obj);
		}

		@Override
		public String toString() {
			return table.toString();
		}

	}

	public static class DbInput {

		protected byte[] bytes;

		protected int offset;

		protected int length;

		public DbInput(byte[] bytes) {
			this.bytes = bytes;
		}

		public int readInt8() {
			return bytes[offset++] & 0xFF;
		}

		public int readInt16() {
			int result = (bytes[offset++] & 0xFF) << 8;
			result += (bytes[offset++] & 0xFF);
			return result;
		}

		public int readInt32() {
			int result = (bytes[offset++] & 0xFF) << 24;
			result += (bytes[offset++] & 0xFF) << 16;
			result += (bytes[offset++] & 0xFF) << 8;
			result += (bytes[offset++] & 0xFF);
			return result;
		}

		public long readInt64() {
			long left = readInt32();
			int right = readInt32();
			return (left << 32) + right;
		}

		public byte[] readBytes(int length) {
			byte[] result = new byte[length];
			System.arraycopy(bytes, offset, result, 0, length);
			offset += length;
			return result;
		}

		public String readUTF() {
			int length = readInt16();
			StringBuilder sb = new StringBuilder(length);
			for (int n = 0; n < length; n++) {
				int c1 = readInt8();
				if (c1 <= 0x7F) {
					sb.append((char) c1);
				} else if (c1 <= 0x7FF) {
					int c2 = readInt8();
					sb.append((char) (((c1 & 0x1F) << 6) + (c2 & 0x3F)));
				} else {
					int c2 = readInt8();
					int c3 = readInt8();
					sb.append((char) (((c1 & 0xF) << 12) + ((c2 & 0x3F) << 6) + (c3 & 0x3F)));
				}
			}
			return sb.toString();
		}

		public Map<String, Object> readMap() {
			int length = readInt16();
			Map<String, Object> map = new TreeMap<String, Object>();
			for (int n = 0; n < length; n++) {
				String key = readUTF();
				Object value = readObject();
				map.put(key, value);
			}
			return map;
		}

		public Object[] readArray() {
			int length = readInt16();
			Object[] array = new Object[length];
			for (int n = 0; n < length; n++) {
				Object value = readObject();
				array[n] = value;
			}
			return array;
		}

		public Object readObject() {
			int code = readInt8();
			switch (code) {
			case 1:
				return Character.valueOf((char) readInt8());
			case 2:
				return Short.valueOf((short) readInt16());
			case 3:
				return Integer.valueOf((int) readInt32());
			case 4:
				return Long.valueOf((int) readInt64());
			case 5:
				return Character.valueOf((char) readInt8());
			case 6:
				return Short.valueOf((short) readInt16());
			case 7:
				return Integer.valueOf((int) readInt32());
			case 8:
				return Long.valueOf((int) readInt64());
			case 9:
				return Double.valueOf(readUTF());
			case 10:
				return Double.valueOf(readUTF());
			case 11:
				return readUTF();
			case 12:
				return readMap();
			case 13:
				return readArray();
			default:
				return null;
			}
		}
		
	}

	public static class DbOutput {

		protected byte[] bytes;

		protected int size;

		protected int max;

		public DbOutput writeInt8(int value) {
			if (size >= max) {
				max *= 2;
				bytes = Arrays.copyOf(bytes, max);
			}
			bytes[size++] = (byte) (value & 0xFF);
			return this;
		}

		public void reset() {
			size = 0;
		}

		public DbOutput writeInt16(int value) {
			if (size + 1 >= max) {
				max *= 2;
				bytes = Arrays.copyOf(bytes, max);
			}
			bytes[size++] = (byte) ((value >> 8) & 0xFF);
			bytes[size++] = (byte) (value & 0xFF);
			return this;
		}

		public DbOutput writeInt32(int value) {
			if (size + 3 >= max) {
				max *= 2;
				bytes = Arrays.copyOf(bytes, max);
			}
			bytes[size++] = (byte) ((value >> 24) & 0xFF);
			bytes[size++] = (byte) ((value >> 16) & 0xFF);
			bytes[size++] = (byte) ((value >> 8) & 0xFF);
			bytes[size++] = (byte) (value & 0xFF);
			return this;
		}

		public DbOutput writeInt64(long value) {
			if (size + 7 >= max) {
				max *= 2;
				bytes = Arrays.copyOf(bytes, max);
			}
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

		public DbOutput writeBytes(byte[] data, int offset, int length) {
			if (size + length - 1 >= max) {
				max = max + (max >> 1) + length;
				bytes = Arrays.copyOf(bytes, max);
			}
			System.arraycopy(data, 0, bytes, size, length);
			size += length;
			return this;
		}

		public DbOutput writeUTF(String value) {
			for (int n = 0; n < value.length(); n++) {
				int c = value.charAt(n);
				if (c <= 0x7F) {
					bytes[size++] = (byte) c;
				} else if (c <= 0x7FF) {
					bytes[size++] = (byte) (((c >> 6) & 0x1F) + 0xC0);
					bytes[size++] = (byte) ((c & 0x3F) + 0x80);
				} else {
					bytes[size++] = (byte) (((c >> 12) & 0xF) + 0xE0);
					bytes[size++] = (byte) (((c >> 6) & 0x3F) + 0x80);
					bytes[size++] = (byte) ((c & 0x3F) + 0x80);
				}
			}
			return this;
		}

		public DbOutput writeFloat(float value) {
			return writeUTF(Float.toString(value));
		}

		public DbOutput writeDouble(double value) {
			return writeUTF(Double.toString(value));
		}

		public DbOutput writeMap(Map<?, ?> value) {
			int size = value.size();
			if (size > 0xFFFF) {
				throw new IllegalArgumentException();
			}
			writeInt16(size);
			for (Entry<?, ?> entry : value.entrySet()) {
				writeUTF(entry.getKey().toString());
				writeObject(entry.getValue());
			}
			return this;
		}

		public DbOutput writeArray(Object[] value) {
			int size = value.length;
			if (size > 0xFFFF) {
				throw new IllegalArgumentException();
			}
			writeInt16(size);
			for (int n = 0; n < value.length; n++) {
				writeObject(value[n]);
			}
			return this;
		}

		public DbOutput writeObject(Object value) {
			if (value instanceof Character) {
				writeInt8(1);
				return writeInt8(((Character) value).charValue());
			} else if (value instanceof Short) {
				writeInt8(2);
				return writeInt16(((Short) value).shortValue());
			} else if (value instanceof Integer) {
				writeInt8(3);
				return writeInt32(((Integer) value).intValue());
			} else if (value instanceof Long) {
				writeInt8(4);
				return writeInt64(((Long) value).longValue());
			} else if (value instanceof Character) {
				writeInt8(5);
				return writeInt8(((Character) value).charValue());
			} else if (value instanceof Short) {
				writeInt8(6);
				return writeInt16(((Short) value).shortValue());
			} else if (value instanceof Integer) {
				writeInt8(7);
				return writeInt32(((Integer) value).intValue());
			} else if (value instanceof Long) {
				writeInt8(8);
				return writeInt64(((Long) value).longValue());
			} else if (value instanceof Float) {
				writeInt8(9);
				return writeFloat(((Float) value).floatValue());
			} else if (value instanceof Double) {
				writeInt8(10);
				return writeDouble(((Double) value).doubleValue());
			} else if (value instanceof String) {
				writeInt8(11);
				return writeUTF((String) value);
			} else if (value instanceof Map<?, ?>) {
				writeInt8(12);
				return writeMap((Map<?, ?>) value);
			} else if (value.getClass().isArray()) {
				writeInt8(13);
				return writeArray((Object[]) value);
			} else {
				throw new IllegalArgumentException();
			}
		}

		public byte[] toBytes() {
			if (size == max) {
				return bytes;
			} else {
				max = size;
				return Arrays.copyOf(bytes, size);
			}
		}

	}

	public static interface DbIO {

		public boolean exist(String name) throws IOException;

		public boolean exist(String name, int page) throws IOException;

		public byte[] read(String name) throws IOException;

		public byte[] read(String name, int page) throws IOException;

		public void write(String name, byte[] bytes) throws IOException;

		public void write(String name, int page, byte[] bytes)
				throws IOException;

		public void remove(String name);

		public void remove(String name, int id);

		public void commit();

		public void rollback();

	}

	public static class DbMemoryIO implements DbIO {

		protected final Map<String, byte[]> cache = new TreeMap<String, byte[]>();

		@Override
		public boolean exist(String name) throws IOException {
			return cache.containsKey(name);
		}

		@Override
		public boolean exist(String name, int page) throws IOException {
			return cache.containsKey(name + "$" + page);
		}

		@Override
		public byte[] read(String name) throws IOException {
			return cache.get(name);
		}

		@Override
		public byte[] read(String name, int page) throws IOException {
			return cache.get(name + "$" + page);
		}

		@Override
		public void write(String name, byte[] bytes) throws IOException {
			cache.put(name, bytes);
		}

		@Override
		public void write(String name, int page, byte[] bytes)
				throws IOException {
			cache.put(name + "$" + page, bytes);
		}

		@Override
		public void remove(String name) {
			cache.remove(name);
		}

		@Override
		public void remove(String name, int id) {
			cache.remove(name + "$" + id);
		}

		@Override
		public void commit() {
		}

		@Override
		public void rollback() {
		}

	}

	public static class DbFileIO implements DbIO {

		protected final Map<String, byte[]> cache = new TreeMap<String, byte[]>();

		@Override
		public boolean exist(String name) throws IOException {
			return cache.containsKey(name);
		}

		@Override
		public boolean exist(String name, int page) throws IOException {
			return cache.containsKey(name + "$" + page);
		}

		@Override
		public byte[] read(String name) throws IOException {
			return cache.get(name);
		}

		@Override
		public byte[] read(String name, int page) throws IOException {
			return cache.get(name + "$" + page);
		}

		@Override
		public void write(String name, byte[] bytes) throws IOException {
			cache.put(name, bytes);
		}

		@Override
		public void write(String name, int page, byte[] bytes)
				throws IOException {
			cache.put(name + "$" + page, bytes);
		}

		@Override
		public void remove(String name) {
			cache.remove(name);
		}

		@Override
		public void remove(String name, int id) {
			cache.remove(name + "$" + id);
		}

		@Override
		public void commit() {
		}

		@Override
		public void rollback() {
		}

	}

}
