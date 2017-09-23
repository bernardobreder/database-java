package database.compile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import database.lexical.DbLexicalStream;
import database.lexical.LexicalStream.LexicalException;
import database.node.ContextNode;
import database.node.Node;
import database.syntax.AbstractSyntaxStream.SyntaxException;
import database.syntax.DbSyntaxStream;
import database.util.EmptyOutputStream;
import database.util.StringInputStream;
import database.vm.DbContext;
import database.vm.DbOpcodeOutputStream;

/**
 * Classe que realiza a compilação de uma consulta
 * 
 * @author Tecgraf
 */
public class DbCompiler {

  /**
   * @param context
   * @param sql
   * @return código assembly do sql
   * @throws LexicalException
   * @throws IOException
   * @throws SyntaxException
   */
  public DbAssembly compile(DbContext context, String sql) throws IOException,
    LexicalException, SyntaxException {
    StringInputStream stringStream = new StringInputStream(sql);
    DbLexicalStream lexicalStream = new DbLexicalStream(stringStream);
    DbSyntaxStream syntaxStream = new DbSyntaxStream(lexicalStream);
    Node read = syntaxStream.read();
    ContextNode contextNode = new ContextNode(context);
    contextNode.reset();
    read.head(contextNode);
    contextNode.reset();
    read.body(contextNode);
    read.build(new DbOpcodeOutputStream(new EmptyOutputStream()));
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DbOpcodeOutputStream opOut = new DbOpcodeOutputStream(out);
    read.build(opOut);
    opOut.writeHalf();
    return new DbAssembly(out.toByteArray());
  }

}
