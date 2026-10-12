/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry;
import java.util.Map;
import java.util.UUID;

public class TimeSeriesMappingFactory extends EntityFactory<MappingEntry, MappingEntry> {

  public TimeSeriesMappingFactory() {
    super(MappingEntry.class);
  }

  @Override
  protected MappingEntry buildModel(Map<String, String> data) {
    UUID asset = getUUID(data, ASSET);
    UUID timeSeries = getUUID(data, TIME_SERIES);
    return new MappingEntry(asset, timeSeries);
  }
}
