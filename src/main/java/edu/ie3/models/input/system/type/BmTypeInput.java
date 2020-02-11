/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system.type;

import edu.ie3.models.StandardUnits;
import edu.ie3.util.quantities.interfaces.Currency;
import edu.ie3.util.quantities.interfaces.DimensionlessRate;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;

/** Describes the type of a {@link edu.ie3.models.input.system.BmInput} */
public class BmTypeInput extends SystemParticipantTypeInput {

  /** Permissible load gradient (typically in %/h) */
  private Quantity<DimensionlessRate> loadGradient;
  /** Efficiency of converter for this type of BM (typically in %) */
  private Quantity<Dimensionless> etaConv;

  /**
   * @param uuid of the input entity
   * @param id of this type of BM
   * @param capex Captial expense for this type of BM (typically in €)
   * @param opex Operating expense for this type of BM (typically in €)
   * @param cosphiRated Power factor for this type of BM
   * @param loadGradient Permissible load gradient
   * @param sRated Rated apparent power for this type of BM (typically in kVA)
   * @param etaConv Efficiency of converter for this type of BM (typically in %)
   */
  public BmTypeInput(
      UUID uuid,
      String id,
      Quantity<Currency> capex,
      Quantity<EnergyPrice> opex,
      Quantity<DimensionlessRate> loadGradient,
      Quantity<Power> sRated,
      double cosphiRated,
      Quantity<Dimensionless> etaConv) {
    super(uuid, id, capex, opex, sRated.to(StandardUnits.S_RATED), cosphiRated);
    this.loadGradient = loadGradient.to(StandardUnits.LOAD_GRADIENT);
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
  }

  public Quantity<DimensionlessRate> getLoadGradient() {
    return loadGradient;
  }

  public void setLoadGradient(Quantity<DimensionlessRate> loadGradient) {
    this.loadGradient = loadGradient.to(StandardUnits.LOAD_GRADIENT);
  }

  public Quantity<Dimensionless> getEtaConv() {
    return etaConv;
  }

  public void setEtaConv(Quantity<Dimensionless> etaConv) {
    this.etaConv = etaConv.to(StandardUnits.EFFICIENCY);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    BmTypeInput that = (BmTypeInput) o;
    return loadGradient.equals(that.loadGradient) && etaConv.equals(that.etaConv);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), loadGradient, etaConv);
  }
}
