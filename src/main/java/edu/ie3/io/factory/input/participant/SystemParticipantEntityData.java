/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input.participant;

import edu.ie3.io.factory.input.AssetInputEntityData;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

/**
 * Data used by {@link SystemParticipantInputEntityFactory} to create an instance of {@link
 * edu.ie3.models.input.system.SystemParticipantInput}, thus needing additional information about
 * the {@link edu.ie3.models.input.NodeInput}, which cannot be provided through the attribute map.
 */
public class SystemParticipantEntityData extends AssetInputEntityData {
  private final NodeInput node;

  /**
   * Creates a new SystemParticipantEntityData object for a non-operable system participant input
   *
   * @param fieldsToAttributes attribute map: field name -> value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      NodeInput node) {
    super(fieldsToAttributes, entityClass);
    this.node = node;
  }

  /**
   * Creates a new SystemParticipantEntityData object for an operable system participant input
   *
   * @param fieldsToAttributes attribute map: field name -> value
   * @param entityClass class of the entity to be created with this data
   * @param node input node
   * @param operatorInput operator input
   */
  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> entityClass,
      OperatorInput operatorInput,
      NodeInput node) {
    super(fieldsToAttributes, entityClass, operatorInput);
    this.node = node;
  }

  public NodeInput getNode() {
    return node;
  }
}
