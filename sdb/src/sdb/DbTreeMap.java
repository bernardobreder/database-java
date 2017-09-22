package sdb;

import java.util.Map;

/**
 * Estrutura de mapa de um registro de banco de dados
 * 
 * @param <K>
 * @param <V>
 * @author bernardobreder
 */
public class DbTreeMap<K extends Comparable<K>, V> {

	/** Raiz */
	private Entry<K, V> root;
	/** Número de elemento */
	private int size;

	/**
	 * Número de elemento
	 * 
	 * @return número de elemento
	 */
	public int size() {
		return size;
	}

	/**
	 * Número de elemento
	 * 
	 * @return número de elemento
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Indica se contém o elemento
	 * 
	 * @param key
	 * @return contém o elemento
	 */
	public boolean has(K key) {
		return getEntry(key) != null;
	}

	/**
	 * Recupera o valor de um elemento
	 * 
	 * @param key
	 * @return valor de um elemento ou nulo
	 */
	public V get(K key) {
		Entry<K, V> p = getEntry(key);
		return (p == null ? null : p.value);
	}

	/**
	 * Recupera a estrutura do elemento
	 * 
	 * @param key
	 * @return estrutura do elemento
	 */
	public Entry<K, V> getEntry(K key) {
		if (key == null) {
			throw new NullPointerException();
		}
		K k = key;
		Entry<K, V> p = root;
		while (p != null) {
			int cmp = k.compareTo(p.key);
			if (cmp < 0) {
				p = p.left;
			} else if (cmp > 0) {
				p = p.right;
			} else {
				return p;
			}
		}
		return null;
	}

	/**
	 * Returns the first Entry in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 * 
	 * @return entidade
	 */
	public Entry<K, V> first() {
		Entry<K, V> p = root;
		if (p != null) {
			while (p.left != null) {
				p = p.left;
			}
		}
		return p;
	}

