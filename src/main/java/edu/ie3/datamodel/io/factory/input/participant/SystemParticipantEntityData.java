/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.EmInput;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Data used for those classes of {@link
 * edu.ie3.datamodel.models.input.system.SystemParticipantInput}, including an (optional) link to an
 * {@link EmInput} entity.
 */
public class SystemParticipantEntityData extends NodeAssetInputEntityData {

  /** Energy management unit that is managing the system participant. Can be null. */
  private final EmInput em;

  /**
   * Creates a new SystemParticipantEntityData object for an operated, always on system participant
   * input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   * @param em The energy management unit that is managing the system participant. Null, if the
   *     system participant is not managed.
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node,
      EmInput em) {
    super(fieldsToAttributes, entityClass, node);
    this.em = em;
  }

  public Optional<EmInput> getEm() {
    return Optional.ofNullable(em);
  }

  /**
   * Creates a new SystemParticipantEntityData object for an operable system participant input
   *
   * @param fieldsToAttributes attribute map: field name to value
   * @param entityClass class of the entity to be created with this data
   * @param operator operator input
   * @param node input node
   * @param em The energy management unit that is managing the system participant. Null, if the
   *     system participant is not managed.
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput node,
      EmInput em) {
    super(fieldsToAttributes, entityClass, operator, node);
    this.em = em;
  }

  /**
   * Creates a new SystemParticipantEntityData object based on a given {@link
   * NodeAssetInputEntityData} object and given energy management unit
   *
   * @param nodeAssetInputEntityData The node asset entity data object to use attributes of
   * @param em The energy management unit that is managing the system participant. Null, if the
   *     system participant is not managed.
   */
  public SystemParticipantEntityData(
      NodeAssetInputEntityData nodeAssetInputEntityData, EmInput em) {
    super(nodeAssetInputEntityData, nodeAssetInputEntityData.getNode());
    this.em = em;
  }

  @Override
  public String toString() {
    return "SystemParticipantEntityData{"
        + "em="
        + getEm().map(EmInput::toString).orElse("")
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
    return getEm().equals(that.getEm());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getEm());
  }
}
