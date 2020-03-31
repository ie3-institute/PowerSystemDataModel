/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

public class DeserializationException extends Exception {
  public DeserializationException(String message) {
    super(message);
  }

  public DeserializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DeserializationException(String message, Object deserializationObject) {
    super(message + "\nAffected object to be deseralized: " + deserializationObject.toString());
  }

  public DeserializationException(String message, Object deserializationObject, Throwable cause) {
    super(
        message + "\nAffected object to be deseralized: " + deserializationObject.toString(),
        cause);
  }
}
