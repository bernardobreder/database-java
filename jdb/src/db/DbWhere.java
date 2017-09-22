package db;

import java.util.Iterator;

public class DbWhere<E extends Comparable<E>> extends DbSelect<E> {

	private DbSelect<E> parent;

	private Where<E> condition;

	public DbWhere(DbSelect<E> parent, Where<E> condition) {
		this.parent = parent;
		this.condition = condition;
	}

	@Override
	public Iterator<E> iterator() {
		return new DbWhereInterator<E>(parent.iterator(), condition);
	}

	protected static class DbWhereInterator<E> implements Iterator<E> {

		private Iterator<E> iterator;

		private Where<E> condition;

		private E next;

		public DbWhereInterator(Iterator<E> iterator, Where<E> condition) {
			this.iterator = iterator;
			this.condition = condition;
		}

		@Override
		public boolean hasNext() {
			while (iterator.hasNext()) {
				E element = iterator.next();
				if (condition.accept(element)) {
					next = element;
					return true;
				}
			}
			next = null;
			return false;
		}

		@Override
		public E next() {
			return next;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

	}

}
