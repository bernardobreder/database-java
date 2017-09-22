package jdb;

public class StorageBPTree {

	protected int rootId;

	protected BPTreeNode root;

	protected int order = 3 + 1;

	protected int cut(int length) {
		return (length % 2 == 0) ? length / 2 : length / 2 + 1;
	}

	protected Object childNode(BPTreeNode node, int index) {
		return node.nodes[index];
	}

	protected BPTreeNode parentNode(BPTreeNode node) {
		return (BPTreeNode) node.parent;
	}

	protected void root(BPTreeNode root) {
		this.rootId = root == null ? 0 : root.id;
		this.root = root;
	}

	protected BPTreeNode root() {
		return root;
	}

	public BPTreeNode next(BPTreeNode node) {
		return (BPTreeNode) node.nodes[order - 1];
	}

	public Object find(int key) {
		BPTreeNode c = findLeaf(root(), key);
		if (c == null) {
			return null;
		}
		int i = 0;
		for (; i < c.length; i++) {
			if (c.keys[i] == key) {
				break;
			}
		}
		if (i == c.length) {
			return null;
		} else {
			return childNode(c, i);
		}
	}

	protected BPTreeNode findLeaf(BPTreeNode root, int key) {
		int i = 0;
		BPTreeNode c = root;
		if (c == null) {
			return c;
		}
		while (!c.leaf) {
			i = 0;
			while (i < c.length) {
				if (key >= c.keys[i]) {
					i++;
				} else {
					break;
				}
			}
			c = (BPTreeNode) childNode(c, i);
		}
		return c;
	}

	public void add(int key, Object value) {
		if (root() == null) {
			root(makeLeaf(key, value));
			return;
		}
		if (find(key) != null) {
			return;
		}
		BPTreeNode leaf = findLeaf(root(), key);
		if (leaf.length < order - 1) {
			leaf = addIntoLeaf(leaf, key, value);
		} else {
			root(addIntoLeafAfterSplitting(root(), leaf, key, value));
		}
	}

	protected BPTreeNode addIntoLeaf(BPTreeNode leaf, int key, Object pointer) {
		int index = 0;
		while (index < leaf.length && leaf.keys[index] < key) {
			index++;
		}
		for (int i = leaf.length; i > index; i--) {
			leaf.keys[i] = leaf.keys[i - 1];
			leaf.nodes[i] = leaf.nodes[i - 1];
			leaf.nodeIds[i] = leaf.nodeIds[i - 1];
		}
		leaf.keys[index] = key;
		leaf.nodes[index] = pointer;
		leaf.nodeIds[index] = 0;
		leaf.length++;
		return leaf;
	}

	protected BPTreeNode addIntoLeafAfterSplitting(BPTreeNode root, BPTreeNode left, int key, Object pointer) {
		int[] temp_keys = new int[order];
		int[] temp_pointerIds = new int[order];
		Object[] temp_pointers = new Object[order];
		int split, i, j;
		int index = 0;
		while (index < order - 1 && left.keys[index] < key) {
			index++;
		}
		for (i = 0, j = 0; i < left.length; i++, j++) {
			if (j == index) {
				j++;
			}
			temp_keys[j] = left.keys[i];
			temp_pointers[j] = left.nodes[i];
			temp_pointerIds[j] = left.nodeIds[i];
		}
		temp_keys[index] = key;
		temp_pointers[index] = pointer;
		split = cut(order - 1);
		left.length = split;
		for (i = 0; i < split; i++) {
			left.nodes[i] = temp_pointers[i];
			left.nodeIds[i] = temp_pointerIds[i];
			left.keys[i] = temp_keys[i];
		}
		for (i = split; i < left.length; i++) {
			left.nodes[i] = null;
			left.nodeIds[i] = 0;
			left.keys[i] = 0;
		}
		BPTreeNode right = makeLeaf();
		right.length = order - split;
		for (i = split, j = 0; i < order; i++, j++) {
			right.nodes[j] = temp_pointers[i];
			right.nodeIds[j] = temp_pointerIds[i];
			right.keys[j] = temp_keys[i];
		}
		right.nodes[order - 1] = left.nodes[order - 1];
		right.nodeIds[order - 1] = left.nodeIds[order - 1];
		left.nodes[order - 1] = right;
		left.nodeIds[order - 1] = right.id;
		for (i = left.length; i < order - 1; i++) {
			left.nodes[i] = null;
			left.nodeIds[i] = 0;
		}
		for (i = right.length; i < order - 1; i++) {
			right.nodes[i] = null;
			right.nodeIds[i] = 0;
		}
		right.parent = left.parent;
		right.parentId = left.parentId;
		return addIntoParent(root, left, right.keys[0], right);
	}

