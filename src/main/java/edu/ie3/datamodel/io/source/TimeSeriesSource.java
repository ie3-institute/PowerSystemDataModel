/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 *
 * @param <V> the type parameter
 */
public abstract class TimeSeriesSource<V extends Value> extends EntitySource {

  /** The Value class. */
  protected Class<V> valueClass;

  /** The Value factory. */
  protected final TimeBasedSimpleValueFactory<V> valueFactory;

  /**
   * Instantiates a new Time series source.
   *
   * @param valueClass the value class
   * @param factory the factory
   */
  protected TimeSeriesSource(Class<V> valueClass, TimeBasedSimpleValueFactory<V> factory) {
    this.valueFactory = factory;
    this.valueClass = valueClass;
  }

  /**
   * Build a {@link TimeBasedValue} of type {@code V}, whereas the underlying {@link Value} does not
   * need any additional information.
   *
   * @param fieldToValues Mapping from field id to values
   * @return {@link Try} of simple time based value
   */
  protected Try<TimeBasedValue<V>, FactoryException> createTimeBasedValue(
      Map<String, String> fieldToValues) {
    SimpleTimeBasedValueData<V> factoryData =
        new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
    return valueFactory.get(factoryData);
  }

  /**
   * Gets time series.
   *
   * @return the time series
   */
  public abstract IndividualTimeSeries<V> getTimeSeries();

  /**
   * Gets time series.
   *
   * @param timeInterval the time interval
   * @return the time series
   * @throws SourceException the source exception
   */
  public abstract IndividualTimeSeries<V> getTimeSeries(ClosedInterval<ZonedDateTime> timeInterval)
      throws SourceException;

  /**
   * Gets value.
   *
   * @param time the time
   * @return the value
   */
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

  /**
   * Gets previous time based value.
   *
   * @param time the time
   * @return the previous time based value
   */
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
