package breder.db;

import java.io.File;

import breder.db.impl.DBDelete;
import breder.db.impl.DBInsert;
import breder.db.impl.DBTransaction;

public class DBTable {

	private final DBDatabase db;

	private final String name;

	private File dir;
	
	public DBTable(DBDatabase db, String name) {
		this.db = db;
		this.name = name;
		dir = new File(new File("db", db.getName()), name);
	}

	public DBTable drop() {
		for (File file : dir.listFiles()) {
			file.delete();
		}
		dir.delete();
		return this;
	}

	public int insert(DBData data) {
		DBTransaction transaction = this.db.getTransaction();
		transaction.add(new DBInsert(data));
		return this.db.getSequence(name);
	}

	public DBTable delete(DBWhere where) {
		DBTransaction transaction = this.db.getTransaction();
		transaction.add(new DBDelete(where));
		return this;
	}

}
