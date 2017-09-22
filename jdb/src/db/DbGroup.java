package db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DbGroup<E extends Comparable<E>> extends DbSelect<E> {

	private DbSelect<E> parent;

	private Group<E> condition;

	private Comparator<E> comparator;

	public DbGroup(DbSelect<E> parent, Group<E> condition) {
		this.parent = parent;
		this.condition = condition;
	}

	public DbGroup(DbSelect<E> parent, Group<E> condition, Comparator<E> comparator) {
		this.parent = parent;
		this.condition = condition;
		this.comparator = comparator;
	}

	@Override
	public Iterator<E> iterator() {
		return new DbGroupInterator<E>(parent.iterator(), condition, comparator);
	}

	protected static class DbGroupInterator<E extends Comparable<E>> implements Iterator<E> {

		private Iterator<E> iterator;

		private Group<E> group;

		private List<E> list;

		private Comparator<E> comparator;

		private E key;

		private List<E> groupList;

		public DbGroupInterator(Iterator<E> iterator, Group<E> group, Comparator<E> comparator) {
			this.iterator = iterator;
			this.group = group;
			this.comparator = comparator;
		}

		@Override
		public boolean hasNext() {
			if (list == null) {
				list = new ArrayList<E>();
				while (iterator.hasNext()) {
					list.add(iterator.next());
				}
				if (comparator != null) {
					Collections.sort(list, comparator);
				} else {
					Collections.sort(list);
				}
				iterator = list.iterator();
				groupList=new ArrayList<E>();
			}
			return iterator.hasNext();
		}

		@Override
		public E next() {
			 while (iterator.hasNext()) {
				E next = iterator.next();
				if (key == null || (comparator == null ? key.compareTo(next) : comparator.compare(key, next)) == 0) {
					groupList.add(key = next);
				} else {
					E result = group.group(key, groupList);
					groupList.clear();
					groupList.add(key = next);
					return result;
				}
			}
			return group.group(key, groupList);
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

	}

}
