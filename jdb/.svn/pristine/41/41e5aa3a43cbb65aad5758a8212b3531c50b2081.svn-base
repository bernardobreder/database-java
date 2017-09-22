package jdb;

public class BPTree {

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

	protected Object find(BPTreeNode root, int key) {
		BPTreeNode c = findLeaf(root, key);
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

	public Object find(int key) {
		return find(root, key);
	}

	public void add(int key, Object value) {
		if (find(root, key) != null) {
			return;
		}
		if (root == null) {
			this.root = startNewTree(key, value);
			return;
		}
		BPTreeNode leaf = findLeaf(root, key);
		if (leaf.length < order - 1) {
			leaf = addIntoLeaf(leaf, key, value);
		} else {
			this.root = addIntoLeafAfterSplitting(root, leaf, key, value);
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
		}
		leaf.keys[index] = key;
		leaf.nodes[index] = pointer;
		leaf.length++;
		return leaf;
	}

	protected BPTreeNode addIntoLeafAfterSplitting(BPTreeNode root, BPTreeNode left, int key, Object pointer) {
		int[] temp_keys = new int[order];
		Object[] temp_pointers = new Object[order];
		int split, newKey, i, j;
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
		}
		temp_keys[index] = key;
		temp_pointers[index] = pointer;
		split = cut(order - 1);
		left.length = split;
		for (i = 0; i < split; i++) {
			left.nodes[i] = temp_pointers[i];
			left.keys[i] = temp_keys[i];
		}
		BPTreeNode right = makeLeaf();
		right.length = order - split;
		for (i = split, j = 0; i < order; i++, j++) {
			right.nodes[j] = temp_pointers[i];
			right.keys[j] = temp_keys[i];
		}
		right.nodes[order - 1] = left.nodes[order - 1];
		left.nodes[order - 1] = right;
		for (i = left.length; i < order - 1; i++) {
			left.nodes[i] = null;
		}
		for (i = right.length; i < order - 1; i++) {
			right.nodes[i] = null;
		}
		right.parent = left.parent;
		newKey = right.keys[0];
		return addIntoParent(root, left, newKey, right);
	}

	protected BPTreeNode addIntoNodeAfterSplitting(BPTreeNode root, BPTreeNode left, int leftIndex, int key, BPTreeNode right) {
		int i, j;
		int[] temp_keys = new int[order];
		Object[] temp_pointers = new Object[order + 1];
		for (i = 0, j = 0; i < left.length + 1; i++, j++) {
			if (j == leftIndex + 1) {
				j++;
			}
			temp_pointers[j] = left.nodes[i];
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
			left.keys[i] = temp_keys[i];
			left.length++;
		}
		left.nodes[i] = temp_pointers[i];
		for (j = i + 1; j < order - 1; j++) {
			left.nodes[j] = null;
			left.keys[j] = 0;
		}
		left.nodes[j] = null;
		int keyPrime = temp_keys[split - 1];
		for (++i, j = 0; i < order; i++, j++) {
			center.nodes[j] = temp_pointers[i];
			center.keys[j] = temp_keys[i];
			center.length++;
		}
		center.nodes[j] = temp_pointers[i];
		center.parent = left.parent;
		for (i = 0; i <= center.length; i++) {
			BPTreeNode child = (BPTreeNode) childNode(center, i);
			child.parent = center;
		}
		return addIntoParent(root, left, keyPrime, center);
	}

	protected BPTreeNode addIntoNode(BPTreeNode root, BPTreeNode node, int leftIndex, int key, BPTreeNode right) {
		for (int i = node.length; i > leftIndex; i--) {
			node.nodes[i + 1] = node.nodes[i];
			node.keys[i] = node.keys[i - 1];
		}
		node.nodes[leftIndex + 1] = right;
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
		while (leftIndex <= parent.length && parent.nodes[leftIndex] != left) {
			leftIndex++;
		}
		return leftIndex;
	}

	protected BPTreeNode addIntoNewRoot(BPTreeNode left, int key, BPTreeNode right) {
		BPTreeNode root = makeNode();
		root.keys[0] = key;
		root.nodes[0] = left;
		root.nodes[1] = right;
		root.length++;
		left.parent = root;
		right.parent = root;
		return root;
	}

	protected BPTreeNode startNewTree(int key, Object pointer) {
		BPTreeNode root = makeLeaf();
		root.keys[0] = key;
		root.nodes[0] = pointer;
		root.length++;
		return root;
	}

	protected BPTreeNode makeLeaf() {
		BPTreeNode new_node = makeNode();
		new_node.leaf = true;
		return new_node;
	}

	protected BPTreeNode makeNode() {
		BPTreeNode new_node = new BPTreeNode();
		new_node.keys = new int[order - 1];
		new_node.nodes = new Object[order];
		return new_node;
	}

	protected int getNeighborIndex(BPTreeNode node) {
		for (int i = 0; i <= parentNode(node).length; i++) {
			if (parentNode(node).nodes[i] == node) {
				return i - 1;
			}
		}
		return -1;
	}

