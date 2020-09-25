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
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices);
    this.tapPos = tapPos;
    this.autoTap = autoTap;
  }

  /**
   * Constructor for an operated, always on transformer
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
  public abstract TransformerInputCopyBuilder<?> copy();

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
        + ", noOfParallelDevices="
        + getParallelDevices()
        + ", tapPos="
        + tapPos
        + ", autoTap="
        + autoTap
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * TransformerInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  abstract static class TransformerInputCopyBuilder<T extends TransformerInputCopyBuilder<T>>
      extends ConnectorInputCopyBuilder<T> {

    private int tapPos;
    private boolean autoTap;

    protected TransformerInputCopyBuilder(TransformerInput entity) {
      super(entity);
      this.tapPos = entity.getTapPos();
      this.autoTap = entity.isAutoTap();
    }

    public T tapPos(int tapPos) {
      this.tapPos = tapPos;
      return childInstance();
    }

    public T autoTap(boolean autoTap) {
      this.autoTap = autoTap;
      return childInstance();
    }

    protected int getTapPos() {
      return tapPos;
    }

    protected boolean isAutoTap() {
      return autoTap;
    }

    @Override
    public abstract TransformerInput build();

    @Override
    protected abstract T childInstance();
  }
}
