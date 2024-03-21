/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.Objects;
import java.util.function.Function;

/**
 * Enhancement of {@link Function} and {@link java.util.function.BiFunction} that accepts three
 * arguments and produces a result.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <D> the type of the fourth argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface QuadFunction<A, B, C, D, R> {

  /**
   * Applies this function to the given arguments.
   *
   * @param a the first function argument
   * @param b the second function argument
   * @param c the third function argument
   * @param d the fourth function argument
   * @return the function result
   */
  R apply(A a, B b, C c, D d);

  /**
   * Returns a composed function that first applies this function to its input, and then applies the
   * {@code after} function to the result. If evaluation of either function throws an exception, it
   * is relayed to the caller of the composed function.
   *
   * @param <V> the type of output of the {@code after} function, and of the composed function
   * @param after the function to apply after this function is applied
   * @return a composed function that first applies this function and then applies the {@code after}
   *     function
   * @throws NullPointerException if after is null
   */
  default <V> QuadFunction<A, B, C, D, V> andThen(Function<? super R, ? extends V> after) {
    Objects.requireNonNull(after);
    return (final A a, final B b, final C c, final D d) -> after.apply(apply(a, b, c, d));
  }
}
