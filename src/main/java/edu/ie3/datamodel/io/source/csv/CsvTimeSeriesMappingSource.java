/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CsvTimeSeriesMappingSource extends TimeSeriesMappingSource {

    protected CsvDataSource dataSource;

  public CsvTimeSeriesMappingSource(
          String csvSep,
          String gridFolderPath,
          FileNamingStrategy fileNamingStrategy
  ) {
    this.dataSource = new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy);
  }

  @Override
  public Map<UUID, UUID> getMapping() {
    return dataSource.buildStreamWithFieldsToAttributesMap(MappingEntry.class, dataSource.connector)
           .map(
                    fieldToValues -> {
                      SimpleEntityData entityData =
                              new SimpleEntityData(fieldToValues, MappingEntry.class);
                      return mappingFactory.get(entityData);
                    })
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(MappingEntry::getParticipant, MappingEntry::getTimeSeries));
  }
}
