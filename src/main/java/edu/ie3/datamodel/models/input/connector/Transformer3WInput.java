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
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.utils.validation.ConnectorValidationUtils;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import tech.units.indriya.quantity.Quantities;

/** Describes a three winding transformer, that is connected to three {@link NodeInput}s. */
public class Transformer3WInput extends TransformerInput implements HasType {
  /** Type of this 3W transformer, containing default values for transformers of this kind. */
  private final Transformer3WTypeInput type;

  /** The lower voltage node. */
  private final NodeInput nodeC;

  /** Internal node of the transformers T equivalent circuit. */
  private final NodeInput nodeInternal;

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer3WInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.nodeC = nodeC;
    this.type = type;
    this.nodeInternal =
        new NodeInput(
            UUID.randomUUID(),
            "internal_node_" + id,
            operator,
            operationTime,
            Quantities.getQuantity(1d, PowerSystemUnits.PU),
            false,
            null,
            nodeA.getVoltLvl(),
            nodeA.getSubnet());
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB, nodeC);
  }

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   * @param additionalInformation Of the input
   */
  public Transformer3WInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.nodeC = nodeC;
    this.type = type;
    this.nodeInternal =
        new NodeInput(
            UUID.randomUUID(),
            "internal_node_" + id,
            operator,
            operationTime,
            Quantities.getQuantity(1d, PowerSystemUnits.PU),
            false,
            null,
            nodeA.getVoltLvl(),
            nodeA.getSubnet());
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB, nodeC);
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer3WInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.nodeC = nodeC;
    this.type = type;
    this.nodeInternal =
        new NodeInput(
            UUID.randomUUID(),
            "internal_node_" + id,
            nodeA.getOperator(),
            nodeA.getOperationTime(),
            Quantities.getQuantity(1d, PowerSystemUnits.PU),
            false,
            null,
            nodeA.getVoltLvl(),
            nodeA.getSubnet());
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB, nodeC);
  }

  /**
   * Constructor for an operated two winding transformer.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices overall amount of parallel transformers to automatically construct (e.g.
   *     parallelDevices = 2 will build a total of two transformers using the specified parameters)
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer3WInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput nodeA,
      NodeInput nodeB,
      NodeInput nodeC,
      int parallelDevices,
      Transformer3WTypeInput type,
      int tapPos,
      boolean autoTap,
      NodeInput nodeInternal) {
    super(uuid, id, operator, operationTime, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.nodeC = nodeC;
    this.type = type;
    this.nodeInternal = nodeInternal;
    ConnectorValidationUtils.connectsNodesToCorrectVoltageSides(nodeA, nodeB, nodeC);
  }

  public Transformer3WTypeInput getType() {
    return type;
  }

  public NodeInput getNodeC() {
    return nodeC;
  }

  public NodeInput getNodeInternal() {
    return nodeInternal;
  }

  @Override
  public List<NodeInput> allNodes() {
    return List.of(getNodeA(), getNodeB(), nodeC);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Transformer3WInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type)
        && Objects.equals(nodeC, that.nodeC)
        && Objects.equals(nodeInternal, that.nodeInternal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, nodeC, nodeInternal);
  }

  @Override
  public String toString() {
    return "Transformer3WInput{"
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
        + ", nodeC="
        + nodeC.getUuid()
        + ", nodeInternal="
        + nodeInternal.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public Transformer3WInputCopyBuilder copy() {
    return new Transformer3WInputCopyBuilder(this);
  }

  public static class Transformer3WInputCopyBuilder
      extends TransformerInputCopyBuilder<Transformer3WInputCopyBuilder> {
    private Transformer3WTypeInput type;

    private NodeInput nodeC;

    private NodeInput nodeInternal;

    protected Transformer3WInputCopyBuilder(Transformer3WInput entity) {
      super(entity);
      this.type = entity.type;
      this.nodeC = entity.nodeC;
      this.nodeInternal = entity.nodeInternal;
    }

    public Transformer3WInputCopyBuilder type(Transformer3WTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected Transformer3WTypeInput getType() {
      return type;
    }

    public Transformer3WInputCopyBuilder nodeC(NodeInput nodeC) {
      this.nodeC = nodeC;
      return thisInstance();
    }

    protected NodeInput getNodeC() {
      return nodeC;
    }

    public Transformer3WInputCopyBuilder nodeInternal(NodeInput nodeInternal) {
      this.nodeInternal = nodeInternal;
      return thisInstance();
    }

    protected NodeInput getNodeInternal() {
      return nodeInternal;
    }

    public Transformer3WInputCopyBuilder internalSlack(boolean internalNodeIsSlack) {
      this.nodeInternal = this.nodeInternal.copy().slack(internalNodeIsSlack).build();
      return thisInstance();
    }

    @Override
    public Transformer3WInput build() {
      return new Transformer3WInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNodeA(),
          getNodeB(),
          nodeC,
          getParallelDevices(),
          type,
          getTapPos(),
          isAutoTap(),
          nodeInternal);
    }

    @Override
    protected Transformer3WInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
