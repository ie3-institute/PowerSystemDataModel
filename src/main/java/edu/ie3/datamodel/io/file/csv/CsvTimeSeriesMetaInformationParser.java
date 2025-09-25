/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.file.TimeSeriesMetaInformationParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.csv.CsvDataSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CsvTimeSeriesMetaInformationParser implements TimeSeriesMetaInformationParser {

  private final CsvDataSource dataSource;

  public CsvTimeSeriesMetaInformationParser(
      Path path, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    Objects.requireNonNull(path, "path must not be null");
    Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy must not be null");
    Objects.requireNonNull(csvSeparator, "csvSeparator must not be null");
    this.dataSource = new CsvDataSource(csvSeparator, path, fileNamingStrategy);
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> parseIndividualTimeSeriesMetaInformation(
      ColumnScheme... columnSchemes) throws SourceException {
    Map<UUID, ? extends IndividualTimeSeriesMetaInformation> csvMetaInformation =
        dataSource.getCsvIndividualTimeSeriesMetaInformation(columnSchemes);
    return csvMetaInformation.entrySet().stream()
        .collect(
            java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                entry -> (IndividualTimeSeriesMetaInformation) entry.getValue()));
  }

  @Override
  public Map<String, FileLoadProfileMetaInformation> parseLoadProfileMetaInformation(
      LoadProfile... profiles) throws SourceException {
    return dataSource.getLoadProfileMetaInformation(profiles);
  }

  public CsvDataSource getDataSource() {
    return dataSource;
  }
}
