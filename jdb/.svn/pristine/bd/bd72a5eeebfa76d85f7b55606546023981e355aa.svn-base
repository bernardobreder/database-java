package jdb.bptree;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

public class StorageBPTreeNode extends BPTreeNode {

	protected int id;

	protected int[] nodeIds;

	protected int parentId;

	private final StorageBPTreeNodeInterface model;

	public StorageBPTreeNode(StorageBPTreeNodeInterface model) {
		super();
		this.model = model;
		this.nodeIds = new int[order];
	}

	public void commit() throws IOException {

	}

	@Override
	protected BPTreeNode createNode() {
		return new StorageBPTreeNode(model);
	}

	@Override
	protected void nodes(int index, Object node) {
		if (node instanceof StorageBPTreeNode) {
			StorageBPTreeNode treeNode = (StorageBPTreeNode) node;
			nodeIds[index] = treeNode.id;
		}
		super.nodes(index, node);
	}

	@Override
	protected void parent(BPTreeNode node) {
		if (node instanceof StorageBPTreeNode) {
			StorageBPTreeNode treeNode = (StorageBPTreeNode) node;
			parentId = treeNode.id;
		}
		super.parent(node);
	}

	@Override
	protected BPTreeNode child(int index) throws IOException {
		StorageBPTreeNode node = (StorageBPTreeNode) super.child(index);
		if (node == null) {
			nodes[index] = node = readNode(model.readNode(index));
		}
		return node;
	}

	@Override
	protected BPTreeNode parent() throws IOException {
		BPTreeNode node = super.parent();
		if (node == null) {
			parent = node = readNode(model.readNode(parentId));
		}
		return node;
	}

	protected StorageBPTreeNode readNode(byte[] bytes) throws IOException {
		StorageBPTreeNode node;
		String text = new String(bytes, Charset.forName("utf-8"));
		node = new StorageBPTreeNode(model);
		StringTokenizer tokenizer = new StringTokenizer(text);
		if (!tokenizer.hasMoreElements()) {
			throw new IOException();
		}
		node.leaf = Boolean.parseBoolean(tokenizer.nextToken());
		if (!tokenizer.hasMoreElements()) {
			throw new IOException();
		}
		node.length = Integer.parseInt(tokenizer.nextToken());
		if (!tokenizer.hasMoreElements()) {
			throw new IOException();
		}
		node.parentId = Integer.parseInt(tokenizer.nextToken());
		for (int n = 0; n < length; n++) {
			if (!tokenizer.hasMoreElements()) {
				throw new IOException();
			}
			node.keys[n] = Integer.parseInt(tokenizer.nextToken());
		}
		if (!node.leaf) {
			for (int n = 0; n <= length; n++) {
				if (!tokenizer.hasMoreElements()) {
					throw new IOException();
				}
				node.nodeIds[n] = Integer.parseInt(tokenizer.nextToken());
			}
		}
		return node;
	}

	public static interface StorageBPTreeNodeInterface {

		public byte[] readStructure() throws IOException;

		public byte[] readNode(int id) throws IOException;

		public void writeStructure(byte[] bytes) throws IOException;

		public void writeNode(int id, byte[] bytes) throws IOException;

	}

}
