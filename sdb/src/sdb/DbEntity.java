package sdb;

import java.io.IOException;

import sdb.DbTreeMap.Entry;

/**
 * Classe de banco de dados que representa uma entidade
 * 
 * @author bernardobreder
 */
public class DbEntity {

  /** Banco de dados */
  protected final DbDatabase database;
  /** Delegator da entidade */
  protected final DbEntityIODelegator entityDelegator;
  /** Nome */
  protected final String entityName;
  /** Arvore B para os dados */
  protected DbTable table;
  /** Indices */
  protected DbTreeMap<String, DbIndex> indexs;
  /** Indica que a estrutura da entidade mudou */
  protected boolean structureChanged;
  /** Indica que a estrutura da entidade mudou */
  protected boolean dataChanged;

  /**
   * Construtor
   * 
   * @param database
   * @param entityName
   * @throws IOException
   */
  protected DbEntity(DbDatabase database, String entityName) throws IOException {
    super();
    this.database = database;
    this.entityDelegator = database.entityDelegate;
    this.entityName = entityName;
    this.table = new DbTable(database.tableTreeDelegator, entityName, 101);
    if (database.entityDelegate.hasEntity(database.databaseName, entityName)) {
      byte[] bytes =
        database.entityDelegate.readEntity(database.databaseName, entityName);
      DbInputBytes in = new DbInputBytes(bytes);
      if (!in.readStringUtf8().equals(entityName)) {
        throw new IllegalStateException("entity writed has wrong name");
      }
      int length = in.readUByte();
      if (length > 0) {
        indexs = new DbTreeMap<String, DbIndex>();
        for (int n = 0; n < length; n++) {
          String key = in.readStringUtf8();
          String[] columns = new String[in.readUByte()];
          for (int c = 0; c < length; c++) {
            columns[c] = in.readStringUtf8();
          }
          DbIndex value = new DbIndex(this, key, columns);
          indexs.put(key, value);
        }
      }
      if (!in.readEof()) {
        throw new IllegalStateException("expected eof");
      }
    }
  }

  /**
   * Cria um indice
   * 
   * @param indexName
   * @param columns
   * @return indice
   * @throws IOException
   */
  public DbIndex addIndex(String indexName, String... columns)
    throws IOException {
    if (indexs != null && indexs.has(entityName)) {
      throw new IllegalArgumentException("entity already exist");
    }
    if (indexs == null) {
      indexs = new DbTreeMap<String, DbIndex>();
    }
    if (indexs.size() >= 0xFF) {
      throw new IllegalStateException("limit of 256 indexs");
    }
    if (columns.length >= 256) {
      throw new IllegalStateException("limit of 256 columns");
    }
    DbIndex index = new DbIndex(this, indexName, columns);
    indexs.put(indexName, index);
    structureChanged = true;
    return index;
  }

  /**
   * Retorna o indice da tabela
   * 
   * @param indexName
   * @return indice
   */
  public DbIndex getIndex(String indexName) {
    DbIndex index = indexs.get(indexName);
    if (index == null) {
      throw new IllegalArgumentException("index not exist");
    }
    return index;
  }

  /**
   * Verifica a existencia de um indice
   * 
   * @param indexName nome do indice
   * @return existencia de um indice
   */
  public boolean hasIndex(String indexName) {
    return indexs.has(indexName);
  }

  /**
   * Remove um indice
   * 
   * @param indexName nome do indice
   */
  public void delIndex(String indexName) {
    structureChanged = true;
  }

  /**
   * Retorna o indice de Id
   * 
   * @param value
   * @return indice de Id
   * @throws IOException
   */
  public long addData(DbObject value) throws IOException {
    long result = table.add(value);
    for (Entry<String, DbIndex> entry = indexs == null ? null : indexs.first(); entry != null; entry =
      entry.successor()) {
      DbIndex index = entry.getValue();
      String[] columns = index.columns;
      long[] key = new long[columns.length];
      for (int n = 0; n < columns.length; n++) {
        String columnName = columns[n];
        if (value.hasAsLong(columnName)) {
          key[n] = value.getAsLong(columnName);
        }
        else {
          key[n] = 0l;
        }
      }
      index.add(result, key);
    }
    dataChanged = true;
    return result;
  }

