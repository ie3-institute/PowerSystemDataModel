/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input.participant;

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import java.util.Map;

/**
 * Data used for those classes of {@link
 * edu.ie3.datamodel.models.input.system.SystemParticipantInput} that need an instance of some type
 * T of {@link SystemParticipantTypeInput} as well.
 *
 * @param <T> Subclass of {@link SystemParticipantTypeInput} that is required for the construction
 *     of the SystemParticipantInput
 */
public class SystemParticipantTypedEntityData<T extends SystemParticipantTypeInput>
                extends NodeAssetInputEntityData {

  private final T typeInput;

  /**
   * Creates a new SystemParticipantEntityData object for an operated, always on system participant
   * input that needs a type input as well
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
   * @param operator operator input
   * @param node input node
   * @param typeInput type input
   */
  public SystemParticipantTypedEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operator,
      NodeInput node,
      T typeInput) {
    super(fieldsToAttributes, entityClass, operator, node);
    this.typeInput = typeInput;
  }

  public T getTypeInput() {
    return typeInput;
  }
}
