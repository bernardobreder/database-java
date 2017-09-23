package breder.db;

import java.util.ArrayList;
import java.util.List;

public class DBData {

	public final List<Entry> entrys;

	public DBData(List<Entry> entrys) {
		this.entrys = entrys;
	}

	public DBData() {
		this.entrys = new ArrayList<DBData.Entry>(5);
	}

	/**
	 * Atribui um valor a uma chave
	 * 
	 * @param key
	 * @param value
	 * @return this
	 */
	public DBData set(String key, Object value) {
		int index = this.indexOf(key);
		if (index < 0) {
			this.add(key, value);
		} else {
			this.entrys.get(index).value = value;
		}
		return this;
	}

	/**
	 * Recupera o valor de uma chave
	 * 
	 * @param key
	 * @return valor de uma chave
	 */
	public String get(String key) {
		int index = this.indexOf(key);
		if (index < 0) {
			return null;
		}
		return (String) this.entrys.get(index).value;
	}

	/**
	 * Retorna um elemento do mapa baseado no hash
	 * 
	 * @param key
	 * @return retorna um elemento do mapa
	 */
	private int indexOf(Object key) {
		long hash = key.hashCode();
		int low = 0;
		int size = entrys.size();
		int high = size - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			Entry midVal = entrys.get(mid);
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
						Entry entry = entrys.get(aux);
						while (entry.hashCode() == hash) {
							if (entry.equals(key)) {
								return aux;
							}
							if (aux == 0) {
								break;
							}
							entry = entrys.get(--aux);
						}
					}
					if (mid < size - 1) {
						int aux = mid + 1;
						Entry entry = entrys.get(aux);
						while (entry.hashCode() == hash) {
							if (entry.equals(key)) {
								return aux;
							}
							if (aux == size - 1) {
								break;
							}
							entry = entrys.get(++aux);
						}
					}
					return -1;
				}
			}
		}
		return -1;
	}

	/**
	 * Adiciona um elemento no mapa
	 * 
	 * @param e
	 * @return indica se foi adicionado ou nao
	 */
	private void add(String key, Object value) {
		Entry e = new Entry();
		e.key = key;
		e.value = value;
		long hash = e.hashCode();
		int low = 0;
		int high = entrys.size() - 1;
		int mid = 0;
		while (low <= high) {
			mid = (low + high) >>> 1;
			Entry midVal = entrys.get(mid);
			long midHash = midVal.hashCode();
			long cmp = midHash - hash;
			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				if (!midVal.equals(e)) {
					entrys.add(mid, e);
					return;
				} else {
					return;
				}
			}
		}
		if (mid > 0) {
			entrys.add(low, e);
		} else {
			entrys.add(low, e);
		}
	}

	public static class Entry {

		public String key;

		public Object value;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entry other = (Entry) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return this.entrys.toString();
	}

}
