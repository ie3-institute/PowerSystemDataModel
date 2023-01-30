/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

public class Failure<R, E extends Exception> extends Try<R, E> {
  /** Private fields. */
  private final E exception;

  public Failure(E exception) {
    super();
    this.exception = exception;
  }

  @Override
  public boolean isSuccess() {
    return false;
  }

  @Override
  public boolean isFailure() {
    return true;
  }

  /** Returns the data. */
  @Override
  public R getData() {
    return null;
  }

  @Override
  public E getException() {
    return exception;
  }
}
