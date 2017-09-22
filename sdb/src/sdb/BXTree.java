package sdb;

import java.io.IOException;

/**
 * Implementação baseada no site
 * https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html
 * 
 * @param <K>
 * @param <V>
 * @author bernardobreder
 */
public class BXTree {

	private static final int NOT_FOUND = -1;
	/** Tamanho */
	protected final int order;
	/** Root */
	protected int rootId;
	/** Root */
	protected DbTreeNode root;

	/**
	 * @param io
	 * @param name
	 * @param order
	 * @param comparator
	 * @throws IOException
	 */
	public BXTree(int order) throws IOException {
		this.order = order;
		this.root = new DbTreeNode(null, 0, order);
	}

	/**
	 * Consulta o valor da chave. Caso não ache, será retornado nulo.
	 * 
	 * @param key
	 *            valor da chave
	 * @return valor da chave ou nulo
	 * @throws IOException
	 */
	public int get(int key) throws IOException {
		DbTreeNode node = get(root, key);
		if (node == null) {
			return NOT_FOUND;
		}
		int idx = findKey(node, key);
		if (idx < 0) {
			return NOT_FOUND;
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
	public boolean set(long key, int value) throws IOException {
		DbTreeNode node = get(root, key);
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
	protected DbTreeNode get(DbTreeNode node, long key) throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (keyIndex < 0) {
				return null;
			}
			return node;
		} else {
			int childIndex = findChildKey(node, key);
			DbTreeNode child = nodeChild(node, childIndex);
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
	public void add(long key, int value) throws IOException {
		add(root, key, value);
		if (nodeIsFull(root)) {
			DbTreeNode node = new DbTreeNode(null, 0, order);
			addSplit(node, root, 0);
			root.parent = node;
			root = node;
		}
		// {
		// List<BPTreeNode> nodes = new ArrayList<DbTree.BPTreeNode>();
		// nodes.add(root);
		// for (int n = 0; n < nodes.size(); n++) {
		// BPTreeNode<K,V> node = nodes.get(n);
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

	/**
	 * Adiciona um valor
	 * 
	 * @param node
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	protected void add(DbTreeNode node, long key, int value) throws IOException {
		int childIndex = findChildKey(node, key);
		if (node.leaf) {
			nodeShiftKeyRight(node, childIndex, 1);
			nodeAssignKey(node, childIndex, key, value);
			node.length++;
		} else {
			DbTreeNode child = nodeChild(node, childIndex);
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
	protected void addSplit(DbTreeNode root, DbTreeNode node, int index) {
		int idx = order / 2;
		DbTreeNode rightNext = nodeNext(node);
		// Constroi o lado da raiz
		nodeShiftKeyRight(root, index, 1);
		nodeShiftChildrenRight(root, index, 1);
		nodeAssignKey(root, index, node, idx);
		root.leaf = false;
		root.length++;
		// Constroi o lado da direita
		DbTreeNode right = new DbTreeNode(root, index + 1, order);
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
		nodeAssignChildInto(root, index, node);
		nodeAssignChildInto(root, index + 1, right);
		root.changed = true;
		// Atualizando o next da esquerda para direita
		nodeAssignNext(node, right);
		nodeAssignNext(right, rightNext);
		node.parent = root;
		node.parentIndex = index;
	}

	public boolean remove(long key) throws IOException {
		boolean removed = remove(root, key);
		if (root.length == 0) {
			root = root.children[0];
		}
		// if (removed) {
		// if (root != null) {
		// List<BPTreeNode> nodes = new ArrayList<DbTree.BPTreeNode>();
		// nodes.add(root);
		// for (int n = 0; n < nodes.size(); n++) {
		// BPTreeNode<K,V> node = nodes.get(n);
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

	protected boolean remove(DbTreeNode node, long key) throws IOException {
		if (node.leaf) {
			int keyIndex = findKey(node, key);
			if (node.keys[keyIndex] != key) {
				return false;
			}
			removeNodeLeafIdx(node, keyIndex);
			if (nodeIsNotEnough(node)) {
				DbTreeNode nextNode = nodeNext(node);
				if (nextNode != null) {
					DbTreeNode leftChildNode = nodeChildLeft(node.parent, node.parentIndex);
					DbTreeNode rightChildNode = nodeChildRight(node.parent, node.parentIndex);
					if (nextNode == leftChildNode) {
						remoteStealLeft(node.parent, node.parentIndex, node, nextNode);
					} else if (nextNode == rightChildNode) {
						remoteStealRight(node.parent, node.parentIndex, node, nextNode);
					}
				} else {
					removeMerge(node.parent, node, node.parentIndex);
				}
			}
			// Verifica se os pais possui o elemento removido
			{
				int parentIndex = Math.max(0, node.parentIndex - 1);
				DbTreeNode parent = node.parent;
				while (parent != null) {
					if (parent.keys[parentIndex] == key) {
						DbTreeNode child = parent.children[parentIndex + 1];
						while (!child.leaf) {
							child = child.children[0];
						}
						parent.keys[parentIndex] = child.keys[0];
						break;
					}
					parentIndex = parent.parentIndex;
					parent = parent.parent;
				}
			}
			return true;
		} else {
			int childIndex = findChildKey(node, key);
			DbTreeNode child = nodeChild(node, childIndex);
			boolean removed = remove(child, key);
			if (removed) {
				removeFixFirst(node, child, key);
				if (nodeIsNotEnough(child)) {
					DbTreeNode nextChild = nodeChildBigger(node, childIndex);
					if (nextChild != null) {
						DbTreeNode leftChild = nodeChildLeft(node, childIndex);
						DbTreeNode rightChild = nodeChildRight(node, childIndex);
						if (nextChild == leftChild) {
							remoteStealLeft(node, childIndex, child, nextChild);
						} else if (nextChild == rightChild) {
							remoteStealRight(node, childIndex, child, nextChild);
						}
					} else {
						// Verifica se removeu a chave inicial folha
						removeMerge(node, child, childIndex);
					}
				}
			}
			return removed;
		}
	}

	protected void removeFixFirst(DbTreeNode node, DbTreeNode child, long key) throws IOException {
		int keyIndex = findKey(node, key);
		if (keyIndex >= 0) {
			DbTreeNode aux = child;
			while (!aux.leaf) {
				aux = nodeChild(aux, 0);
			}
			nodeAssignKey(node, keyIndex, aux, 0);
		}
	}

	protected void remoteStealRight(DbTreeNode node, int childIndex, DbTreeNode child, DbTreeNode nextChild) {
		nodeAssignKey(child, child.length, nextChild, 0);
		nodeAssignChild(child, child.length + 1, nextChild, 0);
		child.length++;
		nodeShiftKeyLeft(nextChild, 1);
		nodeShiftChildrenLeft(nextChild, 1);
		nextChild.length--;
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void remoteStealLeft(DbTreeNode node, int childIndex, DbTreeNode child, DbTreeNode nextChild) {
		insertNodeIntoIdx(child, 0, nextChild.keys[nextChild.length - 1], nextChild.values[nextChild.length - 1], nextChild.children[nextChild.length]);
		nodeClearChild(nextChild, nextChild.length);
		removeNodeSetIdx(nextChild, nextChild.length - 1, nextChild.length);
		nodeAssignKey(node, childIndex, nextChild, 0);
	}

	protected void removeMerge(DbTreeNode node, DbTreeNode child, int childIndex) throws IOException {
		int brotherChildIndex = nodeBrotherChildIndex(childIndex);
		DbTreeNode childBrother = nodeChild(node, brotherChildIndex);
		DbTreeNode leftNode = childIndex < brotherChildIndex ? child : childBrother;
		DbTreeNode rightNode = childIndex < brotherChildIndex ? childBrother : child;
		int index = childIndex < brotherChildIndex ? childIndex : brotherChildIndex;
		removeMerge(node, index, leftNode, rightNode);
	}

	protected void removeMerge(DbTreeNode root, int keyIndex, DbTreeNode left, DbTreeNode right) throws IOException {
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

	protected DbTreeNode nodeChildBigger(DbTreeNode node, int childIndex) throws IOException {
		DbTreeNode leftChild = nodeChildLeft(node, childIndex);
		DbTreeNode rightChild = nodeChildRight(node, childIndex);
		int leftChildLength = leftChild == null ? 0 : leftChild.length;
		int rightChildLength = rightChild == null ? 0 : rightChild.length;
		DbTreeNode nextChild = leftChildLength > rightChildLength ? leftChild : rightChild;
		if (nextChild.length <= order / 2) {
			return null;
		}
		return nextChild;
	}

	protected DbTreeNode nodeChildRight(DbTreeNode node, int childIndex) throws IOException {
		return childIndex == node.length ? null : nodeChild(node, childIndex + 1);
	}

	protected DbTreeNode nodeChildLeft(DbTreeNode node, int childIndex) throws IOException {
		return childIndex == 0 ? null : nodeChild(node, childIndex - 1);
	}

	/**
	 * Indica se o node está abaixo do mínimo
	 * 
	 * @param child
	 * @return node abaixo do nível mínimo
	 */
	protected boolean nodeIsNotEnough(DbTreeNode child) {
		return child.length < order / 2;
	}

	protected DbTreeNode nodeChild(DbTreeNode node, int idx) throws IOException {
		return node.children[idx];
	}

	protected DbTreeNode nodeNext(DbTreeNode node) {
		return node.next;
	}

	protected boolean nodeIsFull(DbTreeNode node) {
		return node.length == order;
	}

	protected int nodeBrotherChildIndex(int childIndex) {
		return childIndex == 0 ? 1 : childIndex - 1;
	}

	protected void nodeShiftKeyLeft(DbTreeNode node, int offset, int count) {
		int size = node.length - count;
		for (int n = offset; n < size; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyLeft(DbTreeNode node, int count) {
		for (int n = 0; n < node.length - count; n++) {
			int i = n + count;
			nodeAssignKey(node, n, node, i);
			nodeClearKey(node, i);
		}
	}

	protected void nodeShiftKeyRight(DbTreeNode node, int offset, int count) {
		for (int n = node.length - 1; n >= offset; n--) {
			nodeAssignKey(node, n + count, node, n);
			nodeAssignKey(node, n, 0, 0);
		}
	}

	protected void nodeShiftChildrenLeft(DbTreeNode node, int offset, int count) {
		int size = node.length - count + 1;
		for (int n = offset - 1; n < size; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChildInto(node, i, null);
		}
	}

	protected void nodeShiftChildrenLeft(DbTreeNode node, int count) {
		for (int n = 0; n < node.length - count + 1; n++) {
			int i = n + count;
			nodeAssignChild(node, n, node, i);
			nodeAssignChildInto(node, i, null);
		}
	}

	protected void nodeShiftChildrenRight(DbTreeNode node, int offset, int count) {
		for (int n = node.length; n >= offset; n--) {
			nodeAssignChild(node, n + count, node, n);
			nodeAssignChildInto(node, n, null);
		}
	}

	protected void nodeCopyKeys(DbTreeNode target, int targetIndex, DbTreeNode source, int sourceIndex) {
		for (int n = 0; n < source.length - sourceIndex; n++) {
			nodeAssignKey(target, targetIndex + n, source, sourceIndex + n);
		}
	}

	protected void nodeCopyChildren(DbTreeNode target, int targetIndex, DbTreeNode source, int sourceIndex) {
		for (int n = 0; n < source.length + 1 - sourceIndex; n++) {
			nodeCopyChild(target, target.length + n, source, n + sourceIndex);
		}
	}

	protected void nodeAssignChild(DbTreeNode target, int targetIndex, DbTreeNode source) {
		nodeAssignChildInto(target, targetIndex, source);
	}

	protected void nodeCopyChild(DbTreeNode target, int targetIndex, DbTreeNode source, int sourceIndex) {
		nodeAssignChild(target, targetIndex, source.children[sourceIndex]);
	}

	protected void nodeClearKeys(DbTreeNode node, int index) {
		for (int n = index; n < node.length; n++) {
			nodeAssignKey(node, n, 0, 0);
		}
	}

	protected void nodeClearKey(DbTreeNode node, int index) {
		nodeAssignKey(node, index, 0, 0);
	}

	protected void nodeClearChildren(DbTreeNode node, int beginIndex) {
		for (int n = beginIndex; n <= node.length; n++) {
			nodeClearChild(node, n);
		}
	}

	protected void nodeClearChild(DbTreeNode node, int index) {
		nodeAssignChildInto(node, index, null);
	}

	protected void nodeAssignKey(DbTreeNode node, int index, DbTreeNode source, int sourceIndex) {
		nodeAssignKey(node, index, source.keys[sourceIndex], source.values[sourceIndex]);
	}

	protected void nodeAssignKey(DbTreeNode node, int index, long key, int value) {
		node.keys[index] = key;
		node.values[index] = value;
	}

	protected void nodeAssignChild(DbTreeNode node, int index, DbTreeNode source, int sourceIndex) {
		nodeAssignChild(node, index, source.children[sourceIndex]);
	}

	protected void nodeAssignChildInto(DbTreeNode node, int index, DbTreeNode childNode) {
		node.children[index] = childNode;
	}

	protected void nodeAssignNext(DbTreeNode node, DbTreeNode next) {
		node.next = next;
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
	protected void insertNodeIntoIdx(DbTreeNode node, int idx, long key, int value, DbTreeNode childNode) {
		nodeShiftKeyRight(node, idx, 1);
		nodeShiftChildrenRight(node, idx, 1);
		nodeAssignKey(node, idx, key, value);
		nodeAssignChild(node, idx, childNode);
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
	protected void removeNodeSetIdx(DbTreeNode node, int idx, int childIndex) {
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
	protected void removeNodeLeafIdx(DbTreeNode node, int index) {
		nodeShiftKeyLeft(node, index, 1);
		nodeClearKey(node, --node.length);
	}

	protected int findChildKey(DbTreeNode node, long key) {
		// int idx = 0;
		// while (idx < node.length && key >= node.keys[idx]) {
		// idx++;
		// }
		// return idx;
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long compare = node.keys[mid] - key;
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

	protected int findKey(DbTreeNode node, long key) {
		int low = 0;
		int high = node.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			long compare = node.keys[mid] - key;
			if (compare < 0) {
				low = mid + 1;
			} else if (compare > 0) {
				high = mid - 1;
			} else {
				return mid;
			}
		}
		return NOT_FOUND;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			DbTreeNode node = root;
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

	public static class DbTreeNode {

		public long id;

		public long[] keys;

		public int[] values;

		public DbTreeNode[] children;

		public DbTreeNode next;

		public int length;

		public boolean leaf;

		public boolean changed;

		public int parentIndex;

		public DbTreeNode parent;

		public DbTreeNode(DbTreeNode parent, int parentIndex, int order) {
			this.parent = parent;
			this.parentIndex = parentIndex;
			this.keys = new long[order];
			this.values = new int[order];
			this.children = new DbTreeNode[order + 1];
			this.leaf = true;
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
					sb.append(children[n] != null ? children[n] : "[" + children[n].id + "]");
					sb.append(keys[n]);
				}
				sb.append(children[length] != null ? children[length] : "[" + children[length].id + "]");
			}
			sb.append('}');
			return sb.toString();
		}
	}

}
