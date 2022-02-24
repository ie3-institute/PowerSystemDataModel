/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.source.TimeSeriesTypeSource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSeriesTypeFactory
    extends EntityFactory<TimeSeriesTypeSource.TypeEntry, SimpleEntityData> {
  private static final String TIME_SERIES = "timeSeries";
  private static final String COLUMN_SCHEME = "columnScheme";

  public TimeSeriesTypeFactory() {
    super(TimeSeriesTypeSource.TypeEntry.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    return Collections.singletonList(
        Stream.of(TIME_SERIES, COLUMN_SCHEME).collect(Collectors.toSet()));
  }

  @Override
  protected TimeSeriesTypeSource.TypeEntry buildModel(SimpleEntityData data) {
    UUID timeSeries = data.getUUID(TIME_SERIES);
    ColumnScheme columnScheme = ColumnScheme.parse(data.getField(COLUMN_SCHEME)).orElseThrow();
    return new TimeSeriesTypeSource.TypeEntry(timeSeries, columnScheme);
  }
}
