/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CsvTimeSeriesMappingSource extends TimeSeriesMappingSource {

  private final CsvDataSource dataSource;

  public CsvTimeSeriesMappingSource(
      String csvSep, Path gridFolderPath, FileNamingStrategy fileNamingStrategy)
      throws SourceException {
    this.dataSource = new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy);

    // validating
    mappingFactory.validate(getSourceFields(), MappingEntry.class);
  }

  @Override
  public Stream<Map<String, String>> getMappingSourceData() {
    return dataSource.buildStreamWithFieldsToAttributesMap(
        MappingEntry.class, dataSource.connector);
  }

  @Override
  public Set<String> getSourceFields() throws SourceException {
    return dataSource.getSourceFields(MappingEntry.class);
  }
}
