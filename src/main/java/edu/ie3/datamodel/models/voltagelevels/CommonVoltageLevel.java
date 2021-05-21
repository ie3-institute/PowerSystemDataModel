/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.voltagelevels;

import edu.ie3.datamodel.exceptions.VoltageLevelException;
import edu.ie3.util.interval.RightOpenInterval;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/** Class with extended information to describe common voltage levels in energy systems. */
public class CommonVoltageLevel extends VoltageLevel {
  private final Set<String> synonymousIds;
  protected final RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange;

  /**
   * Constructs a concrete voltage level
   *
   * @param id Identifier
   * @param nominalVoltage nominal voltage of the voltage level
   * @param synonymousIds Synonymously used identifiers
   * @param voltageRange Range of nominal voltage that is covered by this common voltage level
   */
  public CommonVoltageLevel(
      String id,
      ComparableQuantity<ElectricPotential> nominalVoltage,
      Set<String> synonymousIds,
      RightOpenInterval<ComparableQuantity<ElectricPotential>> voltageRange) {
    super(id, nominalVoltage);
    /* Adding the id to synonyms if not already apparent. This local variable is not useless, as it prevents the
     * provided set to be altered. */
    SortedSet<String> eligibleIds = new TreeSet<>(synonymousIds);
    eligibleIds.add(id);
    this.synonymousIds = Collections.unmodifiableSet(eligibleIds);
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
   * @throws VoltageLevelException If the input is ambiguous
   */
  public boolean covers(String id, ComparableQuantity<ElectricPotential> vRated)
      throws VoltageLevelException {
    boolean idCovered =
        synonymousIds.stream().anyMatch(string -> string.equalsIgnoreCase(id.toLowerCase()));
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

  @Override
  public String toString() {
    return "CommonVoltageLevel{"
        + "id='"
        + id
        + '\''
        + ", nominalVoltage="
        + nominalVoltage
        + ", synonymousIds="
        + synonymousIds
        + ", voltageRange="
        + voltageRange
        + '}';
  }
}
