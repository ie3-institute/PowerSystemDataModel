/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

public class Success<R, E extends Exception> extends Try<R, E> {
  /** Private fields. */
  private final R data;

  public Success(R data) {
    super(data == null);
    this.data = data;
  }

  /**
   * Method to create an empty success.
   *
   * @return empty {@link Success}
   */
  public static <E extends Exception> Success<Void, E> empty() {
    return new Success<>(null);
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public boolean isFailure() {
    return false;
  }

  /** Returns the data. */
  @Override
  public R getData() {
    return data;
  }

  @Override
  public E getException() {
    return null;
  }
}