	protected BPTreeNode addIntoNodeAfterSplitting(BPTreeNode root, BPTreeNode left, int leftIndex, int key, BPTreeNode right) {
		int i, j;
		int[] temp_keys = new int[order];
		Object[] temp_pointers = new Object[order + 1];
		int[] temp_pointerIds = new int[order + 1];
		for (i = 0, j = 0; i < left.length + 1; i++, j++) {
			if (j == leftIndex + 1) {
				j++;
			}
			temp_pointers[j] = left.nodes[i];
			temp_pointerIds[j] = left.nodeIds[i];
		}
		for (i = 0, j = 0; i < left.length; i++, j++) {
			if (j == leftIndex) {
				j++;
			}
			temp_keys[j] = left.keys[i];
		}
		temp_pointers[leftIndex + 1] = right;
		temp_keys[leftIndex] = key;
		int split = cut(order);
		BPTreeNode center = makeNode();
		left.length = 0;
		for (i = 0; i < split - 1; i++) {
			left.nodes[i] = temp_pointers[i];
			left.nodeIds[i] = temp_pointerIds[i];
			left.keys[i] = temp_keys[i];
			left.length++;
		}
		for (i = split - 1; i < left.length; i++) {
			left.nodes[i] = null;
			left.nodeIds[i] = 0;
			left.keys[i] = 0;
		}
		left.nodes[i] = temp_pointers[i];
		left.nodeIds[i] = temp_pointerIds[i];
		for (j = i + 1; j < order - 1; j++) {
			left.nodes[j] = null;
			left.nodeIds[j] = 0;
			left.keys[j] = 0;
		}
		left.nodes[j] = null;
		left.nodeIds[j] = 0;
		int keyPrime = temp_keys[split - 1];
		for (++i, j = 0; i < order; i++, j++) {
			center.nodes[j] = temp_pointers[i];
			center.nodeIds[j] = temp_pointerIds[i];
			center.keys[j] = temp_keys[i];
			center.length++;
		}
		center.nodes[j] = temp_pointers[i];
		center.nodeIds[j] = temp_pointerIds[i];
		center.parent = left.parent;
		center.parentId = left.parentId;
		for (i = 0; i <= center.length; i++) {
			BPTreeNode child = (BPTreeNode) childNode(center, i);
			child.parent = center;
			child.parentId = center.id;
		}
		return addIntoParent(root, left, keyPrime, center);
	}

	protected BPTreeNode addIntoNode(BPTreeNode root, BPTreeNode node, int leftIndex, int key, BPTreeNode right) {
		for (int i = node.length; i > leftIndex; i--) {
			node.nodes[i + 1] = node.nodes[i];
			node.nodeIds[i + 1] = node.nodeIds[i];
			node.keys[i] = node.keys[i - 1];
		}
		node.nodes[leftIndex + 1] = right;
		node.nodeIds[leftIndex + 1] = right.id;
		node.keys[leftIndex] = key;
		node.length++;
		return root;
	}

	protected BPTreeNode addIntoParent(BPTreeNode root, BPTreeNode left, int key, BPTreeNode right) {
		BPTreeNode parent = parentNode(left);
		if (parent == null) {
			return addIntoNewRoot(left, key, right);
		}
		int leftIndex = getLeftIndex(parent, left);
		if (parent.length < order - 1) {
			return addIntoNode(root, parent, leftIndex, key, right);
		} else {
			return addIntoNodeAfterSplitting(root, parent, leftIndex, key, right);
		}
	}

	protected int getLeftIndex(BPTreeNode parent, BPTreeNode left) {
		int leftIndex = 0;
		while (leftIndex <= parent.length && !parent.nodes[leftIndex].equals(left)) {
			leftIndex++;
		}
		return leftIndex;
	}

