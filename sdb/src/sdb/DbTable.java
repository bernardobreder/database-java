package sdb;

import java.io.IOException;

import sdb.DbTableTree.DbTableTreeIODelegator;
import sdb.DbTableTree.DbTableTreeNode;

/**
 * Tabela de banco de dados
 * 
 * @author bernardobreder
 */
public class DbTable {

	/** Nome */
	protected final String name;
	/** Arvore B para os dados */
	protected DbTableTree tree;
	/** Sequence */
	protected long sequence;
	/** Mudança nos dados */
	protected boolean dataChanged;

	/**
	 * Construtor de tabela
	 * 
	 * @param name
	 * @param treeDelegator
	 * @param order
	 * @throws IOException
	 */
	protected DbTable(DbTableTreeIODelegator treeDelegator, String name, int order) throws IOException {
		this.name = name;
		this.sequence = 1;
		if (treeDelegator.hasStructure(name)) {
			this.tree = DbTableTree.readStructure(treeDelegator, name);
		} else {
			this.tree = DbTableTree.createStructure(treeDelegator, name, 1001);
			this.tree.structureChanged = true;
		}
	}

	/**
	 * Consulta um código de id
	 * 
	 * @param id
	 * @return valor
	 * @throws IOException
	 */
	public DbObject get(long id) throws IOException {
		return tree.get(id);
	}

	/**
	 * Consulta um código de id
	 * 
	 * @param id
	 * @return valor
	 * @throws IOException
	 */
	public boolean has(long id) throws IOException {
		return tree.get(id) != null;
	}

	/**
	 * Atualiza um dado baseado no código do id
	 * 
	 * @param id
	 * @param value
	 * @throws IOException
	 */
	public void set(long id, DbObject value) throws IOException {
		if (tree.set(value, id)) {
			dataChanged = true;
		}
	}

	/**
	 * Insere um novo registro
	 * 
	 * @param value
	 * @return código inserido
	 * @throws IOException
	 */
	public long add(DbObject value) throws IOException {
		long id = tree.last() + 1;
		tree.add(value, id);
		dataChanged = true;
		return id;
	}

	/**
	 * Remove um registro
	 * 
	 * @param id
	 * @throws IOException
	 */
	public void del(long id) throws IOException {
		if (tree.set(null, id)) {
			dataChanged = true;
		}
	}

	/**
	 * Retorna o último id
	 * 
	 * @return último id
	 * @throws IOException
	 */
	public long lastDataId() throws IOException {
		return tree.last();
	}

	/**
	 * @throws IOException
	 */
	public void commit() throws IOException {
		if (tree.structureChanged) {
			DbOutputBytes out = new DbOutputBytes();
			tree.writeStructure(out);
			tree.io.writeStructure(name, out.getBytes());
		}
		{
			commit(tree.root);
		}
	}

	/**
	 * @param node
	 * @throws IOException
	 */
	protected void commit(DbTableTreeNode node) throws IOException {
		for (int m = 0; m <= node.length; m++) {
			if (node.childrenNode[m] != null) {
				commit(node.childrenNode[m]);
			}
		}
		if (node.changed) {
			DbOutputBytes out = tree.writeNode(node);
			tree.io.writeNode(name, node.id, out.getBytes());
			node.changed = false;
		}
	}

	/**
	 * Restaura as mudanças
	 */
	public void rollback() {
	}

}
