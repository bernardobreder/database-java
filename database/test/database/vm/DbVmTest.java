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

public class DbVmTest {

  /**
   * @throws SyntaxException
   * @throws LexicalException
   * @throws DbRuntimeException
   * @throws IOException
   * 
   */
  @Test
  public void test() throws IOException, DbRuntimeException, LexicalException,
    SyntaxException {
    DbContext context = new DbContextFake();
    DbAssembly assembly =
      new DbCompiler().compile(context,
        "select p.id, p.lastname, p.firstname, t.number "
          + "from person p, telephone t "
          + "where p.id = 5 and t.person_id = p.id");
    assembly.disassembly(System.out);
    assembly.execute(context, new DataOutputStream(new TextVmOutputStream(
      System.out)));
  }

}
