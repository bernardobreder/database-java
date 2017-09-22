package org.breder.database.map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Sistema de Arquivo em memória
 * 
 * @author Tecgraf
 */
public class MemoryFileSystem {

  /** Memoria */
  private Map<String, byte[]> map;

  /**
   * Construtor
   */
  public MemoryFileSystem() {
    this.map = new HashMap<String, byte[]>();
  }

  /**
   * @param name
   * @return existe o arquivo
   */
  public synchronized boolean exists(String name) {
    return this.map.containsKey(name);
  }

  /**
   * @param name
   * @return stream
   * @throws FileNotFoundException
   */
  public synchronized InputStream getInputStream(String name)
    throws FileNotFoundException {
    byte[] bytes = this.map.get(name);
    if (bytes == null) {
      throw new FileNotFoundException(name);
    }
    return new ByteArrayInputStream(bytes);
  }

  /**
   * @param name
   * @return stream
   */
  public synchronized OutputStream getOutputStream(final String name) {
    return new ByteArrayOutputStream() {
      @Override
      public void close() throws IOException {
        map.put(name, this.toByteArray());
      };
    };
  }

  /**
   * Deleta um arquivo
   * 
   * @param name
   */
  public synchronized void delete(String name) {
    this.map.remove(name);
  }

  /**
   * Renomeia um arquivo
   * 
   * @param fromName
   * @param toName
   */
  public synchronized void rename(String fromName, String toName) {
    this.map.put(toName, this.map.remove(fromName));
  }

}
