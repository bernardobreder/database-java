package breder.db.impl;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mapa na qual seus elementos sao referencias do tipo SoftReference
 * 
 * @author bernardobreder
 * 
 * @param <K>
 * @param <V>
 */
public class SoftMap<K, V> implements Map<K, V> {

	/** Lista de entidades */
	protected List<SoftReference<SoftEntry<K, V>>> list = new ArrayList<SoftReference<SoftEntry<K, V>>>();
	/** Lista de entidades */
	protected List<SoftReference<SoftEntry<K, V>>> unmodifiableList = Collections.unmodifiableList(list);

	/**
	 * Adiciona um elemento no mapa
	 * 
	 * @param e
	 * @return indica se foi adicionado ou nao
	 */
	public boolean add(SoftReference<SoftEntry<K, V>> e) {
		long hash = e.hashCode();
		int low = 0;
		int high = list.size() - 1;
		int mid = 0;
		while (low <= high) {
			mid = (low + high) >>> 1;
			SoftReference<SoftEntry<K, V>> midVal = list.get(mid);
			long midHash = midVal.hashCode();
			long cmp = midHash - hash;
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				if (!midVal.equals(e)) {
					list.add(mid, e);
					return true;
				} else {
					return false;
				}
			}
		}
		if (mid > 0) {
			list.add(low, e);
		} else {
			list.add(low, e);
		}
		return true;
	}

	/**
	 * Retorna um elemento do mapa baseado no hash
	 * 
	 * @param key
	 * @return retorna um elemento do mapa
	 */
	public int indexOf(Object key) {
		long hash = key.hashCode();
		int low = 0;
		int size = list.size();
		int high = size - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			SoftReference<SoftEntry<K, V>> midVal = list.get(mid);
			long midHash = midVal.hashCode();
			long cmp = midHash - hash;
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				if (midVal.equals(key)) {
					return mid;
				} else {
					if (mid > 0) {
						int aux = mid - 1;
						SoftReference<SoftEntry<K, V>> elem = list.get(aux);
						while (elem.hashCode() == hash) {
							SoftEntry<K, V> entry = elem.get();
							if (entry == null) {
								this.list.remove(aux);
								size--;
							} else if (entry.equals(key)) {
								return aux;
							}
							if (aux == 0) {
								break;
							}
							elem = list.get(--aux);
						}
					}
					if (mid < size - 1) {
						int aux = mid + 1;
						SoftReference<SoftEntry<K, V>> elem = list.get(aux);
						while (elem.hashCode() == hash) {
							SoftEntry<K, V> entry = elem.get();
							if (entry == null) {
								this.list.remove(aux--);
								size--;
							} else if (entry.equals(key)) {
								return aux;
							}
							if (aux == size - 1) {
								break;
							}
							elem = list.get(++aux);
						}
					}
					return -1;
				}
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return this.list.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsKey(Object key) {
		return this.indexOf(key) >= 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsValue(Object value) {
		Iterator<SoftReference<SoftEntry<K, V>>> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			SoftReference<SoftEntry<K, V>> ref = iterator.next();
			SoftEntry<K, V> entry = ref.get();
			if (entry == null) {
				iterator.remove();
			} else {
				if (entry.getValue().equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V get(Object key) {
		int index = this.indexOf(key);
		if (index < 0) {
			return null;
		}
		SoftReference<SoftEntry<K, V>> ref = this.list.get(index);
		SoftEntry<K, V> entry = ref.get();
		if (entry == null) {
			return null;
		}
		return entry.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V put(K key, V value) {
		System.out.println(this.list.size());
		int index = this.indexOf(key);
		if (index < 0) {
			this.add(new MySoftReference<SoftEntry<K, V>>(new SoftEntry<K, V>(key, value)));
			return null;
		}
		SoftReference<SoftEntry<K, V>> ref = this.list.get(index);
		SoftEntry<K, V> entry = ref.get();
		if (entry == null) {
			this.list.set(index, new MySoftReference<SoftEntry<K, V>>(new SoftEntry<K, V>(key, value)));
			return null;
		}
		V oldValue = entry.value;
		entry.value = value;
		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public V remove(Object key) {
		int index = this.indexOf(key);
		if (index < 0) {
			return null;
		}
		SoftReference<SoftEntry<K, V>> ref = this.list.get(index);
		SoftEntry<K, V> entry = ref.get();
		if (entry == null) {
			this.list.remove(index);
			return null;
		}
		this.list.remove(index);
		return entry.value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			this.put(key, m.get(key));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.list.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<K> keySet() {
		Set<K> set = new HashSet<K>(this.list.size());
		Iterator<SoftReference<SoftEntry<K, V>>> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			SoftReference<SoftEntry<K, V>> ref = iterator.next();
			SoftEntry<K, V> entry = ref.get();
			if (entry == null) {
				iterator.remove();
			} else {
				set.add(entry.getKey());
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<V> values() {
		Collection<V> set = new ArrayList<V>(this.list.size());
		Iterator<SoftReference<SoftEntry<K, V>>> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			SoftReference<SoftEntry<K, V>> ref = iterator.next();
			SoftEntry<K, V> entry = ref.get();
			if (entry == null) {
				iterator.remove();
			} else {
				set.add(entry.getValue());
			}
		}
		return set;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return list.toString();
	}

	/**
	 * Entidade do mapa
	 * 
	 * @author bernardobreder
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static class SoftEntry<K, V> implements java.util.Map.Entry<K, V> {

		/** Chave */
		private final K key;
		/** Valor */
		private V value;

		/**
		 * Construtor
		 * 
		 * @param key
		 * @param value
		 */
		public SoftEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public K getKey() {
			return this.key;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V getValue() {
			return this.value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public V setValue(V value) {
			V old = this.value;
			this.value = value;
			return old;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			if (this.key == null) {
				return 0;
			}
			return this.key.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this.key == null) {
				return obj.equals(key);
			}
			return this.key.equals(obj);
		}

	}

	/**
	 * Um SoftReference que usa o equals e hashcode da referencia
	 * 
	 * @author bernardobreder
	 * 
	 * @param <T>
	 */
	private static class MySoftReference<T> extends SoftReference<T> {

		/** Hash */
		private int hash;

		/**
		 * Construtor
		 * 
		 * @param referent
		 */
		public MySoftReference(T referent) {
			super(referent);
			this.hash = referent.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return this.hash;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			T ref = this.get();
			if (ref == null) {
				return false;
			}
			return ref.equals(obj);
		}

	}

}
