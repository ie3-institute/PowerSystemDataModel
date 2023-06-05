/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.IoUtil;
import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Definition of a file. */
public record FileDefinition(String fileName, Path directoryPath, Path fullPath) {
  private static final Logger logger = LoggerFactory.getLogger(FileDefinition.class);

  /**
   * Constructor for the {@link FileDefinition}.
   *
   * @param fileName name of the file
   * @param directoryPath option for the directory path
   */
  private FileDefinition(String fileName, Path directoryPath) {
    this(fileName, directoryPath, directoryPath.resolve(fileName));
  }

  /**
   * Method to create a {@link FileDefinition} from a filename and an option of a directory path.
   *
   * @param fileName of the file
   * @param directoryPath option for the directory path
   * @return a definition of a file
   */
  public static FileDefinition of(String fileName, Optional<Path> directoryPath) {
    return new FileDefinition(fileName, IoUtil.harmonizeFileSeparator(directoryPath));
  }

  /**
   * Method to create a {@link FileDefinition}.
   *
   * @param fileName option for a filename
   * @param directoryPath option for a directory path
   * @return a definition of a file
   */
  public static FileDefinition of(Optional<String> fileName, Optional<Path> directoryPath) {
    return new FileDefinition(
        fileName.orElse(""), IoUtil.harmonizeFileSeparator(directoryPath));
  }

  /**
   * Method to create a {@link FileDefinition} for a csv file. This method will check whether the
   * filename contains a csv extension. Also, this method will harmonize the path of the given
   * directory path.
   *
   * @param fileName of the file
   * @param directoryPath path to the directory
   * @return a definition of the file
   */
  public static FileDefinition ofCsvFile(String fileName, Path directoryPath) {
    Pattern FILE_NAME_PATTERN =
        Pattern.compile(
            "^(?<fileName>[^\\\\/\\s.]{0,255})(?:\\.(?<extension>[a-zA-Z0-9]{0,10}(?:\\.[a-zA-Z0-9]{0,10})?))?$");
    String FILE_EXTENSION = "csv";

    /* Remove all file separators at the beginning and end of a directory path and ensure harmonized file separator */
    Path dirPath =
        Objects.nonNull(directoryPath)
            ? IoUtil.harmonizeFileSeparator(Optional.of(directoryPath))
            : Path.of("");

    /* Check the given information of the file name */
    Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);

    if (matcher.matches()) {
      String extension = matcher.group("extension");
      if (Objects.nonNull(extension) && !extension.equalsIgnoreCase(FILE_EXTENSION))
        logger.warn(
            "You provided a file name with extension '{}'. It will be overridden to '{}'.",
            extension,
            FILE_EXTENSION);
      return new FileDefinition(matcher.group("fileName") + "." + FILE_EXTENSION, dirPath);
    } else {
      throw new IllegalArgumentException(
          "The file name '"
              + fileName
              + "' is no valid file name. It may contain everything, except '/', '\\', '.' and any white space character.");
    }
  }

  public Optional<Path> getPathOption() {
    return Optional.of(fullPath);
  }

  /** Returns the defined file. */
  public File getFile() {
    return fullPath.toFile();
  }
}
