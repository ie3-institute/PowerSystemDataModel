/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;

/** Interface for load values. */
public interface LoadValues extends Value {

  /**
   * Method to calculate an actual load power value for the given time.
   *
   * @param time given time
   * @return a new {@link PValue}
   */
  PValue getValue(ZonedDateTime time, LoadProfile loadProfile);
}
