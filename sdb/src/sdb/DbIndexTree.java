package sdb;

import java.io.IOException;

/**
 * Implementação baseada no site
 * https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
 * 
 * @author bernardobreder
 */
public class DbIndexTree {

	/** Nome da arvore */
	protected final String name;
	/** Tamanho */
	protected final int order;
	/** Número de chaves */
	protected final int keys;
	/** Sequence */
	protected long sequence;
	/** Root */
	protected DbIndexTreeNode root;
	/** Root */
	protected boolean structureChanged;
	/** Root */
	protected boolean dataChanged;
	/** Entrada e Saída */
	protected DbIndexTreeIODelegator io;

	/**
	 * @param io
	 * @param name
	 * @param order
	 * @param keys
	 */
	private DbIndexTree(DbIndexTreeIODelegator io, String name, int order, int keys) {
		this.io = io;
		this.name = name;
		this.order = order;
		this.keys = keys;
	}

	/**
	 * @param io
	 * @param name
	 * @param order
	 * @param keys
	 * @return node
	 */
	public static DbIndexTree createStructure(DbIndexTreeIODelegator io, String name, int order, int keys) {
		DbIndexTree self = new DbIndexTree(io, name, order, keys);
		self.root = new DbIndexTreeNode(order, keys, ++self.sequence);
		return self;
	}

	/**
	 * Realiza a leitura da escrutura
	 * 
	 * @param io
	 * @param name
	 * @return node
	 * @throws IOException
	 */
	public static DbIndexTree readStructure(DbIndexTreeIODelegator io, String name) throws IOException {
		byte[] structureBytes = io.readStructure(name);
		DbInputBytes in = new DbInputBytes(structureBytes);
		int order = (int) in.readLongCompressed();
		int keys = in.readUByte();
		DbIndexTree self = new DbIndexTree(io, name, order, keys);
		self.sequence = in.readLongCompressed();
		self.root = self.nodeLoad(in.readLongCompressed());
		if (!in.readEof()) {
			throw new IllegalStateException("expected eof");
		}
		return self;
	}

	/**
	 * Realiza a escrita da estrutura da árvore
	 * 
	 * @param out
	 */
	public void writeStructure(DbOutputBytes out) {
		out.writeLongCompressed(order);
		out.writeUByte(keys);
		out.writeLongCompressed(sequence);
		out.writeLongCompressed(root.id);
	}

	/**
	 * Converte bytes em node
	 * 
	 * @param in
	 * @return node
	 */
	public DbIndexTreeNode readNode(DbInputBytes in) {
		long id = in.readLongCompressed();
		DbIndexTreeNode node = new DbIndexTreeNode(order, keys, id);
		boolean leaf = in.readBoolean();
		node.leaf = leaf;
		int length = in.readUShort();
		node.length = length;
		if (!leaf) {
			node.childrenId[0] = in.readLongCompressed();
		}
		for (int n = 0; n < length; n++) {
			for (int k = 0; k < keys; k++) {
				node.keys[n * keys + k] = in.readLongCompressed();
			}
			node.values[n] = in.readLongCompressed();
			if (!leaf) {
				node.childrenId[n + 1] = in.readLongCompressed();
			}
		}
		node.nextId = in.readLongCompressed();
		return node;
	}

	/**
	 * @param node
	 * @return bytes
	 */
	public DbOutputBytes writeNode(DbIndexTreeNode node) {
		DbOutputBytes out = new DbOutputBytes();
		out.writeLongCompressed(node.id);
		out.writeBoolean(node.leaf);
		out.writeUShort(node.length);
		if (!node.leaf) {
			out.writeLongCompressed(node.childrenId[0]);
		}
		for (int n = 0; n < node.length; n++) {
			for (int k = 0; k < keys; k++) {
				out.writeLongCompressed(node.keys[n * keys + k]);
			}
			out.writeLongCompressed(node.values[n]);
			if (!node.leaf) {
				out.writeLongCompressed(node.childrenId[n + 1]);
			}
		}
		out.writeLongCompressed(node.nextId);
		return out;
	}

	/**
	 * Consulta o valor da chave. Caso não ache, será retornado nulo.
	 * 
	 * @param key
	 *            valor da chave
	 * @return valor da chave ou nulo
	 * @throws IOException
	 */
	public long get(long... key) throws IOException {
		DbIndexTreeNode node = get(root, key);
		if (node == null) {
			return 0;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return 0;
		}
		return node.values[idx];
	}