	protected BPTreeNode removeEntryFromNode(BPTreeNode n, int key, Object pointer) {
		int i = 0, num_pointers;
		while (n.keys[i] != key) {
			i++;
		}
		for (++i; i < n.length; i++) {
			n.keys[i - 1] = n.keys[i];
		}
		num_pointers = n.leaf ? n.length : n.length + 1;
		i = 0;
		while (childNode(n, i) != pointer) {
			i++;
		}
		for (++i; i < num_pointers; i++) {
			n.nodes[i - 1] = childNode(n, i);
		}
		n.length--;
		if (n.leaf) {
			for (i = n.length; i < order - 1; i++) {
				n.nodes[i] = null;
			}
		} else {
			for (i = n.length + 1; i < order; i++) {
				n.nodes[i] = null;
			}
		}
		return n;
	}

	protected BPTreeNode adjustRoot(BPTreeNode root) {
		if (root.length > 0) {
			return root;
		}
		if (!root.leaf) {
			BPTreeNode new_root = (BPTreeNode) root.nodes[0];
			new_root.parent = null;
			return new_root;
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
				neighbor.length++;
				node.length--;
			}
			neighbor.nodes[i] = node.nodes[j];
			for (i = 0; i < neighbor.length + 1; i++) {
				tmp = (BPTreeNode) childNode(neighbor, i);
				tmp.parent = neighbor;
			}
		} else {
			for (i = neighbor_insertion_index, j = 0; j < node.length; i++, j++) {
				neighbor.keys[i] = node.keys[j];
				neighbor.nodes[i] = node.nodes[j];
				neighbor.length++;
			}
			neighbor.nodes[order - 1] = node.nodes[order - 1];
		}
		root = deleteEntry(root, parentNode(node), k_prime, node);
		return root;
	}

	protected BPTreeNode redistributeNodes(BPTreeNode root, BPTreeNode node, BPTreeNode neighbor, int neighbor_index, int k_prime_index, int k_prime) {
		int i;
		if (neighbor_index != -1) {
			if (!node.leaf)
				node.nodes[node.length + 1] = node.nodes[node.length];
			for (i = node.length; i > 0; i--) {
				node.keys[i] = node.keys[i - 1];
				node.nodes[i] = node.nodes[i - 1];
			}
			if (!node.leaf) {
				node.nodes[0] = neighbor.nodes[neighbor.length];
				BPTreeNode tmp = (BPTreeNode) node.nodes[0];
				tmp.parent = node;
				neighbor.nodes[neighbor.length] = null;
				node.keys[0] = k_prime;
				parentNode(node).keys[k_prime_index] = neighbor.keys[neighbor.length - 1];
			} else {
				node.nodes[0] = neighbor.nodes[neighbor.length - 1];
				neighbor.nodes[neighbor.length - 1] = null;
				node.keys[0] = neighbor.keys[neighbor.length - 1];
				parentNode(node).keys[k_prime_index] = node.keys[0];
			}
		} else {
			if (node.leaf) {
				node.keys[node.length] = neighbor.keys[0];
				node.nodes[node.length] = neighbor.nodes[0];
				parentNode(node).keys[k_prime_index] = neighbor.keys[1];
			} else {
				node.keys[node.length] = k_prime;
				node.nodes[node.length + 1] = neighbor.nodes[0];
				BPTreeNode tmp = (BPTreeNode) node.nodes[node.length + 1];
				tmp.parent = node;
				parentNode(node).keys[k_prime_index] = neighbor.keys[0];
			}
			for (i = 0; i < neighbor.length - 1; i++) {
				neighbor.keys[i] = neighbor.keys[i + 1];
				neighbor.nodes[i] = neighbor.nodes[i + 1];
			}
			if (!node.leaf) {
				neighbor.nodes[i] = neighbor.nodes[i + 1];
			}
		}
		node.length++;
		neighbor.length--;
		return root;
	}

	protected BPTreeNode deleteEntry(BPTreeNode root, BPTreeNode node, int key, Object pointer) {
		node = removeEntryFromNode(node, key, pointer);
		if (node == root) {
			return adjustRoot(root);
		}
		int min_keys = node.leaf ? cut(order - 1) : cut(order) - 1;
		if (node.length >= min_keys) {
			return root;
		}
		int neighbor_index = getNeighborIndex(node);
		int k_prime_index = neighbor_index == -1 ? 0 : neighbor_index;
		int k_prime = parentNode(node).keys[k_prime_index];
		BPTreeNode neighbor = (BPTreeNode) (neighbor_index == -1 ? parentNode(node).nodes[1] : parentNode(node).nodes[neighbor_index]);
		int capacity = node.leaf ? order : order - 1;
		if (neighbor.length + node.length < capacity) {
			return coalesceNodes(root, node, neighbor, neighbor_index, k_prime);
		} else {
			return redistributeNodes(root, node, neighbor, neighbor_index, k_prime_index, k_prime);
		}
	}

	public void delete(int key) {
		Object value = find(root, key);
		BPTreeNode keyLeaf = findLeaf(root, key);
		if (value != null && keyLeaf != null) {
			this.root = deleteEntry(root, keyLeaf, key, value);
		}
	}

	public BPTreeNode next(BPTreeNode node) {
		return (BPTreeNode) node.nodes[order - 1];
	}

	public static class BPTreeNode {

		public Object[] nodes;

		public int[] keys;

		public Object parent;

		public boolean leaf;

		public int length;

	}

}
