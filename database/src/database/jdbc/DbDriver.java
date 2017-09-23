package database.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import database.vm.DbContext;
import database.vm.DbContextFake;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbDriver implements Driver {

  static {
    try {
      DriverManager.registerDriver(new DbDriver());
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    String[] args = url.trim().split(":");
    if (args.length > 0 && args[0].equals("jdbc")) {
      if (args.length > 1 && args[1].equals("memory")) {
        DbContext context;
        if (args.length > 2 && args[2].equals("fake")) {
          context = new DbContextFake();
        }
        else {
          context = new DbContext();
        }
        return new DbConnection(url, info, context);
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean acceptsURL(String url) throws SQLException {
    String path = url.trim();
    if (path.startsWith("jdbc:")) {
      path = path.substring("jdbc:".length());
      if (path.equals("memory")) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
    throws SQLException {
    return new DriverPropertyInfo[] {};
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMajorVersion() {
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getMinorVersion() {
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean jdbcCompliant() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

}
