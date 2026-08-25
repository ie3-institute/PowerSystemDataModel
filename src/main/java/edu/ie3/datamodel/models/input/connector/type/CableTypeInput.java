/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;

/**
 * Represents detailed cable construction data at the type/catalog level. This class contains the
 * complete specification of a cable type including conductors, insulation layers, screens, armor,
 * and jacket elements, as well as electrical and thermal parameters.
 */
public class CableTypeInput extends AssetTypeInput {
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
      ScreenLayerInput screen,
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
    this.screen = screen;
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CableTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return coreNumber == that.coreNumber
        && Objects.equals(conductor, that.conductor)
        && Objects.equals(isolation, that.isolation)
        && Objects.equals(screen, that.screen)
        && Objects.equals(filler, that.filler)
        && Objects.equals(armor, that.armor)
        && Objects.equals(jack, that.jack)
        && limitTemperature.equals(that.limitTemperature)
        && frequency.equals(that.frequency)
        && skinEffectCoefficient == that.skinEffectCoefficient
        && proximityEffectCoefficient == that.proximityEffectCoefficient
        && electricalCapacitance.equals(that.electricalCapacitance)
        && tanDelta == that.tanDelta
        && circulatingLossFactor == that.circulatingLossFactor
        && eddyCurrentLossFactor == that.eddyCurrentLossFactor;
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
  public String toString() {
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
        + ", proximityEffectCoefficient="
        + proximityEffectCoefficient
        + ", electricalCapacitance="
        + electricalCapacitance
        + ", tanDelta="
        + tanDelta
        + ", circulatingLossFactor="
        + circulatingLossFactor
        + ", eddyCurrentLossFactor="
        + eddyCurrentLossFactor
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public CableTypeInputCopyBuilder copy() {
    return new CableTypeInputCopyBuilder(this);
  }

  public static class CableTypeInputCopyBuilder
      extends AssetTypeInputCopyBuilder<CableTypeInputCopyBuilder> {
    private int coreNumber;

    private ConductorInput conductor;

    private List<LayerInput> isolation;

    private ScreenLayerInput screen;

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
      this.screen = entity.screen;
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

    protected int getCoreNumber() {
      return coreNumber;
    }

    public CableTypeInputCopyBuilder conductor(ConductorInput conductor) {
      this.conductor = conductor;
      return thisInstance();
    }

    protected ConductorInput getConductor() {
      return conductor;
    }

    public CableTypeInputCopyBuilder isolation(List<LayerInput> isolation) {
      this.isolation = isolation;
      return thisInstance();
    }

    protected List<LayerInput> getIsolation() {
      return isolation;
    }

    public CableTypeInputCopyBuilder screen(ScreenLayerInput screen) {
      this.screen = screen;
      return thisInstance();
    }

    protected ScreenLayerInput getScreen() {
      return screen;
    }

    public CableTypeInputCopyBuilder filler(List<LayerInput> filler) {
      this.filler = filler;
      return thisInstance();
    }

    protected List<LayerInput> getFiller() {
      return filler;
    }

    public CableTypeInputCopyBuilder armor(List<LayerInput> armor) {
      this.armor = armor;
      return thisInstance();
    }

    protected List<LayerInput> getArmor() {
      return armor;
    }

    public CableTypeInputCopyBuilder jack(List<LayerInput> jack) {
      this.jack = jack;
      return thisInstance();
    }

    protected List<LayerInput> getJack() {
      return jack;
    }

    public CableTypeInputCopyBuilder limitTemperature(
        ComparableQuantity<Temperature> limitTemperature) {
      this.limitTemperature = limitTemperature;
      return thisInstance();
    }

    protected ComparableQuantity<Temperature> getLimitTemperature() {
      return limitTemperature;
    }

    public CableTypeInputCopyBuilder frequency(ComparableQuantity<Frequency> frequency) {
      this.frequency = frequency;
      return thisInstance();
    }

    protected ComparableQuantity<Frequency> getFrequency() {
      return frequency;
    }

    public CableTypeInputCopyBuilder skinEffectCoefficient(double skinEffectCoefficient) {
      this.skinEffectCoefficient = skinEffectCoefficient;
      return thisInstance();
    }

    protected double getSkinEffectCoefficient() {
      return skinEffectCoefficient;
    }

    public CableTypeInputCopyBuilder proximityEffectCoefficient(double proximityEffectCoefficient) {
      this.proximityEffectCoefficient = proximityEffectCoefficient;
      return thisInstance();
    }

    protected double getProximityEffectCoefficient() {
      return proximityEffectCoefficient;
    }

    public CableTypeInputCopyBuilder electricalCapacitance(
        ComparableQuantity<ElectricCapacitance> electricalCapacitance) {
      this.electricalCapacitance = electricalCapacitance;
      return thisInstance();
    }

    protected ComparableQuantity<ElectricCapacitance> getElectricalCapacitance() {
      return electricalCapacitance;
    }

    public CableTypeInputCopyBuilder tanDelta(double tanDelta) {
      this.tanDelta = tanDelta;
      return thisInstance();
    }

    protected double getTanDelta() {
      return tanDelta;
    }

    public CableTypeInputCopyBuilder circulatingLossFactor(double circulatingLossFactor) {
      this.circulatingLossFactor = circulatingLossFactor;
      return thisInstance();
    }

    protected double getCirculatingLossFactor() {
      return circulatingLossFactor;
    }

    public CableTypeInputCopyBuilder eddyCurrentLossFactor(double eddyCurrentLossFactor) {
      this.eddyCurrentLossFactor = eddyCurrentLossFactor;
      return thisInstance();
    }

    protected double getEddyCurrentLossFactor() {
      return eddyCurrentLossFactor;
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
