/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

/**
 * Temperature dependant load profiles for night storage heating and heat pumps . The profiles rely
 * on the VDN description for interruptable loads. For more details see <a
 * href="https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf">here</a>.
 */
public interface TemperatureDependantLoadProfile extends LoadProfile {

  /**
   * Returns temperature dependant load profile corresponding to the given key.
   *
   * @param key to look for
   * @return the matching temperature dependant load profile
   */
  static TemperatureDependantLoadProfile parse(String key) {
    return (NbwTemperatureDependantLoadProfile)
        LoadProfile.getProfile(NbwTemperatureDependantLoadProfile.values(), key);
  }
}
