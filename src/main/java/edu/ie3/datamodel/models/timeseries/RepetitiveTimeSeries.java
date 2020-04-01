/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** Describes a TimeSeries with repetitive values that can be calculated from a pattern */
public abstract class RepetitiveTimeSeries<T extends Value> extends TimeSeries<T> {
  public RepetitiveTimeSeries() {
    super();
  }

  public RepetitiveTimeSeries(UUID uuid) {
    super(uuid);
  }

  /** Calculate the value at the given timestep based on a pattern */
  public abstract T calc(ZonedDateTime time);

  @Override
  public Optional<T> getValue(ZonedDateTime time) {
    return Optional.of(calc(time));
  }
}
