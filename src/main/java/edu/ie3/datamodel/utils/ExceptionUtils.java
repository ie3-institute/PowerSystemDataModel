/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.List;
import java.util.function.Function;

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
  public static String combineExceptions(List<? extends Exception> exceptions) {
    String messageSeparator = "\n ";

    // function to convert an exception into a string
    Function<Exception, String> converter =
        e -> {
          String clazz = e.getClass().getName();
          String message = e.getMessage();
          Throwable cause = e.getCause();

          String res = clazz + ": " + message;

          if (cause != null) {
            res += " Caused by: " + cause.getClass().getName() + ": " + cause.getMessage();
          }

          return res;
        };

    String messages =
        exceptions.stream().map(converter).reduce("", (a, b) -> a + messageSeparator + b);

    // some formating
    return messages.replace("\n", "\n       ").replaceFirst(messageSeparator, "");
  }
}
