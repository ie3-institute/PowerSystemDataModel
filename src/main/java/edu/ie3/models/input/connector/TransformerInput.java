/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes an electrical grid transformer, is "located" in the inferior subnet */
public abstract class TransformerInput extends ConnectorInput {
  /** Tap position of this transformer */
  private int tapPos;
  /** True, if the tap position of the transformer is adapted automatically */
  private boolean autoTap;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at the high voltage winding
   * @param nodeB Grid node at the low voltage winding
   * @param parallelDevices Amount of parallel transformers
   * @param tapPos Tap Position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public TransformerInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      int tapPos,
      boolean autoTap) {
    super(uuid, operationInterval, operator, id, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA Grid node at the high voltage winding
   * @param nodeB Grid node at the low voltage winding
   * @param parallelDevices Amount of parallel transformers
   * @param tapPos Tap Position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public TransformerInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      int tapPos,
      boolean autoTap) {
    super(uuid, operatesFrom, operatesUntil, operator, id, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at the high voltage winding
   * @param nodeB Grid node at the low voltage winding
   * @param parallelDevices Amount of parallel transformers
   * @param tapPos Tap Position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public TransformerInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  public int getTapPos() {
    return tapPos;
  }

  public void setTapPos(int tapPos) {
    this.tapPos = tapPos;
  }

  public boolean getAutoTap() {
    return autoTap;
  }

  public void setAutoTap(boolean autoTap) {
    this.autoTap = autoTap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TransformerInput that = (TransformerInput) o;
    return tapPos == that.tapPos && autoTap == that.autoTap;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), tapPos, autoTap);
  }
}
