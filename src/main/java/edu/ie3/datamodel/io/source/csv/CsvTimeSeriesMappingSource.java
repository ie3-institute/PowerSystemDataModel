/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.Map;
import java.util.stream.Stream;

public class CsvTimeSeriesMappingSource extends TimeSeriesMappingSource {

  private final CsvDataSource dataSource;

  public CsvTimeSeriesMappingSource(
      String csvSep, String gridFolderPath, FileNamingStrategy fileNamingStrategy) {
    this.dataSource = new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy);
  }

  @Override
  public Stream<Map<String, String>> getMappingSourceData() {
    return dataSource.buildStreamWithFieldsToAttributesMap(
        MappingEntry.class, dataSource.connector);
  }
}
