/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantEntityData;
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantInputEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.utils.Try;

import java.util.*;

/** Describes a system asset that is connected to a node */
public abstract class SystemParticipantInput extends AssetInput implements HasNodes {

  /** The node that the asset is connected to */
  private final NodeInput node;

  /** Description of a reactive power characteristic. For details see further documentation */
  private final ReactivePowerCharacteristic qCharacteristics;

  /**
   * Optional UUID of the {@link EmInput} that is controlling this system participant. If null, this
   * system participant is not em-controlled.
   */
  private final UUID em;

  /**
   * Constructor for an operated system participant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   */
  protected SystemParticipantInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em) {
    super(uuid, id, operator, operationTime);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.em = em;
  }

  /**
   * Constructor for an operated, always on system participant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   */
  protected SystemParticipantInput(
      UUID uuid, String id, NodeInput node, ReactivePowerCharacteristic qCharacteristics, UUID em) {
    super(uuid, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.em = em;
  }

  public NodeInput getNode() {
    return node;
  }

  public ReactivePowerCharacteristic getqCharacteristics() {
    return qCharacteristics;
  }

  public Optional<UUID> getEm() {
    return Optional.ofNullable(em);
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
    if (!(o instanceof SystemParticipantInput that)) return false;
    if (!super.equals(o)) return false;
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
        + "', em="
        + em
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
          B extends SystemParticipantInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {

    private NodeInput node;
    private ReactivePowerCharacteristic qCharacteristics;
    private UUID em;

    protected SystemParticipantInputCopyBuilder(SystemParticipantInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.qCharacteristics = entity.getqCharacteristics();
    }

    public B node(NodeInput node) {
      this.node = node;
      return thisInstance();
    }

    public B qCharacteristics(ReactivePowerCharacteristic qCharacteristics) {
      this.qCharacteristics = qCharacteristics;
      return thisInstance();
    }

    public B em(UUID em) {
      this.em = em;
      return thisInstance();
    }

    protected NodeInput getNode() {
      return node;
    }

    protected ReactivePowerCharacteristic getqCharacteristics() {
      return qCharacteristics;
    }

    /** @return The {@link EmInput} controlling this system participant. CAN BE NULL. */
    public UUID getEm() {
      return em;
    }

    @Override
    public abstract SystemParticipantInput build();

    @Override
    protected abstract B thisInstance();
  }
}
