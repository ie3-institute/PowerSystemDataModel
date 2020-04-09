/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvFileDefinition {
  private static final Pattern fileNamePattern = Pattern.compile("[\\w\\\\/-]+");
  private static final Pattern fullPathPattern =
      Pattern.compile("(" + fileNamePattern.pattern() + ")\\.+(\\w+)");

  protected static final String FILE_EXTENSION = "csv";

  protected final String fileName;
  protected final String[] headLineElements;
  protected final String csvSep;

  public CsvFileDefinition(String fileName, String[] headLineElements, String csvSep) {
    Matcher fullPathMatcher = fullPathPattern.matcher(fileName);
    if (fullPathMatcher.matches()) {
      this.fileName = fullPathMatcher.group(0).replaceAll("\\\\/", File.separator);
    } else if (fileName.matches(fileNamePattern.pattern())) {
      this.fileName = fileName.replaceAll("\\\\/", File.separator);
    } else {
      throw new IllegalArgumentException(
          "The file name \"" + fileName + "\" is no valid file name.");
    }

    this.headLineElements = headLineElements;
    this.csvSep = csvSep;
  }

  public String getFileName() {
    return fileName;
  }

  public String getFilePath() {
    return fileName + "." + FILE_EXTENSION;
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
    if (o == null || getClass() != o.getClass()) return false;
    CsvFileDefinition that = (CsvFileDefinition) o;
    return fileName.equals(that.fileName)
        && Arrays.equals(headLineElements, that.headLineElements)
        && csvSep.equals(that.csvSep);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(fileName, csvSep);
    result = 31 * result + Arrays.hashCode(headLineElements);
    return result;
  }

  @Override
  public String toString() {
    return "CsvFileDefinition{"
        + "fileName='"
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
