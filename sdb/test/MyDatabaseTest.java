import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import sdb.DbDatabase;
import sdb.DbEntity;
import sdb.DbOutputBytes;
import sdb.DbTreeMap;
import sdb.DbEntity.DbEntityIODelegator;
import sdb.DbIndexTree.DbIndexTreeIODelegator;
import sdb.DbObject;
import sdb.DbTableTree.DbTableTreeIODelegator;

public class MyDatabaseTest extends DbDatabase {

	/**
	 * @param dbFileSystem
	 * @param name
	 */
	public MyDatabaseTest(DbFilesFileSystem dbFileSystem, String name) {
		super(dbFileSystem, dbFileSystem, dbFileSystem, dbFileSystem, name);
	}

	/**
	 * 
	 * 
	 * @author Tecgraf
	 */
	public static class DbFilesFileSystem implements DbDatabaseIODelegator, DbEntityIODelegator, DbIndexTreeIODelegator, DbTableTreeIODelegator {

		/** Dir */
		protected static File DIR;

		static {
			DIR = new File("C:/Documents and Settings/bernardobreder_local/Documents/Dropbox");
			if (!DIR.exists()) {
				DIR = new File("/Users/bernardobreder/Dropbox");
				if (!DIR.exists()) {
					throw new Error();
				}
			}
			DIR = new File(DIR, "db");
			DIR.mkdirs();
		}

		/**
		 * @param in
		 * @param length
		 * @return bytes
		 * @throws IOException
		 */
		protected static byte[] readBytes(InputStream in, long length) throws IOException {
			try {
				if (length > 0) {
					byte[] bytes = new byte[(int) length];
					int len = 0;
					while (len != bytes.length) {
						len += in.read(bytes, len, bytes.length - len);
					}
					return bytes;
				} else {
					ByteArrayOutputStream out = new ByteArrayOutputStream(length > 0 ? (int) length : 1024);
					byte[] bytes = new byte[8 * 1024];
					for (int n; (n = in.read(bytes)) != -1;) {
						out.write(bytes, 0, n);
					}
					return out.toByteArray();
				}
			} finally {
				in.close();
			}
		}

