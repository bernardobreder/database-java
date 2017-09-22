package simple;

import java.io.IOException;
import java.util.Comparator;

import utils.DbInputBytes;
import utils.DbOutputBytes;

/**
 * Implementação baseada no site
 * https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
 * 
 * @param <K>
 * @param <V>
 * @author bernardobreder
 */
public class BXTree<K, V> {

	/** Nome da arvore */
	protected final String name;
	/** Tamanho */
	protected final int order;
	/** Sequence */
	protected long sequence;
	/** Root */
	protected K rootId;
	/** Root */
	protected DbTreeNode<K, V> root;
	/** Entrada e Saída */
	protected DbTreeIODelegator io;
	/** Comparador */
	protected Comparator<K> comparator;

	/**
	 * @param io
	 * @param name
	 * @param order
	 * @throws IOException
	 */
	public BXTree(DbTreeIODelegator io, String name, int order)
			throws IOException {
		this(io, name, order, null);
	}

	/**
	 * @param io
	 * @param name
	 * @param order
	 * @param comparator
	 * @throws IOException
	 */
	public BXTree(DbTreeIODelegator io, String name, int order,
			Comparator<K> comparator) throws IOException {
		this.io = io;
		this.name = name;
		this.comparator = comparator;
		if (io.hasStructure(name)) {
			byte[] structureBytes = io.readStructure(name);
			DbInputBytes structureIn = new DbInputBytes(structureBytes);
			this.sequence = structureIn.readLongCompressed();
			long rootId = structureIn.readLongCompressed();
			this.root = readDbTreeNode(new DbInputBytes(io.readNode(name,
					rootId)));
			this.order = this.root.keys.length;
		} else {
			this.order = order;
			this.root = new DbTreeNode<K, V>(order, ++sequence);
		}
	}

	/**
	 * Realiza a escrita da estrutura da árvore
	 * 
	 * @param out
	 */
	public void writeStructure(DbOutputBytes out) {
		out.writeLongCompressed(sequence);
		out.writeLongCompressed(root.id);
		out.writeUByte(0xFF);
	}

