/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ExceptionUtils {
  private ExceptionUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Creates a string containing multiple exception messages.
   *
   * @param exceptions list of exceptions
   * @return str containing the messages
   */
  public static String getMessages(List<? extends Exception> exceptions) {
    return exceptions.stream()
        .map(Throwable::getMessage)
        .reduce("", (a, b) -> a + "\n " + b)
        .replaceFirst("\n ", "");
  }

  /**
   * Creates a string containing multiple exception messages.
   *
   * @param exceptions list of exceptions
   * @return str containing the messages
   */
  public static String getFullMessages(List<? extends Exception> exceptions) {
    return exceptions.stream()
        .map(e -> e.getMessage() + printStackTrace(e.getStackTrace()))
        .reduce("", (a, b) -> a + "\n " + b)
        .replaceFirst("\n ", "");
  }

  /**
   * Combines multiple {@link UniqueEntity} into a string.
   *
   * @param entities to be combined
   * @return a string
   */
  public static String combine(Collection<? extends UniqueEntity> entities) {
    return "{"
        + entities.stream().map(UniqueEntity::toString).collect(Collectors.joining(", "))
        + "}";
  }

  /**
   * Method for combining {@link StackTraceElement}s.
   *
   * @param elements to be combined
   * @return a string
   */
  public static String printStackTrace(StackTraceElement... elements) {
    return Arrays.stream(elements)
        .map(StackTraceElement::toString)
        .collect(Collectors.joining("\n  "));
  }
}
