/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class CollectionUtils {

  private CollectionUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Creates a new set of attribute names from given list of attributes. This method should always
   * be used when returning attribute sets.
   *
   * @param attributes attribute names
   * @return new set exactly containing attribute names
   */
  public static TreeSet<String> newSet(String... attributes) {
    TreeSet<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    set.addAll(Arrays.asList(attributes));
    return set;
  }

  /**
   * Expands a set of attributes with further attributes. This method should always be used when
   * returning attribute sets, i.e. through getting the needed fields. The set maintains a
   * lexicographic order, that is case-insensitive.
   *
   * @param attributeSet set of attributes to expand
   * @param more attribute names to expand given set with
   * @return new set exactly containing given attribute set plus additional attributes
   */
  public static TreeSet<String> expandSet(Collection<String> attributeSet, String... more) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(attributeSet);
    newSet.addAll(Arrays.asList(more));
    return newSet;
  }

  public static Set<String> toSnakeCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(edu.ie3.util.StringUtils::camelCaseToSnakeCase).toList());
    return newSet;
  }

  public static Set<String> toCamelCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(edu.ie3.util.StringUtils::snakeCaseToCamelCase).toList());
    return newSet;
  }

  public static Set<String> toLowerCase(Set<String> set) {
    TreeSet<String> newSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    newSet.addAll(set.stream().map(String::toLowerCase).toList());
    return newSet;
  }
}