	/**
	 * Consulta o valor da chave. Caso não ache, será retornado nulo.
	 * 
	 * @param key
	 *            valor da chave
	 * @param value
	 *            valor
	 * @return valor da chave ou nulo
	 * @throws IOException
	 */
	public boolean set(long value, long... key) throws IOException {
		DbIndexTreeNode node = get(root, key);
		if (node == null) {
			return false;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return false;
		}
		node.values[idx] = value;
		dataChanged = true;
		return true;
	}

	/**
	 * Adiciona um valor
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void add(long value, long... key) throws IOException {
		add(root, key, value);
		if (root.length == order) {
			DbIndexTreeNode node = new DbIndexTreeNode(order, keys, ++sequence);
			addSplit(node, root, 0);
			root = node;
			structureChanged = true;
		}
		dataChanged = true;
	}

	/**
	 * Retorna o node correspondente a chave ou nulo caso não ache.
	 * 
	 * @param node
	 *            node raiz
	 * @param key
	 *            chave
	 * @return node da chave ou nulo caso não ache
	 * @throws IOException
	 */
	protected DbIndexTreeNode get(DbIndexTreeNode node, long[] key) throws IOException {
		if (node.leaf) {
			return findKey(node, key) < 0 ? null : node;
		} else {
			return get(nodeChild(node, findChildKey(node, key)), key);
		}
	}

	/**
	 * Adiciona um valor
	 * 
	 * @param node
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	protected void add(DbIndexTreeNode node, long[] key, long value) throws IOException {
		int childIndex = findChildKey(node, key);
		if (node.leaf) {
			if (node.length - childIndex > 0) {
				System.arraycopy(node.keys, childIndex * keys, node.keys, childIndex * keys + keys, (node.length - childIndex) * keys);
				System.arraycopy(node.values, childIndex, node.values, childIndex + 1, node.length - childIndex);
			}
			nodeAssignKey(node, childIndex, key, value);
			node.length++;
		} else {
			DbIndexTreeNode child = nodeChild(node, childIndex);
			add(child, key, value);
			if (child.length == order) {
				addSplit(node, child, childIndex);
			}
		}
	}

	/**
	 * Quando um node está cheio (node.keys.length == order), deve-se dividir o
	 * node.<br/>
	 * <br/>
	 * A divisão transforma um node cheio em 2 nodes e o elemento do meio sobe
	 * um nível mais próximo da raiz.<br/>
	 * <br/>
	 * O parâmetro 'root' irá receber o elemento do meio e o parâmetro 'node'
	 * será dividido em 2 nodes das quais o elemento que subiu para o 'root' irá
	 * apontar para os dois nodes criados.<br/>
	 * <br/>
	 * Com o objetivo de reaproveitamento de memória, o parâmetro 'node' será o
	 * elemento a esquerda da chave que subiu para o 'root' e um outro node será
	 * criado a direta da chave que subiu para o 'root'.
	 * 
	 * @param root
	 *            node que receberá o elemento do meio
	 * @param node
	 *            node a ser dividido em dois
	 * @param index
	 * @throws IOException
	 */
	protected void addSplit(DbIndexTreeNode root, DbIndexTreeNode node, int index) throws IOException {
		int idx = order / 2;
		// Constroi o lado da direita
		DbIndexTreeNode right = new DbIndexTreeNode(order, keys, ++sequence);
		right.leaf = node.leaf;
		if (node.leaf) {
			nodeCopyKeys(right, 0, node, idx);
			right.length = idx + 1;
		} else {
			nodeCopyKeys(right, 0, node, idx + 1);
			nodeCopyChildren(right, 0, node, idx + 1);
			right.length = idx;
		}
		right.next = node.next;
		right.nextId = node.nextId;
		// Atualiza a raiz
		for (int n = root.length - 1; n >= index; n--) {
			nodeAssignKey(root, n + 1, root, n);
		}
		for (int n = root.length; n >= index; n--) {
			nodeAssignChild(root, n + 1, root.childrenId[n], root.childrenNode[n]);
		}
		root.length++;
		nodeAssignKey(root, index, node, idx);
		root.leaf = false;
		nodeAssignChild(root, index, node.id, node);
		nodeAssignChild(root, index + 1, right.id, right);
		root.changed = true;
		// Constroi o lado da esquerda
		node.length = idx;
		node.next = right;
		node.nextId = right.id;
		node.changed = true;
		// Atualizando o next da esquerda para direita
	}

