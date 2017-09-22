package org.oonsql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.oonsql.lng.lexical.LexicalStreamTest;
import org.oonsql.lng.syntax.SyntaxStreamTest;
import org.oonsql.lng.util.OpcodeStreamTest;
import org.oonsql.lng.vm.BrederVMTest;

/**
 * Testador
 * 
 * @author bbreder
 */
@RunWith(Suite.class)
@SuiteClasses({ LexicalStreamTest.class, SyntaxStreamTest.class,
    OpcodeStreamTest.class, BrederVMTest.class })
public class AllTest {

}
