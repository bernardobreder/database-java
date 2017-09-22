package org.oonsql.lng.vm;

import org.oonsql.util.MyTreeMap;

/**
 * Ambiente da máquina virtual
 * 
 * @author Bernardo Breder
 */
public class OonsqlEnv {

  /** Variáveis */
  private MyTreeMap<String, Object> variables = new MyTreeMap<String, Object>();
  /** Classes */
  private MyTreeMap<String, Object> classes = new MyTreeMap<String, Object>();

  /**
   * Retorna
   * 
   * @return variables
   */
  public MyTreeMap<String, Object> getVariables() {
    return variables;
  }

  /**
   * Retorna
   * 
   * @return classes
   */
  public MyTreeMap<String, Object> getClasses() {
    return classes;
  }

}
