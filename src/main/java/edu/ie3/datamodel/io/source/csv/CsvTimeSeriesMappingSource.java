/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileTimeSeriesParserFactory;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.file.TimeSeriesMappingParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class CsvTimeSeriesMappingSource extends TimeSeriesMappingSource {

  private final TimeSeriesMappingParser parser;

  public CsvTimeSeriesMappingSource(
      Path path, FileType fileType, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    this(path, fileType, fileNamingStrategy, csvSeparator, new FileTimeSeriesParserFactory());
  }

  CsvTimeSeriesMappingSource(
      Path path,
      FileType fileType,
      FileNamingStrategy fileNamingStrategy,
      String csvSeparator,
      FileTimeSeriesParserFactory parserFactory) {
    String effectiveSeparator = csvSeparator == null ? "," : csvSeparator;
    this.parser =
        parserFactory.mappingParser(path, fileType, fileNamingStrategy, effectiveSeparator);
  }

  @Override
  public Stream<Map<String, String>> getMappingSourceData() throws SourceException {
    return parser.parse();
  }

  @Override
  public Optional<Set<String>> getSourceFields() throws SourceException {
    return parser.availableFields();
  }
}
