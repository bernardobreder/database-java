package sdb;

import org.junit.Assert;
import org.junit.Test;

public class DbInOutputBytesTest {

	/**
	 * Teste de long compressed
	 */
	@Test
	public void testReadWriteLongCompressed() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x7F);
			Assert.assertEquals(0x7F, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x3FFF);
			Assert.assertEquals(0x3FFF, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x3FFFF);
			Assert.assertEquals(0x3FFFF, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x1FFFFF);
			Assert.assertEquals(0x1FFFFF, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0xFFFFFFF);
			Assert.assertEquals(0xFFFFFFF, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x7FFFFFFFFl);
			Assert.assertEquals(0x7FFFFFFFFl, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(0x3FFFFFFFFFFl);
			Assert.assertEquals(0x3FFFFFFFFFFl, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongCompressed(DbOutputBytes.LONG_COMPRESSED_MAX);
			Assert.assertEquals(DbOutputBytes.LONG_COMPRESSED_MAX, new DbInputBytes(out.getBytes()).readLongCompressed());
		}
	}

	/**
	 * Teste de long
	 */
	@Test
	public void testReadWriteLong() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0x7F);
			Assert.assertEquals(0x7F, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(-1);
			Assert.assertEquals(-1, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(-10);
			Assert.assertEquals(-10, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(-0xFFFFFF);
			Assert.assertEquals(-0xFFFFFF, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0xFFFFFFl);
			Assert.assertEquals(0xFFFFFFl, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0xFFFFFFFFl);
			Assert.assertEquals(0xFFFFFFFFl, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0xFFFFFFFFFFl);
			Assert.assertEquals(0xFFFFFFFFFFl, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0xFFFFFFFFFFFFl);
			Assert.assertEquals(0xFFFFFFFFFFFFl, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(0xFFFFFFFFFFFFFFl);
			Assert.assertEquals(0xFFFFFFFFFFFFFFl, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(DbOutputBytes.LONG_COMPRESSED_MAX);
			Assert.assertEquals(DbOutputBytes.LONG_COMPRESSED_MAX, new DbInputBytes(out.getBytes()).readLong());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLong(Long.MAX_VALUE);
			Assert.assertEquals(Long.MAX_VALUE, new DbInputBytes(out.getBytes()).readLong());
		}
	}

	/**
	 * Teste de inteiro
	 */
	@Test
	public void testReadWriteInt() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeInt(0x7F);
			Assert.assertEquals(0x7F, new DbInputBytes(out.getBytes()).readInt());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeInt(Integer.MAX_VALUE);
			Assert.assertEquals(Integer.MAX_VALUE, new DbInputBytes(out.getBytes()).readInt());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeInt(-1);
			Assert.assertEquals(-1, new DbInputBytes(out.getBytes()).readInt());
		}
	}

	/**
	 * Teste de string11AZ
	 */
	@Test
	public void testReadWriteString11AZ_$() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("A");
			Assert.assertEquals("A", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("AB");
			Assert.assertEquals("AB", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABC");
			Assert.assertEquals("ABC", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCD");
			Assert.assertEquals("ABCD", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDE");
			Assert.assertEquals("ABCDE", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEF");
			Assert.assertEquals("ABCDEF", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEFG");
			Assert.assertEquals("ABCDEFG", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEFGH");
			Assert.assertEquals("ABCDEFGH", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEFGHI");
			Assert.assertEquals("ABCDEFGHI", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEFGHIJ");
			Assert.assertEquals("ABCDEFGHIJ", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("ABCDEFGHIJK");
			Assert.assertEquals("ABCDEFGHIJK", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("AbCdEfGhIjK");
			Assert.assertEquals("ABCDEFGHIJK", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeString11AZ_$("A_B$C");
			Assert.assertEquals("A_B$C", new DbInputBytes(out.getBytes()).readString11AZ_$());
		}
	}

	/**
	 * Teste de array de long
	 */
	@Test
	public void testReadWriteLongArray() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeLongArray(new Long[] { -2l, -1l, 0l, 1l, 2l, 3l });
			Assert.assertArrayEquals(new Long[] { -2l, -1l, 0l, 1l, 2l, 3l }, new DbInputBytes(out.getBytes()).readLongArray());
		}
	}

	/**
	 * Teste de array de long
	 */
	@Test
	public void testReadWriteStringUtf8() {
		{
			DbOutputBytes out = new DbOutputBytes(32);
			out.writeStringUtf8("ação");
			Assert.assertEquals("ação", new DbInputBytes(out.getBytes()).readStringUtf8());
		}
	}

}
