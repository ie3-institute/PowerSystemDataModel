/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;

/** Temperature dependant determined by NBW (accessed 05/2022) */
public enum NbwTemperatureDependantLoadProfile implements TemperatureDependantLoadProfile {
  // heat pumps
  EP1("ep1"),

  // night storage heating
  EZ2("ez2");

  private final String key;

  NbwTemperatureDependantLoadProfile(String key) {
    this.key = key.toLowerCase();
  }

  /**
   * Get the predefined nbw load profile based on the given key
   *
   * @param key key to check for
   * @return The corresponding nbw load profile or throw {@link IllegalArgumentException}, if no
   *     matching load profile can be found
   */
  public static NbwTemperatureDependantLoadProfile get(String key) throws ParsingException {
    return LoadProfile.getProfile(NbwTemperatureDependantLoadProfile.values(), key);
  }

  @Override
  public String getKey() {
    return this.key;
  }

  @Override
  public String toString() {
    return "NbwTemperatureDependantLoadProfile{" + "key='" + key + '\'' + '}';
  }
}
