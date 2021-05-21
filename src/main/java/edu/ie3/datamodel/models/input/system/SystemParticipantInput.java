/*
 * © 2021. TU Dortmund University,
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
  protected SystemParticipantInput(
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
  protected SystemParticipantInput(
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
  public abstract SystemParticipantInputCopyBuilder<?> copy();

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
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", node="
        + node.getUuid()
        + ", qCharacteristics='"
        + qCharacteristics
        + '\''
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link
   * SystemParticipantInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public abstract static class SystemParticipantInputCopyBuilder<
          T extends SystemParticipantInputCopyBuilder<T>>
      extends AssetInputCopyBuilder<T> {

    private NodeInput node;
    private ReactivePowerCharacteristic qCharacteristics;

    protected SystemParticipantInputCopyBuilder(SystemParticipantInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.qCharacteristics = entity.getqCharacteristics();
    }

    public T node(NodeInput node) {
      this.node = node;
      return childInstance();
    }

    public T qCharacteristics(ReactivePowerCharacteristic qCharacteristics) {
      this.qCharacteristics = qCharacteristics;
      return childInstance();
    }

    protected NodeInput getNode() {
      return node;
    }

    protected ReactivePowerCharacteristic getqCharacteristics() {
      return qCharacteristics;
    }

    @Override
    public abstract SystemParticipantInput build();

    @Override
    protected abstract T childInstance();
  }
}
