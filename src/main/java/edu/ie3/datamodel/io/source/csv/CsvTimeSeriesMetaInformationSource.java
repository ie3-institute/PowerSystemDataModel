/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CSV implementation for retrieving {@link TimeSeriesMetaInformationSource} from input directory
 * structures
 */
public class CsvTimeSeriesMetaInformationSource extends TimeSeriesMetaInformationSource {

  private final Map<UUID, IndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;

  public CsvTimeSeriesMetaInformationSource(
          String csvSep,
          String gridFolderPath,
          FileNamingStrategy fileNamingStrategy
  ) {
    super(new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy));
    // retrieve only the desired time series
    timeSeriesMetaInformation = null;
    //timeSeriesMetaInformation = dataSource.connector.getIndividualTimeSeriesMetaInformation(TimeSeriesUtils.getAcceptedColumnSchemes().toArray(new ColumnScheme[0]));
  }

  /*
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return timeSeriesMetaInformation.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }


  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(timeSeriesMetaInformation.get(timeSeriesUuid));
  }

   */
}
