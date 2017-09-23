package database.vm;

import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

import database.compile.DbAssembly;
import database.compile.DbCompiler;
import database.lexical.LexicalStream.LexicalException;
import database.syntax.AbstractSyntaxStream.SyntaxException;
import database.vm.DbVm.DbRuntimeException;
import database.vm.out.TextVmOutputStream;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbCompilerTest {

  /**
   * @throws IOException
   * @throws LexicalException
   * @throws SyntaxException
   * @throws DbRuntimeException
   */
  @Test
  public void test() throws IOException, LexicalException, SyntaxException,
    DbRuntimeException {
    DbContext context = new DbContextFake();
    context.createTable("a");
    DbAssembly assembly =
      new DbCompiler().compile(context, "select * from friend");
    assembly.disassembly(System.out);
    assembly.execute(context, new DataOutputStream(new TextVmOutputStream(
      System.out)));
  }

}
