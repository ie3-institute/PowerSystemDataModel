/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.exceptions.FailureException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class Try<T, E extends Exception> {
  private final T data;
  private final E exception;
  private final boolean isEmpty;

  // constructor
  /**
   * Constructor for {@link Try} used when a {@link Success} is created.
   *
   * @param data that is stored
   */
  protected Try(T data) {
    this.data = data;
    this.exception = null;
    this.isEmpty = data == null;
  }

  /**
   * Constructor for {@link Try} used when a {@link Failure} is created.
   *
   * @param ex exception that was thrown
   */
  private Try(E ex) {
    this.data = null;
    this.exception = ex;
    isEmpty = true;
  }

  /**
   * Method to create a {@link Try} object easily.
   *
   * @param supplier that either returns data or throws an exception
   * @return a try object
   * @param <T> type of data
   * @param <E> type of exception that could be thrown
   */
  @SuppressWarnings("unchecked")
  public static <T, E extends Exception> Try<T, E> of(TrySupplier<T, E> supplier) {
    try {
      return new Success<>(supplier.get());
    } catch (Exception e) {
      return (Try<T, E>) new Failure<>(e);
    }
  }

  /**
   * Method to create a {@link Try} object easily.
   *
   * @param supplier that either returns no data or throws an exception
   * @return a try object
   * @param <E> type of exception that could be thrown
   */
  @SuppressWarnings("unchecked")
  public static <E extends Exception> Try<Void, E> ofVoid(TrySupplier<?, E> supplier) {
    try {
      supplier.get();
      return (Try<Void, E>) Success.empty();
    } catch (Exception e) {
      return (Try<Void, E>) Failure.of(e);
    }
  }

  /**
   * Returns true if this object is a {@link Success} or false if this object is a {@link Failure}.
   */
  public abstract boolean isSuccess();

  /**
   * Returns true if this object is a {@link Failure} or false if this object is a {@link Success}.
   */
  public abstract boolean isFailure();

  /** Returns true if this object is either a {@link Success} or a {@link Failure}. */
  public boolean isEmpty() {
    return isEmpty;
  }

  /**
   * Method for getting the data. If this object is a {@link Failure} the exception is thrown.
   *
   * @return data id this object is a {@link Success}
   * @throws E if this object is a {@link Failure}
   */
  public T getOrThrow() throws E {
    if (data != null) {
      return data;
    } else {
      assert exception != null;
      throw exception;
    }
  }

  /**
   * This method will return the stored data if this object is a {@link Success} or the given value.
   *
   * @param value that should be returned if this object is a {@link Failure}
   * @return either the stored data or the given value
   */
  public T getOrElse(T value) {
    return data != null ? data : value;
  }

  /** Returns an option for data. */
  public Optional<T> getData() {
    return data != null ? Optional.of(data) : Optional.empty();
  }

  /** Returns an option for an exception. */
  public Optional<E> getException() {
    return exception != null ? Optional.of(exception) : Optional.empty();
  }

  /**
   * Returns the data. WARNING: This method is for internal usage only and should therefore not be
   * called for other purposes.
   */
  T data() {
    return data;
  }

  /**
   * Returns the exception. WARNING: This method is for internal usage only and should therefore not
   * be called for other purposes.
   */
  E exception() {
    return exception;
  }

  // functional methods

  /**
   * Method to transform the data if this object is a {@link Success}.
   *
   * @param mapper that is used to map the data
   * @return a new {@link Try} object
   * @param <U> type of the data
   */
  public <U> Try<U, E> map(Function<? super T, ? extends U> mapper) {
    return transform(mapper);
  }

  /**
   * Method to transform and flat the data.
   *
   * @param mapper that is used to map the data
   * @return a new {@link Try} object
   * @param <U> type of the data
   */
  @SuppressWarnings("unchecked")
  public <U> Try<U, E> flatMap(Function<? super T, ? extends Try<U, E>> mapper) {
    Try<Try<U, E>, E> t = transform(mapper);
    return t instanceof Success<Try<U, E>, ?> success ? success.data() : (Try<U, E>) t;
  }

  /**
   * Method to transform a {@link Try} object. This method should be used, if processing the
   * exception is not necessary.
   *
   * @param successFunc that will be used to transform the data
   * @return a new {@link Try} object
   * @param <U> type of data
   */
  public <U> Try<U, E> transform(Function<? super T, ? extends U> successFunc) {
    return isSuccess() ? new Success<>(successFunc.apply(data)) : Failure.of((Failure<T, E>) this);
  }

  /**
   * Method to transform a {@link Try} object. This method should be used, if only exception should
   * be processed.
   *
   * @param failureFunc that will be used to transform the exception
   * @return a new {@link Try} object
   * @param <R> type of new exception
   */
  @SuppressWarnings("unchecked")
  public <R extends Exception> Try<T, R> transformEx(Function<? super E, ? extends R> failureFunc) {
    return isFailure() ? (Try<T, R>) Failure.of(failureFunc.apply(exception)) : new Success<>(data);
  }

  /**
   * Method to transform a {@link Try} object. This method should be used, if processing the
   * exception is necessary.
   *
   * @param successFunc that will be used to transform the data
   * @param failureFunc that will be used to transform the exception
   * @return a new {@link Try} object
   * @param <U> type of data
   */
  public <U, R extends Exception> Try<U, R> transform(
      Function<? super T, ? extends U> successFunc, Function<E, R> failureFunc) {
    if (isSuccess()) {
      return new Success<>(successFunc.apply(data));
    } else {
      return new Failure<>(failureFunc.apply(exception));
    }
  }

  /**
   * Method to scan a collection of {@link Try} objects for {@link Failure}'s.
   *
   * @param c collection of {@link Try} objects
   * @param typeOfData type of data
   * @return a {@link Success} if no {@link Failure}'s are found in the collection
   * @param <U> type of data
   */
  public static <U, E extends Exception> Try<Set<U>, FailureException> scanCollection(
      Collection<Try<U, E>> c, Class<U> typeOfData) {
    Exception firstException = null;
    int countException = 0;

    Set<U> newSet = new HashSet<>();

    for (Try<U, E> entry : c) {
      if (entry.isFailure()) {
        if (firstException == null) {
          firstException = entry.exception;
        }
        countException++;
      } else {
        newSet.add(entry.data);
      }
    }

    if (countException > 0) {
      return new Failure<>(
          new FailureException(
              countException
                  + " exception(s) occurred within \""
                  + typeOfData.getSimpleName()
                  + "\" data, one is: "
                  + firstException.getMessage().toLowerCase(),
              firstException.getCause()));
    } else {
      return new Success<>(newSet);
    }
  }

  /**
   * Method to scan a stream of {@link Try} objects for {@link Failure}'s.
   *
   * @param stream of {@link Try} objects
   * @return a {@link Success} if no {@link Failure}'s are found in the stream
   * @param <U> type of data
   */
  @SuppressWarnings("unchecked")
  public static <U, E extends Exception> Try<List<U>, FailureException> scanStream(
      Stream<Try<U, E>> stream) {
    List<Try<U, E>> list = stream.toList();
    List<Exception> exceptions =
        list.stream().filter(Try::isFailure).map(t -> ((Failure<?, Exception>) t).get()).toList();

    if (!exceptions.isEmpty()) {
      return new Failure<>(
          new FailureException(exceptions.size() + " exception(s) occurred.", exceptions.get(0)));
    } else {
      return new Success<>(list.stream().map(t -> t.data).toList());
    }
  }

  /**
   * Method to retrieve the exceptions from all {@link Failure} objects.
   *
   * @param tries collection of {@link Try} objects
   * @return a list of {@link Exception}'s
   */
  public static <D, E extends Exception> List<E> getExceptions(
      Collection<Try<? extends D, E>> tries) {
    return tries.stream().filter(Try::isFailure).map(t -> ((Failure<?, E>) t).get()).toList();
  }

  /** Implementation of {@link Try} class. This class is used to present a successful try. */
  public static class Success<T, E extends Exception> extends Try<T, E> {
    public Success(T data) {
      super(data);
    }

    public static <E extends Exception> Success<?, E> empty() {
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

    /** Returns the stored data. */
    public T get() {
      return data();
    }
  }

  /** Implementation of {@link Try} class. This class is used to present a failed try. */
  public static class Failure<T, E extends Exception> extends Try<T, E> {
    public Failure(E e) {
      super(e);
    }

    public static <E extends Exception> Failure<?, E> of(E exception) {
      return new Failure<>(exception);
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isFailure() {
      return true;
    }

    /** Returns the thrown exception. */
    public E get() {
      return exception();
    }

    /**
     * Method to transform a {@link Failure} into another {@link Failure}.
     *
     * @param failure given failure
     * @return the transformed failure
     * @param <T> type before transformation
     * @param <U> type after transformation
     */
    public static <T, U, E extends Exception> Failure<U, E> of(Failure<T, E> failure) {
      return new Failure<>(failure.exception());
    }
  }

  /**
   * Functional interface for the {@link Try} class.
   *
   * @param <T> type of data that is supplied
   * @param <E> type of exception that could be thrown
   */
  @FunctionalInterface
  public interface TrySupplier<T, E extends Exception> {
    T get() throws E;
  }
}
