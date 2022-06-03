/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;

/**
 * Temperature dependant load profiles for night storage heating and heat pumps . The profiles rely
 * on the VDN description for interruptable loads. For more details see <a
 * href="https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf">here</a>.
 */
public interface TemperatureDependantLoadProfile extends LoadProfile {

  static NbwTemperatureDependantLoadProfile parse(String key) throws ParsingException {
    return (NbwTemperatureDependantLoadProfile)
        LoadProfile.parse(NbwTemperatureDependantLoadProfile.values(), key);
  }
}
