/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.util;

import java.util.Arrays;

/**
 * Some useful functions to manipulate Strings
 *
 * <p>TODO: Move to PowerSystemUtils
 */
public class StringUtils {
  private StringUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated.");
  }

  /**
   * Converts a given camel case string to its snake case representation
   *
   * @param camelCaseString the camel case string
   * @return the resulting snake case representation
   */
  public static String camelCaseToSnakeCase(String camelCaseString) {
    String regularCamelCaseRegex = "([a-z])([A-Z]+)";
    String regularSnakeCaseReplacement = "$1_$2";
    String specialCamelCaseRegex = "((?<!_)[A-Z]?)((?<!^)[A-Z]+)";
    String specialSnakeCaseReplacement = "$1_$2";
    return camelCaseString
        .replaceAll(regularCamelCaseRegex, regularSnakeCaseReplacement)
        .replaceAll(specialCamelCaseRegex, specialSnakeCaseReplacement)
        .toLowerCase();
  }

  /**
   * Converts an Array of camel case strings to its snake case representations
   *
   * @param input Array of Strings to convert
   * @return Array of converted Strings
   */
  public static String[] camelCaseToSnakeCase(String[] input) {
    return Arrays.stream(input).map(StringUtils::camelCaseToSnakeCase).toArray(String[]::new);
  }

  /**
   * Adds quotation marks at the beginning and end of the input, if they are not apparent, yet.
   *
   * @param input String to quote
   * @return Quoted String
   */
  public static String quote(String input) {
    return input.replaceAll("^([^\"])", "\"$1").replaceAll("([^\"])$", "$1\"");
  }

  /**
   * Quotes all entries of the Array
   *
   * @param input Array of Strings to quote
   * @return Array of quoted Strings
   */
  public static String[] quote(String[] input) {
    return Arrays.stream(input).map(StringUtils::quote).toArray(String[]::new);
  }
}
