package database.node;

import java.util.ArrayList;
import java.util.List;

import database.vm.DbContext;

/**
 * 
 * 
 * @author Tecgraf
 */
public class ContextNode {

  /** Tabela */
  public final DbContext context;
  /** Tabela */
  public final List<TableEntry> tables = new ArrayList<TableEntry>();

  /**
   * Reinicia o contexto
   */
  public void reset() {
    this.tables.clear();
  }

  /**
   * @param context
   */
  public ContextNode(DbContext context) {
    this.context = context;
  }

  /**
   * @param alias
   * @param table
   */
  public void addTable(String alias, String table) {
    tables.add(new TableEntry(alias, table));
  }

  public static class TableEntry {

    public String alias;

    public String table;

    public TableEntry(String alias, String table) {
      super();
      this.alias = alias;
      this.table = table;
    }

  }

}