import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

public class DbIndexTest extends DbTest {

	/** Iteração */
	private static final int PAGE_MAX = 32;
	/** Iteração */
	private static final int SLOT_MAX = 32;
	/** Iteração */
	private static final int ITEM_MAX = 16 * 1024;

	/**
	 * @throws IOException
	 */
	@Test
	public void testAdd() throws IOException {
		for (int p = 1; p <= PAGE_MAX; p++) {
			for (int m = 1; m <= SLOT_MAX; m++) {
				{
					DbIndex map = new DbIndex(this, "person", 1, m);
					for (int n = 1; n < ITEM_MAX; n++) {
						map.add(n, n);
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
					DbIndex map = new DbIndex(this, "person", 1, m);
					for (int n = 1; n < ITEM_MAX; n++) {
						map.add(n, n);
					}
					map.commit();
				}
				{
					DbIndex map = new DbIndex(this, "person");
					for (int n = 1; n < ITEM_MAX; n++) {
						Assert.assertEquals(n, map.get(n));
					}
				}
				resetTest();
			}
		}
	}
}
