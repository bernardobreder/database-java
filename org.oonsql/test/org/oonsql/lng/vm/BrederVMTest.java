package org.oonsql.lng.vm;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.oonsql.lng.util.StreamOpcodeInputStream;
import org.oonsql.lng.util.StreamOpcodeOutputStream;

public class BrederVMTest {

  ByteArrayOutputStream bytes;

  StreamOpcodeOutputStream opcodes;

  @Before
  public void before() {
    bytes = new ByteArrayOutputStream();
    opcodes = new StreamOpcodeOutputStream(bytes);
  }

  @Test
  public void testHalf() throws IOException {
    opcodes.opControlHalf();
    Assert.assertNull(ex());
  }

  @Test
  public void testStackString() throws IOException {
    opcodes.opStackString("ação");
    opcodes.opControlHalf();
    Assert.assertEquals("ação", ex());
  }

  @Test
  public void testStackNumber() throws IOException {
    opcodes.opStackDouble(12.34);
    opcodes.opControlHalf();
    Assert.assertEquals(12.34, ex());
  }

  @Test
  public void testStackTrue() throws IOException {
    opcodes.opStackTrue();
    opcodes.opControlHalf();
    Assert.assertEquals(true, ex());
  }

  @Test
  public void testStackFalse() throws IOException {
    opcodes.opStackFalse();
    opcodes.opControlHalf();
    Assert.assertEquals(false, ex());
  }

  @Test
  public void testDoubleSum() throws IOException {
    opcodes.opStackDouble(1.2);
    opcodes.opStackDouble(3.4);
    opcodes.opDoubleSum();
    opcodes.opControlHalf();
    Assert.assertEquals(4.6, ex());
  }

  @Test
  public void testDoubleSub() throws IOException {
    opcodes.opStackDouble(1.2);
    opcodes.opStackDouble(3.4);
    opcodes.opDoubleSub();
    opcodes.opControlHalf();
    Assert.assertEquals(-2.2, ex());
  }

  @Test
  public void testDoubleMul() throws IOException {
    opcodes.opStackDouble(1.2);
    opcodes.opStackDouble(3.4);
    opcodes.opDoubleMul();
    opcodes.opControlHalf();
    Assert.assertEquals(4.08, ex());
  }

  @Test
  public void testDoubleDiv() throws IOException {
    opcodes.opStackDouble(1.2);
    opcodes.opStackDouble(2);
    opcodes.opDoubleDiv();
    opcodes.opControlHalf();
    Assert.assertEquals(0.6, ex());
  }

  @Test
  public void testStringSum() throws IOException {
    opcodes.opStackString("aç");
    opcodes.opStackString("ão");
    opcodes.opStringSum();
    opcodes.opControlHalf();
    Assert.assertEquals("ação", ex());
  }

  @Test
  public void testBooleanAnd() throws IOException {
    opcodes.opStackTrue();
    opcodes.opStackFalse();
    opcodes.opBooleanAnd();
    opcodes.opControlHalf();
    Assert.assertEquals(false, ex());
  }

  @Test
  public void testBooleanOr() throws IOException {
    opcodes.opStackTrue();
    opcodes.opStackFalse();
    opcodes.opBooleanOr();
    opcodes.opControlHalf();
    Assert.assertEquals(true, ex());
  }

  @Test
  public void testBooleanNotFalse() throws IOException {
    opcodes.opStackFalse();
    opcodes.opBooleanNot();
    opcodes.opControlHalf();
    Assert.assertEquals(true, ex());
  }

  @Test
  public void testBooleanNotTrue() throws IOException {
    opcodes.opStackTrue();
    opcodes.opBooleanNot();
    opcodes.opControlHalf();
    Assert.assertEquals(false, ex());
  }

  /**
   * @return objeto
   * @throws IOException
   */
  private Object ex() throws IOException {
    return new OonsqlVM().execute(new StreamOpcodeInputStream(
      new BufferedInputStream(new ByteArrayInputStream(bytes.toByteArray()))));
  }

}