	/**
	 * Recupera um filho ou carrega
	 * 
	 * @param node
	 * @param idx
	 * @return node filho
	 * @throws IOException
	 */
	protected DbIndexTreeNode nodeChild(DbIndexTreeNode node, int idx) throws IOException {
		DbIndexTreeNode child = node.childrenNode[idx];
		if (child != null) {
			return child;
		}
		if (io == null) {
			return null;
		}
		return node.childrenNode[idx] = nodeLoad(node.childrenId[idx]);
	}

	/**
	 * Retorna o próximo node ou carrega
	 * 
	 * @param node
	 * @return próximo node
	 * @throws IOException
	 */
	protected DbIndexTreeNode nodeNext(DbIndexTreeNode node) throws IOException {
		if (node.nextId == 0) {
			return null;
		}
		DbIndexTreeNode next = node.next;
		if (next != null) {
			return next;
		}
		if (io == null) {
			return null;
		}
		return nodeLoad(node.nextId);
	}

	/**
	 * Carrega um node
	 * 
	 * @param id
	 * @return node
	 * @throws IOException
	 */
	protected DbIndexTreeNode nodeLoad(long id) throws IOException {
		byte[] bytes = io.readNode(name, id);
		DbInputBytes in = new DbInputBytes(bytes);
		DbIndexTreeNode node = readNode(in);
		if (!in.readEof()) {
			throw new IllegalStateException("expected eof");
		}
		return node;
	}

