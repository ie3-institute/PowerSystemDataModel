/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A definition of a csv file.
 *
 * @param file definition of the file which contains the relative path of the file
 * @param headLineElements elements of the headline of the defined file
 * @param csvSep the separator that is used in this csv file
 */
public record CsvFileDefinition(FileDefinition file, String[] headLineElements, String csvSep) {
  public CsvFileDefinition(
      String fileName, Path directoryPath, String[] headLineElements, String csvSep) {
    this(FileDefinition.ofCsvFile(fileName, directoryPath), headLineElements, csvSep);
  }

  /**
   * @return The path to the file relative to a not explicitly defined base directory, including the
   *     file extension
   */
  public Path getFilePath() {
    return file.fullPath();
  }

  @Override
  public boolean equals(Object o) {
    // equals implementation is required here because
    // records' equals method and array fields don't play together nicely
    if (this == o) return true;
    if (!(o instanceof CsvFileDefinition that)) return false;
    return file.equals(that.file)
        && Arrays.equals(headLineElements, that.headLineElements)
        && csvSep.equals(that.csvSep);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(file, csvSep);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "CsvFileDefinition{"
        + "fullPath='"
        + file.getFile()
        + '\''
        + ", headLineElements="
        + Arrays.toString(headLineElements)
        + ", csvSep='"
        + csvSep
        + '\''
        + '}';
  }
}
