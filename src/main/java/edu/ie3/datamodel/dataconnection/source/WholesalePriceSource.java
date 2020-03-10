/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.dataconnection.source;

import edu.ie3.datamodel.models.timeseries.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.EnergyPriceValue;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;

/** Describes a data source for wholesale prices */
public interface WholesalePriceSource extends DataSource {
  /** @return wholesale price data for the specified time range as a TimeSeries */
  IndividualTimeSeries<EnergyPriceValue> getWholesalePrice(
      ClosedInterval<ZonedDateTime> timeInterval);

  /** @return wholesale data for the specified tim */
  IndividualTimeSeries<EnergyPriceValue> getWeather(ZonedDateTime time);
}
