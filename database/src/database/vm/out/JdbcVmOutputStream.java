package database.vm.out;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author Tecgraf
 */
public class JdbcVmOutputStream extends AbstractVmOutputStream {

  /** Fechado */
  private boolean closed;
  /** Linhas */
  private List<Object[]> rows = new ArrayList<Object[]>();
  /** Linhas */
  private List<Object> next = new ArrayList<Object>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(int value) throws IOException {
    this.next.add(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(boolean value) throws IOException {
    this.next.add(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column(String value) throws IOException {
    this.next.add(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void column() throws IOException {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void next() throws IOException {
    rows.add(next.toArray());
    next = new ArrayList<Object>();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void eof() throws IOException {
    this.closed = true;
    this.next = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void open() throws IOException {
    this.next = new ArrayList<Object>();
  }

  /**
   * Retorna
   * 
   * @return closed
   */
  public boolean isClosed() {
    return closed;
  }

  /**
   * Retorna
   * 
   * @return rows
   */
  public List<Object[]> getRows() {
    return rows;
  }

  /**
   * @param rowIndex
   * @return indica se tem a linha
   */
  public boolean hasRow(int rowIndex) {
    return rowIndex < rows.size();
  }

}