	protected BPTreeNode addIntoNewRoot(BPTreeNode left, int key, BPTreeNode right) {
		BPTreeNode root = makeNode();
		root.keys[0] = key;
		root.nodes[0] = left;
		root.nodeIds[0] = left.id;
		root.nodes[1] = right;
		root.nodeIds[1] = right.id;
		root.length++;
		left.parent = root;
		left.parentId = root.id;
		right.parent = root;
		right.parentId = root.id;
		return root;
	}

	protected BPTreeNode makeLeaf(int key, Object value) {
		BPTreeNode node = makeLeaf();
		node.keys[0] = key;
		node.nodes[0] = value;
		node.nodeIds[0] = 0;
		node.length++;
		return node;
	}

	protected BPTreeNode makeLeaf() {
		BPTreeNode node = makeNode();
		node.leaf = true;
		return node;
	}

	protected BPTreeNode makeNode() {
		BPTreeNode node = new BPTreeNode();
		node.keys = new int[order - 1];
		node.nodes = new Object[order];
		node.nodeIds = new int[order];
		return node;
	}

	protected int getNeighborIndex(BPTreeNode node) {
		for (int i = 0; i <= parentNode(node).length; i++) {
			if (parentNode(node).nodes[i].equals(node)) {
				return i - 1;
			}
		}
		return -1;
	}

	protected BPTreeNode removeEntryFromNode(BPTreeNode node, int key, Object pointer) {
		int i = 0, num_pointers;
		while (node.keys[i] != key) {
			i++;
		}
		for (++i; i < node.length; i++) {
			node.keys[i - 1] = node.keys[i];
		}
		num_pointers = node.leaf ? node.length : node.length + 1;
		i = 0;
		while (!childNode(node, i).equals(pointer)) {
			i++;
		}
		for (++i; i < num_pointers; i++) {
			Object childNode = childNode(node, i);
			node.nodes[i - 1] = childNode;
			node.nodeIds[i - 1] = childNode instanceof BPTreeNode ? ((BPTreeNode) childNode).id : 0;
		}
		node.length--;
		if (node.leaf) {
			for (i = node.length; i < order - 1; i++) {
				node.nodes[i] = null;
				node.nodeIds[i] = 0;
			}
		} else {
			for (i = node.length + 1; i < order; i++) {
				node.nodes[i] = null;
				node.nodeIds[i] = 0;
			}
		}
		return node;
	}

	protected BPTreeNode adjustRoot(BPTreeNode root) {
		if (root.length > 0) {
			return root;
		}
		if (!root.leaf) {
			BPTreeNode node = (BPTreeNode) childNode(root, 0);
			node.parent = null;
			node.parentId = 0;
			return node;
		} else {
			return null;
		}
	}

	protected BPTreeNode coalesceNodes(BPTreeNode root, BPTreeNode node, BPTreeNode neighbor, int neighbor_index, int k_prime) {
		int i, j;
		BPTreeNode tmp;
		if (neighbor_index == -1) {
			tmp = node;
			node = neighbor;
			neighbor = tmp;
		}
		int neighbor_insertion_index = neighbor.length;
		if (!node.leaf) {
			neighbor.keys[neighbor_insertion_index] = k_prime;
			neighbor.length++;
			int n_end = node.length;
			for (i = neighbor_insertion_index + 1, j = 0; j < n_end; i++, j++) {
				neighbor.keys[i] = node.keys[j];
				neighbor.nodes[i] = node.nodes[j];
				neighbor.nodeIds[i] = node.nodeIds[j];
				neighbor.length++;
				node.length--;
			}
			neighbor.nodes[i] = node.nodes[j];
			neighbor.nodeIds[i] = node.nodeIds[j];
			for (i = 0; i < neighbor.length + 1; i++) {
				tmp = (BPTreeNode) childNode(neighbor, i);
				tmp.parent = neighbor;
				tmp.parentId = neighbor.id;
			}
		} else {
			for (i = neighbor_insertion_index, j = 0; j < node.length; i++, j++) {
				neighbor.keys[i] = node.keys[j];
				neighbor.nodes[i] = node.nodes[j];
				neighbor.nodeIds[i] = node.nodeIds[j];
				neighbor.length++;
			}
			neighbor.nodes[order - 1] = node.nodes[order - 1];
			neighbor.nodeIds[order - 1] = node.nodeIds[order - 1];
		}
		root = deleteEntry(root, parentNode(node), k_prime, node);
		return root;
	}

