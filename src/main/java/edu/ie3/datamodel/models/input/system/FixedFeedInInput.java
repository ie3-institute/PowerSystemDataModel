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
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

/** Dummy class to represent a constant feed in regardless of its type */
public class FixedFeedInInput extends SystemParticipantInput {
  /** Rated apparent power (typically in kVA) */
  private final ComparableQuantity<Power> sRated; // TODO #65 Quantity replaced
  /** Rated power factor */
  private final double cosphiRated;

  /**
   * Constructor for an operated feed in
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param sRated Rated apparent power
   * @param cosphiRated Power factor
   */
  public FixedFeedInInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      ComparableQuantity<Power> sRated, // TODO #65 Quantity replaced
      double cosphiRated) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
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
      ComparableQuantity<Power> sRated, // TODO #65 Quantity replaced
      double cosphiRated) {
    super(uuid, id, node, qCharacteristics);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosphiRated = cosphiRated;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  } // TODO #65 Quantity replaced

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
