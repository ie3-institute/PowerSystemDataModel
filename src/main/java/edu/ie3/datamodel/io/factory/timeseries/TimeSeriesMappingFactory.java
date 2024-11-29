/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TimeSeriesMappingFactory
    extends EntityFactory<TimeSeriesMappingSource.MappingEntry, EntityData> {
  private static final String ASSET = "asset";
  private static final String TIME_SERIES = "timeSeries";

  public TimeSeriesMappingFactory() {
    super(TimeSeriesMappingSource.MappingEntry.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return List.of(newSet(ASSET, TIME_SERIES));
  }

  @Override
  protected TimeSeriesMappingSource.MappingEntry buildModel(EntityData data) {
    UUID asset = data.getUUID(ASSET);
    UUID timeSeries = data.getUUID(TIME_SERIES);
    return new TimeSeriesMappingSource.MappingEntry(asset, timeSeries);
  }
}
