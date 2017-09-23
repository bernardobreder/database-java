package database.junit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;

/**
 * Classe utilitária para assertivas de banco de dados
 * 
 * @author Tecgraf
 */
public class DbAssert {

  /**
   * @param c
   * @param sql
   * @param args
   * @return array
   * @throws SQLException
   */
  public static Object[][] execute(Connection c, String sql, Object... args)
    throws SQLException {
    PreparedStatement ps = c.prepareStatement(sql);
    for (int n = 0; n < args.length; n++) {
      ps.setObject(n + 1, args[n]);
    }
    List<Object[]> list = new ArrayList<Object[]>();
    ResultSet rs = ps.executeQuery();
    ResultSetMetaData metaData = rs.getMetaData();
    if (metaData == null) {
      return new Object[0][0];
    }
    int columnCount = metaData.getColumnCount();
    list.add(new Object[columnCount]);
    list.add(new Object[columnCount]);
    while (rs.next()) {
      Object[] row = new Object[columnCount];
      for (int n = 0; n < columnCount; n++) {
        row[n] = rs.getObject(n + 1);
      }
      list.add(row);
    }
    rs.close();
    ps.close();
    return list.toArray(new Object[list.size()][columnCount]);
  }

  /**
   * Verifica os nomes das colunas da tabela
   * 
   * @param table tabela
   * @param names nomes das colunas
   */
  public static void assertColumnName(Object[][] table, String... names) {
    int length = names.length;
    Object[] objects = table[0];
    if (objects.length == length) {
      boolean found = true;
      for (int m = 0; m < length; m++) {
        if (!objects[m].toString().toUpperCase().equals(names[m].toUpperCase())) {
          found = false;
          break;
        }
      }
      if (found) {
        return;
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("expected:\n\t");
    sb.append(Arrays.toString(names));
    sb.append("\nin:\n");
    sb.append('\t');
    sb.append(Arrays.toString(table[0]));
    Assert.fail(sb.toString());
  }

  /**
   * Verifica os tipos das colunas da tabela
   * 
   * @param table tabela
   * @param types tipos das colunas
   */
  public static void assertColumnType(Object[][] table, String... types) {
    int length = types.length;
    Object[] objects = table[1];
    if (objects.length == length) {
      boolean found = true;
      for (int m = 0; m < length; m++) {
        if (!objects[m].toString().toUpperCase().equals(types[m].toUpperCase())) {
          found = false;
          break;
        }
      }
      if (found) {
        return;
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("expected:\n\t");
    sb.append(Arrays.toString(types));
    sb.append("\nin:\n");
    sb.append('\t');
    sb.append(Arrays.toString(table[1]));
    Assert.fail(sb.toString());
  }

  /**
   * Verifica a quantidade de dados na tabela
   * 
   * @param table tabela
   * @param expectedCount quantidade de dados
   */
  public static void assertDataCount(Object[][] table, int expectedCount) {
    Assert.assertEquals(expectedCount, table.length - 2);
  }

  /**
   * Verifica se existe uma tupla na tabela
   * 
   * @param table tabela
   * @param row tupla na forma de array
   */
  public static void assertData(Object[][] table, Object... row) {
    int length = row.length;
    for (int n = 2; n < table.length; n++) {
      Object[] objects = table[n];
      if (objects.length == length) {
        boolean found = true;
        for (int m = 0; m < length; m++) {
          Object object = objects[m];
          Object obj = row[m];
          if ((object == null && obj != null)
            || (object != null && obj == null)
            || (object != null && obj != null && !object.equals(obj))) {
            found = false;
            break;
          }
        }
        if (found) {
          return;
        }
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("expected:\n\t");
    sb.append(Arrays.toString(row));
    sb.append("\nin:\n");
    for (int n = 2; n < table.length; n++) {
      sb.append('\t');
      sb.append(Arrays.toString(table[n]));
      sb.append('\n');
    }
    sb.deleteCharAt(sb.length() - 1);
    Assert.fail(sb.toString());
  }

  /**
   * Verifica se existe uma tupla na tabela
   * 
   * @param row tupla na forma de array
   * @param data
   */
  public static void assertData(Object[] row, Object... data) {
    int length = row.length;
    if (data.length == length) {
      boolean found = true;
      for (int m = 0; m < length; m++) {
        Object object = data[m];
        Object obj = row[m];
        if ((object == null && obj != null) || (object != null && obj == null)
          || (object != null && obj != null && !object.equals(obj))) {
          found = false;
          break;
        }
      }
      if (found) {
        return;
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append("expected:\n\t");
    sb.append(Arrays.toString(row));
    sb.append("\nin:\n");
    sb.append(Arrays.toString(data));
    Assert.fail(sb.toString());
  }

}
