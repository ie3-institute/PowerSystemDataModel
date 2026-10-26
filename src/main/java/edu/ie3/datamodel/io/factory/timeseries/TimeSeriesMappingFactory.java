/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource.MappingEntry;
import java.util.UUID;

public class TimeSeriesMappingFactory extends EntityFactory<MappingEntry, MappingEntry> {

  public TimeSeriesMappingFactory() {
    super(MappingEntry.class);
  }

  @Override
  protected MappingEntry buildModel(FieldMap data) {
    UUID asset = data.getUUID(ASSET);
    UUID timeSeries = data.getUUID(TIME_SERIES);
    return new MappingEntry(asset, timeSeries);
  }
}
