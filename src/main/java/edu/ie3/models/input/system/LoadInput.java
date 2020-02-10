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
  /** Active Power (typically in kVA) */
  private Quantity<Power> sRated;
  /** Rated power factor */
  private double cosphiRated;
  /**
   * Constructor for an operated load
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosphiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
  }

  /**
   * Constructor for a non-operated load
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, id, node, qCharacteristics);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
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

  public Quantity<Power> getsRated() {
    return sRated;
  }

  public void setsRated(Quantity<Power> sRated) {
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  public double getCosphiRated() {
    return cosphiRated;
  }

  public void setCosphiRated(double cosphiRated) {
    this.cosphiRated = cosphiRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadInput loadInput = (LoadInput) o;
    return dsm == loadInput.dsm
        && Double.compare(loadInput.cosphiRated, cosphiRated) == 0
        && eConsAnnual.equals(loadInput.eConsAnnual)
        && sRated.equals(loadInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dsm, eConsAnnual, sRated, cosphiRated);
  }
}
