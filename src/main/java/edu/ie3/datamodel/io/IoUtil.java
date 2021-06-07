/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.io.File;

public class IoUtil {
  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";

  private IoUtil() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Ensure to have harmonized file separator across the whole String. Will replace all occurences
   * if "\" and "/" by the systems file separator
   *
   * @param in The String to harmonize
   * @return The harmonized String
   */
  public static String harmonizeFileSeparator(String in) {
    return in.replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT);
  }
}
