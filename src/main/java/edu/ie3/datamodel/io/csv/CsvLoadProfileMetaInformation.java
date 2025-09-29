/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.naming.timeseries.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import java.nio.file.Path;

public class CsvLoadProfileMetaInformation extends FileLoadProfileMetaInformation {
  public CsvLoadProfileMetaInformation(String profile, Path fullFilePath) {
    super(profile, fullFilePath);
  }

  public CsvLoadProfileMetaInformation(
      LoadProfileMetaInformation metaInformation, Path fullFilePath) {
    super(metaInformation, fullFilePath);
  }
}
