package breder.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import breder.db.impl.DBDelete;
import breder.db.impl.DBInsert;
import breder.db.impl.DBSelect;
import breder.db.impl.DBTransaction;
import breder.db.impl.SoftMap;
import breder.db.impl.XmlNode;

public class DBDatabase {

	private final String name;

	private final Map<String, DBTable> tables = new SoftMap<String, DBTable>();

	private final Map<String, Integer> sequences = new HashMap<String, Integer>();

	private final ThreadLocal<DBTransaction> transactions = new ThreadLocal<DBTransaction>();

	private XmlNode config;

	public DBDatabase(String name) {
		this.name = name;
	}

	public DBDatabase open() throws IOException, ParseException {
		File dir = new File("db", this.name);
		if (!dir.exists()) {
			throw new IllegalStateException("database not exists");
		}
		if (new File(dir, "config.xml.aux").exists()) {
			try {
				this.config = readXml(new File(dir, "config.xml.aux"));
			} catch (ParseException e) {
				this.config = readXml(new File(dir, "config.xml"));
			}
		} else {
			this.config = readXml(new File(dir, "config.xml"));
		}
		return this;
	}

	protected XmlNode readXml(File file) throws ParseException, IOException {
		FileInputStream input = new FileInputStream(file);
		try {
			return new XmlNode(input);
		} finally {
			input.close();
		}
	}

	public DBDatabase create() throws IOException {
		File dir = new File("db", this.name);
		if (dir.exists()) {
			throw new IllegalStateException("database already exists");
		}
		dir.mkdirs();
		this.config = new XmlNode("database");
		FileOutputStream output = new FileOutputStream(new File(dir, "config.xml"));
		try {
			output.write(config.getBytes());
		} finally {
			output.close();
		}
		return this;
	}

	public DBTable create(String name) {
		File dir = new File(new File("db", this.name), name);
		if (dir.exists()) {
			throw new IllegalStateException("table already exists");
		}
		dir.mkdirs();
		return new DBTable(this, name);
	}

	public DBSelect select(String from, DBWhere where) {
		return null;
	}

	public DBSelect select(String from) {
		return select(from, null);
	}

	public DBInsert insert(DBData data) {
		return null;
	}

	public DBDelete delete(DBWhere where) {
		return null;
	}

	public void close() {
	}

	public DBDatabase drop() {
		File dbDir = new File("db", this.name);
		if (dbDir.exists()) {
			for (File tableFile : dbDir.listFiles()) {
				if (!tableFile.isHidden()) {
					if (tableFile.isFile()) {
						tableFile.delete();
					} else {
						get(tableFile.getName()).drop();
					}
				}
			}
		}
		dbDir.delete();
		return this;
	}

	public DBTable get(String name) {
		DBTable table = this.tables.get(name);
		if (table != null) {
			return table;
		}
		File dir = new File(new File("db", this.name), name);
		if (!dir.exists()) {
			throw new IllegalStateException("table not exists");
		}
		table = new DBTable(this, name);
		this.tables.put(name, table);
		return table;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void commit() {
		DBTransaction transaction = this.getTransaction();
		this.transactions.remove();
	}

	public void rollback() {
		this.transactions.remove();
	}

	/**
	 * Retorna a transação
	 * 
	 * @return transação
	 */
	DBTransaction getTransaction() {
		DBTransaction transaction = this.transactions.get();
		if (transaction == null) {
			this.transactions.set(transaction = new DBTransaction());
		}
		return transaction;
	}

	/**
	 * Retorna a sequence de uma tabela
	 * 
	 * @param table
	 * @return sequence de uma tabela
	 */
	int getSequence(String table) {
		Integer value = this.sequences.get(table);
		this.sequences.put(table, value + 1);
		return value;
	}

}
