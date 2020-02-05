/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import java.util.Map;

/**
 * Data used by {@link AssetEntityFactory} to create an instance of an entity that needs additional
 * information about the {@link OperatorInput} and cannot be created based only on a mapping of
 * fieldName -> value
 *
 * @version 0.1
 * @since 28.01.20
 */
public class SystemParticipantEntityData extends OperationEntityData {

  private final NodeInput node;
  // TODO field for TypeInput (-> WecTypeInput) needed?

  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes, Class<? extends UniqueEntity> clazz, NodeInput node) {
    super(fieldsToAttributes, clazz);
    this.node = node;
  }

  public SystemParticipantEntityData(
      Map<String, String> fieldsToAttributes,
      Class<? extends UniqueEntity> clazz,
      OperatorInput operatorInput,
      NodeInput node) {
    super(fieldsToAttributes, clazz, operatorInput);
    this.node = node;
  }

  public NodeInput getNode() {
    return node;
  }
}
