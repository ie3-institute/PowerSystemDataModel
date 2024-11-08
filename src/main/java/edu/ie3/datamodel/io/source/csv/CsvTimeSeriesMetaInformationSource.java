/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.csv.CsvLoadProfileMetaInformation;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * CSV implementation for retrieving {@link TimeSeriesMetaInformationSource} from input directory
 * structures
 */
public class CsvTimeSeriesMetaInformationSource implements TimeSeriesMetaInformationSource {

  protected final CsvDataSource dataSource;

  private final Map<UUID, CsvIndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;

  private final Map<String, CsvLoadProfileMetaInformation> loadProfileMetaInformation;

  /**
   * Creates a time series type source
   *
   * @param csvSep the CSV separator
   * @param folderPath path that time series reside in
   * @param fileNamingStrategy the file naming strategy
   */
  public CsvTimeSeriesMetaInformationSource(
      String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
    this(new CsvDataSource(csvSep, folderPath, fileNamingStrategy));
  }

  /**
   * Creates a time series type source
   *
   * @param dataSource a csv data source
   */
  public CsvTimeSeriesMetaInformationSource(CsvDataSource dataSource) {
    this.dataSource = dataSource;
    // retrieve only the desired time series
    this.timeSeriesMetaInformation =
        dataSource.getCsvIndividualTimeSeriesMetaInformation(
            TimeSeriesUtils.getAcceptedColumnSchemes().toArray(new ColumnScheme[0]));

    this.loadProfileMetaInformation =
        dataSource.getCsvLoadProfileMetaInformation().values().stream()
            .collect(
                Collectors.toMap(
                    LoadProfileTimeSeriesMetaInformation::getProfile, Function.identity()));
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return Collections.unmodifiableMap(timeSeriesMetaInformation);
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(timeSeriesMetaInformation.get(timeSeriesUuid));
  }

  @Override
  public Map<String, LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation() {
    return Collections.unmodifiableMap(loadProfileMetaInformation);
  }

  @Override
  public Optional<LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation(
      LoadProfile loadProfile) {
    return Optional.ofNullable(loadProfileMetaInformation.get(loadProfile.getKey()));
  }
}
