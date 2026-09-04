/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.utils.QuantityUtils;
import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
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
public class ScreenLayerInput extends AssetTypeInput {
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
   * Constructor for a screen layer.
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
      ComparableQuantity<Area> area,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      ComparableQuantity<Length> lengthOfLay,
      ComparableQuantity<ElectricalResistivity> electricalResistivity) {
    super(uuid, id);
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
  }

  /**
   * Constructor for a screen layer.
   *
   * @param uuid UUID of the screen layer
   * @param id Name/designation of this screen layer
   * @param material Material of the screen
   * @param innerDiameter Inner diameter of the screen layer
   * @param outerDiameter Outer diameter of the screen layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param wiresNumber Number of individual wires in the screen
   * @param wireDiameter Diameter of an individual wire in the screen
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
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      ComparableQuantity<ElectricalResistivity> electricalResistivity) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.wiresNumber = wiresNumber;
    this.wireDiameter = wireDiameter;
    this.electricalResistivity = electricalResistivity;
    this.area = null;
    this.lengthOfLay = null;
  }

  /**
   * Constructor for a screen layer.
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
   * @param electricalResistivity Electrical resistivity specific to the screen material @param
   *     additionalInformation That were provided by the source
   */
  public ScreenLayerInput(
      UUID uuid,
      String id,
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
    super(uuid, id);
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
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for a screen layer.
   *
   * @param uuid UUID of the screen layer
   * @param id Name/designation of this screen layer
   * @param material Material of the screen
   * @param innerDiameter Inner diameter of the screen layer
   * @param outerDiameter Outer diameter of the screen layer
   * @param thermalResistivity Thermal resistivity of the material
   * @param thermalCapacitance Thermal capacitance of the material
   * @param wiresNumber Number of individual wires in the screen
   * @param wireDiameter Diameter of an individual wire in the screen
   * @param electricalResistivity Electrical resistivity specific to the screen material @param
   *     additionalInformation That were provided by the source
   */
  public ScreenLayerInput(
      UUID uuid,
      String id,
      CableMaterial material,
      ComparableQuantity<Length> innerDiameter,
      ComparableQuantity<Length> outerDiameter,
      ComparableQuantity<ThermalResistivity> thermalResistivity,
      ComparableQuantity<ThermalCapacitance> thermalCapacitance,
      int wiresNumber,
      ComparableQuantity<Length> wireDiameter,
      ComparableQuantity<ElectricalResistivity> electricalResistivity,
      Map<String, String> additionalInformation) {
    super(uuid, id);
    this.material = material;
    this.innerDiameter = innerDiameter;
    this.outerDiameter = outerDiameter;
    this.thermalResistivity = thermalResistivity;
    this.thermalCapacitance = thermalCapacitance;
    this.wiresNumber = wiresNumber;
    this.wireDiameter = wireDiameter;
    this.electricalResistivity = electricalResistivity;
    this.area = null;
    this.lengthOfLay = null;
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

  public String name() {
    return getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScreenLayerInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(material, that.material)
        && QuantityUtils.equals(innerDiameter, that.innerDiameter)
        && QuantityUtils.equals(outerDiameter, that.outerDiameter)
        && QuantityUtils.equals(thermalResistivity, that.thermalResistivity)
        && QuantityUtils.equals(thermalCapacitance, that.thermalCapacitance)
        && QuantityUtils.equals(area, that.area)
        && wiresNumber == that.wiresNumber
        && QuantityUtils.equals(wireDiameter, that.wireDiameter)
        && QuantityUtils.equals(lengthOfLay, that.lengthOfLay)
        && QuantityUtils.equals(electricalResistivity, that.electricalResistivity);
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

  @Override
  public String toString() {
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
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public ScreenLayerInputCopyBuilder copy() {
    return new ScreenLayerInputCopyBuilder(this);
  }

  public static class ScreenLayerInputCopyBuilder
      extends AssetTypeInputCopyBuilder<ScreenLayerInputCopyBuilder> {
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

    protected CableMaterial material() {
      return material;
    }

    public ScreenLayerInputCopyBuilder innerDiameter(ComparableQuantity<Length> innerDiameter) {
      this.innerDiameter = innerDiameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> innerDiameter() {
      return innerDiameter;
    }

    public ScreenLayerInputCopyBuilder outerDiameter(ComparableQuantity<Length> outerDiameter) {
      this.outerDiameter = outerDiameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> outerDiameter() {
      return outerDiameter;
    }

    public ScreenLayerInputCopyBuilder thermalResistivity(
        ComparableQuantity<ThermalResistivity> thermalResistivity) {
      this.thermalResistivity = thermalResistivity;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalResistivity> thermalResistivity() {
      return thermalResistivity;
    }

    public ScreenLayerInputCopyBuilder thermalCapacitance(
        ComparableQuantity<ThermalCapacitance> thermalCapacitance) {
      this.thermalCapacitance = thermalCapacitance;
      return thisInstance();
    }

    protected ComparableQuantity<ThermalCapacitance> thermalCapacitance() {
      return thermalCapacitance;
    }

    public ScreenLayerInputCopyBuilder area(ComparableQuantity<Area> area) {
      this.area = area;
      return thisInstance();
    }

    protected ComparableQuantity<Area> area() {
      return area;
    }

    public ScreenLayerInputCopyBuilder wiresNumber(int wiresNumber) {
      this.wiresNumber = wiresNumber;
      return thisInstance();
    }

    protected int wiresNumber() {
      return wiresNumber;
    }

    public ScreenLayerInputCopyBuilder wireDiameter(ComparableQuantity<Length> wireDiameter) {
      this.wireDiameter = wireDiameter;
      return thisInstance();
    }

    protected ComparableQuantity<Length> wireDiameter() {
      return wireDiameter;
    }

    public ScreenLayerInputCopyBuilder lengthOfLay(ComparableQuantity<Length> lengthOfLay) {
      this.lengthOfLay = lengthOfLay;
      return thisInstance();
    }

    protected ComparableQuantity<Length> lengthOfLay() {
      return lengthOfLay;
    }

    public ScreenLayerInputCopyBuilder electricalResistivity(
        ComparableQuantity<ElectricalResistivity> electricalResistivity) {
      this.electricalResistivity = electricalResistivity;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricalResistivity> electricalResistivity() {
      return electricalResistivity;
    }

    @Override
    public ScreenLayerInput build() {
      return new ScreenLayerInput(
          getUuid(),
          getId(),
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
          getAdditionalInformation());
    }

    @Override
    protected ScreenLayerInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