	protected BPTreeNode redistributeNodes(BPTreeNode root, BPTreeNode node, BPTreeNode neighbor, int neighbor_index, int k_prime_index, int k_prime) {
		int i;
		if (neighbor_index != -1) {
			if (!node.leaf) {
				node.nodes[node.length + 1] = node.nodes[node.length];
				node.nodeIds[node.length + 1] = node.nodeIds[node.length];
			}
			for (i = node.length; i > 0; i--) {
				node.keys[i] = node.keys[i - 1];
				node.nodes[i] = node.nodes[i - 1];
				node.nodeIds[i] = node.nodeIds[i - 1];
			}
			if (!node.leaf) {
				node.nodes[0] = neighbor.nodes[neighbor.length];
				node.nodeIds[0] = neighbor.nodeIds[neighbor.length];
				BPTreeNode tmp = (BPTreeNode) childNode(node, 0);
				tmp.parent = node;
				tmp.parentId = node.id;
				neighbor.nodes[neighbor.length] = null;
				neighbor.nodeIds[neighbor.length] = 0;
				node.keys[0] = k_prime;
				parentNode(node).keys[k_prime_index] = neighbor.keys[neighbor.length - 1];
			} else {
				node.nodes[0] = neighbor.nodes[neighbor.length - 1];
				node.nodeIds[0] = neighbor.nodeIds[neighbor.length - 1];
				neighbor.nodes[neighbor.length - 1] = null;
				neighbor.nodeIds[neighbor.length - 1] = 0;
				node.keys[0] = neighbor.keys[neighbor.length - 1];
				parentNode(node).keys[k_prime_index] = node.keys[0];
			}
		} else {
			if (node.leaf) {
				node.keys[node.length] = neighbor.keys[0];
				node.nodes[node.length] = neighbor.nodes[0];
				node.nodeIds[node.length] = neighbor.nodeIds[0];
				parentNode(node).keys[k_prime_index] = neighbor.keys[1];
			} else {
				node.keys[node.length] = k_prime;
				node.nodes[node.length + 1] = neighbor.nodes[0];
				node.nodeIds[node.length + 1] = neighbor.nodeIds[0];
				BPTreeNode tmp = (BPTreeNode) childNode(node, node.length + 1);
				tmp.parent = node;
				tmp.parentId = node.id;
				parentNode(node).keys[k_prime_index] = neighbor.keys[0];
			}
			for (i = 0; i < neighbor.length - 1; i++) {
				neighbor.keys[i] = neighbor.keys[i + 1];
				neighbor.nodes[i] = neighbor.nodes[i + 1];
				neighbor.nodeIds[i] = neighbor.nodeIds[i + 1];
			}
			if (!node.leaf) {
				neighbor.nodes[i] = neighbor.nodes[i + 1];
				neighbor.nodeIds[i] = neighbor.nodeIds[i + 1];
			}
		}
		node.length++;
		neighbor.length--;
		return root;
	}

	protected BPTreeNode deleteEntry(BPTreeNode root, BPTreeNode n, int key, Object pointer) {
		n = removeEntryFromNode(n, key, pointer);
		if (n == root) {
			return adjustRoot(root);
		}
		int min_keys = n.leaf ? cut(order - 1) : cut(order) - 1;
		if (n.length >= min_keys) {
			return root;
		}
		int neighbor_index = getNeighborIndex(n);
		int k_prime_index = neighbor_index == -1 ? 0 : neighbor_index;
		int k_prime = parentNode(n).keys[k_prime_index];
		BPTreeNode neighbor = (BPTreeNode) (neighbor_index == -1 ? childNode(parentNode(n), 1) : childNode(parentNode(n), neighbor_index));
		int capacity = n.leaf ? order : order - 1;
		if (neighbor.length + n.length < capacity) {
			return coalesceNodes(root, n, neighbor, neighbor_index, k_prime);
		} else {
			return redistributeNodes(root, n, neighbor, neighbor_index, k_prime_index, k_prime);
		}
	}

	public void delete(int key) {
		Object value = find(key);
		BPTreeNode keyLeaf = findLeaf(root(), key);
		if (value != null && keyLeaf != null) {
			root(deleteEntry(root(), keyLeaf, key, value));
		}
	}

	public static class BPTreeNode {

		public int id;

		public Object[] nodes;

		public int[] nodeIds;

		public int[] keys;

		public Object parent;

		public int parentId;

		public boolean leaf;

		public int length;

		public boolean changed;

	}

}
