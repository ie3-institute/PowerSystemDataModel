/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

import edu.ie3.datamodel.io.file.csv.CsvTimeSeriesMappingParser;
import edu.ie3.datamodel.io.file.csv.CsvTimeSeriesMetaInformationParser;
import edu.ie3.datamodel.io.file.json.JsonTimeSeriesMappingParser;
import edu.ie3.datamodel.io.file.json.JsonTimeSeriesMetaInformationParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import java.nio.file.Path;

public class FileTimeSeriesParserFactory {

  public TimeSeriesMetaInformationParser metaInformationParser(
      Path path, FileType fileType, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    return switch (fileType) {
      case CSV -> new CsvTimeSeriesMetaInformationParser(path, fileNamingStrategy, csvSeparator);
      case JSON -> new JsonTimeSeriesMetaInformationParser(path, fileNamingStrategy);
    };
  }

  public TimeSeriesMappingParser mappingParser(
      Path path, FileType fileType, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    return switch (fileType) {
      case CSV -> new CsvTimeSeriesMappingParser(path, fileNamingStrategy, csvSeparator);
      case JSON -> new JsonTimeSeriesMappingParser(path, fileNamingStrategy);
    };
  }
}
