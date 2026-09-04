/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
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
 * Represents the conducting core of a cable with its specific geometric and thermal properties.
 * Unlike {@link LayerInput} layers, the conductor has no inner diameter and includes compaction
 * information.
 */
public class ConductorInput extends AssetInput {
  private final CableMaterial material;

  private final ComparableQuantity<Area> crossSection;

  private final ComparableQuantity<Length> diameter;

  private final boolean isCompacted;

  private final ComparableQuantity<ThermalResistivity> thermalResistivity;

  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;

  private final ComparableQuantity<Area> area;

  public ConductorInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Area> crossSection,
      ComparableQuantity<Length> diameter,
      boolean isCompacted,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
    super(uuid, id);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = null;
  }

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
   * @param thermalCapacitance Thermal capacitance of the conductor material
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
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area) {
    super(uuid, id);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
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
      ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = null;
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
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
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
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.crossSection = crossSection;
    this.diameter = diameter;
    this.isCompacted = isCompacted;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
    setAdditionalInformation(additionalInformation);
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
    if (!(o instanceof ConductorInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(material, that.material)
        && crossSection.equals(that.crossSection)
        && diameter.equals(that.diameter)
        && isCompacted == that.isCompacted
        && thermalResistivity.equals(that.thermalResistivity)
        && thermalCapacitance.equals(that.thermalCapacitance)
        && area.equals(that.area);
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
        thermalCapacitance,
        area);
  }

  @Override
  public String toString() {
    return "ConductorInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
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
        + ", thermalCapacitance="
        + thermalCapacitance
        + ", area="
        + area
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public ConductorInputCopyBuilder copy() {
    return new ConductorInputCopyBuilder(this);
  }

  public static class ConductorInputCopyBuilder
      extends AssetInputCopyBuilder<ConductorInputCopyBuilder> {
    private CableMaterial material;

    private ComparableQuantity<Area> crossSection;

    private ComparableQuantity<Length> diameter;

    private boolean isCompacted;

    private ComparableQuantity<ThermalResistivity> thermalResistivity;

    private ComparableQuantity<ThermalCapacitance> thermalCapacitance;

    private ComparableQuantity<Area> area;

    protected ConductorInputCopyBuilder(ConductorInput entity) {
      super(entity);
      this.material = entity.material;
      this.crossSection = entity.crossSection;
      this.diameter = entity.diameter;
      this.isCompacted = entity.isCompacted;
      this.thermalResistivity = entity.thermalResistivity;
      this.thermalCapacitance = entity.thermalCapacitance;
      this.area = entity.area;
    }

    public ConductorInputCopyBuilder material(CableMaterial material) {
      this.material = material;
      return thisInstance();
    }

    protected CableMaterial material() {
      return material;
    }

    public ConductorInputCopyBuilder crossSection(ComparableQuantity<Area> crossSection) {
      this.crossSection = crossSection;
      return thisInstance();
    }

    protected ComparableQuantity<Area> crossSection() {
      return crossSection;
    }

    public ConductorInputCopyBuilder diameter(ComparableQuantity<Length> diameter) {
      this.diameter = diameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> diameter() {
      return diameter;
    }

    public ConductorInputCopyBuilder isCompacted(boolean isCompacted) {
      this.isCompacted = isCompacted;
      return thisInstance();
    }

    protected boolean isCompacted() {
      return isCompacted;
    }

    public ConductorInputCopyBuilder thermalResistivity(
        ComparableQuantity<ThermalResistivity> thermalResistivity) {
      this.thermalResistivity = thermalResistivity;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalResistivity> thermalResistivity() {
      return thermalResistivity;
    }

    public ConductorInputCopyBuilder thermalCapacitance(
        ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
      this.thermalCapacitance = thermalCapacitance;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
      return thermalCapacitance;
    }

    public ConductorInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    protected ComparableQuantity<Area> area() {
      return area;
    }

    @Override
    public ConductorInput build() {
      return new ConductorInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          material,
          crossSection,
          diameter,
          isCompacted,
          thermalResistivity,
          thermalCapacitance,
          area,
          getAdditionalInformation());
    }

    @Override
    protected ConductorInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
