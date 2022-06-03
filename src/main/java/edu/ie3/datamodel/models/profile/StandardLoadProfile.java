/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries;

/**
 * Giving reference to a known standard load profile to apply to a {@link
 * edu.ie3.datamodel.models.input.system.LoadInput}. This interface does nothing more, than giving a
 * reference, the values have to be provided by the simulator using the models.
 *
 * <p>If you intend to provide distinct values, create either an {@link IndividualTimeSeries} or
 * {@link RepetitiveTimeSeries} and assign it to the model via mapping to the model.
 */
public interface StandardLoadProfile extends LoadProfile {

  static StandardLoadProfile parse(String key) {
    return (StandardLoadProfile) LoadProfile.getProfile(BdewStandardLoadProfile.values(), key);
  }
}
