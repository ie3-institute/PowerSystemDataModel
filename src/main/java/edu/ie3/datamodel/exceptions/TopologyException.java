/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** The type Topology exception. */
public class TopologyException extends Exception {
  /** Instantiates a new Topology exception. */
  public TopologyException() {
    super();
  }

  /**
   * Instantiates a new Topology exception.
   *
   * @param s the s
   */
  public TopologyException(String s) {
    super(s);
  }

  /**
   * Instantiates a new Topology exception.
   *
   * @param s the s
   * @param throwable the throwable
   */
  public TopologyException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
