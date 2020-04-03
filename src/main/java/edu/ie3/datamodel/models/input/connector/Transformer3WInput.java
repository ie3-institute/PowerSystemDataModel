/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector;

import static edu.ie3.util.quantities.PowerSystemUnits.PU;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.*;
import tec.uom.se.quantity.Quantities;

/**
 * Describes a three winding transformer, that is connected to three {@link
 * edu.ie3.datamodel.models.input.NodeInput}s
 */
public class Transformer3WInput extends TransformerInput implements HasType {
  /** Type of this 3W transformer, containing default values for transformers of this kind */
  private final Transformer3WTypeInput type;
  /** The lower voltage node */
  private final NodeInput nodeC;
  /** Internal node of the transformers T equivalent circuit */
  private final NodeInput nodeInternal;

  /**
   * Constructor for an operated three winding transformer
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 3W transformer
   * @param tapPos Tap Position of this transformer
   * @param autoTap true, if there is an automated regulation activated for this transformer
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
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
    this.nodeC = nodeC;
    this.nodeInternal =
        new NodeInput(
            UUID.randomUUID(),
            operationTime,
            operator,
            "internal_node_" + id,
            Quantities.getQuantity(1d, PU),
            false,
            null,
            nodeA.getVoltLvl(),
            nodeA.getSubnet());
  }

  /**
   * Constructor for a non-operated three winding transformer
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA The higher voltage node
   * @param nodeB The middle voltage node
   * @param nodeC The lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 3W transformer
   * @param tapPos Tap Position of this transformer
   * @param autoTap true, if there is an automated regulation activated for this transformer
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
    this.type = type;
    this.nodeC = nodeC;
    this.nodeInternal =
        new NodeInput(
            UUID.randomUUID(),
            getOperationTime(),
            getOperator(),
            "internal_node_" + id,
            Quantities.getQuantity(1d, PU),
            false,
            null,
            nodeA.getVoltLvl(),
            nodeA.getSubnet());
  }

  @Override
  public Transformer3WTypeInput getType() {
    return type;
  }

  /** @return the node with the highest voltage level */
  @Override
  public NodeInput getNodeA() {
    return super.getNodeA();
  }

  /** @return the node with the "medium" voltage level */
  @Override
  public NodeInput getNodeB() {
    return super.getNodeB();
  }

  /** @return the node with the lowest voltage level */
  public NodeInput getNodeC() {
    return nodeC;
  }

  /** @return The internal node of the T equivalent circuit */
  public NodeInput getNodeInternal() {
    return nodeInternal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer3WInput that = (Transformer3WInput) o;
    return Objects.equals(type, that.type) && Objects.equals(nodeC, that.nodeC);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, nodeC);
  }

  @Override
  public String toString() {
    return "Transformer3WInput{" + "type=" + type + ", nodeC=" + nodeC + '}';
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.unmodifiableList(Arrays.asList(getNodeA(), getNodeB(), nodeC));
  }
}
