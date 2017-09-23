package database.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Test;

import database.junit.DbAssert;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbConnectionTest {

  static {
    try {
      Class.forName(DbDriver.class.getName());
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * @throws SQLException
   */
  @Test
  public void test1() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table = DbAssert.execute(c, "select 1 from person");
    DbAssert.assertDataCount(table, 6);
    DbAssert.assertData(table[2], 1);
    DbAssert.assertData(table[3], 1);
    DbAssert.assertData(table[4], 1);
    DbAssert.assertData(table[5], 1);
    DbAssert.assertData(table[6], 1);
    DbAssert.assertData(table[7], 1);
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testAllPerson() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table = DbAssert.execute(c, "select * from person");
    DbAssert.assertDataCount(table, 6);
    DbAssert.assertData(table, 1, "Bernardo", "Breder");
    DbAssert.assertData(table, 2, "Raphael", "Breder");
    DbAssert.assertData(table, 3, "Julia", "Breder");
    DbAssert.assertData(table, 4, "Giovanni", "Breder");
    DbAssert.assertData(table, 5, "Vanda", "Breder");
    DbAssert.assertData(table, 6, "Chuck", "Breder");
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testIdPerson() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table = DbAssert.execute(c, "select p.id from person p");
    DbAssert.assertDataCount(table, 6);
    DbAssert.assertData(table, 1);
    DbAssert.assertData(table, 2);
    DbAssert.assertData(table, 3);
    DbAssert.assertData(table, 4);
    DbAssert.assertData(table, 5);
    DbAssert.assertData(table, 6);
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testAllTelephone() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table = DbAssert.execute(c, "select * from telephone");
    DbAssert.assertDataCount(table, 9);
    DbAssert.assertData(table, 1, 1, "11112222");
    DbAssert.assertData(table, 2, 2, "33334444");
    DbAssert.assertData(table, 3, 3, "55556666");
    DbAssert.assertData(table, 4, 4, "77778888");
    DbAssert.assertData(table, 5, 4, "99990000");
    DbAssert.assertData(table, 6, 5, "12123434");
    DbAssert.assertData(table, 7, 5, "21214343");
    DbAssert.assertData(table, 8, 6, "34341212");
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testAllFriend() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table = DbAssert.execute(c, "select * from friend");
    DbAssert.assertData(table, 1, 1, 2);
    DbAssert.assertData(table, 2, 1, 3);
    DbAssert.assertData(table, 3, 1, 4);
    DbAssert.assertData(table, 4, 2, 3);
    DbAssert.assertData(table, 5, 2, 4);
    DbAssert.assertData(table, 6, 2, 6);
    DbAssert.assertData(table, 7, 3, 4);
    DbAssert.assertData(table, 8, 3, 6);
    DbAssert.assertData(table, 9, 4, 5);
    DbAssert.assertData(table, 10, 5, 6);
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testPersonTelephone() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table =
      DbAssert.execute(c, "select p.id, p.firstname, p.lastname, t.number "
        + "from person p, telephone t " + "where t.person_id = p.id");
    DbAssert.assertData(table, 1, "Bernardo", "Breder", "11112222");
    DbAssert.assertData(table, 2, "Raphael", "Breder", "33334444");
    DbAssert.assertData(table, 3, "Julia", "Breder", "55556666");
    DbAssert.assertData(table, 4, "Giovanni", "Breder", "77778888");
    DbAssert.assertData(table, 4, "Giovanni", "Breder", "99990000");
    DbAssert.assertData(table, 5, "Vanda", "Breder", "12123434");
    DbAssert.assertData(table, 5, "Vanda", "Breder", "21214343");
    DbAssert.assertData(table, 6, "Chuck", "Breder", "34341212");
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testPersonFriendTelephone() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table =
      DbAssert
        .execute(
          c,
          "select p.id, p.firstname, pf.firstname, t.number "
            + "from person p, friend f, person pf, telephone t "
            + "where t.person_id = pf.id and f.parent_id = p.id and f.child_id = pf.id and p.id != pf.id");
    DbAssert.assertData(table, 1, "Bernardo", "Raphael", "33334444");
    DbAssert.assertData(table, 1, "Bernardo", "Julia", "55556666");
    DbAssert.assertData(table, 1, "Bernardo", "Giovanni", "77778888");
    DbAssert.assertData(table, 1, "Bernardo", "Giovanni", "99990000");
    DbAssert.assertData(table, 2, "Raphael", "Julia", "55556666");
    DbAssert.assertData(table, 2, "Raphael", "Giovanni", "77778888");
    DbAssert.assertData(table, 2, "Raphael", "Giovanni", "99990000");
    DbAssert.assertData(table, 2, "Raphael", "Chuck", "34341212");
    DbAssert.assertData(table, 3, "Julia", "Giovanni", "77778888");
    DbAssert.assertData(table, 3, "Julia", "Giovanni", "99990000");
    DbAssert.assertData(table, 3, "Julia", "Chuck", "34341212");
    DbAssert.assertData(table, 4, "Giovanni", "Vanda", "12123434");
    DbAssert.assertData(table, 4, "Giovanni", "Vanda", "21214343");
    DbAssert.assertData(table, 5, "Vanda", "Chuck", "34341212");
  }

  /**
   * @throws SQLException
   */
  @Test
  public void testPersonNotBernardo() throws SQLException {
    Connection c = DriverManager.getConnection("jdbc:memory:fake");
    Object[][] table =
      DbAssert.execute(c, "select * " + "from person p " + "where p.id != 1");
    DbAssert.assertDataCount(table, 5);
    DbAssert.assertData(table, 2, "Raphael", "Breder");
    DbAssert.assertData(table, 3, "Julia", "Breder");
    DbAssert.assertData(table, 4, "Giovanni", "Breder");
    DbAssert.assertData(table, 5, "Vanda", "Breder");
    DbAssert.assertData(table, 6, "Chuck", "Breder");
  }

}
