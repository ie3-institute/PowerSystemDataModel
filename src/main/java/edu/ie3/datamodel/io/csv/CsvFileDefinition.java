/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.IoUtil;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record CsvFileDefinition(
    String fileName, Path directoryPath, String[] headLineElements, String csvSep) {
  private static final Logger logger = LoggerFactory.getLogger(CsvFileDefinition.class);

  private static final Pattern FILE_NAME_PATTERN =
      Pattern.compile(
          "^(?<fileName>[^\\\\/\\s.]{0,255})(?:\\.(?<extension>[a-zA-Z0-9]{0,10}(?:\\.[a-zA-Z0-9]{0,10})?))?$");

  private static final String FILE_EXTENSION = "csv";

  public CsvFileDefinition(
      String fileName, Path directoryPath, String[] headLineElements, String csvSep) {
    /* Remove all file separators at the beginning and end of a directory path and ensure harmonized file separator */
    this.directoryPath =
        Path.of(
            Objects.nonNull(directoryPath)
                ? IoUtil.harmonizeFileSeparator(
                    directoryPath
                        .toString()
                        .replaceFirst("^" + IoUtil.FILE_SEPARATOR_REGEX, "")
                        .replaceAll(IoUtil.FILE_SEPARATOR_REGEX + "$", ""))
                : "");

    /* Check the given information of the file name */
    Matcher matcher = FILE_NAME_PATTERN.matcher(fileName);
    if (matcher.matches()) {
      String extension = matcher.group("extension");
      if (Objects.nonNull(extension) && !extension.equalsIgnoreCase(FILE_EXTENSION))
        logger.warn(
            "You provided a file name with extension '{}'. It will be overridden to '{}'.",
            extension,
            FILE_EXTENSION);
      this.fileName = matcher.group("fileName") + "." + FILE_EXTENSION;
    } else {
      throw new IllegalArgumentException(
          "The file name '"
              + fileName
              + "' is no valid file name. It may contain everything, except '/', '\\', '.' and any white space character.");
    }

    this.headLineElements = headLineElements;
    this.csvSep = csvSep;
  }

  /** @deprecated since 3.0. Use {@link #directoryPath()} instead */
  @Deprecated(since = "3.0")
  public Path getDirectoryPath() {
    return directoryPath;
  }

  /**
   * @return The file name including extension
   * @deprecated since 3.0. Use {@link #fileName()} instead
   */
  @Deprecated(since = "3.0")
  public String getFileName() {
    return fileName;
  }

  /**
   * @return The path to the file relative to a not explicitly defined base directory, including the
   *     file extension
   */
  public Path getFilePath() {
    return !directoryPath.toString().isEmpty()
        ? directoryPath.resolve(fileName)
        : Path.of(fileName);
  }

  /** @deprecated since 3.0. Use {@link #headLineElements()} instead */
  @Deprecated(since = "3.0")
  public String[] getHeadLineElements() {
    return headLineElements;
  }

  /** @deprecated since 3.0. Use {@link #csvSep()} instead */
  @Deprecated(since = "3.0")
  public String getCsvSep() {
    return csvSep;
  }

  @Override
  public boolean equals(Object o) {
    // equals implementation is required here because
    // records' equals method and array fields don't play together nicely
    if (this == o) return true;
    if (!(o instanceof CsvFileDefinition that)) return false;
    return directoryPath.equals(that.directoryPath)
        && fileName.equals(that.fileName)
        && Arrays.equals(headLineElements, that.headLineElements)
        && csvSep.equals(that.csvSep);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(directoryPath, fileName, csvSep);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "CsvFileDefinition{"
        + "directoryPath='"
        + directoryPath
        + '\''
        + ", fileName='"
        + fileName
        + '\''
        + ", headLineElements="
        + Arrays.toString(headLineElements)
        + ", csvSep='"
        + csvSep
        + '\''
        + '}';
  }
}
