/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.*;

/** Describes a system asset that is connected to a node */
public abstract class SystemParticipantInput extends AssetInput implements HasNodes {

  /** The node that the asset is connected to */
  private final NodeInput node;

  /** Description of a reactive power characteristic. For details see further documentation */
  private final ReactivePowerCharacteristic qCharacteristics;

  /**
   * Constructor for an operated system participant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   */
  public SystemParticipantInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics) {
    super(uuid, id, operator, operationTime);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
  }

  /**
   * Constructor for an operated, always on system participant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   */
  public SystemParticipantInput(
      UUID uuid, String id, NodeInput node, ReactivePowerCharacteristic qCharacteristics) {
    super(uuid, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
  }

  public ReactivePowerCharacteristic getqCharacteristics() {
    return qCharacteristics;
  }

  public NodeInput getNode() {
    return node;
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.singletonList(node);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantInput that = (SystemParticipantInput) o;
    return Objects.equals(node, that.node)
        && Objects.equals(qCharacteristics, that.qCharacteristics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, qCharacteristics);
  }

  @Override
  public String toString() {
    return "SystemParticipantInput{"
        + "node="
        + node
        + ", qCharacteristics='"
        + qCharacteristics
        + '\''
        + '}';
  }
}
