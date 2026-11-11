/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasEm;
import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * A system participant that is connected to a node and can be controlled by an energy management
 * unit.
 */
public abstract class ExtendedSystemParticipantInput extends SystemParticipantInput
    implements HasNodes, HasEm {

  private final NodeInput node;
  private final EmInput controllingEm;

  protected ExtendedSystemParticipantInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em) {
    super(uuid, id, operator, operationTime, qCharacteristics);
    this.node = node;
    this.controllingEm = em;
  }

  protected ExtendedSystemParticipantInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em) {
    super(uuid, id, qCharacteristics);
    this.node = node;
    this.controllingEm = em;
  }

  public NodeInput getNode() {
    return node;
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
  public abstract ExtendedSystemParticipantInputCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ExtendedSystemParticipantInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(node, that.node) && Objects.equals(controllingEm, that.controllingEm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, controllingEm);
  }

  @Override
  public String toString() {
    return "ExtendedSystemParticipantInput{"
        + "participant="
        + super.toString()
        + ", node="
        + (node == null ? null : node.getUuid())
        + ", controllingEm="
        + getControllingEm().map(UniqueEntity::getUuid).map(UUID::toString).orElse("")
        + '}';
  }

  public abstract static class ExtendedSystemParticipantInputCopyBuilder<
          B extends ExtendedSystemParticipantInputCopyBuilder<B>>
      extends SystemParticipantInputCopyBuilder<B> {

    private NodeInput node;
    private EmInput em;

    protected ExtendedSystemParticipantInputCopyBuilder(ExtendedSystemParticipantInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.em = entity.getControllingEm().orElse(null);
    }

    public B node(NodeInput node) {
      this.node = node;
      return thisInstance();
    }

    public B em(EmInput em) {
      this.em = em;
      return thisInstance();
    }

    protected NodeInput getNode() {
      return node;
    }

    /**
     * @return controlling EM, or {@code null} if none is assigned
     */
    public EmInput getEm() {
      return em;
    }

    @Override
    public abstract ExtendedSystemParticipantInput build();
  }
}
