/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSeriesMappingFactory
    extends EntityFactory<TimeSeriesMappingSource.MappingEntry, EntityData> {
  private static final String ENTITY = "entity";
  private static final String PARTICIPANT = "participant";
  private static final String TIME_SERIES = "timeSeries";

  public TimeSeriesMappingFactory() {
    super(TimeSeriesMappingSource.MappingEntry.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(
        Stream.of(ENTITY, TIME_SERIES).collect(Collectors.toSet()),
        Stream.of(PARTICIPANT, TIME_SERIES).collect(Collectors.toSet()));
  }

  @Override
  protected TimeSeriesMappingSource.MappingEntry buildModel(EntityData data) {
    UUID timeSeries = data.getUUID(TIME_SERIES);

    try {
      UUID entity = data.getUUID(ENTITY);
      return new TimeSeriesMappingSource.EntityMappingEntry(entity, timeSeries);
    } catch (FactoryException e) {
      UUID participant = data.getUUID(PARTICIPANT);
      return new TimeSeriesMappingSource.ParticipantMappingEntry(participant, timeSeries);
    }
  }
}
