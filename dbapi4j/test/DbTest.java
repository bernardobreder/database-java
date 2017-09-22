import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DbTest extends DbIO {

	/** Iteração */
	protected static final int PAGE_MAX = 32;
	/** Iteração */
	protected static final int SLOT_MAX = 32;
	/** Iteração */
	protected static final int ITEM_MAX = 1024;
	/** Arquivos */
	protected Map<String, byte[]> bytes = new HashMap<String, byte[]>();

	protected void resetTest() {
		bytes.clear();
		bytes = new HashMap<String, byte[]>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exist(String name) {
		return bytes.containsKey(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DbInput read(String name) throws IOException {
		byte[] bs = bytes.get(name);
		if (bs == null) {
			return null;
		}
		return new DbInput(bs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(String name, DbOutput output) throws IOException {
		bytes.put(name, output.toBytes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(String name) {
		return bytes.remove(name) != null;
	}

}
