/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

/** Dummy class to represent a constant feed in regardless of its type */
public class FixedFeedInInput extends SystemParticipantInput {
  /** Rated apparent power (typically in kVA) */
  private final Quantity<Power> sRated;
  /** Rated power factor */
  private final double cosphiRated;

  /**
   * Constructor for an operated feed in
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param sRated Rated apparent power
   * @param cosphiRated Power factor
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      String qCharacteristics,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
  }

  /**
   * Constructor for a non-operated feed in
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param sRated Rated apparent power
   * @param cosphiRated Power factor
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      Quantity<Power> sRated,
      double cosphiRated) {
    super(uuid, id, node, qCharacteristics);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
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
    FixedFeedInInput that = (FixedFeedInInput) o;
    return sRated.equals(that.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sRated);
  }

  @Override
  public String toString() {
    return "FixedFeedInInput{" + "sRated=" + sRated + ", cosphiRated=" + cosphiRated + '}';
  }
}
