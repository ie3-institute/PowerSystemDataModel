/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.EvTypeInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes an electric vehicle */
public class EvInput extends SystemParticipantInput {
  /** Type of this EV, containing default values for EVs of this kind */
  private EvTypeInput type;
  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of EV
   */
  public EvInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      EvTypeInput type) {
    super(uuid, operationInterval, operator, id, node, qCharacteristics, cosphi);
    this.type = type;
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
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of EV
   */
  public EvInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      EvTypeInput type) {
    super(uuid, operatesFrom, operatesUntil, operator, id, node, qCharacteristics, cosphi);
    this.type = type;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param cosphi Power factor
   * @param type of EV
   */
  public EvInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      EvTypeInput type) {
    super(uuid, id, node, qCharacteristics, cosphi);
    this.type = type;
  }

  public EvTypeInput getType() {
    return type;
  }

  public void setType(EvTypeInput type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvInput evInput = (EvInput) o;
    return Objects.equals(type, evInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }
}
