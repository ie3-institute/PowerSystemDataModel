/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.StandardUnits;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/** Describes a load */
public class LoadInput extends SystemParticipantInput {
  /** True, if demand side management is activated for this load */
  boolean  dsm;
  /** Annually consumed energy (typically in kWh) */
  Quantity<Energy> eConsAnnual;
  /** Active Power (typically in kW) */
  Quantity<Power> p;
  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
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
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphiRated,
      boolean  dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> p) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphiRated);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
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
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      Double cosphiRated,
      boolean  dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> p) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphiRated);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  /**
   * Constructor for a non-operated asset
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
      Double cosphiRated,
      boolean  dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> p) {
    super(uuid, id, node, qCharacteristics, cosphiRated);
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY);
    this.p = p.to(StandardUnits.ACTIVE_POWER_IN);
  }

  public boolean  getDsm() {
    return dsm;
  }

  public void setDsm(boolean  dsm) {
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
    if(this == o)
      return true;
    if(o == null || getClass() != o.getClass())
      return false;
    if(!super.equals(o))
      return false;
    LoadInput loadInput = (LoadInput) o;
    return dsm == loadInput.dsm && eConsAnnual.equals(loadInput.eConsAnnual) && p.equals(loadInput.p);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dsm, eConsAnnual, p);
  }
}
