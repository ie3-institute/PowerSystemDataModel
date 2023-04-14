/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CsvTimeSeriesMappingSource extends CsvDataSource implements TimeSeriesMappingSource {
  /* Available factories */
  private static final TimeSeriesMappingFactory mappingFactory = new TimeSeriesMappingFactory();

  private final Map<UUID, UUID> mapping;

  public CsvTimeSeriesMappingSource(
      String csvSep, Path folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);

    /* Build the map */
    mapping =
        buildStreamWithFieldsToAttributesMap(MappingEntry.class, connector)
            .map(
                fieldToValues -> {
                  SimpleEntityData entityData =
                      new SimpleEntityData(fieldToValues, MappingEntry.class);
                  return mappingFactory.get(entityData);
                })
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(MappingEntry::getParticipant, MappingEntry::getTimeSeries));
  }

  @Override
  public Map<UUID, UUID> getMapping() {
    return mapping;
  }
}
