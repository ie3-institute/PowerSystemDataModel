/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import edu.ie3.exceptions.VoltageLevelException;
import edu.ie3.util.interval.RightOpenInterval;
import java.util.Set;
import javax.measure.quantity.ElectricPotential;
import tec.uom.se.ComparableQuantity;

/**
 * Interface to define common voltage levels in energy systems. Here, only voltage levels with one
 * rated voltage are covered. Don't mix it up with "Netzebenen" in D-A-CH area, which also cover the
 * transformation levels.
 */
public interface CommonVoltageLevel {
  /**
   * Identifier
   *
   * @return The unique identifier for this voltage level
   */
  String getId();

  /**
   * Get a set of synonymously used identifiers
   *
   * @return set of synonymously used identifiers
   */
  Set<String> getSynonymousIds();

  /**
   * Get a right open interval of covered rated voltages
   *
   * @return a right open interval of covered rated voltages
   */
  RightOpenInterval<ComparableQuantity<ElectricPotential>> getRatedVoltageRange();

  /**
   * Get the nominal voltage of this level
   *
   * @return the nominal voltage of this level
   */
  ComparableQuantity<ElectricPotential> getNominalVoltage();

  /**
   * Checks, whether the given rated voltage is covered
   *
   * @param vRated Rated voltage of a node to test
   * @return true, if it is covered
   */
  boolean covers(ComparableQuantity<ElectricPotential> vRated) throws VoltageLevelException;

  /**
   * Checks, whether the given tuple of identifier and rated voltage is covered
   *
   * @param id Identifier
   * @param vRated Rated voltage of a node to test
   * @return true, if it is covered
   */
  boolean covers(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException;
}
