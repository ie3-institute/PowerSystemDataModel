/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.io.file.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import java.nio.file.Path;

public class CsvLoadProfileMetaInformation extends FileLoadProfileMetaInformation {
  public CsvLoadProfileMetaInformation(String profile, Path fullFilePath) {
    super(profile, fullFilePath, FileType.CSV);
  }

  public CsvLoadProfileMetaInformation(
      LoadProfileMetaInformation metaInformation, Path fullFilePath) {
    super(metaInformation, fullFilePath, FileType.CSV);
  }
}
