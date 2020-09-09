/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.utils.QuantityUtil;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.SystemParticipantInput} */
public abstract class SystemParticipantTypeInput extends AssetTypeInput {
  /** Capital expense for this type of system participant (typically in €) */
  private final ComparableQuantity<Currency> capex;
  /** Operating expense for this type of system participant (typically in €/MWh) */
  private final ComparableQuantity<EnergyPrice> opex;
  /** Rated apparent power of the type (in kVA) */
  private final ComparableQuantity<Power> sRated;
  /** Power factor for this type of system participant */
  private final double cosPhiRated;

  /**
   * @param uuid of the input entity
   * @param id of this type of system participant
   * @param capex Captial expense for this type of system participant (typically in €)
   * @param opex Operating expense for this type of system participant (typically in €/MWh)
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor for this type of system participant
   */
  public SystemParticipantTypeInput(
      UUID uuid,
      String id,
      ComparableQuantity<Currency> capex,
      ComparableQuantity<EnergyPrice> opex,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id);
    this.capex = capex;
    this.opex = opex;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  public ComparableQuantity<Currency> getCapex() {
    return capex;
  }

  public ComparableQuantity<EnergyPrice> getOpex() {
    return opex;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantTypeInput that = (SystemParticipantTypeInput) o;
    return Double.compare(that.cosPhiRated, cosPhiRated) == 0
        && QuantityUtil.equals(capex, that.capex)
        && QuantityUtil.equals(opex, that.opex)
        && QuantityUtil.equals(sRated, that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), capex, opex, sRated, cosPhiRated);
  }

  @Override
  public String toString() {
    return "SystemParticipantTypeInput{"
        + "capex="
        + capex
        + ", opex="
        + opex
        + ", sRated="
        + sRated
        + ", cosphiRated="
        + cosPhiRated
        + '}';
  }
}
