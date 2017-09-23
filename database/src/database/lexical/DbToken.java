package database.lexical;

/**
 * 
 * 
 * @author Tecgraf
 */
public class DbToken extends Token {

  /** Keyword */
  public static final int SELECT = 256;
  /** Keyword */
  public static final int FROM = 257;
  /** Keyword */
  public static final int WHERE = 258;
  /** Keyword */
  public static final int ORDER = 259;
  /** Keyword */
  public static final int GROUP = 260;
  /** Keyword */
  public static final int BY = 261;
  /** Keyword */
  public static final int NOT_EQUAL = 262;
  /** Keyword */
  public static final int GREATER_EQUAL = 263;
  /** Keyword */
  public static final int LOWER_EQUAL = 264;
  /** Keyword */
  public static final int OR = 265;
  /** Keyword */
  public static final int AND = 266;
  /** Keyword */
  public static final int LIKE = 267;
  /** Keyword */
  public static final int TRUE = 268;
  /** Keyword */
  public static final int FALSE = 269;

  /**
   * @param type
   * @param word
   * @param lin
   * @param col
   */
  public DbToken(int type, String word, int lin, int col) {
    super(type, word, lin, col);
  }

}
