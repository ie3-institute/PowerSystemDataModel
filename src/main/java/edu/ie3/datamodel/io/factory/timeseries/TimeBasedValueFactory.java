/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.TimeUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Abstract class that is able to build {@link TimeBasedValue}s from "flat" information
 *
 * @param <D> Type of "flat" information as a sub class of {@link TimeBasedValue}.
 * @param <V> Type of the targeted inner {@link Value}, that is carried.
 */
public abstract class TimeBasedValueFactory<D extends TimeBasedValueData<V>, V extends Value>
    extends Factory<V, D, TimeBasedValue<V>> {

  protected static final String TIME = "time";

  protected final TimeUtil timeUtil;

  protected TimeBasedValueFactory(Class<? extends V>... valueClasses) {
    super(valueClasses);
    this.timeUtil = TimeUtil.withDefaults;
  }

  protected TimeBasedValueFactory(
      Class<? extends V> valueClasses, DateTimeFormatter dateTimeFormatter) {
    super(valueClasses);
    this.timeUtil = new TimeUtil(dateTimeFormatter);
  }

  protected TimeBasedValueFactory(Class<? extends V> valueClasses, TimeUtil timeUtil) {
    super(valueClasses);
    this.timeUtil = timeUtil;
  }

  /**
   * Return the field name for the date time
   *
   * @return the field name for the date time
   */
  public String getTimeFieldString() {
    return TIME;
  }

  /**
   * Method to extract a time string from a given map and convert into a {@link ZonedDateTime}.
   *
   * @param fieldsToAttributes map with time field
   * @return a {@link ZonedDateTime}
   */
  public ZonedDateTime extractTime(Map<String, String> fieldsToAttributes) {
    return toZonedDateTime(fieldsToAttributes.get(getTimeFieldString()));
  }

  /**
   * Method to convert a given string into a {@link ZonedDateTime}.
   *
   * @param time string to convert
   * @return a {@link ZonedDateTime}
   */
  public ZonedDateTime toZonedDateTime(String time) {
    return timeUtil.toZonedDateTime(time);
  }
}
