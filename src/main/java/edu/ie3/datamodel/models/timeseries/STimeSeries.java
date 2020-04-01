/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.SValue;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import java.util.Collection;
import java.util.UUID;

/** Time series, that holds apparent power values for each time step */
public class STimeSeries extends IndividualTimeSeries<SValue> {
  public STimeSeries(UUID uuid, Collection<TimeBasedValue<SValue>> timeBasedValues) {
    super(uuid, timeBasedValues);
  }
}
