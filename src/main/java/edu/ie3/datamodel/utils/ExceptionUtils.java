/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.List;

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
        .reduce("", (a, b) -> a + ", " + b)
        .replaceFirst(", ", "");
  }
}
