/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.util.Objects;
import java.util.Optional;
import tech.units.indriya.ComparableQuantity;

/** Describes an irradiation value as a pair of diffuse and direct radiation */
public class IrradiationValue implements Value {
  /** Direct sun radiation (typically in Wh/m²) */
  private final ComparableQuantity<Irradiation> directIrradiation;
  /** Diffuse sun radiation (typically in Wh/m²) */
  private final ComparableQuantity<Irradiation> diffuseIrradiation;

  /**
   * @param directIrradiation Direct sun radiation (typically in J/m² or Wh/m²)
   * @param diffuseIrradiation Diffuse sun radiation (typically in J/m² Wh/m²)
   */
  public IrradiationValue(
      ComparableQuantity<Irradiation> directIrradiation,
      ComparableQuantity<Irradiation> diffuseIrradiation) {
    this.directIrradiation =
        directIrradiation == null ? null : directIrradiation.to(StandardUnits.SOLAR_IRRADIATION);
    this.diffuseIrradiation =
        diffuseIrradiation == null ? null : diffuseIrradiation.to(StandardUnits.SOLAR_IRRADIATION);
  }

  public Optional<ComparableQuantity<Irradiation>> getDiffuseIrradiation() {
    return Optional.ofNullable(diffuseIrradiation);
  }

  public Optional<ComparableQuantity<Irradiation>> getDirectIrradiation() {
    return Optional.ofNullable(directIrradiation);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IrradiationValue that = (IrradiationValue) o;
    return Objects.equals(directIrradiation, that.directIrradiation)
        && Objects.equals(diffuseIrradiation, that.diffuseIrradiation);
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
