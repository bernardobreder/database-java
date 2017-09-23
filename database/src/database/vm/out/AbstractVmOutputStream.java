package database.vm.out;

import java.io.IOException;
import java.io.OutputStream;

import database.object.DbObject;

/**
 * 
 * 
 * @author Tecgraf
 */
public abstract class AbstractVmOutputStream extends OutputStream {

  /** Iniciado */
  private boolean inited;
  /** Estado */
  private int classid;
  /** Estado */
  private int state;
  /** Estado */
  private int intValue;
  /** Estado */
  private int arrayLen;
  /** Estado */
  private int strState;
  /** Estado */
  private int lineState;
  /** Estado */
  private StringBuilder strBuilder;

  /**
   * 
   */
  public AbstractVmOutputStream() {
    this.strBuilder = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(int b) throws IOException {
    if (!inited) {
      if (b == '\n') {
        this.eof();
      }
      else {
        inited = true;
        this.open();
      }
    }
    if (classid == 0) {
      if (b == '\n') {
        if (lineState == 0) {
          classid = 0;
          lineState++;
          this.next();
        }
        else {
          this.eof();
        }
      }
      else {
        state = 0;
        lineState = 0;
        classid = 0;
        intValue = 0;
        strState = 0;
        switch (b) {
          case DbObject.INT_CLASSID:
          case DbObject.STR_CLASSID:
          case DbObject.BOOL_CLASSID:
          case DbObject.ARRAY_CLASSID: {
            classid = b;
            break;
          }
          default: {
            throw new IOException("invalied classid");
          }
        }
      }
    }
    else {
      switch (classid) {
        case DbObject.INT_CLASSID: {
          intValue += b << ((3 - state) * 8);
          if (state < 3) {
            state++;
          }
          else {
            this.column(intValue);
            if (--arrayLen > 0) {
              this.column();
            }
            classid = 0;
          }
          break;
        }
        case DbObject.STR_CLASSID: {
          if (strState == 0) {
            intValue += b << ((1 - state) * 8);
            if (state < 1) {
              state++;
            }
            else {
              strState++;
            }
          }
          else {
            strBuilder.append((char) b);
            if (intValue > 1) {
              intValue--;
            }
            else {
              this.column(strBuilder.toString());
              strBuilder.delete(0, strBuilder.length());
              if (--arrayLen > 0) {
                this.column();
              }
              classid = 0;
            }
          }
          break;
        }
        case DbObject.BOOL_CLASSID: {
          if (b == 0) {
            this.column(false);
          }
          else {
            this.column(true);
          }
          if (--arrayLen > 0) {
            this.column();
          }
          classid = 0;
          break;
        }
        case DbObject.ARRAY_CLASSID: {
          arrayLen += b << ((1 - state) * 8);
          if (state < 1) {
            state++;
          }
          else {
            classid = 0;
          }
          break;
        }
        default: {
          throw new IOException("invalied classid");
        }
      }
    }
  }

  protected abstract void column(int value) throws IOException;

  protected abstract void column(boolean value) throws IOException;

  protected abstract void column(String value) throws IOException;

  protected abstract void column() throws IOException;

  protected abstract void next() throws IOException;

  protected abstract void open() throws IOException;

  protected abstract void eof() throws IOException;

}
