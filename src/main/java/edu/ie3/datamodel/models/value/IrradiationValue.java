/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.QuantityUtil;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.util.Objects;
import tech.units.indriya.ComparableQuantity;

/** Describes an irradiation value as a pair of diffuse and direct radiation */
public class IrradiationValue implements Value {
  /** Direct sun radiation (typically in kWh/m²) */
  private final ComparableQuantity<Irradiation> directIrradiation;
  /** Diffuse sun radiation (typically in kWh/m²) */
  private final ComparableQuantity<Irradiation> diffuseIrradiation;

  /**
   * @param directIrradiation Direct sun radiation (typically in kWh/m²)
   * @param diffuseIrradiation Diffuse sun radiation (typically in kWh/m²)
   */
  public IrradiationValue(
      ComparableQuantity<Irradiation> directIrradiation,
      ComparableQuantity<Irradiation> diffuseIrradiation) {
    this.directIrradiation = directIrradiation.to(StandardUnits.IRRADIATION);
    this.diffuseIrradiation = diffuseIrradiation.to(StandardUnits.IRRADIATION);
  }

  public ComparableQuantity<Irradiation> getDiffuseIrradiation() {
    return diffuseIrradiation;
  }

  public ComparableQuantity<Irradiation> getDirectIrradiation() {
    return directIrradiation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IrradiationValue that = (IrradiationValue) o;
    return QuantityUtil.isTheSameConsideringEmpty(directIrradiation, that.directIrradiation)
        && QuantityUtil.isTheSameConsideringEmpty(diffuseIrradiation, that.diffuseIrradiation);
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
