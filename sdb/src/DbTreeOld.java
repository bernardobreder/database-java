
import java.io.IOException;

import sdb.DbInputBytes;

/**
 * Implementação baseada no site
 * https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
 * 
 * @author bernardobreder
 * 
 */
public class DbTreeOld {

	/** Tamanho */
	protected final int order;
	/** Sequence */
	protected long sequence;
	/** Root */
	protected BPTreeNode root;
	/** Entrada e Saída */
	protected DbTreeIODelegator io;

	/**
	 * @param order
	 * @throws IOException
	 */
	public DbTreeOld(DbTreeIODelegator io, int order) throws IOException {
		this.io = io;
		if (io.hasStrucuture()) {
			this.order = 3;
		} else {
			this.order = order;
			this.root = new BPTreeNode(order, ++sequence);
		}
	}

	/**
	 * Consulta o valor da chave. Caso não ache, será retornado nulo.
	 * 
	 * @param key
	 *            valor da chave
	 * @return valor da chave ou nulo
	 * @throws IOException
	 */
	public Object get(long key) throws IOException {
		BPTreeNode node = get(root, key);
		if (node == null) {
			return null;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return null;
		}
		return node.values[idx];
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
	protected BPTreeNode get(BPTreeNode node, long key) throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (keyIndex < 0) {
				return null;
			}
			return node;
		} else {
			int childIndex = findChildKey(node, key);
			BPTreeNode child = nodeChild(node, childIndex);
			return get(child, key);
		}
	}

	public void add(long key, Object value) throws IOException {
		add(root, key, value);
		if (nodeIsFull(root)) {
			BPTreeNode node = new BPTreeNode(order, ++sequence);
			addSplit(node, root, 0);
			root = node;
		}
		// {
		// List<BPTreeNode> nodes = new ArrayList<DbTree.BPTreeNode>();
		// nodes.add(root);
		// for (int n = 0; n < nodes.size(); n++) {
		// BPTreeNode node = nodes.get(n);
		// for (int m = 0; m < node.length; m++) {
		// if (node.childrenNode[m] != null) {
		// nodes.add(node.childrenNode[m]);
		// }
		// }
		// Assert.assertTrue(node.length < order);
		// for (int m = node.length; m < node.keys.length; m++) {
		// Assert.assertEquals(0, node.keys[m]);
		// Assert.assertNull(node.values[m]);
		// }
		// for (int m = node.length + 1; m < node.childrenNode.length; m++) {
		// Assert.assertEquals(0, node.childrenId[m]);
		// Assert.assertNull(node.childrenNode[m]);
		// }
		// }
		// }
	}

	protected void add(BPTreeNode node, long key, Object value) throws IOException {
		int childIndex = findChildKey(node, key);
		if (node.leaf) {
			nodeShiftKeyRight(node, childIndex, 1);
			nodeAssignKey(node, childIndex, key, value);
			node.length++;
		} else {
			BPTreeNode child = nodeChild(node, childIndex);
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
	protected void addSplit(BPTreeNode root, BPTreeNode node, int index) {
		int idx = order / 2;
		BPTreeNode rightNext = nodeNext(node);
		// Constroi o lado da raiz
		nodeShiftKeyRight(root, index, 1);
		nodeShiftChildrenRight(root, index, 1);
		nodeAssignKey(root, index, node, idx);
		root.leaf = false;
		root.length++;
		// Constroi o lado da direita
		BPTreeNode right = new BPTreeNode(order, ++sequence);
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

	public boolean remove(long key) throws IOException {
		boolean removed = remove(root, key);
		if (root.length == 0) {
			root = root.childrenNode[0];
		}
		// if (removed) {
		// if (root != null) {
		// List<BPTreeNode> nodes = new ArrayList<DbTree.BPTreeNode>();
		// nodes.add(root);
		// for (int n = 0; n < nodes.size(); n++) {
		// BPTreeNode node = nodes.get(n);
		// for (int m = 0; m < node.length; m++) {
		// if (node.childrenNode[m] != null) {
		// nodes.add(node.childrenNode[m]);
		// }
		// }
		// Assert.assertTrue(node.length < order);
		// for (int m = node.length; m < node.keys.length; m++) {
		// if (node.keys[m] != 0l) {
		// Assert.assertEquals(0, node.keys[m]);
		// }
		// Assert.assertNull(node.values[m]);
		// }
		// }
		// }
		// }
		return removed;
	}

	protected boolean remove(BPTreeNode node, long key) throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (node.keys[keyIndex] != key) {
				return false;
			}
			removeNodeLeafIdx(node, keyIndex);
			return true;
		} else {
			int childIndex = findChildKey(node, key);
			BPTreeNode child = nodeChild(node, childIndex);
			boolean removed = remove(child, key);
			if (removed) {
				removeFixFirst(node, child, key);
				if (nodeIsNotEnough(child)) {
					BPTreeNode nextChild = nodeChildBigger(node, childIndex);
					if (nextChild != null) {
						BPTreeNode leftChild = nodeChildLeft(node, childIndex);
						BPTreeNode rightChild = nodeChildRight(node, childIndex);
						if (nextChild == leftChild) {
							remoteStealLeft(node, childIndex, child, nextChild);
						} else if (nextChild == rightChild) {
							remoteStealRight(node, childIndex, child, nextChild);
						}
						// if (child.leaf) {
						// insertNodeIdx(child, child.length, nextChild.keys[0],
						// nextChild.values[0], nextChild.childrenId[0],
						// nextChild.childrenNode[0]);
						// removeNodeSetIdx(nextChild, 0, 0);
						// nodeAssignKey(node, childIndex, nextChild, 0);
						// if (keyIndex >= 0) {
						// node.keys[keyIndex] = child.keys[0];
						// node.values[keyIndex] = child.values[0];
						// }
						// } else {
						// nodeAssignKey(child, child.length, node, 0);
						// child.length++;
						// nodeAssignChild(child, child.length, nextChild, 0);
						// nodeAssignKey(node, childIndex, nextChild, 0);
						// nodeShiftKeyLeft(nextChild, 1);
						// nodeShiftChildrenLeft(nextChild, 1);
						// nextChild.length--;
						// }
					} else {
						// Verifica se removeu a chave inicial folha
						removeMerge(node, child, childIndex);
					}
				}
			}
			return removed;
		}
	}

	protected void removeFixFirst(BPTreeNode node, BPTreeNode child, long key) throws IOException {
		int keyIndex = findKey(node, key);
		if (keyIndex >= 0) {
			BPTreeNode aux = child;
			while (!aux.leaf) {
				aux = nodeChild(aux, 0);
			}
			nodeAssignKey(node, keyIndex, aux, 0);
		}
	}

	protected void remoteStealRight(BPTreeNode node, int childIndex, BPTreeNode child, BPTreeNode nextChild) {
		nodeAssignKey(child, child.length, nextChild, 0);
		nodeAssignChild(child, child.length + 1, nextChild, 0);
		child.length++;
		nodeShiftKeyLeft(nextChild, 1);
		nodeShiftChildrenLeft(nextChild, 1);
		nextChild.length--;
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void remoteStealLeft(BPTreeNode node, int childIndex, BPTreeNode child, BPTreeNode nextChild) {
		insertNodeIdx(child, 0, nextChild.keys[nextChild.length - 1], nextChild.values[nextChild.length - 1], nextChild.childrenId[nextChild.length], nextChild.childrenNode[nextChild.length]);
		nodeClearChild(nextChild, nextChild.length);
		removeNodeSetIdx(nextChild, nextChild.length - 1, nextChild.length);
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void removeMerge(BPTreeNode node, BPTreeNode child, int childIndex) throws IOException {
		int brotherChildIndex = nodeBrotherChildIndex(childIndex);
		BPTreeNode childBrother = nodeChild(node, brotherChildIndex);
		BPTreeNode leftNode = childIndex < brotherChildIndex ? child : childBrother;
		BPTreeNode rightNode = childIndex < brotherChildIndex ? childBrother : child;
		int index = childIndex < brotherChildIndex ? childIndex : brotherChildIndex;
		removeMerge(node, index, leftNode, rightNode);
	}

	protected void removeMerge(BPTreeNode root, int keyIndex, BPTreeNode left, BPTreeNode right) throws IOException {
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

	protected BPTreeNode nodeChildBigger(BPTreeNode node, int childIndex) throws IOException {
		BPTreeNode leftChild = nodeChildLeft(node, childIndex);
		BPTreeNode rightChild = nodeChildRight(node, childIndex);
		int leftChildLength = leftChild == null ? 0 : leftChild.length;
		int rightChildLength = rightChild == null ? 0 : rightChild.length;
		BPTreeNode nextChild = leftChildLength > rightChildLength ? leftChild : rightChild;
		if (nextChild.length <= order / 2) {
			return null;
		}
		return nextChild;
	}

	protected BPTreeNode nodeChildRight(BPTreeNode node, int childIndex) throws IOException {
		return childIndex == node.length ? null : nodeChild(node, childIndex + 1);
	}

	protected BPTreeNode nodeChildLeft(BPTreeNode node, int childIndex) throws IOException {
		return childIndex == 0 ? null : nodeChild(node, childIndex - 1);
	}

	/**
	 * Indica se o node está abaixo do mínimo
	 * 
	 * @param child
	 * @return node abaixo do nível mínimo
	 */
	protected boolean nodeIsNotEnough(BPTreeNode child) {
		return child.length < order / 2;
	}

	protected BPTreeNode nodeChild(BPTreeNode node, int idx) throws IOException {
		BPTreeNode child = node.childrenNode[idx];
		if (child != null) {
			return child;
		}
		if (io == null) {
			return null;
		}
		long id = node.childrenId[idx];
		child = nodeChildRead(id);
		return child;
	}

	protected BPTreeNode nodeChildRead(long id) throws IOException {
		BPTreeNode child;
		byte[] bytes = io.readNode(id);
		DbInputBytes in = new DbInputBytes(bytes);
		int version = in.readUByte();
		int order = in.readUShort();
		child = new BPTreeNode(order, version);
		boolean leaf = in.readBoolean();
		child.leaf = leaf;
		int length = in.readUShort();
		child.length = length;
		if (in.readUByte() != 0xFE) {
			return null;
		}
		long key, childId = in.readLongCompressed();
		child.childrenId[0] = childId;
		for (int n = 0; n < length; n++) {
			key = in.readLongCompressed();
			child.keys[n] = key;
			childId = in.readLongCompressed();
			child.childrenId[n + 1] = childId;
		}
		long nextId = in.readLongCompressed();
		child.nextId = nextId;
		if (in.readUByte() != 0xFF) {
			return null;
		}
		return child;
	}

	protected BPTreeNode nodeNext(BPTreeNode node) {
		if (node.nextId == 0) {
			return null;
		}
		BPTreeNode next = node.next;
		return next;
	}

	protected boolean nodeIsFull(BPTreeNode node) {
		return node.length == order;
	}

	protected int nodeBrotherChildIndex(int childIndex) {
		return childIndex == 0 ? 1 : childIndex - 1;
	}

	protected void nodeShiftKeyLeft(BPTreeNode node, int offset, int count) {
		int size = node.length - count;
		for (int n = offset; n < size; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyLeft(BPTreeNode node, int count) {
		for (int n = 0; n < node.length - count; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyRight(BPTreeNode node, int offset, int count) {
		for (int n = node.length - 1; n >= offset; n--) {
			nodeAssignKey(node, n + count, node, n);
			nodeAssignKey(node, n, 0, null);
		}
	}

	protected void nodeShiftChildrenLeft(BPTreeNode node, int offset, int count) {
		int size = node.length - count + 1;
		for (int n = offset - 1; n < size; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChild(node, i, 0, null);
		}
	}

	protected void nodeShiftChildrenLeft(BPTreeNode node, int count) {
		for (int n = 0; n < node.length - count + 1; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChild(node, i, 0, null);
		}
	}

	protected void nodeShiftChildrenRight(BPTreeNode node, int offset, int count) {
		for (int n = node.length; n >= offset; n--) {
			nodeAssignChild(node, n + count, node, n);
			nodeAssignChild(node, n, 0, null);
		}
	}

	protected void nodeCopyKeys(BPTreeNode target, int targetIndex, BPTreeNode source, int sourceIndex) {
		for (int n = 0; n < source.length - sourceIndex; n++) {
			nodeAssignKey(target, targetIndex + n, source, sourceIndex + n);
		}
	}

	protected void nodeCopyChildren(BPTreeNode target, int targetIndex, BPTreeNode source, int sourceIndex) {
		for (int n = 0; n < source.length + 1 - sourceIndex; n++) {
			nodeCopyChild(target, target.length + n, source, n + sourceIndex);
		}
	}

	protected void nodeAssignChild(BPTreeNode target, int targetIndex, BPTreeNode source) {
		nodeAssignChild(target, targetIndex, source.id, source);
	}

	protected void nodeCopyChild(BPTreeNode target, int targetIndex, BPTreeNode source, int sourceIndex) {
		nodeAssignChild(target, targetIndex, source.childrenId[sourceIndex], source.childrenNode[sourceIndex]);
	}

	protected void nodeClearKeys(BPTreeNode node, int index) {
		for (int n = index; n < node.length; n++) {
			nodeAssignKey(node, n, 0, null);
		}
	}

	protected void nodeClearKey(BPTreeNode node, int index) {
		nodeAssignKey(node, index, 0, null);
	}

	protected void nodeClearChildren(BPTreeNode node, int beginIndex) {
		for (int n = beginIndex; n <= node.length; n++) {
			nodeClearChild(node, n);
		}
	}

	protected void nodeClearChild(BPTreeNode node, int index) {
		nodeAssignChild(node, index, 0, null);
	}

	protected void nodeAssignKey(BPTreeNode node, int index, BPTreeNode source, int sourceIndex) {
		nodeAssignKey(node, index, source.keys[sourceIndex], source.values[sourceIndex]);
	}

	protected void nodeAssignKey(BPTreeNode node, int index, long key, Object value) {
		node.keys[index] = key;
		node.values[index] = value;
	}

	protected void nodeAssignChild(BPTreeNode node, int index, BPTreeNode source, int sourceIndex) {
		nodeAssignChild(node, index, source.childrenId[sourceIndex], source.childrenNode[sourceIndex]);
	}

	protected void nodeAssignChild(BPTreeNode node, int index, long childId, BPTreeNode childNode) {
		node.childrenId[index] = childId;
		node.childrenNode[index] = childNode;
	}

	protected void nodeAssignNext(BPTreeNode node, BPTreeNode next) {
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
	protected void insertNodeIdx(BPTreeNode node, int idx, long key, Object value, long childId, BPTreeNode childNode) {
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
	protected void removeNodeSetIdx(BPTreeNode node, int idx, int childIndex) {
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
	protected void removeNodeLeafIdx(BPTreeNode node, int index) {
		nodeShiftKeyLeft(node, index, 1);
		nodeClearKey(node, --node.length);
	}

	protected int findChildKey(BPTreeNode node, long key) {
		// int idx = 0;
		// while (idx < node.length && key >= node.keys[idx]) {
		// idx++;
		// }
		// return idx;
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = node.keys[mid];
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return mid + 1;
			}
		}
		return low;
	}

	protected int findKey(BPTreeNode node, long key) {
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = node.keys[mid];
			if (midVal < key) {
				low = mid + 1;
			} else if (midVal > key) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			BPTreeNode node = root;
			while (!node.leaf) {
				node = nodeChild(node, 0);
			}
			while (node != null) {
				for (int n = 0; n < node.length; n++) {
					sb.append(node.keys[n]);
					sb.append(",");
				}
				node = nodeNext(node);
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append(']');
			return sb.toString();
		} catch (IOException e) {
			return "[...]";
		}
	}

	public static class BPTreeNode {

		protected long id;

		protected long keys[];

		protected Object values[];

		protected long childrenId[];

		protected BPTreeNode childrenNode[];

		protected BPTreeNode next;

		protected long nextId;

		protected int length;

		protected boolean leaf;

		protected boolean changed;

		public BPTreeNode(int order, long sequence) {
			id = sequence;
			keys = new long[3 * order];
			values = new Object[3 * order];
			childrenId = new long[3 * order + 1];
			childrenNode = new BPTreeNode[3 * order + 1];
			leaf = true;
			changed = true;
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
					sb.append(childrenNode[n] != null ? childrenNode[n] : "[" + childrenId[n] + "]");
					sb.append(keys[n]);
				}
				sb.append(childrenNode[length] != null ? childrenNode[length] : "[" + childrenId[length] + "]");
			}
			sb.append('}');
			return sb.toString();
		}
	}

	public static interface DbTreeLogger {

		public void readChild(BPTreeNode node, int childIndex, int childId);

	}

	public static interface DbTreeLock {

		public void readNodeLock(long id);

		public void writeNodeLock(long id);

		public void readNodeUnlock(long id);

		public void writeNodeUnlock(long id);

	}

	public static interface DbTreeIODelegator {

		public byte[] readNode(long id) throws IOException;

		public void writeNode(long id, byte[] bytes) throws IOException;

		public byte[] readStructure() throws IOException;

		public void writeStructure(byte[] bytes) throws IOException;

		public boolean hasStrucuture() throws IOException;

	}

}
