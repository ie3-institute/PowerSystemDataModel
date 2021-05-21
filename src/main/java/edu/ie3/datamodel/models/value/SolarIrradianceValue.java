/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Irradiance;
import java.util.Objects;
import java.util.Optional;
import tech.units.indriya.ComparableQuantity;

/** Describes an irradiance value as a pair of diffuse and direct irradiance */
public class SolarIrradianceValue implements Value {
  /** Direct sun irradiance (typically in W/m²) */
  private final ComparableQuantity<Irradiance> directIrradiance;
  /** Diffuse sun irradiance (typically in W/m²) */
  private final ComparableQuantity<Irradiance> diffuseIrradiance;

  /**
   * @param directIrradiance Direct sun radiation (typically in W/m²)
   * @param diffuseIrradiance Diffuse sun radiation (typically in W/m²)
   */
  public SolarIrradianceValue(
      ComparableQuantity<Irradiance> directIrradiance,
      ComparableQuantity<Irradiance> diffuseIrradiance) {
    this.directIrradiance =
        directIrradiance == null ? null : directIrradiance.to(StandardUnits.SOLAR_IRRADIANCE);
    this.diffuseIrradiance =
        diffuseIrradiance == null ? null : diffuseIrradiance.to(StandardUnits.SOLAR_IRRADIANCE);
  }

  public Optional<ComparableQuantity<Irradiance>> getDiffuseIrradiance() {
    return Optional.ofNullable(diffuseIrradiance);
  }

  public Optional<ComparableQuantity<Irradiance>> getDirectIrradiance() {
    return Optional.ofNullable(directIrradiance);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SolarIrradianceValue that = (SolarIrradianceValue) o;
    return Objects.equals(directIrradiance, that.directIrradiance)
        && Objects.equals(diffuseIrradiance, that.diffuseIrradiance);
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
        + '}';
  }
}
