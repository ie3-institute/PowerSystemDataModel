/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

import edu.ie3.datamodel.exceptions.RawInputDataException;
import java.util.HashSet;
import java.util.Set;

/**
 * Try object
 *
 * @param <R> type of the data
 * @param <E> type of the exception
 */
public abstract class Try<R, E extends Exception> {
  /** Private fields. */
  private final R data;

  private final E exception;

  /**
   * Constructor of a try object. One input can be null.
   *
   * @param data given data
   * @param exception given exception
   */
  Try(R data, E exception) {
    this.data = data;
    this.exception = exception;
  }

  /** Returns true if the object is a {@link Success}. */
  public abstract boolean isSuccess();

  /** Returns true if the object is a {@link Failure}. */
  public abstract boolean isFailure();

  /**
   * This method is used to retrieve data from this object. If this objects is an instant {@link
   * Success} the data is returned, else if this object is an instant of {@link Failure} an
   * exception is thrown.
   *
   * @return data
   * @throws E exception
   */
  public R get() throws E {
    if (isSuccess()) {
      return data;
    } else {
      throw exception;
    }
  }

  /** Returns the data. */
  public R getData() {
    return data;
  }

  /** Returns the exception. */
  public E getException() {
    return exception;
  }

  /**
   * Method to scan for exceptions in a set of try objects.
   *
   * @param set of try objects
   * @param typeOfData class of the data
   * @return a try of a set
   * @param <T> type of the data
   * @param <E> type of the exception
   */
  public static <T, E extends Exception> Try<Set<T>, RawInputDataException> scanForExceptions(
      Set<Try<T, E>> set, Class<T> typeOfData) {
    Exception firstException = null;
    int countExceptions = 0;

    Set<T> newSet = new HashSet<>();

    for (Try<T, E> entry : set) {
      if (entry.isFailure()) {
        if (firstException == null) {
          firstException = entry.getException();
        }
        countExceptions++;
      } else {
        newSet.add(entry.getData());
      }
    }

    if (countExceptions > 0) {
      return new Failure<>(
          new RawInputDataException(
              countExceptions
                  + " errors occurred within \""
                  + typeOfData.getSimpleName()
                  + "\" data, first "
                  + firstException.getMessage().toLowerCase(),
              firstException.getCause()));
    } else {
      return new Success<>(newSet);
    }
  }
}
