/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.EnergyPriceValue;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;

/** Describes a data source for wholesale prices */
public interface WholesalePriceSource extends DataSource {
  /**
   * Return the whole sale price for the given time interval
   *
   * @param timeInterval Queried time interval
   * @return wholesale price data for the specified time range as a TimeSeries
   */
  IndividualTimeSeries<EnergyPriceValue> getWholesalePrice(
      ClosedInterval<ZonedDateTime> timeInterval);

  /**
   * Return the whole sale price for the given time
   *
   * @param time Queried time
   * @return wholesale data for the specified time
   */
  IndividualTimeSeries<EnergyPriceValue> getWeather(ZonedDateTime time);
}
