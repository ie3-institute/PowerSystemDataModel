/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.options;

import edu.ie3.datamodel.exceptions.RawInputDataException;
import java.util.HashSet;
import java.util.Set;

public abstract class Try<R, E extends Exception> {
  private final R data;
  private final E exception;

  Try(R data, E exception) {
    this.data = data;
    this.exception = exception;
  }

  public abstract boolean isSuccess();

  public abstract boolean isFailure();

  public R getData() {
    return data;
  }

  public E getException() {
    return exception;
  }

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
              countExceptions + " errors occurred with " + typeOfData + " data.",
              firstException.getCause()));
    } else {
      return new Success<>(newSet);
    }
  }

  public static <T, E extends RuntimeException> T getOrThrowException(Try<T, E> option) {
    if (option.isSuccess()) {
      return option.getData();
    } else {
      throw option.getException();
    }
  }
}
