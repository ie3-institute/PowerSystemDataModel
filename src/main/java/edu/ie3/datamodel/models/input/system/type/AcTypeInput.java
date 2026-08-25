/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of {@link edu.ie3.datamodel.models.input.system.AcInput}. */
public class AcTypeInput extends SystemParticipantTypeInput {
  /**
   * Thermal output of the air condition (typically in kW), when sRated * cosPhiRated is consumed.
   */
  private final ComparableQuantity<Power> pThermal;

  /**
   * @param uuid of the input entity
   * @param id of this type of AC
   * @param capex Capital expense for this type of AC (typically in €)
   * @param opex Operating expense for this type of AC (typically in €)
   * @param cosPhiRated Power factor for this type of AC
   * @param sRated Rated apparent power
   * @param pThermal Thermal output of the air condition, when sRated * cosPhiRated is consumed
   *     electrically
   */
  public AcTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pThermal) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.pThermal = pThermal;
  }

  /**
   * @param uuid of the input entity
   * @param id of this type of AC
   * @param capex Capital expense for this type of AC (typically in €)
   * @param opex Operating expense for this type of AC (typically in €)
   * @param cosPhiRated Power factor for this type of AC
   * @param sRated Rated apparent power
   * @param pThermal Thermal output of the air condition, when sRated * cosPhiRated is consumed
   *     electrically
   * @param additionalInformation That were provided by the source
   */
  public AcTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      ComparableQuantity<Power> pThermal,
      Map<String, String> additionalInformation) {
    super(uuid, id, capex, opex, sRated, cosPhiRated);
    this.pThermal = pThermal;
    setAdditionalInformation(additionalInformation);
  }

  public ComparableQuantity<Power> getpThermal() {
    return pThermal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AcTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return pThermal.equals(that.pThermal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pThermal);
  }

  @Override
  public String toString() {
    return "AcTypeInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosPhiRated="
        + getCosPhiRated()
        + ", pThermal="
        + pThermal
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public AcTypeInputCopyBuilder copy() {
    return new AcTypeInputCopyBuilder(this);
  }

  public static class AcTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<AcTypeInputCopyBuilder> {
    private ComparableQuantity<Power> pThermal;

    protected AcTypeInputCopyBuilder(AcTypeInput entity) {
      super(entity);
      this.pThermal = entity.pThermal;
    }

    public AcTypeInputCopyBuilder pThermal(ComparableQuantity<Power> pThermal) {
      this.pThermal = pThermal;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getpThermal() {
      return pThermal;
    }

    @Override
    public AcTypeInputCopyBuilder scale(double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      pThermal(getpThermal().multiply(factor));
      return thisInstance();
    }

    @Override
    public AcTypeInput build() {
      return new AcTypeInput(
          getUuid(),
          getId(),
          getCapex(),
          getOpex(),
          getsRated(),
          getCosPhiRated(),
          pThermal,
          getAdditionalInformation());
    }

    @Override
    protected AcTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
