/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import static java.util.stream.Collectors.partitioningBy;

import edu.ie3.datamodel.exceptions.FailureException;
import edu.ie3.datamodel.exceptions.TryException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Try<T, E extends Exception> {
  // static utility methods

  /**
   * Method to create a {@link Try} object easily.
   *
   * @param supplier that either returns data or throws an exception
   * @param clazz class of the exception
   * @return a try object
   * @param <T> type of data
   * @param <E> type of exception that could be thrown
   */
  @SuppressWarnings("unchecked")
  public static <T, E extends Exception> Try<T, E> of(TrySupplier<T, E> supplier, Class<E> clazz) {
    try {
      return new Success<>(supplier.get());
    } catch (Exception e) {
      // this is necessary because we only want to catch exceptions that are of type E
      if (e.getClass().isAssignableFrom(clazz)) {
        return new Failure<>((E) e);
      } else {
        throw new TryException("Wrongly caught exception: ", e);
      }
    }
  }

  /**
   * Method to create a {@link Try} object easily.
   *
   * @param supplier that either returns no data or throws an exception
   * @param clazz class of the exception
   * @return a try object
   * @param <E> type of exception that could be thrown
   */
  @SuppressWarnings("unchecked")
  public static <E extends Exception> Try<Void, E> ofVoid(
      VoidSupplier<E> supplier, Class<E> clazz) {
    try {
      supplier.get();
      return Success.empty();
    } catch (Exception e) {
      // this is necessary because we only want to catch exceptions that are of type E
      if (e.getClass().isAssignableFrom(clazz)) {
        return Failure.ofVoid((E) e);
      } else {
        throw new TryException("Wrongly caught exception: ", e);
      }
    }
  }

  /**
   * Method to create a {@link Try} object easily.
   *
   * @param failure a {@link Failure} is returned.
   * @param exception exception that should be wrapped by a {@link Failure}
   * @return a {@link Try}
   * @param <E> type of exception
   */
  public static <E extends Exception> Try<Void, E> ofVoid(
      boolean failure, ExceptionSupplier<E> exception) {
    if (failure) {
      return Failure.ofVoid(exception.get());
    } else {
      return Success.empty();
    }
  }

  /**
   * Utility method to check a list of {@link VoidSupplier}'s.
   *
   * @param supplier list of {@link VoidSupplier}
   * @param clazz class of the exception
   * @return a list of {@link Try}
   * @param <E> type of the exception
   */
  @SafeVarargs
  public static <E extends Exception> List<Try<Void, E>> ofVoid(
      Class<E> clazz, VoidSupplier<E>... supplier) {
    return Arrays.stream(supplier).map(sup -> Try.ofVoid(sup, clazz)).toList();
  }

  /**
   * Method to create a {@link Try} object from Optional.
   *
   * @param opt The optional
   * @param exception Supplier function that supplies an exception if Optional is empty
   * @return a try object
   * @param <T> type of data
   * @param <E> type of exception
   */
  public static <T, E extends Exception> Try<T, E> from(
      Optional<T> opt, ExceptionSupplier<E> exception) {
    return opt.map(data -> (Try<T, E>) Success.<T, E>of(data))
        .orElseGet(() -> new Failure<>(exception.get()));
  }

  /**
   * Method to retrieve the exceptions from all {@link Failure} objects.
   *
   * @param tries collection of {@link Try} objects
   * @return a list of {@link Exception}'s
   */
  public static <D, E extends Exception> List<E> getExceptions(
      Collection<Try<? extends D, E>> tries) {
    return getExceptions(tries.stream());
  }

  /**
   * Method to retrieve the exceptions from all {@link Failure} objects.
   *
   * @param tries stream of {@link Try} objects
   * @return a list of {@link Exception}'s
   */
  public static <D, E extends Exception> List<E> getExceptions(Stream<Try<? extends D, E>> tries) {
    return tries.filter(Try::isFailure).map(t -> ((Failure<?, E>) t).get()).toList();
  }

  /**
   * Method to retrieve the exceptions from all {@link Failure} objects.
   *
   * @param tries array of {@link Try} objects
   * @return a list of {@link Exception}'s
   */
  @SafeVarargs
  public static <D, E extends Exception> List<E> getExceptions(Try<? extends D, E>... tries) {
    return Arrays.stream(tries).filter(Try::isFailure).map(t -> ((Failure<?, E>) t).get()).toList();
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
    return scanStream(c.stream(), typeOfData.getSimpleName())
        .transformS(stream -> stream.collect(Collectors.toSet()));
  }

  /**
   * Method to scan a stream of {@link Try} objects for {@link Failure}'s.
   *
   * @param stream of {@link Try} objects
   * @return a {@link Success} if no {@link Failure}'s are found in the stream
   * @param <U> type of data
   */
  public static <U, E extends Exception> Try<Stream<U>, FailureException> scanStream(
      Stream<Try<U, E>> stream, String typeOfData) {
    Map<Boolean, List<Try<U, E>>> map = stream.collect(partitioningBy(Try::isSuccess));

    List<Try<U, E>> successes = map.get(true);
    List<Try<U, E>> failures = map.get(false);

    // Both lists should exist in map per definition of partitioningBy
    assert successes != null && failures != null;

    if (!failures.isEmpty()) {
      E first = ((Failure<U, E>) failures.get(0)).exception;

      return new Failure<>(
          new FailureException(
              failures.size()
                  + " exception(s) occurred within \""
                  + typeOfData
                  + "\" data, one is: "
                  + first,
              first.getCause()));
    } else {
      return new Success<>(successes.stream().map(t -> ((Success<U, E>) t).data));
    }
  }

  // methods of try object

  /**
   * Returns true if this object is a {@link Success} or false if this object is a {@link Failure}.
   */
  public abstract boolean isSuccess();

  /**
   * Returns true if this object is a {@link Failure} or false if this object is a {@link Success}.
   */
  public abstract boolean isFailure();

  /**
   * Method for getting the data. If this object is a {@link Failure} the exception is thrown.
   *
   * @return data id this object is a {@link Success}
   * @throws E if this object is a {@link Failure}
   */
  public abstract T getOrThrow() throws E;

  /** Returns an option for data. */
  public abstract Optional<T> getData();

  /** Returns an option for an exception. */
  public abstract Optional<E> getException();

  // functional methods

  /**
   * Method to transform the data if this object is a {@link Success}.
   *
   * @param mapper that is used to map the data
   * @return a new {@link Try} object
   * @param <U> type of the data
   */
  public <U> Try<U, E> map(Function<? super T, ? extends U> mapper) {
    return transformS(mapper);
  }

  /**
   * Method to transform and flat the data.
   *
   * @param mapper that is used to map the data
   * @return a new {@link Try} object
   * @param <U> type of the data
   */
  public abstract <U> Try<U, E> flatMap(Function<? super T, ? extends Try<U, E>> mapper);

  /**
   * Method to transform a {@link Try} object. This method should be used, if processing the
   * exception is not necessary.
   *
   * @param successFunc that will be used to transform the data
   * @return a new {@link Try} object
   * @param <U> type of data
   */
  public abstract <U> Try<U, E> transformS(Function<? super T, ? extends U> successFunc);

  /**
   * Method to transform a {@link Try} object. This method should be used, if only exception should
   * be processed.
   *
   * @param failureFunc that will be used to transform the exception
   * @return a new {@link Try} object
   * @param <R> type of new exception
   */
  public abstract <R extends Exception> Try<T, R> transformF(
      Function<? super E, ? extends R> failureFunc);

  /**
   * Method to transform a {@link Try} object. This method should be used, if processing the
   * exception is necessary.
   *
   * @param successFunc that will be used to transform the data
   * @param failureFunc that will be used to transform the exception
   * @return a new {@link Try} object
   * @param <U> type of data
   */
  public abstract <U, R extends Exception> Try<U, R> transform(
      Function<? super T, ? extends U> successFunc, Function<E, R> failureFunc);

  /**
   * Method to convert a {@link Try} object to a common type.
   *
   * @param successFunc that will be used to transform the data to the new type
   * @param failureFunc that will be used to transform the exception to the new type
   * @return the new type
   * @param <U> new type
   */
  public abstract <U> U convert(
      Function<? super T, ? extends U> successFunc, Function<E, U> failureFunc);

  /**
   * If this is a Success, the value is returned, otherwise given default is returned.
   *
   * @param defaultData the value to be returned, if this is a failure.
   * @return the value of a success, otherwise {@code defaultData}
   */
  public abstract T getOrElse(Supplier<T> defaultData);

  /**
   * If this is a Success, it is returned, otherwise given default Try is returned.
   *
   * @param defaultTry the Try to be returned, if this is a failure.
   * @return this try object if it is a Success, otherwise {@code defaultTry}
   */
  public abstract Try<T, E> orElse(Supplier<Try<T, E>> defaultTry);

  /**
   * Turns this Try into an {@link Optional} by returning the wrapped value if this is a success,
   * and an empty optional if this is a failure.
   *
   * @return an optional of the value
   */
  public abstract Optional<T> toOptional();

  /** Implementation of {@link Try} class. This class is used to present a successful try. */
  public static final class Success<T, E extends Exception> extends Try<T, E> {
    private final T data;

    private static final Success<Void, ?> emptySuccess = new Success<>(null);

    public Success(T data) {
      this.data = data;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

    @Override
    public boolean isFailure() {
      return false;
    }

    /** Returns true if this object is an empty {@link Success}. */
    public boolean isEmpty() {
      return data == null;
    }

    @Override
    public T getOrThrow() throws E {
      return get();
    }

    @Override
    public Optional<T> getData() {
      return !isEmpty() ? Optional.of(data) : Optional.empty();
    }

    @Override
    public Optional<E> getException() {
      return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Try<U, E> flatMap(Function<? super T, ? extends Try<U, E>> mapper) {
      Try<Try<U, E>, E> t = transformS(mapper);
      return t instanceof Success<Try<U, E>, ?> success ? success.get() : (Try<U, E>) t;
    }

    @Override
    public <U> Try<U, E> transformS(Function<? super T, ? extends U> successFunc) {
      return new Success<>(successFunc.apply(data));
    }

    @Override
    public <R extends Exception> Try<T, R> transformF(
        Function<? super E, ? extends R> failureFunc) {
      return new Success<>(data);
    }

    @Override
    public <U, R extends Exception> Try<U, R> transform(
        Function<? super T, ? extends U> successFunc, Function<E, R> failureFunc) {
      return new Success<>(successFunc.apply(data));
    }

    @Override
    public <U> U convert(Function<? super T, ? extends U> successFunc, Function<E, U> failureFunc) {
      return successFunc.apply(data);
    }

    @Override
    public T getOrElse(Supplier<T> defaultData) {
      return data;
    }

    @Override
    public Try<T, E> orElse(Supplier<Try<T, E>> defaultTry) {
      return this;
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.of(data);
    }

    /** Returns the stored data. */
    public T get() {
      return data;
    }

    /**
     * Method to create a {@link Success} by applying data.
     *
     * @param data that should be wrapped by the {@link Success}
     * @return a new {@link Success}
     * @param <D> type of data
     * @param <E> type of exception
     */
    public static <D, E extends Exception> Success<D, E> of(D data) {
      return new Success<>(data);
    }

    /**
     * Returns an empty {@link Success}.
     *
     * @param <E> type of exception
     */
    @SuppressWarnings("unchecked")
    public static <E extends Exception> Success<Void, E> empty() {
      return (Success<Void, E>) emptySuccess;
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Success}. The other object is
     * considered equal if:
     *
     * <ul>
     *   <li>it is also a {@code Success} and;
     *   <li>the values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      return obj instanceof Success<?, ?> other && Objects.equals(data, other.data);
    }

    /**
     * Returns the hash code of the value.
     *
     * @return hash code value of the value
     */
    @Override
    public int hashCode() {
      return Objects.hashCode(data);
    }
  }

  /** Implementation of {@link Try} class. This class is used to present a failed try. */
  public static final class Failure<T, E extends Exception> extends Try<T, E> {
    private final E exception;

    public Failure(E e) {
      this.exception = e;
    }

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public boolean isFailure() {
      return true;
    }

    @Override
    public T getOrThrow() throws E {
      throw exception;
    }

    @Override
    public Optional<T> getData() {
      return Optional.empty();
    }

    @Override
    public Optional<E> getException() {
      return exception != null ? Optional.of(exception) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Try<U, E> flatMap(Function<? super T, ? extends Try<U, E>> mapper) {
      return (Failure<U, E>) this;
    }

    @Override
    public <U> Try<U, E> transformS(Function<? super T, ? extends U> successFunc) {
      return Failure.of(this.exception);
    }

    @Override
    public <R extends Exception> Try<T, R> transformF(
        Function<? super E, ? extends R> failureFunc) {
      return Failure.of(failureFunc.apply(exception));
    }

    @Override
    public <U, R extends Exception> Try<U, R> transform(
        Function<? super T, ? extends U> successFunc, Function<E, R> failureFunc) {
      return Failure.of(failureFunc.apply(exception));
    }

    @Override
    public <U> U convert(Function<? super T, ? extends U> successFunc, Function<E, U> failureFunc) {
      return failureFunc.apply(exception);
    }

    @Override
    public T getOrElse(Supplier<T> defaultData) {
      return defaultData.get();
    }

    @Override
    public Try<T, E> orElse(Supplier<Try<T, E>> defaultTry) {
      return defaultTry.get();
    }

    @Override
    public Optional<T> toOptional() {
      return Optional.empty();
    }

    /** Returns the thrown exception. */
    public E get() {
      return exception;
    }

    /**
     * Method to create a {@link Failure} object, when a non-empty {@link Success} can be returned.
     *
     * @param exception that should be saved
     * @return a {@link Failure}
     * @param <T> type of data
     * @param <E> type of exception
     */
    public static <T, E extends Exception> Failure<T, E> of(E exception) {
      return new Failure<>(exception);
    }

    /**
     * Method to create a {@link Failure} object, when an empty {@link Success} can be returned.
     *
     * @param exception that should be saved
     * @return a {@link Failure}
     * @param <E> type of exception
     */
    public static <E extends Exception> Failure<Void, E> ofVoid(E exception) {
      return new Failure<>(exception);
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Failure}. The other object is
     * considered equal if:
     *
     * <ul>
     *   <li>it is also a {@code Failure} and;
     *   <li>the exceptions are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      return obj instanceof Failure<?, ?> other && Objects.equals(exception, other.exception);
    }

    /**
     * Returns the hash code of the exception.
     *
     * @return hash code value of the exception
     */
    @Override
    public int hashCode() {
      return Objects.hashCode(exception);
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

  /**
   * Supplier for void methods to {@link Try} class.
   *
   * @param <E> type of exception that could be thrown
   */
  @FunctionalInterface
  public interface VoidSupplier<E extends Exception> {
    void get() throws E;
  }

  /**
   * Supplier for exceptions.
   *
   * @param <E> type of exception that could be thrown
   */
  @FunctionalInterface
  public interface ExceptionSupplier<E extends Exception> {
    E get();
  }
}
