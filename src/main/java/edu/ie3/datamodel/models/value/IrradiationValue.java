/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Irradiation;
import tec.uom.se.ComparableQuantity;

import java.util.Objects;
import javax.measure.Quantity;

/** Describes an irradiation value as a pair of diffuse and direct radiation */
public class IrradiationValue implements Value {
  /** Direct sun radiation (typically in W/m²) */
  private ComparableQuantity<Irradiation> directIrradiation; // TODO doublecheck
  /** Diffuse sun radiation (typically in W/m²) */
  private ComparableQuantity<Irradiation> diffuseIrradiation; // TODO doublecheck

  /**
   * @param directIrradiation Direct sun radiation (typically in W/m²)
   * @param diffuseIrradiation Diffuse sun radiation (typically in W/m²)
   */
  public IrradiationValue(
          ComparableQuantity<Irradiation> directIrradiation, ComparableQuantity<Irradiation> diffuseIrradiation) { // TODO doublecheck
    this.directIrradiation = directIrradiation.to(StandardUnits.IRRADIATION);
    this.diffuseIrradiation = diffuseIrradiation.to(StandardUnits.IRRADIATION);
  }

  public ComparableQuantity<Irradiation> getDiffuseIrradiation() {
    return diffuseIrradiation;
  } // TODO doublecheck

  public void setDiffuseIrradiation(ComparableQuantity<Irradiation> diffuseIrradiation) { // TODO doublecheck
    this.diffuseIrradiation = diffuseIrradiation.to(StandardUnits.IRRADIATION);
  }

  public ComparableQuantity<Irradiation> getDirectIrradiation() {
    return directIrradiation;
  } // TODO doublecheck

  public void setDirectIrradiation(ComparableQuantity<Irradiation> directIrradiation) { // TODO doublecheck
    this.directIrradiation = directIrradiation.to(StandardUnits.IRRADIATION);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IrradiationValue that = (IrradiationValue) o;
    return directIrradiation.equals(that.directIrradiation)
        && diffuseIrradiation.equals(that.diffuseIrradiation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(directIrradiation, diffuseIrradiation);
  }

  @Override
  public String toString() {
    return "IrradiationValue{"
        + "directIrradiation="
        + directIrradiation
        + ", diffuseIrradiation="
        + diffuseIrradiation
        + '}';
  }
}