	/**
	 * Busca por um filho no array de filhos
	 * 
	 * @param node
	 * @param key
	 * @return indice do filho
	 */
	protected int findChildKey(DbIndexTreeNode node, long[] key) {
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int compare = 0;
			for (int n = 0; n < key.length; n++) {
				long left = node.keys[mid * keys + n];
				long right = key[n];
				if (left < right) {
					compare = -1;
					break;
				} else if (left > right) {
					compare = 1;
					break;
				}
			}
			if (compare < 0) {
				low = mid + 1;
			} else if (compare > 0) {
				high = mid - 1;
			} else {
				return mid + 1;
			}
		}
		return low;
	}

	/**
	 * Busca por uma chave no array de chaves
	 * 
	 * @param node
	 * @param key
	 * @return indice da chave
	 */
	protected int findKey(DbIndexTreeNode node, long[] key) {
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int compare = 0;
			for (int n = 0; n < key.length; n++) {
				long left = node.keys[mid * keys + n];
				long right = key[n];
				if (left < right) {
					compare = -1;
					break;
				} else if (left > right) {
					compare = 1;
					break;
				}
			}
			if (compare < 0) {
				low = mid + 1;
			} else if (compare > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -1;
	}

	/**
	 * Copias as chaves
	 * 
	 * @param target
	 * @param targetIndex
	 * @param source
	 * @param sourceIndex
	 */
	protected void nodeCopyKeys(DbIndexTreeNode target, int targetIndex, DbIndexTreeNode source, int sourceIndex) {
		System.arraycopy(source.keys, sourceIndex * keys, target.keys, targetIndex * keys, (source.length - sourceIndex) * keys);
		System.arraycopy(source.values, sourceIndex, target.values, targetIndex, (source.length - sourceIndex));
	}

	/**
	 * Copia os filhos
	 * 
	 * @param target
	 * @param targetIndex
	 * @param source
	 * @param sourceIndex
	 */
	protected void nodeCopyChildren(DbIndexTreeNode target, int targetIndex, DbIndexTreeNode source, int sourceIndex) {
		System.arraycopy(source.childrenId, sourceIndex, target.childrenId, targetIndex, (source.length - sourceIndex) + 1);
		System.arraycopy(source.childrenNode, sourceIndex, target.childrenNode, targetIndex, (source.length - sourceIndex) + 1);
	}

	/**
	 * Associa uma chave
	 * 
	 * @param node
	 * @param index
	 * @param source
	 * @param sourceIndex
	 */
	protected void nodeAssignKey(DbIndexTreeNode node, int index, DbIndexTreeNode source, int sourceIndex) {
		for (int k = 0; k < keys; k++) {
			node.keys[index * keys + k] = source.keys[sourceIndex * keys + k];
		}
		node.values[index] = source.values[sourceIndex];
	}

	/**
	 * Associa uma chave
	 * 
	 * @param node
	 * @param index
	 * @param key
	 * @param value
	 */
	protected void nodeAssignKey(DbIndexTreeNode node, int index, long[] key, long value) {
		for (int k = 0; k < keys; k++) {
			node.keys[index * keys + k] = key[k];
		}
		node.values[index] = value;
	}

	/**
	 * Associa um filho
	 * 
	 * @param node
	 * @param index
	 * @param childId
	 * @param childNode
	 */
	protected void nodeAssignChild(DbIndexTreeNode node, int index, long childId, DbIndexTreeNode childNode) {
		node.childrenId[index] = childId;
		node.childrenNode[index] = childNode;
	}

	/**
	 * Estrtura de node
	 * 
	 * @author bernardobreder
	 */
	public static class DbIndexTreeNode {

		/** Identificador do node */
		public long id;
		/** Chaves */
		public long[] keys;
		/** Valores */
		public long[] values;
		/** Filhos */
		public long[] childrenId;
		/** Filhos */
		public DbIndexTreeNode[] childrenNode;
		/** Próximo */
		public long nextId;
		/** Próximo */
		public DbIndexTreeNode next;
		/** Comprimento */
		public int length;
		/** Folha */
		public boolean leaf;
		/** Mudado */
		public boolean changed;

		/**
		 * @param order
		 * @param keys
		 * @param id
		 */
		public DbIndexTreeNode(int order, int keys, long id) {
			this.id = id;
			this.keys = new long[order * keys];
			this.values = new long[order];
			this.childrenId = new long[order + 1];
			this.childrenNode = new DbIndexTreeNode[order + 1];
			this.leaf = true;
			this.changed = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			int keyn = keys.length / values.length;
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			if (leaf) {
				for (int n = 0; n < length; n++) {
					for (int k = 0; k < keyn; k++) {
						sb.append(keys[n * keyn + k]);
						if (k != keyn - 1) {
							sb.append(',');
						}
					}
					if (n != length - 1) {
						sb.append('|');
					}
				}
				if (next != null) {
					sb.append("->");
					if (next.length > 0) {
						for (int k = 0; k < keyn; k++) {
							sb.append(next.keys[k]);
							if (k != keyn - 1) {
								sb.append(',');
							}
						}
					}
				}
			} else {
				for (int n = 0; n < length; n++) {
					sb.append(childrenNode[n] != null ? childrenNode[n] : "[" + childrenId[n] + "]");
					for (int k = 0; k < keyn; k++) {
						sb.append(keys[n * keyn + k]);
						if (k != keyn - 1) {
							sb.append(',');
						}
					}
				}
				sb.append(childrenNode[length] != null ? childrenNode[length] : "[" + childrenId[length] + "]");
			}
			sb.append('}');
			return sb.toString();
		}
	}

	/**
	 * Estrutura que realiza a persistencia dos bytes de uma árvore de banco de
	 * dados
	 * 
	 * @author bernardobreder
	 */
	public static interface DbIndexTreeIODelegator {

		/**
		 * Retorna os bytes de um node
		 * 
		 * @param name
		 * @param id
		 *            codigo do node
		 * @return bytes do node
		 * @throws IOException
		 */
		public byte[] readNode(String name, long id) throws IOException;

		/**
		 * Escreve os bytes de um node
		 * 
		 * @param name
		 * @param id
		 *            código do node
		 * @param bytes
		 *            bytes do node
		 * @throws IOException
		 */
		public void writeNode(String name, long id, byte[] bytes) throws IOException;

		/**
		 * Retorna os bytes da estrutura de arvore
		 * 
		 * @param name
		 * @return bytes da estrutura de arquivo
		 * @throws IOException
		 */
		public byte[] readStructure(String name) throws IOException;

		/**
		 * Escreve os bytes da estrutura de arvore
		 * 
		 * @param name
		 * @param bytes
		 * @throws IOException
		 */
		public void writeStructure(String name, byte[] bytes) throws IOException;

		/**
		 * Indica se tem alguma estrutura de arvore persistida
		 * 
		 * @param name
		 * @return existe a estrutura
		 * @throws IOException
		 */
		public boolean hasStructure(String name) throws IOException;

	}

}
