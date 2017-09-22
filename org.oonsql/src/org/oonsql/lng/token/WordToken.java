package org.oonsql.lng.token;

import java.util.HashMap;
import java.util.Map;

/**
 * Palavra reservada
 * 
 * @author Bernardo Breder
 */
public class WordToken extends Token {

  /** Texto */
  public String lexeme = "";

  /**
   * Palavras
   */
  private static final Map<String, WordToken> words =
    new HashMap<String, WordToken>();

  /** Opcode de */
  public final static int AND = 256;
  /** Opcode de */
  public final static int CONTINUE = 257;
  /** Opcode de */
  public final static int BREAK = 258;
  /** Opcode de */
  public final static int DO = 259;
  /** Opcode de */
  public final static int ELSE = 260;
  /** Opcode de */
  public final static int EQ = 261;
  /** Opcode de */
  public final static int FALSE = 262;
  /** Opcode de */
  public final static int GE = 263;
  /** Opcode de */
  public final static int ID = 264;
  /** Opcode de */
  public final static int IF = 265;
  /** Opcode de */
  public final static int LE = 267;
  /** Opcode de */
  public final static int NE = 269;
  /** Opcode de */
  public final static int NUM = 270;
  /** Opcode de */
  public final static int OR = 271;
  /** Opcode de */
  public final static int TRUE = 274;
  /** Opcode de */
  public final static int WHILE = 295;
  /** Opcode de */
  public final static int END = 276;
  /** Opcode de */
  public final static int REPEAT = 277;
  /** Opcode de */
  public final static int FOR = 278;
  /** Opcode de */
  public final static int STR = 290;
  /** Opcode de */
  public final static int THIS = 291;
  /** Opcode de */
  public final static int DEC = 292;
  /** Opcode de */
  public final static int INC = 293;

  /** Opcode de */
  public static final WordToken EQ_TOKEN = new WordToken("==", EQ);
  /** Opcode de */
  public static final WordToken NOT_EQUAL_TOKEN = new WordToken("!=", NE);
  /** Opcode de */
  public static final WordToken LE_TOKEN = new WordToken("<=", LE);
  /** Opcode de */
  public static final WordToken GE_TOKEN = new WordToken(">=", GE);
  /** Opcode de */
  public static final WordToken INC_TOKEN = new WordToken("++", INC);
  /** Opcode de */
  public static final WordToken DEC_TOKEN = new WordToken("--", DEC);

  static {
    words.put("--", DEC_TOKEN);
    words.put("++", INC_TOKEN);
    words.put(">=", GE_TOKEN);
    words.put("<=", LE_TOKEN);
    words.put("!=", NOT_EQUAL_TOKEN);
    words.put("==", EQ_TOKEN);
    words.put("and", new WordToken("and", AND));
    words.put("break", new WordToken("break", BREAK));
    words.put("continue", new WordToken("continue", CONTINUE));
    words.put("do", new WordToken("do", DO));
    words.put("else", new WordToken("else", ELSE));
    words.put("end", new WordToken("end", END));
    words.put("false", new WordToken("false", FALSE));
    words.put("for", new WordToken("for", FOR));
    words.put("if", new WordToken("if", IF));
    words.put("or", new WordToken("or", OR));
    words.put("repeat", new WordToken("repeat", REPEAT));
    words.put("this", new WordToken("this", THIS));
    words.put("true", new WordToken("true", TRUE));
    words.put("while", new WordToken("while", WHILE));
  }

  /**
   * @param s
   * @param tag
   */
  protected WordToken(String s, int tag) {
    super(tag);
    lexeme = s;
  }

  /**
   * @param token
   * @return token
   */
  public static WordToken build(String token) {
    return words.get(token);
  }

  /**
   * @param tag
   * @return token
   */
  public static WordToken build(int tag) {
    for (WordToken token : words.values()) {
      if (token.tag == tag) {
        return token;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return lexeme.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WordToken other = (WordToken) obj;
    if (lexeme == null) {
      if (other.lexeme != null) {
        return false;
      }
    }
    else if (!lexeme.equals(other.lexeme)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return lexeme;
  }

}
