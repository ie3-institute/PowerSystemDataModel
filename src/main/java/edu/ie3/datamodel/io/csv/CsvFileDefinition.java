/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFileDefinition {
  private static final Logger logger = LoggerFactory.getLogger(CsvFileDefinition.class);

  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";
  private static final Pattern FILE_NAME_PATTERN =
      Pattern.compile(
          "^(?<fileName>[^\\\\/\\s.]{0,255})(?:\\.(?<extension>[a-zA-Z0-9]{0,10}(?:\\.[a-zA-Z0-9]{0,10})?))?$");

  protected static final String FILE_EXTENSION = "csv";

  private final String directoryPath;
  private final String fileName;
  private final String[] headLineElements;
  private final String csvSep;

  public CsvFileDefinition(
      String fileName, String directoryPath, String[] headLineElements, String csvSep) {
    /* Remove all file separators at the beginning and end of a directory path and ensure harmonized file separator */
    this.directoryPath =
        Objects.nonNull(directoryPath)
            ? directoryPath
                .replaceFirst("^" + FILE_SEPARATOR_REGEX, "")
                .replaceAll(FILE_SEPARATOR_REGEX + "$", "")
                .replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT)
            : "";

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

  public String getDirectoryPath() {
    return directoryPath;
  }

  /** @return The file name including extension */
  public String getFileName() {
    return fileName;
  }

  /**
   * @return The path to the file relative to a not explicitly defined base directory, including the
   *     file extension
   */
  public String getFilePath() {
    return !directoryPath.isEmpty() ? FilenameUtils.concat(directoryPath, fileName) : fileName;
  }

  public String[] getHeadLineElements() {
    return headLineElements;
  }

  public String getCsvSep() {
    return csvSep;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CsvFileDefinition)) return false;
    CsvFileDefinition that = (CsvFileDefinition) o;
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
