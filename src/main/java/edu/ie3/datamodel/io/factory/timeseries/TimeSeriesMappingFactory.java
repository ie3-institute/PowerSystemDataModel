/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.util.UUID;

public class TimeSeriesMappingFactory
    extends EntityFactory<TimeSeriesMappingSource.MappingEntry, EntityData> {

  public TimeSeriesMappingFactory() {
    super(TimeSeriesMappingSource.MappingEntry.class);
  }

  @Override
  protected TimeSeriesMappingSource.MappingEntry buildModel(EntityData data) {
    UUID asset = data.getUUID(ASSET);
    UUID timeSeries = data.getUUID(TIME_SERIES);
    return new TimeSeriesMappingSource.MappingEntry(asset, timeSeries);
  }
}
