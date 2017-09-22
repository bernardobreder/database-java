package db;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

public class DbTable<E extends Comparable<E>> {

	private final TreeSet<E> dataSet;

	public DbTable() {
		this(null);
	}

	public DbTable(Comparator<E> comparator) {
		this.dataSet = new TreeSet<E>(comparator);
	}

	public boolean insert(E element) {
		return dataSet.add(element);
	}

	public boolean insertAll(Collection<E> elements) {
		return dataSet.addAll(elements);
	}

	public boolean remove(E element) {
		return dataSet.remove(element);
	}

	public DbSelect<E> select(E fromElement, E toElement) {
		return new DbFilter<E>(dataSet, fromElement, toElement);
	}

	public DbSelect<E> select(E element) {
		return new DbFilter<E>(dataSet, element, element);
	}

	public boolean isEmpty() {
		return dataSet.isEmpty();
	}

	public E first() {
		return dataSet.first();
	}

	public E last() {
		return dataSet.last();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (E element : dataSet) {
			sb.append(element.toString());
			sb.append('\n');
		}
		return sb.toString();
	}

	public static interface Group<E> {
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

}
