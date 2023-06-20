/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public class IoUtil {
  public static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  public static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";

  private IoUtil() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Ensure to have harmonized file separator across the whole String. Will replace all occurrences
   * of "\" and "/" by the systems file separator.
   *
   * @param in The String to harmonize
   * @return The harmonized String
   */
  public static String harmonizeFileSeparator(String in) {
    return in.replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT);
  }

  /**
   * Ensure to have harmonized file separator across the whole path. Will replace all occurrences *
   * of "\" and "/" by the systems file separator.
   *
   * @param path an option for a path to harmonize
   * @return the option for a harmonized path
   */
  public static Path harmonizeFileSeparator(Path path) {
    String in = path.toString();
    in =
        IoUtil.FILE_SEPARATOR_REPLACEMENT.equals("\\\\")
            ? in.replaceFirst("^" + IoUtil.FILE_SEPARATOR_REGEX, "")
            : in;
    return Path.of(IoUtil.harmonizeFileSeparator(in));
  }

  /**
   * Method to wrap a string of a path in an option for a path.
   *
   * @param in string of the path
   * @return option of the path
   */
  public static Optional<Path> pathOption(String in) {
    return Optional.of(Path.of(in));
  }
}
