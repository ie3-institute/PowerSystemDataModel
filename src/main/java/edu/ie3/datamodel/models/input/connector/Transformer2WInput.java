/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.utils.validation.ConnectorValidationUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Describes a two winding transformer, that is connected to two {@link NodeInput}s. */
public class Transformer2WInput extends TransformerInput implements HasType {
  /** Type of this 2W transformer, containing default values for transformers of this kind. */
  private final Transformer2WTypeInput type;

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
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
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB);
  }

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   * @param additionalInformation Of the input
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
      boolean autoTap,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB);
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
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
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB);
  }

  public Transformer2WTypeInput getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer2WInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type);
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
        + getTapPos()
        + ", autoTap="
        + isAutoTap()
        + ", type="
        + type.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public Transformer2WInputCopyBuilder copy() {
    return new Transformer2WInputCopyBuilder(this);
  }

  public static class Transformer2WInputCopyBuilder
      extends TransformerInputCopyBuilder<Transformer2WInputCopyBuilder> {
    private Transformer2WTypeInput type;

    protected Transformer2WInputCopyBuilder(Transformer2WInput entity) {
      super(entity);
      this.type = entity.type;
    }

    public Transformer2WInputCopyBuilder type(Transformer2WTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected Transformer2WTypeInput getType() {
      return type;
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
          isAutoTap(),
          getAdditionalInformation());
    }

    @Override
    protected Transformer2WInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
