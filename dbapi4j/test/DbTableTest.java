
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class DbTableTest extends DbTest {

	/**
	 * @throws IOException
	 */
	@Test
	public void testAdd() throws IOException {
		DbOutput out = new DbOutput(4);
		for (int p = 1; p <= PAGE_MAX; p++) {
			for (int m = 1; m <= SLOT_MAX; m++) {
				{
					DbTable map = new DbTable(this, "person", 1, m);
					for (int n = 1; n < ITEM_MAX; n++) {
						Assert.assertEquals(n, map.add(new DbInput(out.reset().writeUint32(n).toBytes())));
					}
					map.commit();
				}
				resetTest();
			}
		}
	}

	/**
	 * @throws IOException
	 */
	@Test
	public void testAddAndGet() throws IOException {
		for (int p = 1; p <= PAGE_MAX; p++) {
			for (int m = 1; m <= SLOT_MAX; m++) {
				{
					DbTable map = new DbTable(this, "person", 1, m);
					for (int n = 1; n < ITEM_MAX; n++) {
						Assert.assertEquals(n, map.add(new DbInput(new DbOutput(4).writeUint32(n).toBytes())));
					}
					map.commit();
				}
				{
					DbTable map = new DbTable(this, "person");
					for (int n = 1; n < ITEM_MAX; n++) {
						Assert.assertEquals(n, map.get(n).readUInt32());
					}
				}
				resetTest();
			}
		}
	}

}
