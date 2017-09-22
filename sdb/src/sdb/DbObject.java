package sdb;

/**
 * Estrutura de mapa de um registro de banco de dados
 * 
 * @author bernardobreder
 */
public class DbObject {

  /** Armazena os valores */
  protected DbTreeMap<String, Object> values;

  /**
   * Constroi uma estrutura nova
   */
  public DbObject() {
    this.values = new DbTreeMap<String, Object>();
  }

  /**
   * @param values
   */
  public DbObject(DbTreeMap<String, Object> values) {
    this.values = values;
  }

  /**
   * Adiciona um valor como string
   * 
   * @param name
   * @param value
   * @return this
   */
  public DbObject putAsString(String name, String value) {
    values.put(name, value);
    return this;
  }

  /**
   * Adiciona um valor como inteiro
   * 
   * @param name
   * @param value
   * @return this
   */
  public DbObject putAsInteger(String name, Integer value) {
    values.put(name, value);
    return this;
  }

  /**
   * Adiciona um valor como inteiro
   * 
   * @param name
   * @param value
   * @return this
   */
  public DbObject putAsLong(String name, Long value) {
    values.put(name, value);
    return this;
  }

  /**
   * Recupera um valor como String
   * 
   * @param name
   * @return valor como string
   */
  public String getAsString(String name) {
    Object value = values.get(name);
    if (value == null) {
      return null;
    }
    if (value instanceof String) {
      return (String) value;
    }
    return value.toString();
  }

  /**
   * Recupera um valor como inteiro
   * 
   * @param name
   * @return valor como inteiro
   */
  public Integer getAsInteger(String name) {
    Object value = values.get(name);
    if (value == null) {
      return null;
    }
    if (value instanceof Integer) {
      return (Integer) value;
    }
    else if (value instanceof Long) {
      return ((Long) value).intValue();
    }
    else if (value instanceof Float) {
      return ((Float) value).intValue();
    }
    else if (value instanceof Double) {
      return ((Double) value).intValue();
    }
    return Integer.parseInt(value.toString());
  }

  /**
   * Recupera um valor como inteiro
   * 
   * @param name
   * @return valor como inteiro
   */
  public Long getAsLong(String name) {
    Object value = values.get(name);
    if (value == null) {
      return null;
    }
    if (value instanceof Long) {
      return (Long) value;
    }
    else if (value instanceof Integer) {
      return ((Integer) value).longValue();
    }
    else if (value instanceof Float) {
      return ((Float) value).longValue();
    }
    else if (value instanceof Double) {
      return ((Double) value).longValue();
    }
    return Long.parseLong(value.toString());
  }

  /**
   * Tem um dado inteiro
   * 
   * @param name
   * @return tem dado
   */
  public boolean hasAsInteger(String name) {
    Object value = values.get(name);
    if (value == null) {
      return false;
    }
    if (value instanceof Integer || value instanceof Long
      || value instanceof Float || value instanceof Double) {
      return true;
    }
    return false;
  }

  /**
   * Tem um dado long
   * 
   * @param name
   * @return tem dado
   */
  public boolean hasAsLong(String name) {
    Object value = values.get(name);
    if (value == null) {
      return false;
    }
    if (value instanceof Integer || value instanceof Long
      || value instanceof Float || value instanceof Double) {
      return true;
    }
    return false;
  }

  /**
   * @return numero de elementos
   */
  public int size() {
    return values.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return values.hashCode();
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
    DbObject other = (DbObject) obj;
    return values.equals(other.values);
  }
  
  @Override
	public String toString() {
		return values.toString();
	}

}
