/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

import edu.ie3.datamodel.exceptions.SourceException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Try object
 *
 * @param <R> type of the data
 * @param <E> type of the exception
 */
public abstract class Try<R, E extends Exception> {
  private final boolean isEmpty;

  /** Constructor of a try object. One input can be null. */
  Try(boolean isEmpty) {
    this.isEmpty = isEmpty;
  }

  /**
   * Method to apply a callable to Try class. This method will return either a {@link Success} or a
   * {@link Failure}.
   *
   * @param method applied method
   * @return a try object
   */
  public static <R, E extends Exception> Try<R, E> apply(Callable<R> method) {
    try {
      return new Success<>(method.call());
    } catch (Exception e) {
      return new Failure<>((E) e);
    }
  }

  /**
   * Method to apply a runnable to Try class. This method will return either an empty {@link
   * Success} if no exception occurred or a {@link Failure}.
   *
   * @param method applied method
   * @return a try object
   */
  public static <E extends Exception> Try<Void, E> apply(Runnable method) {
    try {
      method.run();
      return Success.empty();
    } catch (Exception e) {
      return new Failure<>((E) e);
    }
  }

  /** Returns true if the object is a {@link Success}. */
  public abstract boolean isSuccess();

  /** Returns true if the object is a {@link Failure}. */
  public abstract boolean isFailure();

  /** Returns true if the data of this object is empty. */
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * This method is used to retrieve data from this object. If this objects is an instant {@link
   * Success} the data is returned, else if this object is an instant of {@link Failure} an
   * exception is thrown.
   *
   * @return data
   * @throws E exception
   */
  public R get() throws E {
    if (this instanceof Success<R, E> success) {
      return success.getData();
    } else {
      throw this.getException();
    }
  }

  /** Returns the data. */
  public abstract R getData();

  /** Returns the exception. */
  public abstract E getException();

  /**
   * Method to scan for exceptions in a set of try objects.
   *
   * @param set of try objects
   * @param typeOfData class of the data
   * @return a try of a set
   * @param <T> type of the data
   * @param <E> type of the exception
   */
  public static <T, E extends Exception> Try<Set<T>, SourceException> scanForExceptions(
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
          new SourceException(
              countExceptions
                  + " exception(s) occurred within \""
                  + typeOfData.getSimpleName()
                  + "\" data, one is: "
                  + firstException.getMessage().toLowerCase(),
              firstException.getCause()));
    } else {
      return new Success<>(newSet);
    }
  }
}
