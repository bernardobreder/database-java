package database.vm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import database.util.SimpleTreeMap;

/**
 * Banco de Dados
 * 
 * @author Tecgraf
 */
public class DbContext {

  /** Sequence de estrutura */
  private long sequence = 1;
  /** Tabelas */
  private TreeMap<String, DbTable> tables = new TreeMap<String, DbTable>();

  /**
   * @param name
   * @return tabela
   */
  public DbTable getTable(String name) {
    return this.tables.get(name);
  }

  /**
   * @param name
   * @param columns
   * @return mapa
   */
  public DbTable createTable(String name, DbColumn... columns) {
    List<DbColumn> columnList = new ArrayList<DbColumn>(Arrays.asList(columns));
    SimpleTreeMap<Object> data = new SimpleTreeMap<Object>();
    this.tables.put(name, new DbTable(columnList, data));
    return this.tables.get(name);
  }

  /**
   * @return tabelas
   */
  public Collection<DbTable> getTables() {
    return tables.values();
  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class DbTable {

    /** Cabeçalho */
    public List<DbColumn> head;
    /** Conteúdo */
    public SimpleTreeMap<Object> data;

    /**
     * @param head
     * @param data
     */
    public DbTable(List<DbColumn> head, SimpleTreeMap<Object> data) {
      super();
      this.head = head;
      this.data = data;
    }

  }

  /**
   * 
   * 
   * @author Tecgraf
   */
  public static class DbColumn {

    /** Nome */
    public String name;
    /** Nome */
    public String table;

    /**
     * @param name
     */
    public DbColumn(String name) {
      super();
      this.name = name;
      this.table = name;
    }

    /**
     * @param name
     * @param table
     */
    public DbColumn(String name, String table) {
      super();
      this.name = name;
      this.table = table;
    }

  }

}
