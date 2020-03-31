/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io;

import java.util.Arrays;

public class CsvFileDefinition extends FileDefinition {
  private static final String EXTENSION = "csv";

  public CsvFileDefinition(String fileName, String[] headLineElements) {
    super(fileName, EXTENSION, headLineElements);
  }

  @Override
  public String toString() {
    return "CsvFileDefinition{"
        + "fileName='"
        + fileName
        + '\''
        + ", fileExtension='"
        + fileExtension
        + '\''
        + ", headLineElements="
        + Arrays.toString(headLineElements)
        + '}';
  }
}
