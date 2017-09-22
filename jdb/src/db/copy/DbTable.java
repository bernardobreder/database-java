package db.copy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

public class DbTable<E extends Comparable<E>> {

	private final Comparator<E> comparator;

	private final TreeSet<E> set;

	public DbTable() {
		this(null);
	}

	public DbTable(Comparator<E> comparator) {
		this.comparator = comparator;
		this.set = new TreeSet<E>(comparator);
	}

	public boolean insert(E element) {
		return set.add(element);
	}

	public boolean insertAll(Collection<E> elements) {
		return set.addAll(elements);
	}

	public boolean remove(E element) {
		return set.remove(element);
	}

	public DbTable<E> select() {
		DbTable<E> table = new DbTable<E>(new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				int compare = comparator == null ? o1.compareTo(o2) : comparator.compare(o1, o2);
				if (compare != 0) {
					return compare;
				}
				return System.identityHashCode(o1) - System.identityHashCode(o2);
			}
		});
		table.insertAll(set);
		return table;
	}

	public E filter(E element) {
		NavigableSet<E> subSet = set.subSet(element, true, element, true);
		if (subSet.isEmpty()) {
			return null;
		}
		return subSet.first();
	}

	public DbTable<E> filter(E from, E to) {
		NavigableSet<E> subSet = set.subSet(from, true, to, true);
		DbTable<E> table = new DbTable<E>(comparator);
		table.insertAll(subSet);
		return table;
	}

	public DbTable<E> change(Change<E> callback) {
		DbTable<E> table = new DbTable<E>(comparator);
		for (E element : set) {
			table.insert(callback.change(element));
		}
		return table;
	}

	public DbTable<E> where(Where<E> where) {
		DbTable<E> table = new DbTable<E>(comparator);
		for (E element : set) {
			if (where.accept(element)) {
				table.insert(element);
			}
		}
		return table;
	}

	public DbTable<E> order(Comparator<E> comparator) {
		TreeSet<E> set = new TreeSet<E>(comparator);
		DbTable<E> table = new DbTable<E>(comparator);
		table.insertAll(set);
		return table;
	}

	public DbTable<E> group(Comparator<E> comparator, GroupBy<E> grouper) {
		if (set.isEmpty()) {
			return this;
		}
		TreeSet<E> set = this.set;
		if (this.comparator != comparator) {
			set = new TreeSet<E>(comparator);
			set.addAll(this.set);
		}
		DbTable<E> table = new DbTable<E>(comparator);
		Iterator<E> iterator = set.iterator();
		List<E> list = new ArrayList<E>();
		E key = iterator.next();
		list.add(key);
		while (iterator.hasNext()) {
			E next = iterator.next();
			if (next.compareTo(key) == 0) {
				list.add(next);
			} else {
				table.insert(grouper.group(list.get(0), list));
				list.clear();
				list.add(key = next);
			}
		}
		if (!list.isEmpty()) {
			table.insert(grouper.group(list.get(0), list));
		}
		return table;
	}

	public DbTable<E> group(GroupBy<E> grouper) {
		return group(comparator, grouper);
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public E first() {
		return set.first();
	}

	public E last() {
		return set.last();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E element : set) {
			sb.append(element.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	public static interface Change<E> {
		public E change(E element);
	}

	public static interface Where<E> {
		public boolean accept(E element);
	}

	public static interface GroupBy<E> {
		public E group(E key, Collection<E> group);
	}

	public static class OpenStock implements Comparable<OpenStock> {

		public int day;
		public int balanceId;
		public int pointId;
		public String productId;
		public double mass;
		public double volume;

		public OpenStock(int day, int balanceId, int pointId, String productId) {
			this.day = day;
			this.balanceId = balanceId;
			this.pointId = pointId;
			this.productId = productId;
		}

		public OpenStock(int day, int balanceId, int pointId, String productId, double mass, double volume) {
			this.day = day;
			this.balanceId = balanceId;
			this.pointId = pointId;
			this.productId = productId;
			this.mass = mass;
			this.volume = volume;
		}

		@Override
		public int compareTo(OpenStock o) {
			int compare = this.day - o.day;
			if (compare != 0) {
				return compare;
			}
			compare = this.balanceId - o.balanceId;
			if (compare != 0) {
				return compare;
			}
			compare = this.pointId - o.pointId;
			if (compare != 0) {
				return compare;
			}
			compare = this.productId.compareTo(o.productId);
			if (compare != 0) {
				return compare;
			}
			return 0;
		}

		@Override
		public String toString() {
			return "OpenStock [day=" + day + ", balanceId=" + balanceId + ", pointId=" + pointId + ", productId=" + productId + ", mass=" + mass
					+ ", volume=" + volume + "]";
		}
	}

	public static class Produto implements Comparable<Produto> {

		public String id;

		public String name;

		public double density;

		public Produto(String id) {
			this.id = id;
		}

		public Produto(String id, String name, double density) {
			super();
			this.id = id;
			this.name = name;
			this.density = density;
		}

		@Override
		public int compareTo(Produto o) {
			return id.compareTo(o.id);
		}

		@Override
		public String toString() {
			return "Produto [id=" + id + ", name=" + name + ", density=" + density + "]";
		}

	}

	public static class Ponto implements Comparable<Ponto> {

		public int id;

		public String pointTypeId;

		public Ponto(int pointId) {
			this.id = pointId;
		}

		public Ponto(int pointId, String pointTypeId) {
			super();
			this.id = pointId;
			this.pointTypeId = pointTypeId;
		}

		@Override
		public int compareTo(Ponto o) {
			return id - o.id;
		}

		@Override
		public String toString() {
			return "Ponto [id=" + id + ", pointTypeId=" + pointTypeId + "]";
		}

	}

	public static void main(String[] args) {
		final DbTable<Ponto> pointTable = new DbTable<Ponto>();
		pointTable.insert(new Ponto(149, "REF"));
		pointTable.insert(new Ponto(150, "REF"));
		pointTable.insert(new Ponto(151, "REF"));
		final DbTable<Produto> productTable = new DbTable<Produto>();
		productTable.insert(new Produto("610", "Glp", 0.5));
		productTable.insert(new Produto("611", "Butano", 0.3));
		productTable.insert(new Produto("612", "Propano", 0.7));
		final DbTable<OpenStock> openStockTable = new DbTable<OpenStock>();
		openStockTable.insert(new OpenStock(20100601, 0, 149, "610", 90, 90));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 149, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 150, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100601, 0, 152, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 149, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 150, "613", 180, 180));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "610", 100, 100));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "611", 150, 120));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "612", 150, 130));
		openStockTable.insert(new OpenStock(20100602, 0, 152, "613", 180, 180));
		DbTable<OpenStock> resultTable = openStockTable
				.select()
				.filter(new OpenStock(20100601, 0, pointTable.first().id, productTable.first().id),
						new OpenStock(20100631, 0, pointTable.last().id, productTable.last().id)) //
				.where(new Where<OpenStock>() {
					@Override
					public boolean accept(OpenStock element) {
						if (element.mass == 0. && element.volume == 0.) {
							return false;
						}
						Produto produto = productTable.filter(new Produto(element.productId));
						if (produto == null) {
							return false;
						}
						Ponto point = pointTable.filter(new Ponto(element.pointId));
						if (point == null) {
							return false;
						}
						return true;
					}
				}) //
				.change(new Change<OpenStock>() {
					@Override
					public OpenStock change(OpenStock element) {
						return new OpenStock(element.day / 100, element.balanceId, element.pointId, element.productId, element.mass, element.volume);
					}
				}).group(new GroupBy<OpenStock>() {
					@Override
					public OpenStock group(OpenStock key, Collection<OpenStock> group) {
						double mass = 0;
						double volume = 0;
						for (OpenStock item : group) {
							mass += item.mass;
							volume += item.volume;
						}
						return new OpenStock(key.day, key.balanceId, key.pointId, key.productId, mass, volume);
					}
				});
		System.out.println(resultTable);
	}

}
