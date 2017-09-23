package database.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import database.vm.DbContext;

/**
 * Connection
 * 
 * @author Tecgraf
 */
public class DbConnection implements Connection {

  /** Url */
  private String url;
  /** Propriedade */
  private Properties info;
  /** Contexto */
  private DbContext context;
  /** Client Info */
  private Properties clientInfo;
  /** Save Point */
  private Map<String, Savepoint> savepointMap =
    new HashMap<String, Savepoint>();
  /** Read only */
  private boolean readOnly;
  /** Meta Data */
  private DatabaseMetaData metaData;
  /** Closed */
  private boolean closed;
  /** Auto Commit */
  private boolean autoCommit;

  /**
   * @param url
   * @param info
   * @param context
   */
  public DbConnection(String url, Properties info, DbContext context) {
    this.url = url;
    this.info = info;
    this.context = context;
  }

  /**
   * @return contexto
   */
  public DbContext getContext() {
    return this.context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Statement createStatement() throws SQLException {
    return new DbStatement();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return new DbPreparedStatement(this, sql);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CallableStatement prepareCall(String sql) throws SQLException {
    return new DbCallableStatement(sql);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String nativeSQL(String sql) throws SQLException {
    return sql;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAutoCommit(boolean autoCommit) throws SQLException {
    this.autoCommit = autoCommit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean getAutoCommit() throws SQLException {
    return this.autoCommit;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void commit() throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void rollback() throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws SQLException {
    this.closed = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isClosed() throws SQLException {
    return closed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DatabaseMetaData getMetaData() throws SQLException {
    return this.metaData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setReadOnly(boolean readOnly) throws SQLException {
    this.readOnly = readOnly;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isReadOnly() throws SQLException {
    return this.readOnly;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCatalog(String catalog) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCatalog() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTransactionIsolation(int level) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getTransactionIsolation() throws SQLException {
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLWarning getWarnings() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clearWarnings() throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency)
    throws SQLException {
    return new DbStatement(resultSetType, resultSetConcurrency);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType,
    int resultSetConcurrency) throws SQLException {
    return new DbPreparedStatement(this, sql, resultSetType,
      resultSetConcurrency);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType,
    int resultSetConcurrency) throws SQLException {
    return new DbCallableStatement(sql, resultSetType, resultSetConcurrency);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHoldability(int holdability) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getHoldability() throws SQLException {
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Savepoint setSavepoint() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Savepoint setSavepoint(String name) throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void rollback(Savepoint savepoint) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    this.savepointMap.remove(savepoint.getSavepointName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Statement createStatement(int resultSetType, int resultSetConcurrency,
    int resultSetHoldability) throws SQLException {
    return new DbStatement(resultSetType, resultSetConcurrency,
      resultSetHoldability);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int resultSetType,
    int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new DbPreparedStatement(this, sql, resultSetType,
      resultSetConcurrency, resultSetHoldability);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CallableStatement prepareCall(String sql, int resultSetType,
    int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new DbCallableStatement(sql, resultSetType, resultSetConcurrency,
      resultSetHoldability);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
    throws SQLException {
    return new DbPreparedStatement(this, sql, autoGeneratedKeys);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
    throws SQLException {
    return new DbPreparedStatement(this, sql, columnIndexes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreparedStatement prepareStatement(String sql, String[] columnNames)
    throws SQLException {
    return new DbPreparedStatement(this, sql, columnNames);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Clob createClob() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Blob createBlob() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NClob createNClob() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SQLXML createSQLXML() throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(int timeout) throws SQLException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setClientInfo(String name, String value)
    throws SQLClientInfoException {
    this.clientInfo.setProperty(name, value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setClientInfo(Properties properties)
    throws SQLClientInfoException {
    this.clientInfo = properties;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getClientInfo(String name) throws SQLException {
    return this.clientInfo.getProperty(name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Properties getClientInfo() throws SQLException {
    return this.clientInfo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Array createArrayOf(String typeName, Object[] elements)
    throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Struct createStruct(String typeName, Object[] attributes)
    throws SQLException {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSchema(String schema) throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSchema() throws SQLException {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void abort(Executor executor) throws SQLException {
    this.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setNetworkTimeout(Executor executor, int milliseconds)
    throws SQLException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNetworkTimeout() throws SQLException {
    return -1;
  }

}
