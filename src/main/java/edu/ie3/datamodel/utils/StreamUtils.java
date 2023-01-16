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

public class StreamUtils {
  private StreamUtils() {}

  private static final Stream<Integer> intStream = Stream.iterate(0, i -> i + 1);

  public record Pair<A, B>(A a, B b) {}

  public static <A> Stream<Pair<A, Integer>> zipWithRowIndex(Stream<A> a) {
    return zip(a, intStream);
  }

  public static <A, B> Stream<Pair<A, B>> zip(Stream<A> a, Stream<B> b) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            zip(a.iterator(), b.iterator()), Spliterator.ORDERED | Spliterator.NONNULL),
        false);
  }

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
}
