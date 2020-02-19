/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input.participant;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.SystemParticipantTypeInput;
import java.util.Map;

/**
 * Data used for those classes of {@link edu.ie3.models.input.system.SystemParticipantInput} that
 * need an instance of some type T of {@link SystemParticipantTypeInput} as well.
 *
 * @param <T> Subclass of {@link SystemParticipantTypeInput} that is required for the construction
 *     of the SystemParticipantInput
 */
class SystemParticipantTypedEntityData<T extends SystemParticipantTypeInput>
    extends SystemParticipantEntityData {

  private final T typeInput;

  /**
   * Creates a new SystemParticipantEntityData object for a non-operable system participant input
   * that needs a type input as well
   *
   * @param fieldsToAttributes attribute map: field name -> value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   * @param typeInput type input
   */
  public SystemParticipantTypedEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node,
      T typeInput) {
    super(fieldsToAttributes, entityClass, node);
    this.typeInput = typeInput;
  }

  /**
   * Creates a new SystemParticipantEntityData object for an operable system participant input that
   * needs a type input as well
   *
   * @param fieldsToAttributes attribute map: field name -> value
   * @param entityClass class of the entity to be created with this data
   * @param operatorInput operator input
   * @param node input node
   * @param typeInput type input
   */
  public SystemParticipantTypedEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput node,
      T typeInput) {
    super(fieldsToAttributes, entityClass, operatorInput, node);
    this.typeInput = typeInput;
  }

  public T getTypeInput() {
    return typeInput;
  }
}
