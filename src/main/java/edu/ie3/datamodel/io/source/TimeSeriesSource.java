/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public abstract class TimeSeriesSource<V extends Value> implements DataSource {

  /**
   * Checks whether the given column scheme can be used with time series.
   *
   * @param scheme the column scheme to check
   * @return whether the scheme is accepted or not
   * @deprecated since 3.0. Use {@link TimeSeriesUtils#isSchemeAccepted(ColumnScheme)} instead.
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public static boolean isSchemeAccepted(edu.ie3.datamodel.io.csv.timeseries.ColumnScheme scheme) {
    return EnumSet.of(
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ACTIVE_POWER,
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.APPARENT_POWER,
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ENERGY_PRICE,
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND,
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND,
            edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.HEAT_DEMAND)
        .contains(scheme);
  }

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @param valueClass Class of the desired underlying value
   * @param factory Factory to process the "flat" information
   * @return Optional simple time based value
   */
  public Optional<TimeBasedValue<V>> buildTimeBasedValue(
      Map<String, String> fieldToValues,
      Class<V> valueClass,
      TimeBasedSimpleValueFactory<V> factory) {
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return factory.get(factoryData);
  }

  public abstract IndividualTimeSeries<V> getTimeSeries();

  public abstract IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval);

  public abstract Optional<V> getValue(ZonedDateTime time);
}
