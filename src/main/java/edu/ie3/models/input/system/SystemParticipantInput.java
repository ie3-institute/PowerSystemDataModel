/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes a system asset that is connected to a node */
public abstract class SystemParticipantInput extends AssetInput {

  /** The node that the asset is connected to */
  private NodeInput node;

  /** Description of a reactive power characteristic. For details see further documentation */
  private String qCharacteristics;

  /** Rated power factor */
  private double cosphiRated;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   */
  public SystemParticipantInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated) {
    super(uuid, operationInterval, operator, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.cosphiRated = cosphiRated;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   */
  public SystemParticipantInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphiRated) {
    super(uuid, operatesFrom, operatesUntil, operator, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.cosphiRated = cosphiRated;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param cosphiRated Power factor
   */
  public SystemParticipantInput(
      UUID uuid, String id, NodeInput node, String qCharacteristics, double cosphiRated) {
    super(uuid, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.cosphiRated = cosphiRated;
  }

  public NodeInput getNode() {
    return node;
  }

  public void setNode(NodeInput node) {
    this.node = node;
  }

  public String getQCharacteristics() {
    return qCharacteristics;
  }

  public void setQCharacteristics(String qCharacteristics) {
    this.qCharacteristics = qCharacteristics;
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
    SystemParticipantInput that = (SystemParticipantInput) o;
    return Objects.equals(node, that.node)
        && Objects.equals(qCharacteristics, that.qCharacteristics)
        && Objects.equals(cosphiRated, that.cosphiRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, qCharacteristics, cosphiRated);
  }
}
