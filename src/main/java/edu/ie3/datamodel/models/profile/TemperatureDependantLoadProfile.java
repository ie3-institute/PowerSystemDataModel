/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/**
 * Temperature dependant load profiles for night storage heating and heat pumps . The profiles rely
 * on the VDN description for interruptable loads. For more details see <a
 * href="https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf">here</a>.
 */
public interface TemperatureDependantLoadProfile extends LoadProfile {

  /**
   * Maximum temperature to which load profiles are scaled. If temperature is higher the load
   * profile according to the reference temperature is used.
   *
   * @return the reference temperature
   */
  ComparableQuantity<Temperature> getReferenceTemperature();

  /**
   * Minimum temperature to which load profiles are scaled. If temperature is lower the load profile
   * according to the reference temperature is used.
   *
   * @return the reference temperature
   */
  ComparableQuantity<Temperature> getMinTemperature();

  /**
   * Downscaling of load profiles gets limited to the limiting constant. For more information see
   * the official VDN description.
   *
   * @return the limiting constant
   */
  int getLimitingConstant();

  /**
   * Returns temperature dependant load profile corresponding to the given key.
   *
   * @param key to look for
   * @return the matching temperature dependant load profile
   */
  static TemperatureDependantLoadProfile parse(String key) throws ParsingException {
    return LoadProfile.getProfile(NbwTemperatureDependantLoadProfile.values(), key);
  }
}
