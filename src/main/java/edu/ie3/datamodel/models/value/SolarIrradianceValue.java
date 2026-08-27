/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.Irradiance;
import java.util.Objects;
import java.util.Optional;
import tech.units.indriya.ComparableQuantity;

/** Describes an irradiance value as a pair of diffuse and direct irradiance. */
public class SolarIrradianceValue implements Value {
  /** Direct sun irradiance (typically in W/m²). */
  private final ComparableQuantity<Irradiance> directIrradiance;

  /** Diffuse sun irradiance (typically in W/m²). */
  private final ComparableQuantity<Irradiance> diffuseIrradiance;

  /**
   * @param directIrradiance Direct sun radiation (typically in W/m²)
   * @param diffuseIrradiance Diffuse sun radiation (typically in W/m²)
   */
  public SolarIrradianceValue(
      ComparableQuantity<Irradiance> directIrradiance,
      ComparableQuantity<Irradiance> diffuseIrradiance) {
    this.directIrradiance = directIrradiance;
    this.diffuseIrradiance = diffuseIrradiance;
  }

  public Optional<ComparableQuantity<Irradiance>> getDirectIrradiance() {
    return Optional.ofNullable(directIrradiance);
  }

  public Optional<ComparableQuantity<Irradiance>> getDiffuseIrradiance() {
    return Optional.ofNullable(diffuseIrradiance);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SolarIrradianceValue that)) return false;
    return QuantityUtils.equals(directIrradiance, that.directIrradiance)
        && QuantityUtils.equals(diffuseIrradiance, that.diffuseIrradiance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(directIrradiance, diffuseIrradiance);
  }

  @Override
  public String toString() {
    return "SolarIrradianceValue{"
        + "directIrradiance="
        + directIrradiance
        + ", diffuseIrradiance="
        + diffuseIrradiance
        + "}";
  }
}
