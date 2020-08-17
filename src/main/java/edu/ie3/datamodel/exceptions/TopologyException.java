/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class TopologyException extends Exception {
  public TopologyException() {
    super();
  }

  public TopologyException(String s) {
    super(s);
  }

  public TopologyException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
