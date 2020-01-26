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
import javax.measure.quantity.Power;

/** Dummy class to represent a constant feed in regardless of its type */
public class FixedFeedInInput extends SystemParticipantInput {
  /** Rated apparent power (typically in kVA) */
  private Quantity<Power> sRated;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphi Power factor
   * @param sRated Rated apparent power
   */
  public FixedFeedInInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      Quantity<Power> sRated) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphi);
    this.sRated = sRated.to(StandardUnits.S_RATED);
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
   * @param cosphi Power factor
   * @param sRated Rated apparent power
   */
  public FixedFeedInInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      Quantity<Power> sRated) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphi);
    this.sRated = sRated.to(StandardUnits.S_RATED);
  }

  /**
   * Constructor for a non-operated asset
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
