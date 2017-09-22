import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author bernardobreder_local
 */
@RunWith(Suite.class)
@SuiteClasses({ DbTableTest.class, DbIndexTest.class })
public class AllTest {

}
