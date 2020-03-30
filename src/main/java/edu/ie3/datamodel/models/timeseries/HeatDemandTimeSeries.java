/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.HeatDemandValue;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import java.util.Collection;
import java.util.UUID;

public class HeatDemandTimeSeries extends IndividualTimeSeries<HeatDemandValue> {
  public HeatDemandTimeSeries(
      UUID uuid, Collection<TimeBasedValue<HeatDemandValue>> timeBasedValues) {
    super(uuid, timeBasedValues);
  }
}
