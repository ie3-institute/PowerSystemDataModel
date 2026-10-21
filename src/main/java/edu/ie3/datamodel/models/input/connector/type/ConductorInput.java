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
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents the conducting core of a cable with its specific geometric and thermal properties.
 * Unlike {@link LayerInput} layers, the conductor has no inner diameter and includes compaction
 * information.
 */
public class ConductorInput extends AssetInput implements Serializable {

  private final CableMaterial material;
  private final ComparableQuantity<Area> crossSection;
  private final ComparableQuantity<Length> diameter;
  private final boolean isCompacted;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity;
  private final ComparableQuantity<Area> area;

  /**
   * Constructor for the conductor of a cable.
   *
   * @param uuid UUID of the ConductorInput
   * @param id Human-readable id
   * @param material Material of the conductor (e.g., copper or aluminium)
   * @param crossSection Real nominal cross-sectional area (electrically effective)
   * @param diameter Geometric outer diameter of the conductor
   * @param isCompacted Whether the conductor is compacted
   * @param thermalResistivity Thermal resistivity of the conductor material
   * @param specificHeatCapacity Specific heat capacity of the conductor material
   * @param area Optional real cross-sectional area (if different from geometric calculation)
   */
  public ConductorInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area) {
    this(
        uuid,
        id,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        material,
        crossSection,
        diameter,
        isCompacted,
        thermalResistivity,
        specificHeatCapacity,
        area,
        null);
  }

  public ConductorInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        material,
        crossSection,
        diameter,
        isCompacted,
        thermalResistivity,
        specificHeatCapacity,
        area,
        null);
  }

  public ConductorInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity,
      ComparableQuantity<Area> area,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.specificHeatCapacity = specificHeatCapacity;
    this.area = area;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity() {
    return specificHeatCapacity;
  }

  public Optional<ComparableQuantity<Area>> area() {
    return Optional.ofNullable(area);
  }

  public Optional<ComparableQuantity<Area>> areaOptional() {
    return area();
  }

  public String name() {
    return getId();
  }

  public CableMaterial material() {
    return material;
  }

  public ComparableQuantity<Area> crossSection() {
    return crossSection;
  }

  public ComparableQuantity<Length> diameter() {
    return diameter;
  }

  public boolean isCompacted() {
    return isCompacted;
  }

  public ComparableQuantity<ThermalResistivity> thermalResistivity() {
    return thermalResistivity;
  }

  @Override
  public @NonNull String toString() {
    return "ConductorInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", material="
        + material
        + ", crossSection="
        + crossSection
        + ", diameter="
        + diameter
        + ", isCompacted="
        + isCompacted
        + ", thermalResistivity="
        + thermalResistivity
        + ", specificHeatCapacity="
        + specificHeatCapacity
        + ", area="
        + area()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ConductorInput)) return false;
    if (!super.equals(o)) return false;
    ConductorInput that = (ConductorInput) o;
    return isCompacted == that.isCompacted
        && material == that.material
        && crossSection.equals(that.crossSection)
        && diameter.equals(that.diameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && specificHeatCapacity.equals(that.specificHeatCapacity)
        && Objects.equals(area, that.area);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        material,
        crossSection,
        diameter,
        isCompacted,
        thermalResistivity,
        specificHeatCapacity,
        area);
  }

  @Override
  public AssetInputCopyBuilder<?> copy() {
    return new ConductorInputCopyBuilder(this);
  }

  public static class ConductorInputCopyBuilder
      extends AssetInputCopyBuilder<ConductorInputCopyBuilder> {

    private CableMaterial material;
    private ComparableQuantity<Area> crossSection;
    private ComparableQuantity<Length> diameter;
    private boolean isCompacted;
    private ComparableQuantity<ThermalResistivity> thermalResistivity;
    private ComparableQuantity<SpecificHeatCapacity> specificHeatCapacity;
    private ComparableQuantity<Area> area;

    protected ConductorInputCopyBuilder(ConductorInput entity) {
      super(entity);
      this.material = entity.material;
      this.crossSection = entity.crossSection;
      this.diameter = entity.diameter;
      this.isCompacted = entity.isCompacted;
      this.thermalResistivity = entity.thermalResistivity;
      this.specificHeatCapacity = entity.specificHeatCapacity;
      this.area = entity.area;
    }

    public ConductorInputCopyBuilder material(CableMaterial material) {
      this.material = material;
      return thisInstance();
    }

    public ConductorInputCopyBuilder crossSection(ComparableQuantity<Area> crossSection) {
      this.crossSection = crossSection;
      return thisInstance();
    }

    public ConductorInputCopyBuilder diameter(ComparableQuantity<Length> diameter) {
      this.diameter = diameter;
      return thisInstance();
    }

    public ConductorInputCopyBuilder isCompacted(boolean isCompacted) {
      this.isCompacted = isCompacted;
      return thisInstance();
    }

    public ConductorInputCopyBuilder thermalResistivity(ComparableQuantity<ThermalResistivity> tr) {
      this.thermalResistivity = tr;
      return thisInstance();
    }

    public ConductorInputCopyBuilder specificHeatCapacity(
        ComparableQuantity<SpecificHeatCapacity> tc) {
      this.specificHeatCapacity = tc;
      return thisInstance();
    }

    public ConductorInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    @Override
    public ConductorInput build() {
      return new ConductorInput(
          super.getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          material,
          crossSection,
          diameter,
          isCompacted,
          thermalResistivity,
          specificHeatCapacity,
          area);
    }

    @Override
    protected ConductorInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
