/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.AcInput} */
public class AcTypeInput extends SystemParticipantTypeInput {
  /**
   * Thermal output of the air condition (typically in kW), when sRated * cosphi_rated is consumed
   */
  private final ComparableQuantity<Power> pThermal;

  /**
   * @param uuid of the input entity
   * @param id of this type of AC
   * @param capex Captial expense for this type of AC (typically in €)
   * @param opex Operating expense for this type of AC (typically in €)
   * @param cosphiRated Power factor for this type of AC
   * @param sRated Rated apparent power
   * @param pThermal Thermal output of the air condition, when sRated * cosphi_rated is consumed
   *     electrically
   */
  public AcTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosphiRated,
      ComparableQuantity<Power> pThermal) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.pThermal = pThermal.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public ComparableQuantity<Power> getpThermal() {
    return pThermal;
  }

  @Override
  public AcTypeInputCopyBuilder copy() {
    return new AcTypeInputCopyBuilder(this);
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
        + "capex="
        + getCapex()
        + ", opex="
        + getOpex()
        + ", sRated="
        + getsRated()
        + ", cosphiRated="
        + getCosPhiRated()
        + "pThermal="
        + pThermal
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link AcTypeInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link AcTypeInput}
   */
  public static class AcTypeInputCopyBuilder
      extends SystemParticipantTypeInputCopyBuilder<AcTypeInput.AcTypeInputCopyBuilder> {

    private ComparableQuantity<Power> pThermal;

    private AcTypeInputCopyBuilder(AcTypeInput entity) {
      super(entity);
      this.pThermal = entity.getpThermal();
    }

    public AcTypeInputCopyBuilder pThermal(ComparableQuantity<Power> pThermal) {
      this.pThermal = pThermal;
      return thisInstance();
    }

    public ComparableQuantity<Power> getpThermal() {
      return pThermal;
    }

    @Override
    public AcTypeInput.AcTypeInputCopyBuilder scale(Double factor) {
      capex(getCapex().multiply(factor));
      sRated(getsRated().multiply(factor));
      pThermal(getpThermal().multiply(factor));
      return thisInstance();
    }

    @Override
    public AcTypeInput build() {
      return new AcTypeInput(
          getUuid(), getId(), getCapex(), getOpex(), getsRated(), getCosPhiRated(), pThermal);
    }

    @Override
    protected AcTypeInput.AcTypeInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
