import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Estrutura responsável por fazer as operações de Entrada e Saída do banco de
 * dados
 * 
 * @author Bernardo Breder
 */
public class DbIO {

	/**
	 * @param name
	 * @return existe o arquivo
	 */
	public boolean exist(String name) {
		return new File(name).exists();
	}

	/**
	 * @param name
	 * @return dado
	 * @throws IOException
	 */
	public DbInput read(String name) throws IOException {
		byte[] b = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(name);
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.write(b, 0, n);
			}
		} finally {
			in.close();
		}
		return new DbInput(out.toByteArray());
	}

	/**
	 * @param name
	 * @param output
	 * @throws IOException
	 */
	public void write(String name, DbOutput output) throws IOException {
		FileOutputStream out = new FileOutputStream(name);
		try {
			out.write(output.getBytes(), 0, output.size());
		} finally {
			out.close();
		}
	}

	/**
	 * @param name
	 * @return indica se removeu
	 */
	public boolean remove(String name) {
		return new File(name).delete();
	}

}
