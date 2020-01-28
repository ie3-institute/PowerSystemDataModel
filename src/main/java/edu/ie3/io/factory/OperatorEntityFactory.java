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
import java.util.Optional;

/**
 * Enum containing all {@link EntityFactory}s that can be used to create an instance of an {@link
 * UniqueEntity} based on {@link OperatorEntityData}
 *
 * @version 0.1
 * @since 28.01.20
 */
public enum OperatorEntityFactory implements EntityFactory<OperatorEntityFactory> {
  NodeInputFactory(NodeInput.class) {
    @Override
    public Class<? extends UniqueEntity>[] classes() {
      return new Class[0]; // todo fix
    }

    @Override
    public Optional<NodeInput> getEntity(EntityData entityData) {

      OperatorEntityData operatorEntityData =
          OperatorEntityFactory.getOperatorEntityData(entityData);
      Map<String, String> fieldsToAttributes = operatorEntityData.getFieldsToAttributes();
      OperatorInput operatorInput = operatorEntityData.getOperatorInput();

      // todo sanity check

      return Optional.empty();
    }
  };

  OperatorEntityFactory(Class<? extends UniqueEntity>... classes) {
    this.classes = classes;
  }

  private final Class<? extends UniqueEntity>[] classes;

  @Override
  public Class<? extends UniqueEntity>[] classes() {
    return classes;
  }

  @Override
  public OperatorEntityFactory getRaw() {
    return this;
  }

  @Override
  public abstract Optional<? extends UniqueEntity> getEntity(EntityData metaData);

  private static OperatorEntityData getOperatorEntityData(EntityData entityData) {
    if (!(entityData instanceof OperatorEntityData)) {
      throw new RuntimeException("Invalid"); // todo
    } else {
      return (OperatorEntityData) entityData;
    }
  }
}
