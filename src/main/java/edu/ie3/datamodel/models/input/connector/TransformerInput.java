/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Describes an electrical grid transformer, is "located" in the inferior subnet. */
public abstract class TransformerInput extends ConnectorInput {
  /** Tap position of this transformer. */
  private final int tapPos;

  /** True, if the tap position of the transformer is adapted automatically. */
  private final boolean autoTap;

  /**
   * Constructor for an operated transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA Grid node at the high voltage winding
   * @param nodeB Grid node at the low voltage winding
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param tapPos Tap Position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  protected TransformerInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  /**
   * Constructor for an operated transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA Grid node at the high voltage winding
   * @param nodeB Grid node at the low voltage winding
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param tapPos Tap Position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  protected TransformerInput(
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

  public boolean isAutoTap() {
    return autoTap;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TransformerInput that)) return false;
    if (!super.equals(o)) return false;
    return tapPos == that.tapPos && autoTap == that.autoTap;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), tapPos, autoTap);
  }

  @Override
  public String toString() {
    return "TransformerInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", nodeA="
        + getNodeA().getUuid()
        + ", nodeB="
        + getNodeB().getUuid()
        + ", parallelDevices="
        + getParallelDevices()
        + ", tapPos="
        + tapPos
        + ", autoTap="
        + autoTap
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract TransformerInputCopyBuilder<?> copy();

  public abstract static class TransformerInputCopyBuilder<B extends TransformerInputCopyBuilder<B>>
      extends ConnectorInputCopyBuilder<B> {
    private int tapPos;

    private boolean autoTap;

    protected TransformerInputCopyBuilder(TransformerInput entity) {
      super(entity);
      this.tapPos = entity.tapPos;
      this.autoTap = entity.autoTap;
    }

    public B tapPos(int tapPos) {
      this.tapPos = tapPos;
      return thisInstance();
    }

    protected int getTapPos() {
      return tapPos;
    }

    public B autoTap(boolean autoTap) {
      this.autoTap = autoTap;
      return thisInstance();
    }

    protected boolean isAutoTap() {
      return autoTap;
    }

    @Override
    public abstract TransformerInput build();

    @Override
    protected abstract B thisInstance();
  }
}
