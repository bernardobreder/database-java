package org.oonsql.lng.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.oonsql.lng.vm.OonsqlVM;

public class AssemblyOpcodeOutputStreamTest {

  ByteArrayOutputStream bytes;

  AssemblyOpcodeOutputStream opcodes;

  @Before
  public void before() {
    bytes = new ByteArrayOutputStream();
    opcodes = new AssemblyOpcodeOutputStream(bytes);
  }

  @Test
  public void testStackTrue() throws IOException {
    opcodes.write("stack.true");
    opcodes.write("half");
    Assert.assertEquals(true, ex());
  }

  @Test
  public void testStackFalse() throws IOException {
    opcodes.write("stack.false");
    opcodes.write("half");
    Assert.assertEquals(false, ex());
  }

  @Test
  public void testStackString() throws IOException {
    opcodes.write("stack.string \"ação\"");
    opcodes.write("half");
    Assert.assertEquals("ação", ex());
  }

  private Object ex() throws IOException {
    opcodes.close();
    return new OonsqlVM().execute(new StreamOpcodeInputStream(
      new ByteArrayInputStream(bytes.toByteArray())));
  }
}
