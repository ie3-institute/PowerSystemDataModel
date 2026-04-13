/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

/** Temperature dependant determined by NBW (accessed 05/2022) */
public enum NbwTemperatureDependantLoadProfile implements TemperatureDependantLoadProfile {
  // heat pumps
  EP1("ep1"),

  // night storage heating
  EZ2("ez2");

  private final PowerProfileKey key;

  NbwTemperatureDependantLoadProfile(String key) {
    this.key = new PowerProfileKey(key);
  }

  @Override
  public PowerProfileKey getKey() {
    return this.key;
  }

  @Override
  public String toString() {
    return "NbwTemperatureDependantLoadProfile{" + "key='" + key + '\'' + '}';
  }
}
