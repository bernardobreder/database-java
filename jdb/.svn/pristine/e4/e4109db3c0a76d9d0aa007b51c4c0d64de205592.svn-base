package jdb.bptree;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import jdb.bptree.StorageBPTreeNode.StorageBPTreeNodeInterface;

import org.junit.Test;

public class StorageBPTreeNodeTest implements StorageBPTreeNodeInterface {

	private Map<String, byte[]> bytes = new TreeMap<String, byte[]>();

	@Test
	public void test() throws IOException {
		StorageBPTreeNode root = new StorageBPTreeNode(this);
		root.commit();
	}

	@Override
	public byte[] readStructure() throws IOException {
		return bytes.get("table.db");
	}

	@Override
	public byte[] readNode(int id) throws IOException {
		return bytes.get("table.node." + id + ".db");
	}

	@Override
	public void writeStructure(byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeNode(int id, byte[] bytes) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
