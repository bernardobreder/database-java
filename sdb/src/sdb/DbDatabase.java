package sdb;

import java.io.IOException;

import sdb.DbEntity.DbEntityIODelegator;
import sdb.DbIndexTree.DbIndexTreeIODelegator;
import sdb.DbTableTree.DbTableTreeIODelegator;

/**
 * Classe de banco de dados.
 * 
 * @author bernardobreder
 */
public class DbDatabase {

  /** Delegator do banco de dados */
  protected final DbDatabaseIODelegator databaseDelegate;
  /** Delegator da entidade */
  protected final DbEntityIODelegator entityDelegate;
  /** Delegator da btree */
  protected final DbTableTreeIODelegator tableTreeDelegator;
  /** Delegator da btree */
  protected final DbIndexTreeIODelegator indexTreeDelegator;
  /** Nome */
  protected final String databaseName;
  /** Versão */
  protected int version;
  /** Entidades do banco de dados */
  protected DbTreeMap<String, DbEntity> entitys;
  /** Indica se o banco de dados já foi criado */
  protected boolean created;
  /** Indica se o banco de dados já foi criado */
  protected boolean droped;

  /**
   * Construtor
   * 
   * @param databaseDelegate
   * @param entityDelegate
   * @param tableTreeDelegator
   * @param indexTreeDelegator
   * @param name
   */
  public DbDatabase(DbDatabaseIODelegator databaseDelegate,
    DbEntityIODelegator entityDelegate,
    DbTableTreeIODelegator tableTreeDelegator,
    DbIndexTreeIODelegator indexTreeDelegator, String name) {
    super();
    this.databaseDelegate = databaseDelegate;
    this.entityDelegate = entityDelegate;
    this.tableTreeDelegator = tableTreeDelegator;
    this.indexTreeDelegator = indexTreeDelegator;
    this.databaseName = name;
  }

  /**
   * Ação de criação de banco de dados. Caso o banco exista, será gerado um
   * erro.
   */
  public void create() {
    created = true;
  }

  /**
   * Ação de abrir o banco de dados. Caso o banco não exista, será gerado um
   * erro.
   * 
   * @throws IOException
   */
  public void open() throws IOException {
    if (!databaseDelegate.hasDatabase(databaseName)) {
      throw new IllegalStateException("database not created");
    }
    byte[] bytes = databaseDelegate.readDatabase(databaseName);
    DbInputBytes in = new DbInputBytes(bytes);
    version = in.readUByte();
    if (!in.readStringUtf8().equals(databaseName)) {
      throw new IllegalStateException("database storaged with different name");
    }
    long entityCount = in.readLongCompressed();
    entitys = new DbTreeMap<String, DbEntity>();
    for (long n = 0; n < entityCount; n++) {
      String entityName = in.readStringUtf8();
      DbEntity entity = new DbEntity(this, entityName);
      entitys.put(entityName, entity);
    }
    if (!in.readEof()) {
      throw new IllegalStateException("expected eof");
    }
  }

  /**
   * Verifica se o banco existe.
   * 
   * @return existencia do banco
   * @throws IOException
   */
  public boolean exist() throws IOException {
    return databaseDelegate.hasDatabase(databaseName);
  }

  /**
   * Remove o banco de dados
   */
  public void drop() {
    droped = true;
  }

  /**
   * Cria uma entidade
   * 
   * @param entityName
   * @return entidade
   * @throws IOException
   */
  public DbEntity addEntity(String entityName) throws IOException {
    if (entitys != null && entitys.has(entityName)) {
      throw new IllegalArgumentException("entity already exist");
    }
    if (entitys == null) {
      entitys = new DbTreeMap<String, DbEntity>();
    }
    DbEntity entity = new DbEntity(this, entityName);
    entitys.put(entityName, entity);
    return entity;
  }

  /**
   * Retorna o indice da tabela
   * 
   * @param entityName nome da entidade
   * @return entidade
   */
  public DbEntity getEntity(String entityName) {
    DbEntity entity = entitys == null ? null : entitys.get(entityName);
    if (entity == null) {
      throw new IllegalArgumentException("entity not exist");
    }
    return entity;
  }

  /**
   * Verifica a existencia da entidade
   * 
   * @param entityName
   * @return existencia da entidade
   */
  public boolean hasEntity(String entityName) {
    return entitys == null ? false : entitys.has(entityName);
  }

  /**
   * Deleta uma entidade
   * 
   * @param entityName
   */
  public void delEntity(String entityName) {
  }

  /**
   * Persiste as modificações realizada no banco de dados.
   * 
   * @throws IOException
   */
  public void commit() throws IOException {
    {
      boolean hasDatabase = databaseDelegate.hasDatabase(databaseName);
      if (!hasDatabase && created && !droped) {
        DbOutputBytes out = new DbOutputBytes();
        out.writeUByte(version);
        out.writeStringUtf8(databaseName);
        int entityLength = entitys == null ? 0 : entitys.size();
        out.writeLongCompressed(entityLength);
        if (entityLength > 0) {
          DbTreeMap.Entry<String, DbEntity> entry = entitys.first();
          while (entry != null) {
            out.writeStringUtf8(entry.getKey());
            entry.getValue().commit();
            entry = entry.successor();
          }
        }
        databaseDelegate.writeDatabase(databaseName, out.getBytes());
      }
      else if (hasDatabase && droped) {
        databaseDelegate.dropDatabase(databaseName);
      }
    }
    {
      if (this.entitys != null) {
        DbTreeMap.Entry<String, DbEntity> entry = entitys.first();
        while (entry != null) {
          DbEntity entity = entry.getValue();
          entity.commit();
          entry = entry.successor();
        }
      }
    }
  }

  /**
   * Remove todas as modificações realizadas no banco de dados
   */
  public void rollback() {
    created = false;
    entitys = null;
  }

  /**
   * Delega a leitura e escrita para a classe que utilizar o {@link DbDatabase}.
   * 
   * @author bernardobreder
   */
  public static interface DbDatabaseIODelegator {

    /**
     * Realiza a leitura dos dados do banco de dados
     * 
     * @param name
     * @return bytes
     * @throws IOException
     */
    public byte[] readDatabase(String name) throws IOException;

    /**
     * Realiza a escrita dos dados do banco de dados
     * 
     * @param name
     * @param bytes
     * @throws IOException
     */
    public void writeDatabase(String name, byte[] bytes) throws IOException;

    /**
     * Verifica se o banco de dados existe
     * 
     * @param name
     * @return existencia do banco de dados
     * @throws IOException
     */
    public boolean hasDatabase(String name) throws IOException;

    /**
     * Verifica se o banco de dados existe
     * 
     * @param name
     * @throws IOException
     */
    public void dropDatabase(String name) throws IOException;

  }

}
