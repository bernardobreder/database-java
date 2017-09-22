package db;

import java.util.Collection;
import java.util.Iterator;

public abstract class DbSelect<E extends Comparable<E>> implements Iterable<E> {

	public DbSelect<E> where(Where<E> condition) {
		return new DbWhere<E>(this, condition);
	}

	public DbSelect<E> change(Change<E> change) {
		return new DbChange<E>(this, change);
	}

	public DbSelect<E> group(Group<E> group) {
		return new DbGroup<E>(this, group);
	}

	public E first() {
		Iterator<E> iterator = iterator();
		if (!iterator.hasNext()) {
			return null;
		}
		return iterator.next();
	}

	public static interface Group<E> {
		public E group(E key, Collection<E> group);
	}

	public static interface Change<E> {
		public E change(E element);
	}

	public static interface Where<E> {
		public boolean accept(E element);
	}

}
