/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/** Describes a load */
public class LoadInput extends SystemParticipantInput {
  /** True, if demand side management is activated for this load */
  private boolean dsm;
  /** Annually consumed energy (typically in kWh) */
  private Quantity<Energy> eConsAnnual;
  /** Active Power (typically in kW) */
  private Quantity<Power> p;
  /**
   * Constructor for an operated load
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param p Active Power (typically in KW)
   */
  public LoadInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> p) {
    super(uuid, operationTime, operator, id, node, qCharacteristics, cosphiRated);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * Constructor for a non-operated load
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param p Active Power (typically in KW)
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> p) {
    super(uuid, id, node, qCharacteristics, cosphiRated);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public boolean getDsm() {
    return dsm;
  }

  public void setDsm(boolean dsm) {
    this.dsm = dsm;
  }

  public Quantity<Energy> geteConsAnnual() {
    return eConsAnnual;
  }

  public void seteConsAnnual(Quantity<Energy> eConsAnnual) {
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
  }

  public Quantity<Power> getP() {
    return p;
  }

  public void setP(Quantity<Power> p) {
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadInput loadInput = (LoadInput) o;
    return dsm == loadInput.dsm
        && eConsAnnual.equals(loadInput.eConsAnnual)
        && p.equals(loadInput.p);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dsm, eConsAnnual, p);
  }
}
