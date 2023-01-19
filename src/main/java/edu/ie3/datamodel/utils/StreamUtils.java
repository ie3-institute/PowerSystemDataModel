/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Class containing some stream utils. */
public class StreamUtils {
  private StreamUtils() {}

  public record Pair<A, B>(A a, B b) {}

  /**
   * Used to zip a stream with an integer stream.
   *
   * @param a the stream that should be zipped
   * @return a stream of pairs of input stream elements and a corresponding integer value
   * @param <A> type of the input stream
   */
  public static <A> Stream<Pair<A, Integer>> zipWithRowIndex(Stream<A> a) {
    return zip(a, getIntStream());
  }

  /**
   * Used to zip two stream with each other.
   *
   * @param a first input stream
   * @param b second input stream
   * @return a stream of pairs of the two input streams
   * @param <A> type of the first input stream
   * @param <B> type of the second input stream
   */
  public static <A, B> Stream<Pair<A, B>> zip(Stream<A> a, Stream<B> b) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            zip(a.iterator(), b.iterator()), Spliterator.ORDERED | Spliterator.NONNULL),
        false);
  }

  /**
   * Used to zip to iterators.
   *
   * @param a first iterator
   * @param b second iterator
   * @return an iterator of pairs of the two input iterators
   * @param <A> type of the first iterator
   * @param <B> type of the second iterator
   */
  public static <A, B> Iterator<Pair<A, B>> zip(Iterator<A> a, Iterator<B> b) {
    return new Iterator<>() {
      public boolean hasNext() {
        return a.hasNext() && b.hasNext(); // This uses the shorter of the two `Iterator`s.
      }

      public Pair<A, B> next() {
        return new Pair<>(a.next(), b.next());
      }
    };
  }

  /** Returns an infinite integer stream. */
  private static Stream<Integer> getIntStream() {
    return Stream.iterate(1, i -> i + 1);
  }
}
