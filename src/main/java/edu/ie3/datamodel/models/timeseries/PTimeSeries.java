/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import java.util.Collection;
import java.util.UUID;

/** Time series, that holds active power values for each time step */
public class PTimeSeries extends IndividualTimeSeries<PValue> {
  public PTimeSeries(UUID uuid, Collection<TimeBasedValue<PValue>> timeBasedValues) {
    super(uuid, timeBasedValues);
  }
}
