/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public abstract class TimeSeriesSource<V extends Value> extends EntitySource {

  protected Class<V> valueClass;
  protected final TimeBasedSimpleValueFactory<V> valueFactory;

  protected TimeSeriesSource(Class<V> valueClass, TimeBasedSimpleValueFactory<V> factory) {
    this.valueFactory = factory;
    this.valueClass = valueClass;
  }

  public abstract IndividualTimeSeries<V> getTimeSeries();

  public abstract IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval)
      throws SourceException;

  public abstract Optional<V> getValue(ZonedDateTime time);

  /**
   * Method to retrieve the value of the given time or the last timestamp before the given time.
   *
   * @param time given time
   * @return an option for a value
   */
  public Optional<V> getValueOrLast(ZonedDateTime time) {
    Optional<V> value = getValue(time);

    if (value.isEmpty()) {
      return getPreviousTimeBasedValue(time).map(TimeBasedValue::getValue);
    }

    return value;
  }

  public abstract Optional<TimeBasedValue<V>> getPreviousTimeBasedValue(ZonedDateTime time);

  /**
   * Method to return all time keys after a given timestamp.
   *
   * @param time given time
   * @return a list of time keys
   */
  public abstract List<ZonedDateTime> getTimeKeysAfter(ZonedDateTime time);

  /**
   * Method to return all last known time keys before a given timestamp.
   *
   * @param time given time
   * @return an option for the time key
   */
  public abstract Optional<ZonedDateTime> getLastTimeKeyBefore(ZonedDateTime time);
}
