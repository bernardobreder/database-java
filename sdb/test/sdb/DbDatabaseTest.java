package sdb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import sdb.DbDatabase.DbDatabaseIODelegator;
import sdb.DbEntity.DbEntityIODelegator;
import sdb.DbIndexTree.DbIndexTreeIODelegator;
import sdb.DbTableTree.DbTableTreeIODelegator;

public class DbDatabaseTest implements DbDatabaseIODelegator,
  DbEntityIODelegator, DbTableTreeIODelegator, DbIndexTreeIODelegator {

  @Test(expected = IllegalStateException.class)
  public void openNoDatabase() throws IOException {
    new DbDatabase(this, this, this, this, "test").open();
  }

  @Test(expected = IllegalStateException.class)
  public void openDatabaseNotCommited() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.create();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
    }
  }

  @Test
  public void createDatabaseAndCommit() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertFalse(hasDatabase("test"));
      database.create();
      Assert.assertFalse(hasDatabase("test"));
      database.commit();
      Assert.assertTrue(hasDatabase("test"));
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertTrue(hasDatabase("test"));
      database.open();
      Assert.assertTrue(hasDatabase("test"));
    }
  }

  @Test
  public void openDatabaseAndRollback() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertFalse(hasDatabase("test"));
      database.create();
      Assert.assertFalse(hasDatabase("test"));
      database.rollback();
      Assert.assertFalse(hasDatabase("test"));
    }
  }

  @Test
  public void createDatabaseAndCommitAndDropAndCommit() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertFalse(hasDatabase("test"));
      database.create();
      Assert.assertFalse(hasDatabase("test"));
      database.commit();
      Assert.assertTrue(hasDatabase("test"));
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertTrue(hasDatabase("test"));
      database.drop();
      Assert.assertTrue(hasDatabase("test"));
      database.commit();
      Assert.assertFalse(hasDatabase("test"));
    }
  }

  @Test
  public void openDatabaseAndCommitAndDropAndRollback() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertFalse(hasDatabase("test"));
      database.create();
      Assert.assertFalse(hasDatabase("test"));
      database.commit();
      Assert.assertTrue(hasDatabase("test"));
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      Assert.assertTrue(hasDatabase("test"));
      database.drop();
      Assert.assertTrue(hasDatabase("test"));
      database.rollback();
      Assert.assertTrue(hasDatabase("test"));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void createEntityAlreadyExist() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.create();
      database.addEntity("person");
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      database.addEntity("person");
      database.commit();
    }
  }

  @Test
  public void createEntity() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.create();
      database.addEntity("person");
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      Assert.assertEquals(1l, personEntity.addData(new DbObject().putAsString(
        "name", "Bernardo")));
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      DbObject data = personEntity.getData(1);
      Assert.assertNotNull(data);
      Assert.assertEquals("Bernardo", data.getAsString("name"));
    }
  }

  @Test
  public void createEntityStress() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.create();
      database.addEntity("person");
      database.commit();
    }
    int max = 2 * 1024;
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      for (int n = 1; n <= max; n++) {
        Assert.assertEquals(n, personEntity.addData(new DbObject().putAsString(
          "name", Integer.toHexString(n))));
      }
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      for (int n = 1; n <= max; n++) {
        DbObject data = personEntity.getData(n);
        Assert.assertNotNull(data);
        Assert.assertEquals(Integer.toHexString(n), data.getAsString("name"));
      }
    }
  }

  @Test
  public void createIndex() throws IOException {
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.create();
      DbEntity personEntry = database.addEntity("person");
      personEntry.addIndex("cpf", "cpf");
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      Assert.assertEquals(1l, personEntity.addData(new DbObject().putAsString(
        "name", "Bernardo").putAsInteger("cpf", 123456789)));
      database.commit();
    }
    {
      DbDatabase database = new DbDatabase(this, this, this, this, "test");
      database.open();
      DbEntity personEntity = database.getEntity("person");
      DbIndex personCpfIndex = personEntity.getIndex("cpf");
      DbObject data = personCpfIndex.get(123456789l);
      Assert.assertNotNull(data);
      Assert.assertEquals("Bernardo", data.getAsString("name"));
      Assert.assertEquals((Object) 123456789, data.getAsInteger("cpf"));
    }
  }

  /** Mapa de Bytes */
  private Map<String, byte[]> cache = new HashMap<String, byte[]>();

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readNode(String name, long id) throws IOException {
    return cache.get(name + ".body." + id + ".db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeNode(String name, long id, byte[] bytes) throws IOException {
    cache.put(name + ".body." + id + ".db", bytes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readStructure(String name) throws IOException {
    return cache.get(name + ".head.db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeStructure(String name, byte[] bytes) throws IOException {
    cache.put(name + ".head.db", bytes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasStructure(String name) throws IOException {
    return cache.containsKey(name + ".head.db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readEntity(String databaseName, String entityName)
    throws IOException {
    return cache.get(databaseName + "." + entityName + ".entity.db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeEntity(String databaseName, String entityName, byte[] bytes)
    throws IOException {
    cache.put(databaseName + "." + entityName + ".entity.db", bytes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasEntity(String databaseName, String entityName)
    throws IOException {
    return cache.containsKey(databaseName + "." + entityName + ".entity.db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte[] readDatabase(String name) throws IOException {
    return cache.get(name + ".db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeDatabase(String name, byte[] bytes) throws IOException {
    cache.put(name + ".db", bytes);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasDatabase(String name) throws IOException {
    return cache.containsKey(name + ".db");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dropDatabase(String name) throws IOException {
    cache.remove(name + ".db");
  }

}
