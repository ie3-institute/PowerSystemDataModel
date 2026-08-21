/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
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
 * Represents a cable screen layer with specific parameters for conductor shielding. Extends the
 * properties of a standard layer with wire-specific parameters.
 */
public class ScreenLayerInput extends AssetInput implements Serializable {

  private final CableMaterial material;
  private final ComparableQuantity<Length> innerDiameter;
  private final ComparableQuantity<Length> outerDiameter;
  private final ComparableQuantity<ThermalResistivity> thermalResistivity;
  private final ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private final ComparableQuantity<Area> area;
  private final int wiresNumber;
  private final ComparableQuantity<Length> wireDiameter;
  private final ComparableQuantity<Length> lengthOfLay;
  private final ComparableQuantity<ElectricalResistivity> electricalResistivity;

  /**
   * Constructor for a screen layer
   *
   * @param uuid UUID of the screen layer
   * @param id Name/designation of this screen layer
   * @param material Material of the screen
   * @param innerDiameter Inner diameter of the screen layer
   * @param outerDiameter Outer diameter of the screen layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param area Optional real cross-sectional area (e.g. if different from geometry)
   * @param wiresNumber Number of individual wires in the screen
   * @param wireDiameter Diameter of an individual wire in the screen
   * @param lengthOfLay Optional length of lay (pitch) of the screen winding
   * @param electricalResistivity Electrical resistivity specific to the screen material
   */
  public ScreenLayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Optional<ComparableQuantity<Area>> area,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      Optional<ComparableQuantity<Length>> lengthOfLay,
      ComparableQuantity<ElectricalResistivity> electricalResistivity) {
    this(
        uuid,
        id,
        OperatorInput.NO_OPERATOR_ASSIGNED,
        OperationTime.notLimited(),
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        area.orElse(null),
        wiresNumber,
        wireDiameter,
        lengthOfLay.orElse(null),
        electricalResistivity,
        null);
  }

  public ScreenLayerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      Optional<ComparableQuantity<Area>> area,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      Optional<ComparableQuantity<Length>> lengthOfLay,
      ComparableQuantity<ElectricalResistivity> electricalResistivity) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        material,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        area.orElse(null),
        wiresNumber,
        wireDiameter,
        lengthOfLay.orElse(null),
        electricalResistivity,
        null);
  }

  public ScreenLayerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      ComparableQuantity<Area> area,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      ComparableQuantity<Length> lengthOfLay,
      ComparableQuantity<ElectricalResistivity> electricalResistivity,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.area = area;
    this.wiresNumber = wiresNumber;
    this.wireDiameter = wireDiameter;
    this.lengthOfLay = lengthOfLay;
    this.electricalResistivity = electricalResistivity;
    if (additionalInformation != null) setAdditionalInformation(additionalInformation);
  }

  public static class ScreenLayerInputCopyBuilder
      extends AssetInputCopyBuilder<ScreenLayerInputCopyBuilder> {

    private CableMaterial material;
    private ComparableQuantity<Length> innerDiameter;
    private ComparableQuantity<Length> outerDiameter;
    private ComparableQuantity<ThermalResistivity> thermalResistivity;
    private ComparableQuantity<ThermalCapacitance> thermalCapacitance;
    private ComparableQuantity<Area> area;
    private int wiresNumber;
    private ComparableQuantity<Length> wireDiameter;
    private ComparableQuantity<Length> lengthOfLay;
    private ComparableQuantity<ElectricalResistivity> electricalResistivity;

    protected ScreenLayerInputCopyBuilder(ScreenLayerInput entity) {
      super(entity);
      this.material = entity.material;
      this.innerDiameter = entity.innerDiameter;
      this.outerDiameter = entity.outerDiameter;
      this.thermalResistivity = entity.thermalResistivity;
      this.thermalCapacitance = entity.thermalCapacitance;
      this.area = entity.area;
      this.wiresNumber = entity.wiresNumber;
      this.wireDiameter = entity.wireDiameter;
      this.lengthOfLay = entity.lengthOfLay;
      this.electricalResistivity = entity.electricalResistivity;
    }

    public ScreenLayerInputCopyBuilder material(CableMaterial material) {
      this.material = material;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder innerDiameter(ComparableQuantity<Length> innerDiameter) {
      this.innerDiameter = innerDiameter;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder outerDiameter(ComparableQuantity<Length> outerDiameter) {
      this.outerDiameter = outerDiameter;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder thermalResistivity(
        ComparableQuantity<ThermalResistivity> tr) {
      this.thermalResistivity = tr;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder thermalCapacitance(
        ComparableQuantity<ThermalCapacitance> tc) {
      this.thermalCapacitance = tc;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder wiresNumber(int wiresNumber) {
      this.wiresNumber = wiresNumber;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder wireDiameter(ComparableQuantity<Length> wireDiameter) {
      this.wireDiameter = wireDiameter;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder lengthOfLay(ComparableQuantity<Length> lengthOfLay) {
      this.lengthOfLay = lengthOfLay;
      return thisInstance();
    }

    public ScreenLayerInputCopyBuilder electricalResistivity(
        ComparableQuantity<ElectricalResistivity> er) {
      this.electricalResistivity = er;
      return thisInstance();
    }

    @Override
    public ScreenLayerInput build() {
      return new ScreenLayerInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          material,
          innerDiameter,
          outerDiameter,
          thermalResistivity,
          thermalCapacitance,
          area,
          wiresNumber,
          wireDiameter,
          lengthOfLay,
          electricalResistivity,
          null);
    }

    @Override
    protected ScreenLayerInputCopyBuilder thisInstance() {
      return this;
    }
  }

  @Override
  public ScreenLayerInputCopyBuilder copy() {
    return new ScreenLayerInputCopyBuilder(this);
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

  public ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
    return thermalCapacitance;
  }

  public Optional<ComparableQuantity<Area>> area() {
    return Optional.ofNullable(area);
  }

  public int wiresNumber() {
    return wiresNumber;
  }

  public ComparableQuantity<Length> wireDiameter() {
    return wireDiameter;
  }

  public Optional<ComparableQuantity<Length>> lengthOfLay() {
    return Optional.ofNullable(lengthOfLay);
  }

  public ComparableQuantity<ElectricalResistivity> electricalResistivity() {
    return electricalResistivity;
  }

  @Override
  public @NonNull String toString() {
    return "ScreenLayerInput{"
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
        + ", wiresNumber="
        + wiresNumber
        + ", wireDiameter="
        + wireDiameter
        + ", lengthOfLay="
        + lengthOfLay
        + ", electricalResistivity="
        + electricalResistivity
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScreenLayerInput)) return false;
    if (!super.equals(o)) return false;
    ScreenLayerInput that = (ScreenLayerInput) o;
    return wiresNumber == that.wiresNumber
        && material == that.material
        && innerDiameter.equals(that.innerDiameter)
        && outerDiameter.equals(that.outerDiameter)
        && thermalResistivity.equals(that.thermalResistivity)
        && thermalCapacitance.equals(that.thermalCapacitance)
        && Objects.equals(area, that.area)
        && wireDiameter.equals(that.wireDiameter)
        && Objects.equals(lengthOfLay, that.lengthOfLay)
        && electricalResistivity.equals(that.electricalResistivity);
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
        area,
        wiresNumber,
        wireDiameter,
        lengthOfLay,
        electricalResistivity);
  }
}
