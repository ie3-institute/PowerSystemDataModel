/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.voltagelevels;

import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.util.interval.RightOpenInterval;
import java.util.Set;
import javax.measure.quantity.ElectricPotential;
import tec.uom.se.ComparableQuantity;

/** Class with extended information to describe common voltage levels in energy systems. */
public class CommonVoltageLevel extends VoltageLevel {
  protected Set<String> synonymousIds;
  protected RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange;

  /**
   * Constructs a concrete voltage level
   *
   * @param id Identifier
   * @param nominalVoltage nominal voltage of the voltage level
   */
  public CommonVoltageLevel(
      String id,
      ComparableQuantity<ElectricPotential> nominalVoltage,
      Set<String> synonymousIds,
      RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange) {
    super(id, nominalVoltage);
    this.synonymousIds = synonymousIds;
    this.voltageRange = voltageRange;
  }

  /**
   * Checks, whether the given rated voltage is covered
   *
   * @param vRated Rated voltage of a node to test
   * @return true, if it is covered
   */
  public boolean covers(ComparableQuantity<ElectricPotential> vRated) {
    return voltageRange.includes(vRated);
  }

  /**
   * Checks, whether the given tuple of identifier and rated voltage is covered
   *
   * @param id Identifier
   * @param vRated Rated voltage of a node to test
   * @return true, if it is covered
   */
  public boolean covers(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    boolean idCovered = synonymousIds.contains(id.toLowerCase());
    boolean voltageCovered = covers(vRated);

    if (idCovered ^ voltageCovered)
      throw new VoltageLevelException(
          "The provided id \""
              + id
              + "\" and rated voltage \""
              + vRated
              + "\" could possibly meet the voltage level \""
              + this.id
              + "\" ("
              + voltageRange
              + "), but are inconsistent.");
    return idCovered; /* voltage covered is always true, otherwise the exception would have been thrown. */
  }
}
