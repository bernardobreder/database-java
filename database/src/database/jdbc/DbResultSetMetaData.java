package database.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbResultSetMetaData implements ResultSetMetaData {

  /** Estrutura */
  private DbResultSet dbResultSet;
  private Object[] row;

  /**
   * @param dbResultSet
   * @param row
   */
  public DbResultSetMetaData(DbResultSet dbResultSet, Object[] row) {
    this.dbResultSet = dbResultSet;
    this.row = row;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getColumnCount() throws SQLException {
    return this.row.length;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSearchable(int column) throws SQLException {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCurrency(int column) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int isNullable(int column) throws SQLException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSigned(int column) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnLabel(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSchemaName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPrecision(int column) throws SQLException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getScale(int column) throws SQLException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTableName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCatalogName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getColumnType(int column) throws SQLException {
    // TODO Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnTypeName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isReadOnly(int column) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWritable(int column) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getColumnClassName(int column) throws SQLException {
    // TODO Auto-generated method stub
    return null;
  }

}
