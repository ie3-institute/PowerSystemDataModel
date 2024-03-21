/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.FileUtils;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * A definition of a csv file.
 *
 * @param filePath the path of the csv file (including filename and relative path)
 * @param headLineElements elements of the headline of the defined file
 * @param csvSep the separator that is used in this csv file
 */
public record CsvFileDefinition(Path filePath, String[] headLineElements, String csvSep) {
  public CsvFileDefinition(
      String fileName, Path directoryPath, String[] headLineElements, String csvSep) {
    this(FileUtils.ofCsv(fileName, directoryPath), headLineElements, csvSep);
  }

  /**
   * Builds a new file definition consisting of file name and headline elements
   *
   * @param clz Class that is meant to be serialized into this file
   * @param headLineElements Array of headline elements
   * @param csvSep Separator for csv columns
   * @param fileNamingStrategy that should be used
   * @throws FileException If the definition cannot be determined
   */
  public CsvFileDefinition(
      Class<? extends Entity> clz,
      String[] headLineElements,
      String csvSep,
      FileNamingStrategy fileNamingStrategy)
      throws FileException {
    this(
        FileUtils.ofCsv(
            fileNamingStrategy
                .getEntityName(clz)
                .orElseThrow(
                    () ->
                        new FileException(
                            "Cannot determine the file name for class '"
                                + clz.getSimpleName()
                                + "'.")),
            fileNamingStrategy.getDirectoryPath(clz).orElse(Path.of(""))),
        headLineElements,
        csvSep);
  }

  /**
   * Builds a new file definition consisting of file name and headline elements
   *
   * @param timeSeries Time series to derive naming information from
   * @param headLineElements Array of headline elements
   * @param csvSep Separator for csv columns
   * @param fileNamingStrategy that should be used
   * @throws FileException If the definition cannot be determined
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      CsvFileDefinition(
          T timeSeries,
          String[] headLineElements,
          String csvSep,
          FileNamingStrategy fileNamingStrategy)
          throws FileException {
    this(
        FileUtils.ofCsv(
            fileNamingStrategy
                .getEntityName(timeSeries)
                .orElseThrow(
                    () ->
                        new FileException(
                            "Cannot determine the file name for time series '"
                                + timeSeries
                                + "'.")),
            fileNamingStrategy.getDirectoryPath(timeSeries).orElse(Path.of(""))),
        headLineElements,
        csvSep);
  }

  /**
   * @return The path to the file relative to a not explicitly defined base directory, including the
   *     file extension
   */
  public Path getFilePath() {
    return filePath;
  }

  /** Returns the directory path of this file. */
  public Path getDirectoryPath() {
    Path parent = filePath.getParent();
    return parent != null ? parent : Path.of("");
  }

  @Override
  public boolean equals(Object o) {
    // equals implementation is required here because
    // records' equals method and array fields don't play together nicely
    if (this == o) return true;
    if (!(o instanceof CsvFileDefinition that)) return false;
    return filePath.equals(that.filePath)
        && Arrays.equals(headLineElements, that.headLineElements)
        && csvSep.equals(that.csvSep);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(filePath, csvSep);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "CsvFileDefinition{"
        + "fullPath='"
        + filePath
        + '\''
        + ", headLineElements="
        + Arrays.toString(headLineElements)
        + ", csvSep='"
        + csvSep
        + '\''
        + '}';
  }
}
