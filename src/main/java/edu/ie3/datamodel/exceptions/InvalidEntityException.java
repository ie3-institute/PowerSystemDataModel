/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import edu.ie3.datamodel.models.UniqueEntity;

/** Is thrown, when a checked entity is illegal and thus not usable */
public class InvalidEntityException extends ValidationException {

  private static final long serialVersionUID = 809496087520306374L;

  /**
   * Instantiates a new Invalid entity exception.
   *
   * @param faultDescription the fault description
   * @param invalidEntity the invalid entity
   */
  public InvalidEntityException(String faultDescription, UniqueEntity invalidEntity) {
    super("Entity is invalid because of: " + faultDescription + " [" + invalidEntity + "]");
  }

  /**
   * Instantiates a new Invalid entity exception.
   *
   * @param faultDescription the fault description
   * @param cause the cause
   * @param invalidEntity the invalid entity
   */
  public InvalidEntityException(
      String faultDescription, Throwable cause, UniqueEntity invalidEntity) {
    super("Entity is invalid because of: " + faultDescription + " [" + invalidEntity + "]", cause);
  }

  /**
   * Instantiates a new Invalid entity exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public InvalidEntityException(String message, Throwable cause) {
    super(message, cause);
  }
}