	/**
	 * Converte bytes em node
	 * 
	 * @param in
	 * @return node
	 */
	public DbTreeNode<K, V> readDbTreeNode(DbInputBytes in) {
		int order = in.readUShort();
		long id = in.readLongCompressed();
		DbTreeNode<K, V> node = new DbTreeNode<K, V>(order, id);
		boolean leaf = in.readBoolean();
		node.leaf = leaf;
		int length = in.readUShort();
		node.length = length;
		if (!leaf) {
			node.childrenId[0] = in.readLongCompressed();
		}
		for (int n = 0; n < length; n++) {
			node.keys[n] = in.readObject();
			node.values[n] = in.readObject();
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
	public DbOutputBytes writeDbTreeNode(DbTreeNode<?, ?> node) {
		DbOutputBytes out = new DbOutputBytes();
		out.writeUShort(order);
		out.writeLongCompressed(node.id);
		out.writeBoolean(node.leaf);
		out.writeUShort(node.length);
		if (!node.leaf) {
			out.writeLongCompressed(node.childrenId[0]);
		}
		for (int n = 0; n < node.length; n++) {
			out.writeObject(node.keys[n]);
			out.writeObject(node.values[n]);
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
	public V get(K key) throws IOException {
		DbTreeNode<K, V> node = get(root, key);
		if (node == null) {
			return null;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return null;
		}
		return (V) node.values[idx];
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
	public boolean set(K key, V value) throws IOException {
		DbTreeNode<K, V> node = get(root, key);
		if (node == null) {
			return false;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return false;
		}
		node.values[idx] = value;
		return true;
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
	protected DbTreeNode<K, V> get(DbTreeNode<K, V> node, K key)
			throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (keyIndex < 0) {
				return null;
			}
			return node;
		} else {
			int childIndex = findChildKey(node, key);
			DbTreeNode<K, V> child = nodeChild(node, childIndex);
			return get(child, key);
		}
	}

	/**
	 * Adiciona um valor
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void add(K key, V value) throws IOException {
		add(root, key, value);
		if (nodeIsFull(root)) {
			DbTreeNode<K, V> node = new DbTreeNode<K, V>(order, ++sequence);
			addSplit(node, root, 0);
			root = node;
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
	protected void add(DbTreeNode<K, V> node, K key, V value)
			throws IOException {
		int childIndex = findChildKey(node, key);
		if (node.leaf) {
			nodeShiftKeyRight(node, childIndex, 1);
			nodeAssignKey(node, childIndex, key, value);
			node.length++;
		} else {
			DbTreeNode<K, V> child = nodeChild(node, childIndex);
			add(child, key, value);
			if (nodeIsFull(child)) {
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
	 */
	protected void addSplit(DbTreeNode<K, V> root, DbTreeNode<K, V> node,
			int index) {
		int idx = order / 2;
		DbTreeNode<K, V> rightNext = nodeNext(node);
		// Constroi o lado da raiz
		nodeShiftKeyRight(root, index, 1);
		nodeShiftChildrenRight(root, index, 1);
		nodeAssignKey(root, index, node, idx);
		root.leaf = false;
		root.length++;
		// Constroi o lado da direita
		DbTreeNode<K, V> right = new DbTreeNode<K, V>(order, ++sequence);
		right.leaf = node.leaf;
		if (node.leaf) {
			nodeCopyKeys(right, 0, node, idx);
			right.length = idx + 1;
		} else {
			nodeCopyKeys(right, 0, node, idx + 1);
			nodeCopyChildren(right, 0, node, idx + 1);
			right.length = idx;
		}
		// Constroi o lado da esquerda
		nodeClearKeys(node, idx);
		if (!node.leaf) {
			nodeClearChildren(node, idx + 1);
		}
		node.length = idx;
		node.changed = true;
		// Atualiza a raiz
		nodeAssignChild(root, index, node.id, node);
		nodeAssignChild(root, index + 1, right.id, right);
		root.changed = true;
		// Atualizando o next da esquerda para direita
		nodeAssignNext(node, right);
		nodeAssignNext(right, rightNext);
	}

	public boolean remove(K key) throws IOException {
		boolean removed = remove(root, key);
		if (root.length == 0) {
			if (root.childrenNode[0] != null) {
				root = root.childrenNode[0];
			}
		}
		return removed;
	}

	protected boolean remove(DbTreeNode<K, V> node, K key) throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (!node.keys[keyIndex].equals(key)) {
				return false;
			}
			removeNodeLeafIdx(node, keyIndex);
			return true;
		} else {
			int childIndex = findChildKey(node, key);
			DbTreeNode<K, V> child = nodeChild(node, childIndex);
			boolean removed = remove(child, key);
			if (removed) {
				removeFixFirst(node, child, key);
				if (nodeIsNotEnough(child)) {
					join(node, child, childIndex);
				}
			}
			return removed;
		}
	}

	protected void removeFixFirst(DbTreeNode<K, V> node,
			DbTreeNode<K, V> child, K key) throws IOException {
		int keyIndex = findKey(node, key);
		if (keyIndex >= 0) {
			DbTreeNode<K, V> aux = child;
			while (!aux.leaf) {
				aux = nodeChild(aux, 0);
			}
			nodeAssignKey(node, keyIndex, aux, 0);
		}
	}

	protected void remoteStealRight(DbTreeNode<K, V> node, int childIndex,
			DbTreeNode<K, V> child, DbTreeNode<K, V> nextChild) {
		nodeAssignKey(child, child.length, nextChild, 0);
		nodeAssignChild(child, child.length + 1, nextChild, 0);
		child.length++;
		nodeShiftKeyLeft(nextChild, 1);
		nodeShiftChildrenLeft(nextChild, 1);
		nextChild.length--;
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void remoteStealLeft(DbTreeNode<K, V> node, int childIndex,
			DbTreeNode<K, V> child, DbTreeNode<K, V> nextChild) {
		insertNodeIdx(child, 0, (K) nextChild.keys[nextChild.length - 1],
				(V) nextChild.values[nextChild.length - 1],
				nextChild.childrenId[nextChild.length],
				nextChild.childrenNode[nextChild.length]);
		nodeClearChild(nextChild, nextChild.length);
		removeNodeSetIdx(nextChild, nextChild.length - 1, nextChild.length);
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void join(DbTreeNode<K, V> node, DbTreeNode<K, V> child,
			int childIndex) throws IOException {
		int brotherChildIndex = nodeBrotherChildIndex(childIndex);
		DbTreeNode<K, V> childBrother = nodeChild(node, brotherChildIndex);
		DbTreeNode<K, V> leftNode = childIndex < brotherChildIndex ? child
				: childBrother;
		DbTreeNode<K, V> rightNode = childIndex < brotherChildIndex ? childBrother
				: child;
		int index = childIndex < brotherChildIndex ? childIndex
				: brotherChildIndex;
		join(node, index, leftNode, rightNode);
	}

	protected void join(DbTreeNode<K, V> root, int keyIndex,
			DbTreeNode<K, V> left, DbTreeNode<K, V> right) throws IOException {
		if (left.leaf) {
			// Copia todos os elementos da direita para esquerda
			if (right.length > 0) {
				nodeCopyKeys(left, left.length, right, 0);
				left.length += right.length;
				right.length = 0;
			}
			nodeShiftKeyLeft(root, keyIndex, 1);
			nodeClearKey(root, root.length - 1);
			nodeShiftChildrenLeft(root, keyIndex + 1, 1);
			root.length--;
			nodeAssignChild(root, keyIndex, left);
			nodeAssignNext(left, nodeNext(right));
		} else {
			// Copia o elemento root[ki] para a esquerda left[l0,..,ln,ki]
			nodeAssignKey(left, left.length, root, keyIndex);
			left.length++;
			nodeCopyChild(left, left.length, right, 0);
			// Retira o elemento root[ki]
			nodeShiftKeyLeft(root, keyIndex, 1);
			nodeClearKey(root, root.length - 1);
			nodeShiftChildrenLeft(root, keyIndex + 2, 1);
			nodeClearChild(root, root.length);
			root.length--;
			// Copia todos os elementos da direita para esquerda
			if (right.length > 0) {
				nodeCopyKeys(left, left.length, right, 0);
				nodeCopyChildren(left, left.length, right, 0);
				left.length += right.length;
			}
		}
	}

	protected DbTreeNode<K, V> nodeChildBigger(DbTreeNode<K, V> node,
			int childIndex) throws IOException {
		DbTreeNode<K, V> leftChild = nodeChildLeft(node, childIndex);
		DbTreeNode<K, V> rightChild = nodeChildRight(node, childIndex);
		int leftChildLength = leftChild == null ? 0 : leftChild.length;
		int rightChildLength = rightChild == null ? 0 : rightChild.length;
		DbTreeNode<K, V> nextChild = leftChildLength > rightChildLength ? leftChild
				: rightChild;
		if (nextChild.length <= order / 2) {
			return null;
		}
		return nextChild;
	}

	protected DbTreeNode<K, V> nodeChildRight(DbTreeNode<K, V> node,
			int childIndex) throws IOException {
		return childIndex == node.length ? null : nodeChild(node,
				childIndex + 1);
	}

	protected DbTreeNode<K, V> nodeChildLeft(DbTreeNode<K, V> node,
			int childIndex) throws IOException {
		return childIndex == 0 ? null : nodeChild(node, childIndex - 1);
	}

	/**
	 * Indica se o node está abaixo do mínimo
	 * 
	 * @param child
	 * @return node abaixo do nível mínimo
	 */
	protected boolean nodeIsNotEnough(DbTreeNode<K, V> child) {
		return child.length < order / 2;
	}

	protected DbTreeNode<K, V> nodeChild(DbTreeNode<K, V> node, int idx)
			throws IOException {
		DbTreeNode<K, V> child = node.childrenNode[idx];
		if (child != null) {
			return child;
		}
		if (io == null) {
			return null;
		}
		long id = node.childrenId[idx];
		byte[] bytes = io.readNode(name, id);
		DbInputBytes in = new DbInputBytes(bytes);
		child = readDbTreeNode(in);
		return child;
	}

	protected DbTreeNode<K, V> nodeNext(DbTreeNode<K, V> node) {
		if (node.nextId == 0) {
			return null;
		}
		DbTreeNode<K, V> next = node.next;
		return next;
	}

	protected boolean nodeIsFull(DbTreeNode<K, V> node) {
		return node.length >= order;
	}

	protected int nodeBrotherChildIndex(int childIndex) {
		return childIndex == 0 ? 1 : childIndex - 1;
	}

	protected void nodeShiftKeyLeft(DbTreeNode<K, V> node, int offset, int count) {
		int size = node.length - count;
		for (int n = offset; n < size; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyLeft(DbTreeNode<K, V> node, int count) {
		for (int n = 0; n < node.length - count; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyRight(DbTreeNode<K, V> node, int offset,
			int count) {
		for (int n = node.length - 1; n >= offset; n--) {
			nodeAssignKey(node, n + count, node, n);
			nodeAssignKey(node, n, null, null);
		}
	}

	protected void nodeShiftChildrenLeft(DbTreeNode<K, V> node, int offset,
			int count) {
		int size = node.length - count + 1;
		for (int n = offset - 1; n < size; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChild(node, i, 0, null);
		}
	}

	protected void nodeShiftChildrenLeft(DbTreeNode<K, V> node, int count) {
		for (int n = 0; n < node.length - count + 1; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChild(node, i, 0, null);
		}
	}

	protected void nodeShiftChildrenRight(DbTreeNode<K, V> node, int offset,
			int count) {
		for (int n = node.length; n >= offset; n--) {
			nodeAssignChild(node, n + count, node, n);
			nodeAssignChild(node, n, 0, null);
		}
	}

	protected void nodeCopyKeys(DbTreeNode<K, V> target, int targetIndex,
			DbTreeNode<K, V> source, int sourceIndex) {
		for (int n = 0; n < source.length - sourceIndex; n++) {
			nodeAssignKey(target, targetIndex + n, source, sourceIndex + n);
		}
	}

	protected void nodeCopyChildren(DbTreeNode<K, V> target, int targetIndex,
			DbTreeNode<K, V> source, int sourceIndex) {
		for (int n = 0; n < source.length + 1 - sourceIndex; n++) {
			nodeCopyChild(target, target.length + n, source, n + sourceIndex);
		}
	}

	protected void nodeAssignChild(DbTreeNode<K, V> target, int targetIndex,
			DbTreeNode<K, V> source) {
		nodeAssignChild(target, targetIndex, source.id, source);
	}

	protected void nodeCopyChild(DbTreeNode<K, V> target, int targetIndex,
			DbTreeNode<K, V> source, int sourceIndex) {
		nodeAssignChild(target, targetIndex, source.childrenId[sourceIndex],
				source.childrenNode[sourceIndex]);
	}

	protected void nodeClearKeys(DbTreeNode<K, V> node, int index) {
		for (int n = index; n < node.length; n++) {
			nodeAssignKey(node, n, null, null);
		}
	}

	protected void nodeClearKey(DbTreeNode<K, V> node, int index) {
		nodeAssignKey(node, index, null, null);
	}

	protected void nodeClearChildren(DbTreeNode<K, V> node, int beginIndex) {
		for (int n = beginIndex; n <= node.length; n++) {
			nodeClearChild(node, n);
		}
	}

	protected void nodeClearChild(DbTreeNode<K, V> node, int index) {
		nodeAssignChild(node, index, 0, null);
	}

	protected void nodeAssignKey(DbTreeNode<K, V> node, int index,
			DbTreeNode<K, V> source, int sourceIndex) {
		nodeAssignKey(node, index, (K) source.keys[sourceIndex],
				(V) source.values[sourceIndex]);
	}

	protected void nodeAssignKey(DbTreeNode<K, V> node, int index, K key,
			V value) {
		node.keys[index] = key;
		node.values[index] = value;
	}

	protected void nodeAssignChild(DbTreeNode<K, V> node, int index,
			DbTreeNode<K, V> source, int sourceIndex) {
		nodeAssignChild(node, index, source.childrenId[sourceIndex],
				source.childrenNode[sourceIndex]);
	}

	protected void nodeAssignChild(DbTreeNode<K, V> node, int index,
			long childId, DbTreeNode<K, V> childNode) {
		node.childrenId[index] = childId;
		node.childrenNode[index] = childNode;
	}

	protected void nodeAssignNext(DbTreeNode<K, V> node, DbTreeNode<K, V> next) {
		node.next = next;
		node.nextId = next != null ? next.id : 0;
	}

	/**
	 * Insere no node 'node' no indice 'idx' a chave, valor e seu filho a
	 * esquerda.<br/>
	 * <br/>
	 * 
	 * @param node
	 * @param idx
	 * @param key
	 * @param value
	 * @param childId
	 * @param childNode
	 */
	protected void insertNodeIdx(DbTreeNode<K, V> node, int idx, K key,
			V value, long childId, DbTreeNode<K, V> childNode) {
		nodeShiftKeyRight(node, idx, 1);
		nodeShiftChildrenRight(node, idx, 1);
		nodeAssignKey(node, idx, key, value);
		nodeAssignChild(node, idx, childId, childNode);
		node.length++;
	}

	/**
	 * Remove um elemento de um node.<br/>
	 * <br/>
	 * Remover um elemento i de um node significa remove a chave e valor i do
	 * node, mover todas as chaves e valores a esquerda. <br/>
	 * <br/>
	 * Se o node não for filha, remove o filho i e move todos os filhos a
	 * esquerda.
	 * 
	 * @param node
	 * @param idx
	 * @param childIndex
	 */
	protected void removeNodeSetIdx(DbTreeNode<K, V> node, int idx,
			int childIndex) {
		nodeClearKey(node, idx);
		nodeShiftKeyLeft(node, idx, 1);
		nodeShiftChildrenLeft(node, childIndex + 1, 1);
		nodeClearChild(node, node.length);
		node.length--;
		nodeClearKey(node, node.length);
	}

	/**
	 * Remove um elemento de um node folha.<br/>
	 * <br/>
	 * Remover um elemento i de um node significa remove a chave e valor i do
	 * node, mover todas as chaves e valores a esquerda. <br/>
	 * 
	 * @param node
	 * @param index
	 */
	protected void removeNodeLeafIdx(DbTreeNode<K, V> node, int index) {
		nodeShiftKeyLeft(node, index, 1);
		nodeClearKey(node, --node.length);
	}

	protected int findChildKey(DbTreeNode<K, V> node, K key) {
		// int idx = 0;
		// while (idx < node.length && key >= node.keys[idx]) {
		// idx++;
		// }
		// return idx;
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int compare = compare(node, key, mid);
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

	protected int findKey(DbTreeNode<K, V> node, K key) {
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			int compare = compare(node, key, mid);
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

	protected int compare(DbTreeNode<K, V> node, K key, int keyIndex) {
		K midVal = (K) node.keys[keyIndex];
		int compare = ((Comparable<K>) midVal).compareTo(key);
		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			DbTreeNode<K, V> node = root;
			while (!node.leaf) {
				node = nodeChild(node, 0);
			}
			while (node != null) {
				for (int n = 0; n < node.length; n++) {
					sb.append(node.keys[n]);
					sb.append(", ");
				}
				node = nodeNext(node);
			}
			if (sb.length() != 1) {
				sb.deleteCharAt(sb.length() - 1);
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(']');
			return sb.toString();
		} catch (IOException e) {
			return "[...]";
		}
	}

	public static class DbTreeNode<K, V> {

		public long id;

		public Object[] keys;

		public Object[] values;

		public long[] childrenId;

		public DbTreeNode<K, V>[] childrenNode;

		public long nextId;

		public DbTreeNode<K, V> next;

		public int length;

		public boolean leaf;

		public boolean changed;

		public DbTreeNode(int order, long id) {
			this.id = id;
			this.keys = new Object[order];
			this.values = new Object[order];
			this.childrenId = new long[order + 1];
			this.childrenNode = new DbTreeNode[order + 1];
			this.leaf = true;
			this.changed = true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			if (leaf) {
				for (int n = 0; n < length; n++) {
					sb.append(keys[n]);
					if (n != length - 1) {
						sb.append('|');
					}
				}
				if (next != null) {
					sb.append("->");
					if (next.length > 0) {
						sb.append(next.keys[0]);
					}
				}
			} else {
				for (int n = 0; n < length; n++) {
					sb.append(childrenNode[n] != null ? childrenNode[n] : "["
							+ childrenId[n] + "]");
					sb.append(keys[n]);
				}
				sb.append(childrenNode[length] != null ? childrenNode[length]
						: "[" + childrenId[length] + "]");
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
	public static interface DbTreeIODelegator {

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
		public void writeNode(String name, long id, byte[] bytes)
				throws IOException;

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
		public void writeStructure(String name, byte[] bytes)
				throws IOException;

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
