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
  /** Households. */
  H0("h0"),

  /** Household profile updated in 2025. */
  H25("h25"),

  /** Agricultural enterprises without further differentiation. */
  L0("l0"),

  /** Agricultural enterprises with dairy sector. */
  L1("l1"),

  /** Agricultural enterprises without dairy sector. */
  L2("l2"),
  /** Agricultural enterprises without further differentiation (Updated in 2025). */
  L25("l25"),

  /** Businesses without further differentiation. */
  G0("g0"),

  /** Workday businesses operating from 8 a.m. to 6 p.m. */
  G1("g1"),

  /** Businesses with high consumption during evening hours. */
  G2("g2"),

  /** Businesses with enduring consumption throughout the day. */
  G3("g3"),

  /** Vendor or barber shop load profile. */
  G4("g4"),

  /** Bakery load profile. */
  G5("g5"),

  /** Business with main consumption on weekends. */
  G6("g6"),
  /** Businesses without further differentiation (Updated in 2025). */
  G25("g25"),

  /** PV profile for photovoltaic systems. */
  P25("p25"),

  /** Combined PV and storage profile for hybrid systems. */
  S25("s25");

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
