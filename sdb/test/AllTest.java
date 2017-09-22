import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import sdb.DbDatabaseTest;
import sdb.DbInOutputBytesTest;
import sdb.DbIndexTreeTest;
import sdb.DbTableTreeTest;
import sdb.DbTreeTest;
import simple.BXTreeTest;

/**
 * 
 * 
 * @author Tecgraf/PUC-Rio
 */
@RunWith(Suite.class)
@SuiteClasses({ /*
				 * DbTreeTest.class, DbInOutputBytesTest.class,
				 * DbDatabaseTest.class, DbIndexTreeTest.class,
				 * DbTableTreeTest.class
				 */BXTreeTest.class })
public class AllTest {

}
