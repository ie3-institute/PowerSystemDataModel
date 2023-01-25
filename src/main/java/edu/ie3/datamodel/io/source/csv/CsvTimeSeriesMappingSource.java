/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;

public class CsvTimeSeriesMappingSource extends TimeSeriesMappingSource {
  public CsvTimeSeriesMappingSource(
          String csvSep,
          String gridFolderPath,
          FileNamingStrategy fileNamingStrategy
  ) {
    super(new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy));
  }
}
