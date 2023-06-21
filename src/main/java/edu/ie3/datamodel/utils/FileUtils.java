/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.io.IoUtil;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Some utility functionalities. */
public class FileUtils {
  public static final Pattern FILE_NAME_PATTERN =
      Pattern.compile(
          "^(?<fileName>[^\\\\/\\s.]{0,255})(?:\\.(?<extension>[a-zA-Z0-9]{0,10}(?:\\.[a-zA-Z0-9]{0,10})?))?$");
  public static final String CSV_FILE_EXTENSION = "csv";

  private FileUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Method to get a {@link Path} from a filename and an option of a directory path.
   *
   * @param fileName of the file
   * @param directoryPath option for the directory path
   * @return a definition of a file
   */
  public static Path of(String fileName, Optional<Path> directoryPath) {
    return directoryPath.map(IoUtil::harmonizeFileSeparator).orElse(Path.of("")).resolve(fileName);
  }

  /**
   * Method to get a {@link Path} when two {@link Optional}'s are provided.
   *
   * @param fileName option for a filename
   * @param directoryPath option for a directory path
   * @return an option for a path
   */
  public static Optional<Path> of(Optional<String> fileName, Optional<Path> directoryPath) {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return Optional.of(
        directoryPath
            .map(IoUtil::harmonizeFileSeparator)
            .orElseGet(() -> Path.of(""))
            .resolve(fileName.orElseGet(() -> "")));
  }

  /**
   * Method to get the {@link Path} of a csv file. This method will check whether the filename
   * contains a csv extension. Also, this method will harmonize the path of the given directory
   * path.
   *
   * @param fileName of the file
   * @param directoryPath path to the directory
   * @return a definition of the file
   */
  public static Path ofCsv(String fileName, Path directoryPath) {
    /* Remove all file separators at the beginning and end of a directory path and ensure harmonized file separator */
    Path dirPath =
        Objects.nonNull(directoryPath) ? IoUtil.harmonizeFileSeparator(directoryPath) : Path.of("");

    /* Check the given information of the file name */
    Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);

    if (matcher.matches()) {
      String extension = matcher.group("extension");
      if (Objects.nonNull(extension) && !extension.equalsIgnoreCase(CSV_FILE_EXTENSION))
        logger.warn(
            "You provided a file name with extension '{}'. It will be overridden to '{}'.",
            extension,
            CSV_FILE_EXTENSION);
      return dirPath.resolve(matcher.group("fileName") + "." + CSV_FILE_EXTENSION);
    } else {
      throw new IllegalArgumentException(
          "The file name '"
              + fileName
              + "' is no valid file name. It may contain everything, except '/', '\\', '.' and any white space character.");
    }
  }
}
