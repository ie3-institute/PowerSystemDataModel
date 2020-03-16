/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardLoadProfile;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;

/** Describes a load */
public class LoadInput extends SystemParticipantInput {
  private final StandardLoadProfile standardLoadProfile;
  /** True, if demand side management is activated for this load */
  private final boolean dsm;
  /** Annually consumed energy (typically in kWh) */
  private final Quantity<Energy> eConsAnnual;
  /** Active Power (typically in kVA) */
  private final Quantity<Power> sRated;
  /** Rated power factor */
  private final double cosphiRated;
  /**
   * Constructor for an operated load
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param standardLoadProfile Standard load profile to use for this model
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
      StandardLoadProfile standardLoadProfile,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
    this.standardLoadProfile = standardLoadProfile;
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY_IN);
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
   * @param standardLoadProfile Standard load profile to use for this model
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      StandardLoadProfile standardLoadProfile,
      boolean dsm,
      Quantity<Energy> eConsAnnual,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, id, node, qCharacteristics);
    this.standardLoadProfile = standardLoadProfile;
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY_IN);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
  }

  public StandardLoadProfile getStandardLoadProfile() {
    return standardLoadProfile;
  }

  public boolean isDsm() {
    return dsm;
  }

  public Quantity<Energy> geteConsAnnual() {
    return eConsAnnual;
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

  @Override
  public String toString() {
    return "LoadInput{"
        + "dsm="
        + dsm
        + ", eConsAnnual="
        + eConsAnnual
        + ", sRated="
        + sRated
        + ", cosphiRated="
        + cosphiRated
        + '}';
  }
}
