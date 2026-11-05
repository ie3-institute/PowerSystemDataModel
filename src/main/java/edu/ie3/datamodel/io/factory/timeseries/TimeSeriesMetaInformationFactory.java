/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.TimeSeriesMetaInformation;
import java.util.UUID;

/**
 * Factory that creates {@link IndividualTimeSeriesMetaInformation} entities from source field
 * mappings.
 */
public class TimeSeriesMetaInformationFactory<R extends TimeSeriesMetaInformation>
    extends EntityFactory<TimeSeriesMetaInformation, R> {

  private final Class<R> targetClass;

  public TimeSeriesMetaInformationFactory(Class<R> targetClass) {
    super(IndividualTimeSeriesMetaInformation.class, LoadProfileMetaInformation.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected R buildModel(FieldMap data) {
    if (LoadProfileMetaInformation.class.isAssignableFrom(targetClass)) {
      String profile = data.getField(LOAD_PROFILE);
      return (R) new LoadProfileMetaInformation(profile);
    } else {
      UUID timeSeries = data.getUUID(TIME_SERIES);

      ColumnScheme columnScheme = ColumnScheme.parse(data.getField(COLUMN_SCHEME)).orElseThrow();
      return (R) new IndividualTimeSeriesMetaInformation(timeSeries, columnScheme);
    }
  }
}
