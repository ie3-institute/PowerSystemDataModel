/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasEm;
import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.*;

/** Describes a system asset that is connected to a node */
public abstract class SystemParticipantInput extends AssetInput implements HasNodes, HasEm {

  /** The node that the asset is connected to */
  private final NodeInput node;

  /** Description of a reactive power characteristic. For details see further documentation */
  private final ReactivePowerCharacteristic qCharacteristics;

  /**
   * Optional {@link EmInput} that is controlling this system participant. If null, this system
   * participant is not em-controlled.
   */
  private final EmInput controllingEm;

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
    this.controllingEm = em;
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
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em) {
    super(uuid, id);
    this.node = node;
    this.qCharacteristics = qCharacteristics;
    this.controllingEm = em;
  }

  public NodeInput getNode() {
    return node;
  }

  public ReactivePowerCharacteristic getqCharacteristics() {
    return qCharacteristics;
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.singletonList(node);
  }

  @Override
  public Optional<EmInput> getControllingEm() {
    return Optional.ofNullable(controllingEm);
  }

  @Override
  public abstract SystemParticipantInputCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipantInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(node, that.node)
        && Objects.equals(qCharacteristics, that.qCharacteristics)
        && Objects.equals(controllingEm, that.controllingEm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, qCharacteristics, controllingEm);
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
        + "', controllingEm="
        + getControllingEm().map(UniqueEntity::getUuid).map(UUID::toString).orElse("")
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
    private EmInput em;

    protected SystemParticipantInputCopyBuilder(SystemParticipantInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.qCharacteristics = entity.getqCharacteristics();
      this.em = entity.getControllingEm().orElse(null);
    }

    public B node(NodeInput node) {
      this.node = node;
      return thisInstance();
    }

    public B qCharacteristics(ReactivePowerCharacteristic qCharacteristics) {
      this.qCharacteristics = qCharacteristics;
      return thisInstance();
    }

    public B em(EmInput em) {
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
    public EmInput getEm() {
      return em;
    }

    /**
     * Scales the input entity in a way that tries to preserve proportions that are related to
     * power. This means that capacity, consumption etc. are scaled with the same factor. Related
     * properties associated with the input type (if applicable) are scaled as well.
     *
     * @param factor The factor to scale with
     * @return A copy builder with scaled relevant properties
     */
    public abstract B scale(Double factor);

    @Override
    public abstract SystemParticipantInput build();

    @Override
    protected abstract B thisInstance();
  }
}
