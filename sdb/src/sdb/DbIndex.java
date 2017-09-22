package sdb;

import java.io.IOException;

import sdb.DbIndexTree.DbIndexTreeNode;

/**
 * Indice de um banco de dados
 * 
 * @author bernardobreder
 */
public class DbIndex {

  /** Entidade */
  protected final DbEntity entity;
  /** Nome */
  protected final String name;
  /** Arvore B para os dados */
  protected DbIndexTree tree;
  /** Tabela que referencia */
  protected DbTable table;
  /** Colunas */
  protected String[] columns;

  /**
   * Construtor
   * 
   * @param entity
   * @param name
   * @param columns
   * @throws IOException
   */
  protected DbIndex(DbEntity entity, String name, String... columns)
    throws IOException {
    super();
    this.entity = entity;
    this.name = name;
    this.columns = columns;
    this.table = entity.table;
    String filename = entity.entityName + "$" + name;
    if (entity.database.indexTreeDelegator.hasStructure(filename)) {
      this.tree =
        DbIndexTree.readStructure(entity.database.indexTreeDelegator, filename);
    }
    else {
      this.tree =
        DbIndexTree.createStructure(entity.database.indexTreeDelegator, name,
          20001, columns.length);
      this.tree.structureChanged = true;
    }
  }

  /**
   * @param ids
   * @return valor
   * @throws IOException
   */
  public DbObject get(long... ids) throws IOException {
    long id = tree.get(ids);
    if (id == 0) {
      return null;
    }
    DbObject value = table.get(id);
    return value;
  }

  /**
   * Adiciona um indice
   * 
   * @param value
   * @param ids
   * @throws IOException
   */
  protected void add(long value, long... ids) throws IOException {
    tree.add(value, ids);
  }

  /**
   * Escreve as mudanças
   * 
   * @throws IOException
   */
  protected void commit() throws IOException {
    if (tree.structureChanged) {
      String filename = entity.entityName + "$" + name;
      DbOutputBytes out = new DbOutputBytes();
      tree.writeStructure(out);
      tree.io.writeStructure(filename, out.getBytes());
    }
    {
      commit(tree.root);
    }
  }

  /**
   * @param node
   * @throws IOException
   */
  private void commit(DbIndexTreeNode node) throws IOException {
    for (int m = 0; m <= node.length; m++) {
      if (node.childrenNode[m] != null) {
        commit(node.childrenNode[m]);
      }
    }
    if (node.changed) {
      String filename = entity.entityName + "$" + name;
      DbOutputBytes out = tree.writeNode(node);
      tree.io.writeNode(filename, node.id, out.getBytes());
      node.changed = false;
    }
  }

  /**
   * Retorna as mudanças
   */
  protected void rollback() {
    rollback(tree.root);
    if (tree.root.changed) {
      // TODO reiniciar o root
    }
  }

  /**
   * @param node
   */
  private void rollback(DbIndexTreeNode node) {
    for (int m = 0; m <= node.length; m++) {
      DbIndexTreeNode child = node.childrenNode[m];
      if (child != null && child.changed) {
        node.childrenNode[m] = null;
        rollback(child);
      }
    }
  }

}
