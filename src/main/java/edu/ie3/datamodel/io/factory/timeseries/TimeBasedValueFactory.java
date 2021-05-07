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
import java.time.ZoneId;
import java.util.Locale;

/**
 * Abstract class that is able to build {@link TimeBasedValue}s from "flat" information
 *
 * @param <D> Type of "flat" information as a sub class of {@link TimeBasedValue}.
 * @param <V> Type of the targeted inner {@link Value}, that is carried.
 */
public abstract class TimeBasedValueFactory<D extends TimeBasedValueData<V>, V extends Value>
    extends Factory<V, D, TimeBasedValue<V>> {
  /* Static field names, that are harmonized across different time based values */
  protected static final String UUID = "uuid";
  protected static final String TIME = "time";

  protected final TimeUtil timeUtil;

  /**
   * Build an instance of of a time based value factory. Time zone defaults to UTC, Locale to {@link
   * Locale#GERMANY} and the time stamp pattern to RFC 3339 standard.
   *
   * @param valueClasses Classes, that are covered by this factory
   * @see <a href="https://tools.ietf.org/html/rfc3339">RFC 3339 standard definition</a>
   */
  public TimeBasedValueFactory(Class<? extends V>... valueClasses) {
    this(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'", valueClasses);
  }

  /**
   * Build an instance of of a time based value factory. Time zone defaults to UTC and Locale to
   * {@link Locale#GERMANY}.
   *
   * @param timeStampPattern Pattern, that should be used to interpret a String to date time
   * @param valueClasses Classes, that are covered by this factory
   */
  public TimeBasedValueFactory(String timeStampPattern, Class<? extends V>... valueClasses) {
    this(ZoneId.of("UTC"), Locale.GERMANY, timeStampPattern, valueClasses);
  }

  /**
   * Build an instance of of a time based value factory.
   *
   * @param timeZone Time zone to use, when parsing to date time
   * @param locale Locale to use, when parsing to date time
   * @param timeStampPattern Pattern, that should be used to interpret a String to date time
   * @param valueClasses Classes, that are covered by this factory
   */
  public TimeBasedValueFactory(
      ZoneId timeZone, Locale locale, String timeStampPattern, Class<? extends V>... valueClasses) {
    super(valueClasses);
    timeUtil = new TimeUtil(timeZone, locale, timeStampPattern);
  }

  public String getTimeStampPattern() {
    return timeUtil.getDtfPattern();
  }
}
