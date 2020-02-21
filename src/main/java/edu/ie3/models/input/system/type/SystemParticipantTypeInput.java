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
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.SystemParticipantInput} */
public abstract class SystemParticipantTypeInput extends AssetTypeInput {
  /** Capital expense for this type of system participant (typically in €) */
  private final Quantity<Currency> capex;
  /** Operating expense for this type of system participant (typically in €) */
  private final Quantity<EnergyPrice> opex;
  /** Rated apparent power of the type (in kVA) */
  private final Quantity<Power> sRated;
  /** Power factor for this type of system participant */
  private final double cosphiRated;

  /**
   * @param uuid of the input entity
   * @param id of this type of system participant
   * @param capex Captial expense for this type of system participant (typically in €)
   * @param opex Operating expense for this type of system participant (typically in €)
   * @param cosphiRated Power factor for this type of system participant
   */
  public SystemParticipantTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, id);
    this.capex = capex;
    this.opex = opex;
    this.sRated = sRated;
    this.cosphiRated = cosphiRated;
  }

  public Quantity<Currency> getCapex() {
    return capex;
  }

  public Quantity<EnergyPrice> getOpex() {
    return opex;
  }

  public Quantity<Power> getsRated() {
    return sRated;
  }

  public double getCosphiRated() {
    return cosphiRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantTypeInput that = (SystemParticipantTypeInput) o;
    return Double.compare(that.cosphiRated, cosphiRated) == 0
        && capex.equals(that.capex)
        && opex.equals(that.opex)
        && sRated.equals(that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), capex, opex, sRated, cosphiRated);
  }
}
