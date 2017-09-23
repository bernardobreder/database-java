package database.vm;


/**
 * Base de dados
 * 
 * @author Tecgraf
 */
public class DbContextFake extends DbContext {

  /**
   * Construtor
   */
  public DbContextFake() {
    this.person();
    this.telephone();
    this.friend();
  }

  /**
   * 
   */
  public void person() {
    DbTable table =
      this.createTable("person", new DbColumn("id"), new DbColumn("firstname"),
        new DbColumn("lastname"));
    add(table, 1, "Bernardo", "Breder");
    add(table, 2, "Raphael", "Breder");
    add(table, 3, "Julia", "Breder");
    add(table, 4, "Giovanni", "Breder");
    add(table, 5, "Vanda", "Breder");
    add(table, 6, "Chuck", "Breder");
  }

  /**
   * 
   */
  public void telephone() {
    DbTable table =
      this.createTable("telephone", new DbColumn("id"), new DbColumn(
        "person_id"), new DbColumn("number"));
    add(table, 1, 1, "11112222");
    add(table, 2, 2, "33334444");
    add(table, 3, 3, "55556666");
    add(table, 4, 4, "77778888");
    add(table, 5, 4, "99990000");
    add(table, 6, 5, "12123434");
    add(table, 7, 5, "21214343");
    add(table, 8, 6, "34341212");
    add(table, 9, 7, "43432121");
  }

  /**
   * 
   */
  public void friend() {
    DbTable table =
      this.createTable("friend", new DbColumn("id"), new DbColumn("parent_id"),
        new DbColumn("child_id"));
    add(table, 1, 1, 2);
    add(table, 2, 1, 3);
    add(table, 3, 1, 4);
    add(table, 4, 2, 3);
    add(table, 5, 2, 4);
    add(table, 6, 2, 6);
    add(table, 7, 3, 4);
    add(table, 8, 3, 6);
    add(table, 9, 4, 5);
    add(table, 10, 5, 6);
  }

  /**
   * @param table
   * @param id
   * @param objects
   */
  protected static void add(DbTable table, long id, Object... objects) {
    Object[] array = new Object[objects.length + 1];
    array[0] = Integer.valueOf((int) id);
    for (int n = 0; n < objects.length; n++) {
      Object item = objects[n];
      if (item instanceof Boolean) {
        Boolean value = (Boolean) item;
        item = value.booleanValue() ? Boolean.TRUE : Boolean.FALSE;
      }
      array[n + 1] = item;
    }
    table.data.put(id, array);
  }

}
