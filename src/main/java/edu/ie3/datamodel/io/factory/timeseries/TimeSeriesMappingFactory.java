/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSeriesMappingFactory
    extends EntityFactory<TimeSeriesMappingSource.MappingEntry, SimpleEntityData> {
  private static final String UUID = "uuid";
  private static final String PARTICIPANT = "participant";
  private static final String TIME_SERIES = "timeSeries";

  public TimeSeriesMappingFactory() {
    super(TimeSeriesMappingSource.MappingEntry.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    return Collections.singletonList(
        Stream.of(UUID, PARTICIPANT, TIME_SERIES).collect(Collectors.toSet()));
  }

  @Override
  protected TimeSeriesMappingSource.MappingEntry buildModel(SimpleEntityData data) {
    UUID uuid = data.getUUID(UUID);
    UUID participant = data.getUUID(PARTICIPANT);
    UUID timeSeries = data.getUUID(TIME_SERIES);
    return new TimeSeriesMappingSource.MappingEntry(uuid, participant, timeSeries);
  }
}
