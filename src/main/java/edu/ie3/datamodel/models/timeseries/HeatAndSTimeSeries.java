/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.HeatAndSValue;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import java.util.Collection;
import java.util.UUID;

/** Time series, that holds apparent power and heat demand values for each time step */
public class HeatAndSTimeSeries extends IndividualTimeSeries<HeatAndSValue> {
  public HeatAndSTimeSeries(UUID uuid, Collection<TimeBasedValue<HeatAndSValue>> timeBasedValues) {
    super(uuid, timeBasedValues);
  }
}
