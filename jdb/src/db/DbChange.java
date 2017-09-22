package db;

import java.util.Iterator;

public class DbChange<E extends Comparable<E>> extends DbSelect<E> {

	private DbSelect<E> parent;

	private Change<E> condition;

	public DbChange(DbSelect<E> parent, Change<E> condition) {
		this.parent = parent;
		this.condition = condition;
	}

	@Override
	public Iterator<E> iterator() {
		return new DbChangeInterator<E>(parent.iterator(), condition);
	}

	

	protected static class DbChangeInterator<E> implements Iterator<E> {

		private Iterator<E> iterator;

		private Change<E> condition;

		public DbChangeInterator(Iterator<E> iterator, Change<E> condition) {
			this.iterator = iterator;
			this.condition = condition;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public E next() {
			return condition.change(iterator.next());
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

	}

}
