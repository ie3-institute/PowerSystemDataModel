/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Utilities for collections. */
public class CollectionUtils {
  private CollectionUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Method to map a {@link Collection} of {@link UniqueEntity}
   *
   * @param c collection to map
   * @return a map: UUID to {@link UniqueEntity}
   * @param <V> type of unique entity
   */
  public static <V extends UniqueEntity> Map<UUID, V> toMap(Collection<V> c) {
    return toMap(c, UniqueEntity::getUuid);
  }

  /**
   * Method to map a {@link Collection} to a specific field.
   *
   * @param c collection to map
   * @param keyExtractor function to extract a key
   * @return a map: key to values
   * @param <K> type of key
   * @param <V> type of value
   */
  public static <K, V> Map<K, V> toMap(Collection<V> c, Function<V, K> keyExtractor) {
    return c.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
  }

  /**
   * Method to group a collection to keys.
   *
   * @param c collection to group
   * @param keyExtractor function to extract a key
   * @return a map: key to set of values
   * @param <K> type of key
   * @param <V> type of value
   */
  public static <K, V> Map<K, Set<V>> groupBy(Collection<V> c, Function<V, K> keyExtractor) {
    return c.stream().collect(Collectors.groupingBy(keyExtractor)).entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, v -> new HashSet<>(v.getValue())));
  }
}
