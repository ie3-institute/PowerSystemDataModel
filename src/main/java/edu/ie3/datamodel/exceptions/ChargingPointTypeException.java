/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

/** Is thrown in case, there is some problem when building VoltageLevelInformation */
public class ChargingPointTypeException extends Exception {
  public ChargingPointTypeException(String message) {
    super(message);
  }

  public ChargingPointTypeException(String message, Throwable cause) {
    super(message, cause);
  }
}
