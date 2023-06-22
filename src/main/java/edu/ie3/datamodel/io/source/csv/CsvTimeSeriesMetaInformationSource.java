/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CSV implementation for retrieving {@link TimeSeriesMetaInformationSource} from input directory
 * structures
 */
public class CsvTimeSeriesMetaInformationSource implements TimeSeriesMetaInformationSource {

  protected final CsvDataSource dataSource;

  private final Map<UUID, CsvIndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;

  /**
   * Creates a time series type source
   *
   * @param csvSep the CSV separator
   * @param folderPath path that time series reside in
   * @param fileNamingStrategy the file naming strategy
   */
  public CsvTimeSeriesMetaInformationSource(
      String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
    this.dataSource = new CsvDataSource(csvSep, folderPath, fileNamingStrategy);
    // retrieve only the desired time series
    this.timeSeriesMetaInformation =
        dataSource.connector.getCsvIndividualTimeSeriesMetaInformation(
            TimeSeriesUtils.getAcceptedColumnSchemes().toArray(new ColumnScheme[0]));
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return timeSeriesMetaInformation.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(timeSeriesMetaInformation.get(timeSeriesUuid));
  }
}