		/**
		 * @param out
		 * @param bytes
		 * @throws IOException
		 */
		protected static void writeBytes(OutputStream out, byte[] bytes) throws IOException {
			try {
				out.write(bytes);
			} finally {
				out.close();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readNode(String name, long id) throws IOException {
			File file = new File(DIR, name + ".body." + id + ".db");
			return readBytes(new FileInputStream(file), file.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeNode(String name, long id, byte[] bytes) throws IOException {
			File file = new File(DIR, name + ".body." + id + ".db");
			writeBytes(new FileOutputStream(file), bytes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readStructure(String name) throws IOException {
			File file = new File(DIR, name + ".head.db");
			return readBytes(new FileInputStream(file), file.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeStructure(String name, byte[] bytes) throws IOException {
			File file = new File(DIR, name + ".head.db");
			writeBytes(new FileOutputStream(file), bytes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasStructure(String name) throws IOException {
			File file = new File(DIR, name + ".head.db");
			return file.exists();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readEntity(String databaseName, String entityName) throws IOException {
			File file = new File(DIR, databaseName + "." + entityName + ".entity.db");
			return readBytes(new FileInputStream(file), file.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeEntity(String databaseName, String entityName, byte[] bytes) throws IOException {
			File file = new File(DIR, databaseName + "." + entityName + ".entity.db");
			writeBytes(new FileOutputStream(file), bytes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasEntity(String databaseName, String entityName) throws IOException {
			File file = new File(DIR, databaseName + "." + entityName + ".entity.db");
			return file.exists();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readDatabase(String name) throws IOException {
			File file = new File(DIR, name + ".db");
			return readBytes(new FileInputStream(file), file.length());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeDatabase(String name, byte[] bytes) throws IOException {
			File file = new File(DIR, name + ".db");
			writeBytes(new FileOutputStream(file), bytes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasDatabase(String name) throws IOException {
			File file = new File(DIR, name + ".db");
			return file.exists();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dropDatabase(String name) throws IOException {
		}

	}

	/**
	 * 
	 * 
	 * @author Tecgraf
	 */
	public static class DbUniqueFileFileSystem implements DbDatabaseIODelegator, DbEntityIODelegator, DbIndexTreeIODelegator, DbTableTreeIODelegator {

		/** Dir */
		protected static File FILE;
		/** Dir */
		protected static int BUFFER_SIZE = 128 * 1024;
		/** Dir */
		protected RandomAccessFile file;

		protected DbTreeMap<Long, Long[]> pages;

		protected DbTreeMap<Long, Long> sizes;

		static {
			FILE = new File("C:/Documents and Settings/bernardobreder_local/Documents/Dropbox");
			if (!FILE.exists()) {
				FILE = new File("/Users/bernardobreder/Dropbox");
				if (!FILE.exists()) {
					throw new Error();
				}
			}
			FILE = new File(new File(FILE, "db"), "database.db");
			FILE.getParentFile().mkdirs();
		}

		public DbUniqueFileFileSystem() throws IOException {
			file = new RandomAccessFile(FILE, "rws");
			pages = new DbTreeMap<Long, Long[]>();
			sizes = new DbTreeMap<Long, Long>();
		}

		public void close() throws IOException {
			file.close();
		}

		/**
		 * @param in
		 * @param length
		 * @return bytes
		 * @throws IOException
		 */
		protected static byte[] readBytes(RandomAccessFile file, long offset, long length) throws IOException {
			if (offset + length > file.length()) {
				return null;
			}
			file.seek(offset);
			byte[] bytes = new byte[(int) length];
			int len = 0;
			while (len != bytes.length) {
				len += file.read(bytes, len, bytes.length - len);
			}
			return bytes;
		}

		/**
		 * @param in
		 * @param length
		 * @return bytes
		 * @throws IOException
		 */
		protected static boolean hasBytes(RandomAccessFile file, long offset, long length) throws IOException {
			if (offset + length > file.length()) {
				return false;
			}
			return true;
		}

		/**
		 * @param out
		 * @param bytes
		 * @throws IOException
		 */
		protected static void writeBytes(RandomAccessFile file, long offset, byte[] bytes, int bufferSize) throws IOException {
			file.seek(offset);
			file.write(bytes);
			int size = bytes.length % bufferSize;
			bytes = new byte[size];
			Arrays.fill(bytes, (byte) 0);
			file.write(bytes);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readDatabase(String name) throws IOException {
			return readBytes(file, 0, BUFFER_SIZE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeDatabase(String name, byte[] bytes) throws IOException {
			DbOutputBytes out = new DbOutputBytes(bytes.length);
			bytes = out.getBytes();
			sizes.put(0l, (long) bytes.length);
			writeBytes(file, 0, bytes, BUFFER_SIZE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasDatabase(String name) throws IOException {
			return hasBytes(file, 0, BUFFER_SIZE);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dropDatabase(String name) throws IOException {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readEntity(String databaseName, String entityName) throws IOException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeEntity(String databaseName, String entityName, byte[] bytes) throws IOException {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasEntity(String databaseName, String entityName) throws IOException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readNode(String name, long id) throws IOException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeNode(String name, long id, byte[] bytes) throws IOException {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public byte[] readStructure(String name) throws IOException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void writeStructure(String name, byte[] bytes) throws IOException {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasStructure(String name) throws IOException {
			return false;
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		MyDatabaseTest db = new MyDatabaseTest(new DbFilesFileSystem(), "test");
		if (!db.exist()) {
			db.create();
			{
				DbEntity personEntity = db.addEntity("person");
				personEntity.addIndex("cpf", "cpf");
			}
			{
				DbEntity phoneEntity = db.addEntity("phone");
				phoneEntity.addIndex("person_id", "person_id");
			}
			db.commit();
		} else {
			db.open();
			db.getEntity("person").getIndex("cpf").get(512);
		}
		long id = db.getEntity("person").getDataLastId();
		DbEntity personEntity = db.getEntity("person");
		for (int n = 0; n < 400 * 1024; n++) {
			DbObject value = new DbObject().putAsString("name", "Bernardo").putAsLong("cpf", ++id);
			personEntity.addData(value);
			if ((n % 1024) == 0) {
				System.out.println(n);
			}
		}
		db.commit();
	}
}
