/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.system.BmInput;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes the type of a {@link edu.ie3.datamodel.models.input.system.HpInput} */
public class HpTypeInput extends SystemParticipantTypeInput {
  /** Thermal output of the heat pump (typically in kW), when sRated * cosphi_rated is consumed */
  private final ComparableQuantity<Power> pThermal;

  /**
   * @param uuid of the input entity
   * @param id of this type of HP
   * @param capex Captial expense for this type of HP (typically in €)
   * @param opex Operating expense for this type of HP (typically in €)
   * @param cosphiRated Power factor for this type of HP
   * @param sRated Rated apparent power
   * @param pThermal Thermal output of the heat pump, when sRated * cosphi_rated is consumed
   *     electrically
   */
  public HpTypeInput(
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HpTypeInput that)) return false;
    if (!super.equals(o)) return false;
    return pThermal.equals(that.pThermal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), pThermal);
  }

  @Override
  public String toString() {
    return "HpTypeInput{"
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
}
