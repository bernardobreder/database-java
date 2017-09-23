package database;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import database.jdbc.DbConnectionTest;
import database.syntax.DbSyntaxStreamTest;
import database.vm.DbCompilerTest;
import database.vm.DbVmTest;

/**
 * 
 * 
 * @author Tecgraf
 */
@RunWith(Suite.class)
@SuiteClasses({ LexicalStreamTest.class, DbSyntaxStreamTest.class,
    DbCompilerTest.class, DbVmTest.class, DbConnectionTest.class })
public class AllTests {

}
