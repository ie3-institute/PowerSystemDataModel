/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;

/**
 * German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
 * Wasserwirtschaft; engl.Federal Association of the Energy and Water Industry). For more details
 * see <a href="https://www.bdew.de/energie/standardlastprofile-strom/">here</a>.
 */
public enum BdewStandardLoadProfile implements StandardLoadProfile {
  /** H 0 bdew standard load profile. */
  H0("h0"), // Households
  /** H 25 bdew standard load profile. */
  H25("h25"), // household (Updated 2025)
  /** L 0 bdew standard load profile. */
  L0("l0"), // Agricultural enterprises without further differentiation
  /** L 1 bdew standard load profile. */
  L1("l1"), // Agricultural enterprises with dairy sector
  /** L 2 bdew standard load profile. */
  L2("l2"), // Agricultural enterprises without dairy sector
  /** L 25 bdew standard load profile. */
  L25("l25"), // Agricultural enterprises without further differentiation (Updated 2025)
  /** G 0 bdew standard load profile. */
  G0("g0"), // Businesses without further differentiation
  /** G 1 bdew standard load profile. */
  G1("g1"), // Workday businesses from 8 a.m. to 6 p.m.
  /** G 2 bdew standard load profile. */
  G2("g2"), // Businesses with high consumption in evening hours
  /** G 3 bdew standard load profile. */
  G3("g3"), // Businesses with enduring consumption
  /** G 4 bdew standard load profile. */
  G4("g4"), // Vendor or barber shop
  /** G 5 bdew standard load profile. */
  G5("g5"), // Bakery
  /** G 6 bdew standard load profile. */
  G6("g6"), // Business with main consumption on weekends
  /** G 25 bdew standard load profile. */
  G25("g25"), // Businesses without further differentiation (Updated 2025)
  /** P 25 bdew standard load profile. */
  P25("p25"), // PV profile
  /** S 25 bdew standard load profile. */
  S25("s25"); // Combined PV and storage profile

  private final String key;

  BdewStandardLoadProfile(String key) {
    this.key = key.toLowerCase();
  }

  /**
   * Get the predefined bdew load profile based on the given key
   *
   * @param key key to check for
   * @return The corresponding bdew load profile or throw {@link IllegalArgumentException}, if no
   *     matching load profile can be found
   * @throws ParsingException the parsing exception
   */
  public static BdewStandardLoadProfile get(String key) throws ParsingException {
    return LoadProfile.getProfile(BdewStandardLoadProfile.values(), key);
  }

  @Override
  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return "BdewLoadProfile{" + "key='" + key + '\'' + '}';
  }
}
