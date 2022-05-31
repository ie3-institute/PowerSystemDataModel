/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

/**
 * Temperature dependant load profiles for night storage heating and heat pumps . The profiles rely
 * on the VDN description for interruptable loads. For more details see <a
 * href="https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf">here</a>.
 *
 * <p>These are the ones determined by NBW (accessed 05/2022)
 */
public enum TemperatureDependantLoadProfile implements StandardLoadProfile {
  // heat pumps
  EP1("ep1"),

  // night storage heating
  EZ2("ez2");

  private final String key;

  TemperatureDependantLoadProfile(String key) {
    this.key = key.toLowerCase();
  }

  @Override
  public String getKey() {
    return this.key;
  }
}
