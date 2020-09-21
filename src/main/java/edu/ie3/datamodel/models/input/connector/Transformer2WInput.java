/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import java.util.Objects;
import java.util.UUID;

/**
 * Describes a two winding transformer, that is connected to two {@link
 * edu.ie3.datamodel.models.input.NodeInput}s
 */
public class Transformer2WInput extends TransformerInput implements HasType {
  /** Type of this 2W transformer, containing default values for transformers of this kind */
  private final Transformer2WTypeInput type;

  /**
   * Constructor for an operated two winding transformer
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer2WInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }

  /**
   * Constructor for an operated, always on two winding transformer
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer2WInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }

  public Transformer2WInputCopyBuilder copy() {
    return new Transformer2WInputCopyBuilder(this);
  }

  @Override
  public Transformer2WTypeInput getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer2WInput that = (Transformer2WInput) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "Transformer2WInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + '\''
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
        + ", type="
        + type.getUuid()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link Transformer2WInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * Transformer2WInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class Transformer2WInputCopyBuilder
      extends TransformerInputCopyBuilder<Transformer2WInputCopyBuilder> {

    private Transformer2WTypeInput type;

    private Transformer2WInputCopyBuilder(Transformer2WInput entity) {
      super(entity);
      this.type = entity.getType();
    }

    @Override
    public Transformer2WInput build() {
      return new Transformer2WInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNodeA(),
          getNodeB(),
          getParallelDevices(),
          type,
          getTapPos(),
          isAutoTap());
    }

    public Transformer2WInputCopyBuilder type(Transformer2WTypeInput type) {
      this.type = type;
      return this;
    }

    @Override
    protected Transformer2WInputCopyBuilder childInstance() {
      return this;
    }
  }
}
