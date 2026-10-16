/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.TimeSeriesMetaInformation;
import java.util.Map;
import java.util.UUID;

/**
 * Factory that creates {@link IndividualTimeSeriesMetaInformation} entities from source field
 * mappings.
 */
public class TimeSeriesMetaInformationFactory<R extends TimeSeriesMetaInformation>
    extends EntityFactory<TimeSeriesMetaInformation, R> {

  private final Class<? extends TimeSeriesMetaInformation> targetClass;

  public TimeSeriesMetaInformationFactory(Class<? extends TimeSeriesMetaInformation> targetClass) {
    super(IndividualTimeSeriesMetaInformation.class, LoadProfileMetaInformation.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected R buildModel(Map<String, String> data) {
    if (LoadProfileMetaInformation.class.isAssignableFrom(targetClass)) {
      String profile = getField(data, LOAD_PROFILE);
      return (R) new LoadProfileMetaInformation(profile);
    } else {
      UUID timeSeries = getUUID(data, TIME_SERIES);

      ColumnScheme columnScheme = ColumnScheme.parse(getField(data, COLUMN_SCHEME)).orElseThrow();
      return (R) new IndividualTimeSeriesMetaInformation(timeSeries, columnScheme);
    }
  }
}