	/**
	 * Returns the last Entry in the TreeMap (according to the TreeMap's
	 * key-sort function). Returns null if the TreeMap is empty.
	 * 
	 * @return último
	 */
	public Entry<K, V> last() {
		Entry<K, V> p = root;
		if (p != null) {
			while (p.right != null) {
				p = p.right;
			}
		}
		return p;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry
	 * exists, returns the entry for the least key greater than the specified
	 * key; if no such entry exists (i.e., the greatest key in the Tree is less
	 * than the specified key), returns {@code null}.
	 * 
	 * @param key
	 * @return entry
	 */
	public Entry<K, V> getCeilingEntry(K key) {
		Entry<K, V> p = root;
		while (p != null) {
			int cmp = key.compareTo(p.key);
			if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					return p;
				}
			} else if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else {
				return p;
			}
		}
		return null;
	}

	/**
	 * Gets the entry corresponding to the specified key; if no such entry
	 * exists, returns the entry for the greatest key less than the specified
	 * key; if no such entry exists, returns {@code null}.
	 * 
	 * @param key
	 * @return entry
	 */
	public Entry<K, V> getFloorEntry(K key) {
		Entry<K, V> p = root;
		while (p != null) {
			int cmp = key.compareTo(p.key);
			if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					return p;
				}
			} else if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			} else {
				return p;
			}

		}
		return null;
	}

	/**
	 * Gets the entry for the least key greater than the specified key; if no
	 * such entry exists, returns the entry for the least key greater than the
	 * specified key; if no such entry exists returns {@code null}.
	 * 
	 * @param key
	 * @return entry
	 */
	public Entry<K, V> getHigherEntry(K key) {
		Entry<K, V> p = root;
		while (p != null) {
			int cmp = key.compareTo(p.key);
			if (cmp < 0) {
				if (p.left != null) {
					p = p.left;
				} else {
					return p;
				}
			} else {
				if (p.right != null) {
					p = p.right;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.right) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the entry for the greatest key less than the specified key; if no
	 * such entry exists (i.e., the least key in the Tree is greater than the
	 * specified key), returns {@code null}.
	 * 
	 * @param key
	 * @return entry
	 */
	public Entry<K, V> getLowerEntry(K key) {
		Entry<K, V> p = root;
		while (p != null) {
			int cmp = key.compareTo(p.key);
			if (cmp > 0) {
				if (p.right != null) {
					p = p.right;
				} else {
					return p;
				}
			} else {
				if (p.left != null) {
					p = p.left;
				} else {
					Entry<K, V> parent = p.parent;
					Entry<K, V> ch = p;
					while (parent != null && ch == parent.left) {
						ch = parent;
						parent = parent.parent;
					}
					return parent;
				}
			}
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * 
	 * @return the previous value associated with {@code key}, or {@code null}
	 *         if there was no mapping for {@code key}. (A {@code null} return
	 *         can also indicate that the map previously associated {@code null}
	 *         with {@code key}.)
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public V put(K key, V value) {
		if (key == null) {
			throw new NullPointerException();
		}
		Entry<K, V> t = root;
		if (t == null) {
			root = new Entry<K, V>(key, value, null);
			size = 1;
			return null;
		}
		int cmp;
		Entry<K, V> parent;
		do {
			parent = t;
			cmp = key.compareTo(t.key);
			if (cmp < 0) {
				t = t.left;
			} else if (cmp > 0) {
				t = t.right;
			} else {
				return t.setValue(value);
			}
		} while (t != null);
		Entry<K, V> e = new Entry<K, V>(key, value, parent);
		if (cmp < 0) {
			parent.left = e;
		} else {
			parent.right = e;
		}
		fixAfterInsertion(e);
		size++;
		return null;
	}

	/**
	 * Removes the mapping for this key from this TreeMap if present.
	 * 
	 * @param key
	 *            key for which mapping should be removed
	 * @return the previous value associated with {@code key}, or {@code null}
	 *         if there was no mapping for {@code key}. (A {@code null} return
	 *         can also indicate that the map previously associated {@code null}
	 *         with {@code key}.)
	 * @throws ClassCastException
	 *             if the specified key cannot be compared with the keys
	 *             currently in the map
	 * @throws NullPointerException
	 *             if the specified key is null and this map uses natural
	 *             ordering, or its comparator does not permit null keys
	 */
	public V remove(K key) {
		Entry<K, V> p = getEntry(key);
		if (p == null) {
			return null;
		}
		V oldValue = p.value;
		deleteEntry(p);
		return oldValue;
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	public void clear() {
		size = 0;
		root = null;
	}

	/** Cor */
	private static final boolean RED = false;
	/** Cor */
	private static final boolean BLACK = true;

	/**
	 * Retorna a cor
	 * 
	 * @param p
	 * @return cor
	 */
	private boolean colorOf(Entry<K, V> p) {
		return (p == null ? BLACK : p.color);
	}

	/**
	 * @param p
	 * @return pai
	 */
	private Entry<K, V> parentOf(Entry<K, V> p) {
		return (p == null ? null : p.parent);
	}

	/**
	 * @param p
	 * @param c
	 */
	private void setColor(Entry<K, V> p, boolean c) {
		if (p != null) {
			p.color = c;
		}
	}

	/**
	 * @param p
	 * @return left
	 */
	private Entry<K, V> leftOf(Entry<K, V> p) {
		return (p == null) ? null : p.left;
	}

	/**
	 * @param p
	 * @return right
	 */
	private Entry<K, V> rightOf(Entry<K, V> p) {
		return (p == null) ? null : p.right;
	}

	/**
	 * From CLR
	 * 
	 * @param p
	 */
	private void rotateLeft(Entry<K, V> p) {
		if (p != null) {
			Entry<K, V> r = p.right;
			p.right = r.left;
			if (r.left != null) {
				r.left.parent = p;
			}
			r.parent = p.parent;
			if (p.parent == null) {
				root = r;
			} else if (p.parent.left == p) {
				p.parent.left = r;
			} else {
				p.parent.right = r;
			}
			r.left = p;
			p.parent = r;
		}
	}

	/**
	 * From CLR
	 * 
	 * @param p
	 */
	private void rotateRight(Entry<K, V> p) {
		if (p != null) {
			Entry<K, V> l = p.left;
			p.left = l.right;
			if (l.right != null) {
				l.right.parent = p;
			}
			l.parent = p.parent;
			if (p.parent == null) {
				root = l;
			} else if (p.parent.right == p) {
				p.parent.right = l;
			} else {
				p.parent.left = l;
			}
			l.right = p;
			p.parent = l;
		}
	}

	/**
	 * From CLR
	 * 
	 * @param x
	 */
	private void fixAfterInsertion(Entry<K, V> x) {
		x.color = RED;
		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry<K, V> y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry<K, V> y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 * 
	 * @param p
	 */
	private void deleteEntry(Entry<K, V> p) {
		size--;
		if (p.left != null && p.right != null) {
			Entry<K, V> s = p.successor();
			p.key = s.key;
			p.value = s.value;
			p = s;
		}
		Entry<K, V> replacement = (p.left != null ? p.left : p.right);
		if (replacement != null) {
			replacement.parent = p.parent;
			if (p.parent == null) {
				root = replacement;
			} else if (p == p.parent.left) {
				p.parent.left = replacement;
			} else {
				p.parent.right = replacement;
			}
			p.left = p.right = p.parent = null;
			if (p.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (p.parent == null) {
			root = null;
		} else {
			if (p.color == BLACK) {
				fixAfterDeletion(p);
			}
			if (p.parent != null) {
				if (p == p.parent.left) {
					p.parent.left = null;
				} else if (p == p.parent.right) {
					p.parent.right = null;
				}
				p.parent = null;
			}
		}
	}

	/**
	 * From CLR
	 * 
	 * @param x
	 */
	private void fixAfterDeletion(Entry<K, V> x) {
		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				Entry<K, V> sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { // symmetric
				Entry<K, V> sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}
		setColor(x, BLACK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return root == null ? 0 : root.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DbTreeMap<?, ?> other = (DbTreeMap<?, ?>) obj;
		if (size != other.size) {
			return false;
		}
		if (root == null) {
			return other.root == null;
		} else if (!root.equals(other.root)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * Node in the Tree. Doubles as a means to pass key-value pairs back to user
	 * (see Map.Entry).
	 * 
	 * @param <K>
	 * @param <V>
	 */
	public static class Entry<K, V> implements Map.Entry<K, V> {

		/** Chave */
		protected K key;
		/** Valor */
		protected V value;
		/** Esquerda */
		protected Entry<K, V> left;
		/** Direita */
		protected Entry<K, V> right;
		/** Pai */
		protected Entry<K, V> parent;
		/** Cor */
		protected boolean color = BLACK;

		/**
		 * Make a new cell with given key, value, and parent, and with
		 * {@code null} child links, and BLACK color.
		 * 
		 * @param key
		 * @param value
		 * @param parent
		 */
		protected Entry(K key, V value, Entry<K, V> parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		/**
		 * Returns the key.
		 * 
		 * @return the key
		 */
		@Override
		public K getKey() {
			return key;
		}

		/**
		 * Returns the value associated with the key.
		 * 
		 * @return the value associated with the key
		 */
		@Override
		public V getValue() {
			return value;
		}

		/**
		 * Replaces the value currently associated with the key with the given
		 * value.
		 * 
		 * @return the value associated with the key before this method was
		 *         called
		 */
		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		/**
		 * Returns the successor of the specified Entry, or null if no such.
		 * 
		 * @return próxima estrutura do elemento
		 */
		public Entry<K, V> successor() {
			if (this.right != null) {
				Entry<K, V> p = this.right;
				while (p.left != null) {
					p = p.left;
				}
				return p;
			} else {
				Entry<K, V> p = this.parent;
				Entry<K, V> ch = this;
				while (p != null && ch == p.right) {
					ch = p;
					p = p.parent;
				}
				return p;
			}
		}

		/**
		 * Returns the predecessor of the specified Entry, or null if no such.
		 * 
		 * @return entidade
		 */
		public Entry<K, V> predecessor() {
			if (this.left != null) {
				Entry<K, V> p = this.left;
				while (p.right != null) {
					p = p.right;
				}
				return p;
			} else {
				Entry<K, V> p = this.parent;
				Entry<K, V> ch = this;
				while (p != null && ch == p.left) {
					ch = p;
					p = p.parent;
				}
				return p;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> e = (Entry<?, ?>) o;
			return key.equals(e.key);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int hashCode() {
			return key.hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return key + "=" + value;
		}

	}

}
