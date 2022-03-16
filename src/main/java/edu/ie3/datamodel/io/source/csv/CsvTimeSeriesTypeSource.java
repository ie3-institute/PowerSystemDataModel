/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesTypeSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CSV implementation for retrieving {@link TimeSeriesTypeSource} from input directory structures
 */
public class CsvTimeSeriesTypeSource extends CsvDataSource implements TimeSeriesTypeSource {

  /**
   * Creates a time series type source
   *
   * @param csvSep the CSV separator
   * @param folderPath path that time series reside in
   * @param fileNamingStrategy the file naming strategy
   */
  public CsvTimeSeriesTypeSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);
  }

  @Override
  public Map<UUID, ? extends IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return connector.getIndividualTimeSeriesMetaInformation().entrySet().stream()
        .filter(entry -> TimeSeriesUtils.isSchemeAccepted(entry.getValue().getColumnScheme()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
