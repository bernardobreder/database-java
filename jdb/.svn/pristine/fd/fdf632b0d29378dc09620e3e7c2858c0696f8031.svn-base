package jdb;

import java.util.Arrays;

public class SortedList {

	private int keys;

	private int order;

	private int pages;

	private int size;

	private Chuck[] chucks;

	public SortedList(int keys, int pages, int order) {
		this.keys = keys;
		this.pages = pages;
		this.order = order;
		this.chucks = new Chuck[pages];
		for (int n = 0; n < pages; n++) {
			this.chucks[n] = new Chuck(order);
		}
	}

	public byte[] find(Object... objects) {
		if (objects.length != keys) {
			return null;
		}
		return null;
	}

	public Cursor range(Object[] from, Object[] to) {
		return null;
	}

	public void insert(byte[] bytes, Comparable<?>... keys) {
		Chuck chuck = chucks[findChuckForInsert(keys)];
		while (chuck.elements.length == order || size >= pages * order * 0.75) {
			grow();
			chuck = chucks[findChuckForInsert(keys)];
		}
		chuck.elements[chuck.length++] = new Data(keys, bytes);
		chuck.first = chuck.elements[0].key;
		size++;
	}

	public void delete(Object... objects) {

	}

	protected <E extends Comparable<E>> int findChuckForInsert(Comparable<?>[] keys) {
		for (int n = 0; n < pages; n++) {
			Object[] first = chucks[n].first;
			if (first == null || compare(first, keys) >= 0) {
				return n;
			}
		}
		return pages - 1;
	}

	private void grow() {
		
	}

	protected int compare(Object[] left, Object[] right) {
		for (int n = 0; n < keys; n++) {
			@SuppressWarnings("unchecked")
			Comparable<Object> comparable = (Comparable<Object>) left[n];
			int cmp = comparable.compareTo(right[n]);
			if (cmp != 0) {
				return cmp;
			}
		}
		return 0;
	}

	public static class First {

		public Object[] value;

		@Override
		public String toString() {
			return "First [value=" + Arrays.toString(value) + "]";
		}

	}

	public static class Chuck {

		public int length;

		public Object[] first;

		public Data[] elements;

		public Chuck(int order) {
			super();
			this.length = 0;
			this.elements = new Data[order];
		}

		@Override
		public String toString() {
			return "Chuck [elements=" + Arrays.toString(elements) + "]";
		}
	}

	public static class Data {

		public Object[] key;

		public byte[] bytes;

		public Data(Object[] key, byte[] bytes) {
			super();
			this.key = key;
			this.bytes = bytes;
		}

		@Override
		public String toString() {
			return Arrays.toString(key) + "->" + Arrays.toString(bytes);
		}
	}

	public static class Cursor {
	}

	public static void main(String[] args) {
		SortedList list = new SortedList(1, 1, 2);
		list.insert(new byte[0], 1);
		list.insert(new byte[0], 2);
		list.insert(new byte[0], 3);
		list.insert(new byte[0], 4);
		list.find(1);
		list.find(2);
		list.find(3);
		list.find(4);
	}

}
