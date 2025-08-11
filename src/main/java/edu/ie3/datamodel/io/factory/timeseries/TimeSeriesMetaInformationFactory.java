/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.naming.TimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Factory that creates {@link IndividualTimeSeriesMetaInformation} entities from source field
 * mappings
 */
public class TimeSeriesMetaInformationFactory
    extends EntityFactory<TimeSeriesMetaInformation, EntityData> {
  private static final String TIME_SERIES = "timeSeries";
  private static final String COLUMN_SCHEME = "columnScheme";
  private static final String LOAD_PROFILE = "loadProfile";

  /** Instantiates a new Time series meta information factory. */
  public TimeSeriesMetaInformationFactory() {
    super(IndividualTimeSeriesMetaInformation.class, LoadProfileMetaInformation.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return Collections.singletonList(
        Stream.of(TIME_SERIES, COLUMN_SCHEME).collect(Collectors.toSet()));
  }

  @Override
  protected TimeSeriesMetaInformation buildModel(EntityData data) {
    if (LoadProfileMetaInformation.class.isAssignableFrom(data.getTargetClass())) {
      String profile = data.getField(LOAD_PROFILE);
      return new LoadProfileMetaInformation(profile);
    } else {
      UUID timeSeries = data.getUUID(TIME_SERIES);

      ColumnScheme columnScheme = ColumnScheme.parse(data.getField(COLUMN_SCHEME)).orElseThrow();
      return new IndividualTimeSeriesMetaInformation(timeSeries, columnScheme);
    }
  }
}
