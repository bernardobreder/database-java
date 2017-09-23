package breder.db;

import java.io.File;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBDatabaseTest {

	@Before
	public void before() {
		new DBDatabase("test").drop();
		new File("db").delete();
	}

	@After
	public void after() {
		this.before();
	}

	@Test
	public void create() throws Exception {
		new DBDatabase("test").create();
		Assert.assertTrue(new File("db", "test").exists());
	}

	@Test(expected = IllegalStateException.class)
	public void failCreate() throws Exception {
		new DBDatabase("test").create().create();
	}

	@Test
	public void dropAndCreate() throws Exception {
		new DBDatabase("test").drop().create();
	}

	@Test
	public void createtable() throws Exception {
		Assert.assertNotNull(new DBDatabase("test").create().create("table1"));
	}

	@Test
	public void gettable() throws Exception {
		DBDatabase db = new DBDatabase("test").create();
		db.create("table1");
		Assert.assertNotNull(db.get("table1"));
	}

	@Test(expected = IllegalStateException.class)
	public void droptable() throws Exception {
		DBDatabase db = new DBDatabase("test").create();
		db.create("table1").drop();
		db.get("table1");
	}
	
	@Test
	public void open() throws Exception {
		new DBDatabase("test").create();
		new DBDatabase("test").open();		
	}

	@Test()
	public void insert() throws Exception {
		DBDatabase db = new DBDatabase("test").create();
		DBTable table = db.create("table1");
		int id = table.insert(new DBData().set("a", "1").set("b", "2"));
		db.commit();
	}

}
