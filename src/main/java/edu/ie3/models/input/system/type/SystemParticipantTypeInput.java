/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.type;

import edu.ie3.models.input.AssetTypeInput;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;

/** Describes the type of a {@link edu.ie3.models.input.system.SystemParticipantInput} */
public abstract class SystemParticipantTypeInput extends AssetTypeInput {
  /** Capital expense for this type of system participant (typically in €) */
  private Quantity<Currency> capex;
  /** Operating expense for this type of system participant (typically in €) */
  private Quantity<EnergyPrice> opex;
  /** Power factor for this type of system participant */
  private double cosphi;

  /**
   * @param uuid of the input entity
   * @param id of this type of system participant
   * @param capex Captial expense for this type of system participant (typically in €)
   * @param opex Operating expense for this type of system participant (typically in €)
   * @param cosphi Power factor for this type of system participant
   */
  public SystemParticipantTypeInput(
      UUID uuid, String id, Quantity<Currency> capex, Quantity<EnergyPrice> opex, double cosphi) {
    super(uuid, id);
    this.capex = capex;
    this.opex = opex;
    this.cosphi = cosphi;
  }

  public Quantity<Currency> getCapex() {
    return capex;
  }

  public void setCapex(Quantity<Currency> capex) {
    this.capex = capex;
  }

  public Quantity<EnergyPrice> getOpex() {
    return opex;
  }

  public void setOpex(Quantity<EnergyPrice> opex) {
    this.opex = opex;
  }

  public double getCosphi() {
    return cosphi;
  }

  public void setCosphi(double cosphi) {
    this.cosphi = cosphi;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantTypeInput that = (SystemParticipantTypeInput) o;
    return Double.compare(that.cosphi, cosphi) == 0
        && capex.equals(that.capex)
        && opex.equals(that.opex);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), capex, opex, cosphi);
  }
}
