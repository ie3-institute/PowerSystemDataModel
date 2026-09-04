/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents a concentric layer of a cable, including insulation, filler, armor, and jacket layers.
 */
public class LayerInput extends AssetTypeInput {
  private final CableMaterial material;

  private final ComparableQuantity<Length> innerDiameter;

  private final ComparableQuantity<Length> outerDiameter;

  private final ComparableQuantity<ThermalResistivity> thermalResistivity;

  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;

  private final ComparableQuantity<Area> area;

  /**
   * Constructor of a cable layer.
   *
   * @param uuid UUID of this layer
   * @param id Name/designation of this layer
   * @param material Material of this layer
   * @param innerDiameter Inner diameter of this layer
   * @param outerDiameter Outer diameter of this layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param area Real cross-sectional area (if different from geometry)
   */
  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
  }

  /**
   * Constructor of a cable layer.
   *
   * @param uuid UUID of this layer
   * @param id Name/designation of this layer
   * @param material Material of this layer
   * @param innerDiameter Inner diameter of this layer
   * @param outerDiameter Outer diameter of this layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   */
  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = null;
  }

  /**
   * Constructor of a cable layer.
   *
   * @param uuid UUID of this layer
   * @param id Name/designation of this layer
   * @param material Material of this layer
   * @param innerDiameter Inner diameter of this layer
   * @param outerDiameter Outer diameter of this layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param area Real cross-sectional area (if different from geometry)
   * @param additionalInformation That were provided by the source
   */
  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area,
      Map<String, String> additionalInformation) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor of a cable layer.
   *
   * @param uuid UUID of this layer
   * @param id Name/designation of this layer
   * @param material Material of this layer
   * @param innerDiameter Inner diameter of this layer
   * @param outerDiameter Outer diameter of this layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param additionalInformation That were provided by the source
   */
  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Map<String, String> additionalInformation) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = null;
    setAdditionalInformation(additionalInformation);
  }

  public CableMaterial material() {
    return material;
  }

  public ComparableQuantity<Length> innerDiameter() {
    return innerDiameter;
  }

  public ComparableQuantity<Length> outerDiameter() {
    return outerDiameter;
  }

  public ComparableQuantity<ThermalResistivity> thermalResistivity() {
    return thermalResistivity;
  }

  public ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
    return thermalCapacitance;
  }

  public Optional<ComparableQuantity<Area>> area() {
    return Optional.ofNullable(area);
  }

  public String name() {
    return getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LayerInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(material, that.material)
        && QuantityUtils.equals(innerDiameter, that.innerDiameter)
        && QuantityUtils.equals(outerDiameter, that.outerDiameter)
        && QuantityUtils.equals(thermalResistivity, that.thermalResistivity)
        && QuantityUtils.equals(thermalCapacitance, that.thermalCapacitance)
        && QuantityUtils.equals(area, that.area);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        area);
  }

  @Override
  public String toString() {
    return "LayerInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", material="
        + material
        + ", innerDiameter="
        + innerDiameter
        + ", outerDiameter="
        + outerDiameter
        + ", thermalResistivity="
        + thermalResistivity
        + ", thermalCapacitance="
        + thermalCapacitance
        + ", area="
        + area
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public LayerInputCopyBuilder copy() {
    return new LayerInputCopyBuilder(this);
  }

  public static class LayerInputCopyBuilder
      extends AssetTypeInputCopyBuilder<LayerInputCopyBuilder> {
    private CableMaterial material;

    private ComparableQuantity<Length> innerDiameter;

    private ComparableQuantity<Length> outerDiameter;

    private ComparableQuantity<ThermalResistivity> thermalResistivity;

    private ComparableQuantity<ThermalCapacitance> thermalCapacitance;

    private ComparableQuantity<Area> area;

    protected LayerInputCopyBuilder(LayerInput entity) {
      super(entity);
      this.material = entity.material;
      this.innerDiameter = entity.innerDiameter;
      this.outerDiameter = entity.outerDiameter;
      this.thermalResistivity = entity.thermalResistivity;
      this.thermalCapacitance = entity.thermalCapacitance;
      this.area = entity.area;
    }

    public LayerInputCopyBuilder material(CableMaterial material) {
      this.material = material;
      return thisInstance();
    }

    protected CableMaterial material() {
      return material;
    }

    public LayerInputCopyBuilder innerDiameter(ComparableQuantity<Length> innerDiameter) {
      this.innerDiameter = innerDiameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> innerDiameter() {
      return innerDiameter;
    }

    public LayerInputCopyBuilder outerDiameter(ComparableQuantity<Length> outerDiameter) {
      this.outerDiameter = outerDiameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> outerDiameter() {
      return outerDiameter;
    }

    public LayerInputCopyBuilder thermalResistivity(
        ComparableQuantity<ThermalResistivity> thermalResistivity) {
      this.thermalResistivity = thermalResistivity;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalResistivity> thermalResistivity() {
      return thermalResistivity;
    }

    public LayerInputCopyBuilder thermalCapacitance(
        ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
      this.thermalCapacitance = thermalCapacitance;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
      return thermalCapacitance;
    }

    public LayerInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    protected ComparableQuantity<Area> area() {
      return area;
    }

    @Override
    public LayerInput build() {
      return new LayerInput(
          getUuid(),
          getId(),
          material,
          innerDiameter,
          outerDiameter,
          thermalResistivity,
          thermalCapacitance,
          area,
          getAdditionalInformation());
    }

    @Override
    protected LayerInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
