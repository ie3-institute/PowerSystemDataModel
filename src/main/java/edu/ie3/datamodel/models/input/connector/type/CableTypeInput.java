/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import org.jspecify.annotations.NonNull;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents detailed cable construction data at the type/catalog level. This class contains the
 * complete specification of a cable type including conductors, insulation layers, screens, armor,
 * and jacket elements, as well as electrical and thermal parameters.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CableTypeInput extends AssetTypeInput implements InputEntity {

  private final int coreNumber;
  private final ConductorInput conductor;
  private final List<LayerInput> isolation;
  private final ScreenLayerInput screen;
  private final List<LayerInput> filler;
  private final List<LayerInput> armor;
  private final List<LayerInput> jack;
  private final ComparableQuantity<Temperature> limitTemperature;
  private final ComparableQuantity<Frequency> frequency;
  private final double skinEffectCoefficient;
  private final double proximityEffectCoefficient;
  private final ComparableQuantity<ElectricCapacitance> electricalCapacitance;
  private final double tanDelta;
  private final double circulatingLossFactor;
  private final double eddyCurrentLossFactor;

  /**
   * Represents detailed cable construction data at the type/catalog level. This class contains the
   * complete specification of a cable type including conductors, insulation layers, screens, armor,
   * and jacket elements, as well as electrical and thermal parameters.
   *
   * @param uuid Unique identifier for this cable type
   * @param id Human-readable identifier/name for this cable type (e.g., "NA2XS2Y 1x120 RM/25 12/20
   *     kV")
   * @param coreNumber Number of cores/conductors in the cable (e.g., 1 for single-core, 3 for
   *     three-phase)
   * @param conductor The innermost conductor/core
   * @param isolation List of insulation layers (from inner to outer)
   * @param screen Optional cable screen layer
   * @param filler List of filler layers (from inner to outer)
   * @param armor List of armor layers (from inner to outer)
   * @param jack List of jacket/outer sheath layers (from inner to outer)
   * @param limitTemperature Maximum permissible operating temperature (°C)
   * @param frequency Rated frequency
   * @param skinEffectCoefficient Skin effect coefficient
   * @param proximityEffectCoefficient Proximity effect coefficient
   * @param electricalCapacitance Capacitance per unit length
   * @param tanDelta Dielectric loss factor tan(δ)
   * @param circulatingLossFactor Circulating loss factor
   * @param eddyCurrentLossFactor Eddy current loss factor
   */
  public CableTypeInput(
      UUID uuid,
      String id,
      int coreNumber,
      ConductorInput conductor,
      List<LayerInput> isolation,
      Optional<ScreenLayerInput> screen,
      List<LayerInput> filler,
      List<LayerInput> armor,
      List<LayerInput> jack,
      ComparableQuantity<Temperature> limitTemperature,
      ComparableQuantity<Frequency> frequency,
      double skinEffectCoefficient,
      double proximityEffectCoefficient,
      ComparableQuantity<ElectricCapacitance> electricalCapacitance,
      double tanDelta,
      double circulatingLossFactor,
      double eddyCurrentLossFactor) {

    super(uuid, id);

    this.coreNumber = coreNumber;
    this.conductor = conductor;
    this.isolation = List.copyOf(isolation);
    this.screen = screen.orElse(null);
    this.filler = List.copyOf(filler);
    this.armor = List.copyOf(armor);
    this.jack = List.copyOf(jack);
    this.limitTemperature = limitTemperature;
    this.frequency = frequency;
    this.skinEffectCoefficient = skinEffectCoefficient;
    this.proximityEffectCoefficient = proximityEffectCoefficient;
    this.electricalCapacitance = electricalCapacitance;
    this.tanDelta = tanDelta;
    this.circulatingLossFactor = circulatingLossFactor;
    this.eddyCurrentLossFactor = eddyCurrentLossFactor;
  }

  public int getCoreNumber() {
    return coreNumber;
  }

  public ConductorInput getConductor() {
    return conductor;
  }

  public List<LayerInput> getIsolation() {
    return isolation;
  }

  public Optional<ScreenLayerInput> getScreen() {
    return Optional.ofNullable(screen);
  }

  public List<LayerInput> getFiller() {
    return filler;
  }

  public List<LayerInput> getArmor() {
    return armor;
  }

  public List<LayerInput> getJack() {
    return jack;
  }

  public ComparableQuantity<Temperature> getLimitTemperature() {
    return limitTemperature;
  }

  public ComparableQuantity<Frequency> getFrequency() {
    return frequency;
  }

  public double getSkinEffectCoefficient() {
    return skinEffectCoefficient;
  }

  public double getProximityEffectCoefficient() {
    return proximityEffectCoefficient;
  }

  public ComparableQuantity<ElectricCapacitance> getElectricalCapacitance() {
    return electricalCapacitance;
  }

  public double getTanDelta() {
    return tanDelta;
  }

  public double getCirculatingLossFactor() {
    return circulatingLossFactor;
  }

  public double getEddyCurrentLossFactor() {
    return eddyCurrentLossFactor;
  }

  @Override
  public CableTypeInputCopyBuilder copy() {
    return new CableTypeInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CableTypeInput that)) return false;
    if (!super.equals(o)) return false;

    return coreNumber == that.coreNumber
        && Double.compare(that.skinEffectCoefficient, skinEffectCoefficient) == 0
        && Double.compare(that.proximityEffectCoefficient, proximityEffectCoefficient) == 0
        && Double.compare(that.tanDelta, tanDelta) == 0
        && Double.compare(that.circulatingLossFactor, circulatingLossFactor) == 0
        && Double.compare(that.eddyCurrentLossFactor, eddyCurrentLossFactor) == 0
        && conductor.equals(that.conductor)
        && isolation.equals(that.isolation)
        && Objects.equals(screen, that.screen)
        && filler.equals(that.filler)
        && armor.equals(that.armor)
        && jack.equals(that.jack)
        && limitTemperature.equals(that.limitTemperature)
        && frequency.equals(that.frequency)
        && electricalCapacitance.equals(that.electricalCapacitance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(),
        coreNumber,
        conductor,
        isolation,
        screen,
        filler,
        armor,
        jack,
        limitTemperature,
        frequency,
        skinEffectCoefficient,
        proximityEffectCoefficient,
        electricalCapacitance,
        tanDelta,
        circulatingLossFactor,
        eddyCurrentLossFactor);
  }

  @Override
  public @NonNull String toString() {
    return "CableTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", coreNumber="
        + coreNumber
        + ", conductor="
        + conductor
        + ", isolation="
        + isolation
        + ", screen="
        + screen
        + ", filler="
        + filler
        + ", armor="
        + armor
        + ", jack="
        + jack
        + ", limitTemperature="
        + limitTemperature
        + ", frequency="
        + frequency
        + ", skinEffectCoefficient="
        + skinEffectCoefficient
        + ", proximityEffectCoeff="
        + proximityEffectCoefficient
        + ", electricalCapacitance="
        + electricalCapacitance
        + ", tanDelta="
        + tanDelta
        + ", circulatingLossFactor="
        + circulatingLossFactor
        + ", eddyCurrentLossFactor="
        + eddyCurrentLossFactor
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * CableTypeInput}
   */
  public static final class CableTypeInputCopyBuilder
      extends AssetTypeInput.AssetTypeInputCopyBuilder<CableTypeInputCopyBuilder> {

    private int coreNumber;
    private ConductorInput conductor;
    private List<LayerInput> isolation;
    private Optional<ScreenLayerInput> screen;
    private List<LayerInput> filler;
    private List<LayerInput> armor;
    private List<LayerInput> jack;
    private ComparableQuantity<Temperature> limitTemperature;
    private ComparableQuantity<Frequency> frequency;
    private double skinEffectCoefficient;
    private double proximityEffectCoefficient;
    private ComparableQuantity<ElectricCapacitance> electricalCapacitance;
    private double tanDelta;
    private double circulatingLossFactor;
    private double eddyCurrentLossFactor;

    protected CableTypeInputCopyBuilder(CableTypeInput entity) {
      super(entity);
      this.coreNumber = entity.coreNumber;
      this.conductor = entity.conductor;
      this.isolation = entity.isolation;
      this.screen = Optional.ofNullable(entity.screen);
      this.filler = entity.filler;
      this.armor = entity.armor;
      this.jack = entity.jack;
      this.limitTemperature = entity.limitTemperature;
      this.frequency = entity.frequency;
      this.skinEffectCoefficient = entity.skinEffectCoefficient;
      this.proximityEffectCoefficient = entity.proximityEffectCoefficient;
      this.electricalCapacitance = entity.electricalCapacitance;
      this.tanDelta = entity.tanDelta;
      this.circulatingLossFactor = entity.circulatingLossFactor;
      this.eddyCurrentLossFactor = entity.eddyCurrentLossFactor;
    }

    public CableTypeInputCopyBuilder coreNumber(int coreNumber) {
      this.coreNumber = coreNumber;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder conductor(ConductorInput conductor) {
      this.conductor = conductor;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder isolation(List<LayerInput> isolation) {
      this.isolation = isolation;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder screen(Optional<ScreenLayerInput> screen) {
      this.screen = screen;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder filler(List<LayerInput> filler) {
      this.filler = filler;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder armor(List<LayerInput> armor) {
      this.armor = armor;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder jack(List<LayerInput> jack) {
      this.jack = jack;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder limitTemperature(
        ComparableQuantity<Temperature> limitTemperature) {
      this.limitTemperature = limitTemperature;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder frequency(ComparableQuantity<Frequency> frequency) {
      this.frequency = frequency;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder skinEffectCoefficient(double value) {
      this.skinEffectCoefficient = value;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder proximityEffectCoefficient(double value) {
      this.proximityEffectCoefficient = value;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder electricalCapacitance(
        ComparableQuantity<ElectricCapacitance> value) {
      this.electricalCapacitance = value;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder tanDelta(double value) {
      this.tanDelta = value;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder circulatingLossFactor(double value) {
      this.circulatingLossFactor = value;
      return thisInstance();
    }

    public CableTypeInputCopyBuilder eddyCurrentLossFactor(double value) {
      this.eddyCurrentLossFactor = value;
      return thisInstance();
    }

    @Override
    public CableTypeInput build() {
      return new CableTypeInput(
          getUuid(),
          getId(),
          coreNumber,
          conductor,
          isolation,
          screen,
          filler,
          armor,
          jack,
          limitTemperature,
          frequency,
          skinEffectCoefficient,
          proximityEffectCoefficient,
          electricalCapacitance,
          tanDelta,
          circulatingLossFactor,
          eddyCurrentLossFactor);
    }

    @Override
    protected CableTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
