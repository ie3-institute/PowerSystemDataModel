/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

/**
 * German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
 * Wasserwirtschaft; engl.Federal Association of the Energy and Water Industry). For more details
 * see https://www.bdew.de/energie/standardlastprofile-strom/
 */
public enum BdewLoadProfile implements StandardLoadProfile {
  H0("h0"), // Households
  L0("l0"), // Agricultural enterprises without further differentiation
  L1("l1"), // Agricultural enterprises with dairy sector
  L2("l2"), // Agricultural enterprises without dairy sector
  G0("g0"), // Businesses without further differentiation
  G1("g1"), // Workday businesses from 8 a.m. to 6 p.m.
  G2("g2"), // Businesses with high consumption in evening hours
  G3("g3"), // Businesses with enduring consumption
  G4("g4"), // Vendor or barber shop
  G5("g5"), // Bakery
  G6("g6"); // Business with main consumption on weekends

  private final String key;

  BdewLoadProfile(String key) {
    this.key = key.toLowerCase();
  }

  @Override
  public String getKey() {
    return key;
  }
}
