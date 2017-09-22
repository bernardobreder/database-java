package jdb.bptree;

public class BPTreeNode {

	protected int order = 3 + 1;

	protected Object[] nodes;

	protected int[] keys;

	protected BPTreeNode parent;

	protected boolean leaf;

	protected int length;

	public BPTreeNode() {
		leaf = true;
		keys = new int[order - 1];
		nodes = new Object[order];
	}

	protected BPTreeNode createNode() {
		return new BPTreeNode();
	}

	protected void key(int index, int value) {
		keys[index] = value;
	}

	protected void length(int value) {
		length = value;
	}

	protected void lengthInc() {
		length(length + 1);
	}

	protected void lengthDec() {
		length(length - 1);
	}

	protected void leaf(boolean flag) {
		leaf = flag;
	}

	protected void nodes(int index, Object node) {
		nodes[index] = node;
	}

	protected void parent(BPTreeNode node) {
		parent = node;
	}

	protected Object find(int key) {
		BPTreeNode c = findLeaf(key);
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
			return c.value(i);
		}
	}

	public boolean contain(int key) {
		return find(key) != null;
	}

	protected BPTreeNode findLeaf(int key) {
		BPTreeNode node = this;
		while (!node.leaf) {
			int i = 0;
			while (i < node.length) {
				if (key >= node.keys[i]) {
					i++;
				} else {
					break;
				}
			}
			node = node.child(i);
		}
		return node;
	}

	protected BPTreeNode child(int index) {
		return (BPTreeNode) nodes[index];
	}

	protected Object value(int index) {
		return nodes[index];
	}

	protected Object node(int index) {
		return nodes[index];
	}

	protected BPTreeNode parent() {
		return parent;
	}

	protected int cut(int length) {
		return (length % 2 == 0) ? length / 2 : length / 2 + 1;
	}

	public BPTreeNode add(int key, Object value) {
		if (contain(key)) {
			return this;
		}
		BPTreeNode leaf = findLeaf(key);
		if (leaf.length < order - 1) {
			leaf.addIntoLeaf(key, value);
			return this;
		} else {
			return addIntoLeafAfterSplitting(leaf, key, value);
		}
	}

	protected void addIntoLeaf(int key, Object pointer) {
		int index = 0;
		while (index < length && keys[index] < key) {
			index++;
		}
		for (int i = length; i > index; i--) {
			key(i, keys[i - 1]);
			nodes(i, nodes[i - 1]);
		}
		key(index, key);
		nodes(index, pointer);
		lengthInc();
	}

	protected BPTreeNode addIntoLeafAfterSplitting(BPTreeNode left, int key, Object pointer) {
		int[] temp_keys = new int[order];
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
		}
		temp_keys[index] = key;
		temp_pointers[index] = pointer;
		split = cut(order - 1);
		left.length(split);
		for (i = 0; i < split; i++) {
			left.nodes(i, temp_pointers[i]);
			left.key(i, temp_keys[i]);
		}
		BPTreeNode right = createNode();
		right.length(order - split);
		for (i = split, j = 0; i < order; i++, j++) {
			right.nodes(j, temp_pointers[i]);
			right.key(j, temp_keys[i]);
		}
		right.nodes(order - 1, left.nodes[order - 1]);
		left.nodes(order - 1, right);
		for (i = left.length; i < order - 1; i++) {
			left.nodes(i, null);
		}
		for (i = right.length; i < order - 1; i++) {
			right.nodes(i, null);
		}
		right.parent(left.parent);
		return addIntoParent(left, right.keys[0], right);
	}

	protected BPTreeNode addIntoNodeAfterSplitting(BPTreeNode left, int leftIndex, int key, BPTreeNode right) {
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
		BPTreeNode center = createNode();
		center.leaf(false);
		left.length(0);
		for (i = 0; i < split - 1; i++) {
			left.nodes(i, temp_pointers[i]);
			left.key(i, temp_keys[i]);
			left.lengthInc();
		}
		left.nodes(i, temp_pointers[i]);
		for (j = i + 1; j < order - 1; j++) {
			left.nodes(j, null);
			left.key(j, 0);
		}
		left.nodes(j, null);
		int keyPrime = temp_keys[split - 1];
		for (++i, j = 0; i < order; i++, j++) {
			center.nodes(j, temp_pointers[i]);
			center.key(j, temp_keys[i]);
			center.lengthInc();
		}
		center.nodes(j, temp_pointers[i]);
		center.parent(left.parent);
		for (i = 0; i <= center.length; i++) {
			BPTreeNode child = center.child(i);
			child.parent(center);
		}
		return addIntoParent(left, keyPrime, center);
	}

	protected BPTreeNode addIntoNode(BPTreeNode node, int leftIndex, int key, BPTreeNode right) {
		for (int i = node.length; i > leftIndex; i--) {
			node.nodes(i + 1, node.nodes[i]);
			node.key(i, node.keys[i - 1]);
		}
		node.nodes(leftIndex + 1, right);
		node.key(leftIndex, key);
		node.lengthInc();
		return this;
	}

	protected BPTreeNode addIntoParent(BPTreeNode left, int key, BPTreeNode right) {
		BPTreeNode parent = left.parent();
		if (parent == null) {
			return addIntoNewRoot(left, key, right);
		}
		int leftIndex = getLeftIndex(parent, left);
		if (parent.length < order - 1) {
			return addIntoNode(parent, leftIndex, key, right);
		} else {
			return addIntoNodeAfterSplitting(parent, leftIndex, key, right);
		}
	}

	protected BPTreeNode addIntoNewRoot(BPTreeNode left, int key, BPTreeNode right) {
		BPTreeNode node = createNode();
		node.leaf(false);
		node.key(0, key);
		node.nodes(0, left);
		node.nodes(1, right);
		node.lengthInc();
		left.parent(node);
		right.parent(node);
		return node;
	}

	protected int getLeftIndex(BPTreeNode parent, BPTreeNode left) {
		int leftIndex = 0;
		while (leftIndex <= parent.length && !parent.child(leftIndex).equals(left)) {
			leftIndex++;
		}
		return leftIndex;
	}

	protected int getNeighborIndex(BPTreeNode node) {
		for (int i = 0; i <= node.parent().length; i++) {
			if (node.parent().child(i).equals(node)) {
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
			n.key(i - 1, n.keys[i]);
		}
		num_pointers = n.leaf ? n.length : n.length + 1;
		i = 0;
		while (!n.node(i).equals(pointer)) {
			i++;
		}
		for (++i; i < num_pointers; i++) {
			n.nodes(i - 1, n.node(i));
		}
		n.lengthDec();
		if (n.leaf) {
			for (i = n.length; i < order - 1; i++) {
				n.nodes(i, null);
			}
		} else {
			for (i = n.length + 1; i < order; i++) {
				n.nodes(i, null);
			}
		}
		return n;
	}

	protected BPTreeNode adjustRoot() {
		if (length > 0) {
			return this;
		}
		if (!leaf) {
			BPTreeNode node = child(0);
			node.parent(null);
			return node;
		} else {
			return null;
		}
	}

	protected BPTreeNode coalesceNodes(BPTreeNode node, BPTreeNode neighbor, int neighbor_index, int k_prime) {
		int i, j;
		BPTreeNode tmp;
		if (neighbor_index == -1) {
			tmp = node;
			node = neighbor;
			neighbor = tmp;
		}
		int neighbor_insertion_index = neighbor.length;
		if (!node.leaf) {
			neighbor.key(neighbor_insertion_index, k_prime);
			neighbor.lengthInc();
			int n_end = node.length;
			for (i = neighbor_insertion_index + 1, j = 0; j < n_end; i++, j++) {
				neighbor.key(i, node.keys[j]);
				neighbor.nodes(i, node.nodes[j]);
				neighbor.lengthInc();
				node.lengthDec();
			}
			neighbor.nodes(i, node.nodes[j]);
			for (i = 0; i < neighbor.length + 1; i++) {
				tmp = neighbor.child(i);
				tmp.parent(neighbor);
			}
		} else {
			for (i = neighbor_insertion_index, j = 0; j < node.length; i++, j++) {
				neighbor.key(i, node.keys[j]);
				neighbor.nodes(i, node.nodes[j]);
				neighbor.lengthInc();
			}
			neighbor.nodes(order - 1, node.nodes[order - 1]);
		}
		return deleteEntry(node.parent(), k_prime, node);
	}

	protected BPTreeNode redistributeNodes(BPTreeNode node, BPTreeNode neighbor, int neighbor_index, int k_prime_index, int k_prime) {
		int i;
		if (neighbor_index != -1) {
			if (!node.leaf)
				node.nodes(node.length + 1, node.nodes[node.length]);
			for (i = node.length; i > 0; i--) {
				node.key(i, node.keys[i - 1]);
				node.nodes(i, node.nodes[i - 1]);
			}
			if (!node.leaf) {
				node.nodes(0, neighbor.nodes[neighbor.length]);
				BPTreeNode tmp = node.child(0);
				tmp.parent(node);
				neighbor.nodes(neighbor.length, null);
				node.key(0, k_prime);
				node.parent().key(k_prime_index, neighbor.keys[neighbor.length - 1]);
			} else {
				node.nodes(0, neighbor.nodes[neighbor.length - 1]);
				neighbor.nodes(neighbor.length - 1, null);
				node.key(0, neighbor.keys[neighbor.length - 1]);
				node.parent().key(k_prime_index, node.keys[0]);
			}
		} else {
			if (node.leaf) {
				node.key(node.length, neighbor.keys[0]);
				node.nodes(node.length, neighbor.nodes[0]);
				node.parent().key(k_prime_index, neighbor.keys[1]);
			} else {
				node.key(node.length, k_prime);
				node.nodes(node.length + 1, neighbor.nodes[0]);
				BPTreeNode tmp = node.child(node.length + 1);
				tmp.parent(node);
				node.parent().key(k_prime_index, neighbor.keys[0]);
			}
			for (i = 0; i < neighbor.length - 1; i++) {
				neighbor.key(i, neighbor.keys[i + 1]);
				neighbor.nodes(i, neighbor.nodes[i + 1]);
			}
			if (!node.leaf) {
				neighbor.nodes(i, neighbor.nodes[i + 1]);
			}
		}
		node.lengthInc();
		neighbor.lengthDec();
		return this;
	}

	protected BPTreeNode deleteEntry(BPTreeNode node, int key, Object pointer) {
		node = removeEntryFromNode(node, key, pointer);
		if (node == this) {
			return adjustRoot();
		}
		int min_keys = node.leaf ? cut(order - 1) : cut(order) - 1;
		if (node.length >= min_keys) {
			return this;
		}
		int neighbor_index = getNeighborIndex(node);
		int k_prime_index = neighbor_index == -1 ? 0 : neighbor_index;
		int k_prime = node.parent().keys[k_prime_index];
		BPTreeNode neighbor = neighbor_index == -1 ? node.parent().child(1) : node.parent().child(neighbor_index);
		int capacity = node.leaf ? order : order - 1;
		if (neighbor.length + node.length < capacity) {
			return coalesceNodes(node, neighbor, neighbor_index, k_prime);
		} else {
			return redistributeNodes(node, neighbor, neighbor_index, k_prime_index, k_prime);
		}
	}

	public BPTreeNode delete(int key) {
		Object value = find(key);
		BPTreeNode keyLeaf = findLeaf(key);
		if (value != null && keyLeaf != null) {
			return deleteEntry(keyLeaf, key, value);
		}
		return this;
	}

	public BPTreeNode next() {
		return child(order - 1);
	}

}
