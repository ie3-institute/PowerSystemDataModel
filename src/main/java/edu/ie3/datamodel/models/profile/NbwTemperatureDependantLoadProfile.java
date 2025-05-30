/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.StandardUnits;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/** Temperature dependant determined by NBW (accessed 05/2022) */
public enum NbwTemperatureDependantLoadProfile implements TemperatureDependantLoadProfile {

  // heat pumps
  EP1("ep1", 1),

  // night storage heating
  EZ2("ez2", 0);

  private final String key;
  private final int limitingConstant;

  NbwTemperatureDependantLoadProfile(String key, int limitingConstant) {
    this.key = key.toLowerCase();
    this.limitingConstant = limitingConstant;
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

  /**
   * Maximum temperature to which load profiles are scaled. If temperature is higher the load
   * profile according to the reference temperature is used.
   *
   * @return the reference temperature
   */
  @Override
  public ComparableQuantity<Temperature> getReferenceTemperature() {
    return Quantities.getQuantity(17, StandardUnits.TEMPERATURE);
  }

  /**
   * Minimum temperature to which load profiles are scaled. If temperature is lower the load profile
   * according to the reference temperature is used.
   *
   * @return the reference temperature
   */
  @Override
  public ComparableQuantity<Temperature> getMinTemperature() {
    return Quantities.getQuantity(-17, StandardUnits.TEMPERATURE);
  }

  /**
   * Downscaling of load profiles gets limited to the limiting constant. For more information see
   * the official VDN description.
   *
   * @return the limiting constant
   */
  @Override
  public int getLimitingConstant() {
    return this.limitingConstant;
  }

  @Override
  public String toString() {
    return "NbwTemperatureDependantLoadProfile{" + "key='" + key + '\'' + '}';
  }
}
