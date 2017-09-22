package db;

import java.util.Iterator;
import java.util.TreeSet;

public class DbFilter<E extends Comparable<E>> extends DbSelect<E> {

	private TreeSet<E> dataSet;

	private E fromElement;

	private E toElement;

	public DbFilter(TreeSet<E> dataSet, E fromElement, E toElement) {
		this.dataSet = dataSet;
		this.fromElement = fromElement;
		this.toElement = toElement;
	}

	@Override
	public Iterator<E> iterator() {
		return dataSet.subSet(fromElement, true, toElement, true).iterator();
	}

}
