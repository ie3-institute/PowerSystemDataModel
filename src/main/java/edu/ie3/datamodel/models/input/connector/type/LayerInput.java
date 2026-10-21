/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.io.Serializable;
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
public class LayerInput extends AssetInput implements Serializable {

  private final CableMaterial material;
  private final ComparableQuantity<Length> innerDiameter;
  private final ComparableQuantity<Length> outerDiameter;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity;
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
   * @param specificHeatCapacity Specific heat capacity of the material
   * @param area Real cross-sectional area (if different from geometry)
   */
  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area) {
    this(
        uuid,
        id,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        specificHeatCapacity,
        area,
        null);
  }

  public LayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      Optional<ComparableQuantity<Area>> area) {
    this(
        uuid,
        id,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        specificHeatCapacity,
        area.orElse(null),
        null);
  }

  public LayerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        specificHeatCapacity,
        area,
        null);
  }

  public LayerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.specificHeatCapacity = specificHeatCapacity;
    this.area = area;
    setAdditionalInformation(additionalInformation);
  }

  public LayerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      Optional<ComparableQuantity<Area>> area,
      Map<String, String> additionalInformation) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        specificHeatCapacity,
        area.orElse(null),
        additionalInformation);
  }

  public UUID uuid() {
    return getUuid();
  }

  public String name() {
    return getId();
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

  public ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity() {
    return specificHeatCapacity;
  }

  public Optional<ComparableQuantity<Area>> area() {
    return Optional.ofNullable(area);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LayerInput)) return false;
    if (!super.equals(o)) return false;
    LayerInput that = (LayerInput) o;
    return material == that.material
        && innerDiameter.equals(that.innerDiameter)
        && outerDiameter.equals(that.outerDiameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && specificHeatCapacity.equals(that.specificHeatCapacity)
        && Objects.equals(area, that.area);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        specificHeatCapacity,
        area);
  }

  @Override
  public LayerInputCopyBuilder copy() {
    return new LayerInputCopyBuilder(this);
  }

  public static class LayerInputCopyBuilder extends AssetInputCopyBuilder<LayerInputCopyBuilder> {

    private CableMaterial material;
    private ComparableQuantity<Length> innerDiameter;
    private ComparableQuantity<Length> outerDiameter;
    private ComparableQuantity<ThermalResistivity> thermalResistivity;
    private ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity;
    private ComparableQuantity<Area> area;

    protected LayerInputCopyBuilder(LayerInput entity) {
      super(entity);
      this.material = entity.material;
      this.innerDiameter = entity.innerDiameter;
      this.outerDiameter = entity.outerDiameter;
      this.thermalResistivity = entity.thermalResistivity;
      this.specificHeatCapacity = entity.specificHeatCapacity;
      this.area = entity.area;
    }

    public LayerInputCopyBuilder material(CableMaterial material) {
      this.material = material;
      return thisInstance();
    }

    public LayerInputCopyBuilder innerDiameter(ComparableQuantity<Length> innerDiameter) {
      this.innerDiameter = innerDiameter;
      return thisInstance();
    }

    public LayerInputCopyBuilder outerDiameter(ComparableQuantity<Length> outerDiameter) {
      this.outerDiameter = outerDiameter;
      return thisInstance();
    }

    public LayerInputCopyBuilder thermalResistivity(ComparableQuantity<ThermalResistivity> tr) {
      this.thermalResistivity = tr;
      return thisInstance();
    }

    public LayerInputCopyBuilder specificHeatCapacity(
        ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity) {
      this.specificHeatCapacity = specificHeatCapacity;
      return thisInstance();
    }

    public LayerInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    @Override
    public LayerInput build() {
      return new LayerInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          material,
          innerDiameter,
          outerDiameter,
          thermalResistivity,
          specificHeatCapacity,
          area);
    }

    @Override
    protected LayerInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
