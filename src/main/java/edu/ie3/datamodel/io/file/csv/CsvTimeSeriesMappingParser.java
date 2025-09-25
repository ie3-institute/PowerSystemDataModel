/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.TimeSeriesMappingParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class CsvTimeSeriesMappingParser implements TimeSeriesMappingParser {

  private final CsvDataSource dataSource;

  public CsvTimeSeriesMappingParser(
      Path path, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    Objects.requireNonNull(path, "path must not be null");
    Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy must not be null");
    Objects.requireNonNull(csvSeparator, "csvSeparator must not be null");
    this.dataSource = new CsvDataSource(csvSeparator, path, fileNamingStrategy);
  }

  @Override
  public Stream<Map<String, String>> parse() throws SourceException {
    return dataSource.getSourceData(TimeSeriesMappingSource.MappingEntry.class);
  }

  @Override
  public Optional<Set<String>> availableFields() throws SourceException {
    return dataSource.getSourceFields(TimeSeriesMappingSource.MappingEntry.class);
  }

  public CsvDataSource getDataSource() {
    return dataSource;
  }
}
