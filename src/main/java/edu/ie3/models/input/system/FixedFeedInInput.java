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

import javax.measure.Quantity;
import javax.measure.quantity.Power;
import java.util.Objects;
import java.util.UUID;

/** Dummy class to represent a constant feed in regardless of its type */
public class FixedFeedInInput extends SystemParticipantInput {
  /** Rated apparent power (typically in kVA) */
  private Quantity<Power> sRated;

  /**
   * Constructor for an operated feed in
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param sRated Rated apparent power
   */
  public FixedFeedInInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      Quantity<Power> sRated) {
    super(uuid, operationTime, operator, id, node, qCharacteristics, cosphi);
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  /**
   * Constructor for a non-operated feed in
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param sRated Rated apparent power
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      Quantity<Power> sRated) {
    super(uuid, id, node, qCharacteristics, cosphi);
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  public Quantity<Power> getSRated() {
    return sRated;
  }

  public void setSRated(Quantity<Power> sRated) {
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    FixedFeedInInput that = (FixedFeedInInput) o;
    return sRated.equals(that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sRated);
  }
}
