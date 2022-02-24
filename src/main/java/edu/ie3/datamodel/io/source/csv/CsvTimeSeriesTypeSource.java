/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesTypeSource;
import edu.ie3.datamodel.io.source.TimeSeriesUtils;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CsvTimeSeriesTypeSource extends CsvDataSource implements TimeSeriesTypeSource {

  protected CsvTimeSeriesTypeSource(
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
