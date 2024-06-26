/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.voltagelevels;

import java.io.Serializable;
import java.util.Objects;
import javax.measure.quantity.ElectricPotential;
import tech.units.indriya.ComparableQuantity;

/**
 * Definition of a concrete voltage level. Here, only voltage levels with one rated voltage are
 * covered. Don't mix it up with "Netzebenen" in D-A-CH area, which also cover the transformation
 * levels.
 */
public class VoltageLevel implements Serializable {
  protected final String id;
  protected final ComparableQuantity<ElectricPotential> nominalVoltage;

  /**
   * Constructs a concrete voltage level
   *
   * @param id Identifier
   * @param nominalVoltage nominal voltage of the voltage level
   */
  public VoltageLevel(String id, ComparableQuantity<ElectricPotential> nominalVoltage) {
    this.id = id;
    this.nominalVoltage = nominalVoltage;
  }

  /**
   * Get the identifier of the voltage level
   *
   * @return The identifier
   */
  public String getId() {
    return id;
  }

  /**
   * Get the nominal voltage of the voltage level
   *
   * @return The nominal voltage of the voltage level
   */
  public ComparableQuantity<ElectricPotential> getNominalVoltage() {
    return nominalVoltage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    VoltageLevel that = (VoltageLevel) o;
    return this.nominalVoltage.isEquivalentTo(that.nominalVoltage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nominalVoltage);
  }

  @Override
  public String toString() {
    return "VoltageLevel{" + "id='" + id + '\'' + ", nominalVoltage=" + nominalVoltage + '}';
  }
}
