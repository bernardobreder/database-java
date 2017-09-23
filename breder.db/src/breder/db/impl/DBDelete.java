package breder.db.impl;

import breder.db.DBWhere;

public class DBDelete extends DBCommand {

	private final DBWhere where;

	public DBDelete(DBWhere where) {
		this.where = where;
	}

}
