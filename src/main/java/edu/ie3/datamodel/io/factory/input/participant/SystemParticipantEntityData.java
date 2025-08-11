/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Data used for those classes of {@link
 * edu.ie3.datamodel.models.input.system.SystemParticipantInput}*, including an (optional) link to
 * an {@link EmInput} entity.
 */
public class SystemParticipantEntityData extends NodeAssetInputEntityData {

  /** Energy management unit that is managing the system participant. Can be null. */
  private final EmInput controllingEm;

  /**
   * Creates a new SystemParticipantEntityData object for an operated, always on system participant
   * input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   * @param controllingEm The energy management unit that is managing the system participant. Null,
   *     if the system participant is not managed.
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node,
      EmInput controllingEm) {
    super(fieldsToAttributes, entityClass, node);
    this.controllingEm = controllingEm;
  }

  /**
   * Gets controlling em.
   *
   * @return the controlling em
   */
  public Optional<EmInput> getControllingEm() {
    return Optional.ofNullable(controllingEm);
  }

  /**
   * Creates a new SystemParticipantEntityData object for an operable system participant input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param operator operator input
   * @param node input node
   * @param controllingEm The energy management unit that is managing the system participant. Null,
   *     if the system participant is not managed.
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput node,
      EmInput controllingEm) {
    super(fieldsToAttributes, entityClass, operator, node);
    this.controllingEm = controllingEm;
  }

  /**
   * Creates a new SystemParticipantEntityData object based on a given {@link
   * NodeAssetInputEntityData}* object and given energy management unit
   *
   * @param nodeAssetInputEntityData The node asset entity data object to use attributes of
   * @param controllingEm The energy management unit that is managing the system participant. Null,
   *     if the system participant is not managed.
   */
  public SystemParticipantEntityData(
      NodeAssetInputEntityData nodeAssetInputEntityData, EmInput controllingEm) {
    super(nodeAssetInputEntityData, nodeAssetInputEntityData.getNode());
    this.controllingEm = controllingEm;
  }

  @Override
  public String toString() {
    return "SystemParticipantEntityData{"
        + "em="
        + getControllingEm().map(EmInput::toString).orElse("")
        + ", node="
        + getNode().getUuid()
        + ", operatorInput="
        + getOperatorInput().getUuid()
        + ", fieldsToValues="
        + getFieldsToValues()
        + ", targetClass="
        + getTargetClass()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    SystemParticipantEntityData that = (SystemParticipantEntityData) o;
    return getControllingEm().equals(that.getControllingEm());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getControllingEm());
  }
}