  /**
   * Retorna o indice de Id
   * 
   * @param id
   * @return indice de Id
   * @throws IOException
   */
  public DbObject getData(long id) throws IOException {
    return table.get(id);
  }

  /**
   * Retorna o indice de Id
   * 
   * @param id
   * @return indica se tem o dado
   * @throws IOException
   */
  public boolean hasData(long id) throws IOException {
    return table.get(id) != null;
  }
  
  	/**
	 * Retorna o número de tuplas
	 * 
	 * @return
	 * @throws IOException
	 */
  public long getDataLastId() throws IOException{
	return table.lastDataId();
  }

  /**
   * Retorna o indice de Id
   * 
   * @param id
   * @param value
   * @throws IOException
   */
  public void setData(long id, DbObject value) throws IOException {
    table.set(id, value);
  }

  /**
   * Retorna o indice de Id
   * 
   * @param id
   * @throws IOException
   */
  public void delData(long id) throws IOException {
    table.del(id);
  }

  /**
   * @throws IOException
   */
  public void commit() throws IOException {
    table.commit();
    if (indexs != null && !indexs.isEmpty()) {
      if (structureChanged) {
        DbOutputBytes out = new DbOutputBytes();
        out.writeStringUtf8(entityName);
        out.writeUByte(indexs.size());
        for (Entry<String, DbIndex> entry = indexs.first(); entry != null; entry =
          entry.successor()) {
          String name = entry.getKey();
          out.writeStringUtf8(name);
          DbIndex index = entry.getValue();
          out.writeUByte(index.columns.length);
          for (int n = 0; n < index.columns.length; n++) {
            out.writeStringUtf8(index.columns[n]);
          }
        }
        byte[] bytes = out.getBytes();
        database.entityDelegate.writeEntity(database.databaseName, entityName,
          bytes);
      }
      for (Entry<String, DbIndex> entry =
        indexs == null ? null : indexs.first(); entry != null; entry =
        entry.successor()) {
        DbIndex index = entry.getValue();
        index.commit();
      }
    }
  }

  /**
   * @throws IOException
   */
  public void rollback() throws IOException {
    table.rollback();
    for (Entry<String, DbIndex> entry = indexs == null ? null : indexs.first(); entry != null; entry =
      entry.successor()) {
      entry.getValue().rollback();
      entry = entry.successor();
    }
  }

  /**
   * @return the databaseName
   */
  public String getDatabaseName() {
    return database.databaseName;
  }

  /**
   * @return the entityName
   */
  public String getEntityName() {
    return entityName;
  }

  /**
   * Delega a leitura e escrita para a classe que utilizar o {@link DbEntity}.
   * 
   * @author bernardobreder
   */
  public static interface DbEntityIODelegator {

    /**
     * Realiza a leitura dos dados do banco de dados
     * 
     * @param databaseName
     * @param entityName
     * @return bytes
     * @throws IOException
     */
    public byte[] readEntity(String databaseName, String entityName)
      throws IOException;

    /**
     * Realiza a escrita dos dados do banco de dados
     * 
     * @param databaseName
     * @param entityName
     * @param bytes
     * @throws IOException
     */
    public void writeEntity(String databaseName, String entityName, byte[] bytes)
      throws IOException;

    /**
     * Verifica se o banco de dados existe
     * 
     * @param databaseName
     * @param entityName
     * @return existencia do banco de dados
     * @throws IOException
     */
    public boolean hasEntity(String databaseName, String entityName)
      throws IOException;

  }

}
