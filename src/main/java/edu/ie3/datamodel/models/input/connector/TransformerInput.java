/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an electrical grid transformer, is "located" in the inferior subnet */
public abstract class TransformerInput extends ConnectorInput {
  /** Tap position of this transformer */
  private final int tapPos;
  /** True, if the tap position of the transformer is adapted automatically */
  private final boolean autoTap;

  /**
   * Constructor for an operated transformer
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
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
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      int tapPos,
      boolean autoTap) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  /**
   * Constructor for a non-operated transformer
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

  public boolean isAutoTap() {
    return autoTap;
  }

  public int getTapPos() {
    return tapPos;
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
