package breder.db.impl;

import java.util.ArrayList;
import java.util.List;

public class DBTransaction {

	/** Revisão */
	private long id;
	/** Tempo que foi criado a transação */
	private long timer;
	/** Lista de modificações */
	private List<DBCommand> list;

	public DBTransaction() {
		this.timer = System.currentTimeMillis();
		this.list = new ArrayList<DBCommand>(10);
	}

	public void add(DBCommand command) {
		this.list.add(command);
	}

}
