/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.value.PValue;
import tech.units.indriya.quantity.Quantities;

/**
 * German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
 * Wasserwirtschaft; engl.Federal Association of the Energy and Water Industry). For more details
 * see <a href="https://www.bdew.de/energie/standardlastprofile-strom/">here</a>.
 */
public enum BdewStandardLoadProfile implements StandardLoadProfile {
  H0("h0"), // Households
  H25("h25"), // household (Updated 2025)
  L0("l0"), // Agricultural enterprises without further differentiation
  L1("l1"), // Agricultural enterprises with dairy sector
  L2("l2"), // Agricultural enterprises without dairy sector
  L25("l25"), // Agricultural enterprises without further differentiation (Updated 2025)
  G0("g0"), // Businesses without further differentiation
  G1("g1"), // Workday businesses from 8 a.m. to 6 p.m.
  G2("g2"), // Businesses with high consumption in evening hours
  G3("g3"), // Businesses with enduring consumption
  G4("g4"), // Vendor or barber shop
  G5("g5"), // Bakery
  G6("g6"), // Business with main consumption on weekends
  G25("g25"), // Businesses without further differentiation (Updated 2025)
  P25("p25"), // PV profile
  S25("s25"); // Combined PV and storage profile

  private final PowerProfileKey key;

  BdewStandardLoadProfile(String key) {
    this.key = new PowerProfileKey(key);
  }

  @Override
  public PowerProfileKey getKey() {
    return key;
  }

  @Override
  public String toString() {
    return "BdewLoadProfile{" + "key='" + key + '\'' + '}';
  }

  /**
   * Calculates the dynamization factor for given day of year. Cf. <a
   * href="https://www.bdew.de/media/documents/2000131_Anwendung-repraesentativen_Lastprofile-Step-by-step.pdf">
   * Anwendung der repräsentativen Lastprofile - Step by step</a> page 19
   *
   * @param load load value
   * @param t day of year (1-366)
   * @return dynamization factor
   */
  public static PValue dynamization(PowerProfileKey powerProfileKey, double load, int t) {
    double value;

    if (powerProfileKey.equalsAny(H0, H25, P25, S25)) {
      value = dynamization(load, t);
    } else {
      value = load;
    }

    return new PValue(Quantities.getQuantity(value, WATT));
  }

  public static double dynamization(double load, int t) {
    /* For the residential average profile, a dynamization has to be taken into account */
    double factor =
        (-3.92e-10 * pow(t, 4) + 3.2e-7 * pow(t, 3) - 7.02e-5 * pow(t, 2) + 2.1e-3 * t + 1.24);
    double rndFactor = round(factor * 1e4) / 1e4; // round to 4 decimal places
    return round(load * rndFactor * 1e1) / 1e1; // rounded to 1 decimal place
  }
}
