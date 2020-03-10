/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class AggregationException extends Exception {
  public AggregationException(String message) {
    super(message);
  }

  public AggregationException(String message, Throwable cause) {
    super(message, cause);
  }
}
