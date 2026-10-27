/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import edu.ie3.util.quantities.interfaces.VolumetricHeatCapacity;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Temperature;
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/** Represents the physical properties of a soil type. */
public class SoilType extends UniqueInputEntity {

  private final String id;
  private final ComparableQuantity<ThermalResistivity> thermalResistivityWet;
  private final ComparableQuantity<ThermalResistivity> thermalResistivityDry;
  private final ComparableQuantity<VolumetricHeatCapacity> volumetricHeatCapacity;
  private final ComparableQuantity<Temperature> criticalTemperatureDifference;

  /**
   * Constructor of a soil type.
   *
   * @param uuid unique identifier of this soil type
   * @param id human-readable id of this soil type
   * @param thermalResistivityWet thermal resistivity of the soil in the wet state
   * @param thermalResistivityDry thermal resistivity of the soil in the dry state
   * @param volumetricHeatCapacity volumetric heat capacity of the soil
   * @param criticalTemperatureDifference over-temperature at which the soil transitions from the
   *     wet to the dry state
   */
  public SoilType(
      UUID uuid,
      String id,
      ComparableQuantity<ThermalResistivity> thermalResistivityWet,
      ComparableQuantity<ThermalResistivity> thermalResistivityDry,
      ComparableQuantity<VolumetricHeatCapacity> volumetricHeatCapacity,
      ComparableQuantity<Temperature> criticalTemperatureDifference) {
    this(
        uuid,
        id,
        thermalResistivityWet,
        thermalResistivityDry,
        volumetricHeatCapacity,
        criticalTemperatureDifference,
        null);
  }

  /**
   * Constructor of a soil type with additional information.
   *
   * @param uuid unique identifier of this soil type
   * @param id human-readable id of this soil type
   * @param thermalResistivityWet thermal resistivity of the soil in the wet state
   * @param thermalResistivityDry thermal resistivity of the soil in the dry state
   * @param volumetricHeatCapacity volumetric heat capacity of the soil
   * @param criticalTemperatureDifference over-temperature at which the soil transitions from the
   *     wet to the dry state
   * @param additionalInformation additional information; may be {@code null}
   */
  public SoilType(
      UUID uuid,
      String id,
      ComparableQuantity<ThermalResistivity> thermalResistivityWet,
      ComparableQuantity<ThermalResistivity> thermalResistivityDry,
      ComparableQuantity<VolumetricHeatCapacity> volumetricHeatCapacity,
      ComparableQuantity<Temperature> criticalTemperatureDifference,
      Map<String, String> additionalInformation) {
    super(uuid);
    this.id = id;
    this.thermalResistivityWet = thermalResistivityWet;
    this.thermalResistivityDry = thermalResistivityDry;
    this.volumetricHeatCapacity = volumetricHeatCapacity;
    this.criticalTemperatureDifference = criticalTemperatureDifference;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Returns the human-readable id of this soil type.
   *
   * @return the human-readable id of this soil type
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the thermal resistivity of the soil in the wet state.
   *
   * @return the thermal resistivity of the soil in the wet state
   */
  public ComparableQuantity<ThermalResistivity> getThermalResistivityWet() {
    return thermalResistivityWet;
  }

  /**
   * Returns the thermal resistivity of the soil in the dry state.
   *
   * @return the thermal resistivity of the soil in the dry state
   */
  public ComparableQuantity<ThermalResistivity> getThermalResistivityDry() {
    return thermalResistivityDry;
  }

  /**
   * Returns the volumetric heat capacity of the soil.
   *
   * @return the volumetric heat capacity of the soil
   */
  public ComparableQuantity<VolumetricHeatCapacity> getVolumetricHeatCapacity() {
    return volumetricHeatCapacity;
  }

  /**
   * Returns the over-temperature at which the soil transitions from the wet to the dry state.
   *
   * @return the critical temperature difference
   */
  public ComparableQuantity<Temperature> getCriticalTemperatureDifference() {
    return criticalTemperatureDifference;
  }

  /**
   * Creates a copy builder pre-populated with the current field values of this soil type.
   *
   * @return a copy builder for this soil type
   */
  public SoilTypeCopyBuilder copy() {
    return new SoilTypeCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SoilType that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(id, that.id)
        && Objects.equals(thermalResistivityWet, that.thermalResistivityWet)
        && Objects.equals(thermalResistivityDry, that.thermalResistivityDry)
        && Objects.equals(volumetricHeatCapacity, that.volumetricHeatCapacity)
        && Objects.equals(criticalTemperatureDifference, that.criticalTemperatureDifference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        id,
        thermalResistivityWet,
        thermalResistivityDry,
        volumetricHeatCapacity,
        criticalTemperatureDifference);
  }

  @Override
  public @NonNull String toString() {
    return "SoilType{"
        + "uuid="
        + getUuid()
        + ", id='"
        + id
        + '\''
        + ", thermalResistivityWet="
        + thermalResistivityWet
        + ", thermalResistivityDry="
        + thermalResistivityDry
        + ", volumetricHeatCapacity="
        + volumetricHeatCapacity
        + ", criticalTemperatureDifference="
        + criticalTemperatureDifference
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  public static class SoilTypeCopyBuilder
      extends UniqueInputEntityCopyBuilder<SoilTypeCopyBuilder> {

    private String id;
    private ComparableQuantity<ThermalResistivity> thermalResistivityWet;
    private ComparableQuantity<ThermalResistivity> thermalResistivityDry;
    private ComparableQuantity<VolumetricHeatCapacity> volumetricHeatCapacity;
    private ComparableQuantity<Temperature> criticalTemperatureDifference;
    private Map<String, String> additionalInformation;

    private SoilTypeCopyBuilder(SoilType entity) {
      super(entity);
      this.id = entity.getId();
      this.thermalResistivityWet = entity.getThermalResistivityWet();
      this.thermalResistivityDry = entity.getThermalResistivityDry();
      this.volumetricHeatCapacity = entity.getVolumetricHeatCapacity();
      this.criticalTemperatureDifference = entity.getCriticalTemperatureDifference();
      this.additionalInformation = entity.getAdditionalInformation();
    }

    public SoilTypeCopyBuilder id(String id) {
      this.id = id;
      return thisInstance();
    }

    public SoilTypeCopyBuilder thermalResistivityWet(
        ComparableQuantity<ThermalResistivity> thermalResistivityWet) {
      this.thermalResistivityWet = thermalResistivityWet;
      return thisInstance();
    }

    public SoilTypeCopyBuilder thermalResistivityDry(
        ComparableQuantity<ThermalResistivity> thermalResistivityDry) {
      this.thermalResistivityDry = thermalResistivityDry;
      return thisInstance();
    }

    public SoilTypeCopyBuilder volumetricHeatCapacity(
        ComparableQuantity<VolumetricHeatCapacity> volumetricHeatCapacity) {
      this.volumetricHeatCapacity = volumetricHeatCapacity;
      return thisInstance();
    }

    public SoilTypeCopyBuilder criticalTemperatureDifference(
        ComparableQuantity<Temperature> criticalTemperatureDifference) {
      this.criticalTemperatureDifference = criticalTemperatureDifference;
      return thisInstance();
    }

    @Override
    protected SoilTypeCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public SoilType build() {
      return new SoilType(
          getUuid(),
          id,
          thermalResistivityWet,
          thermalResistivityDry,
          volumetricHeatCapacity,
          criticalTemperatureDifference,
          additionalInformation);
    }
  }
}
