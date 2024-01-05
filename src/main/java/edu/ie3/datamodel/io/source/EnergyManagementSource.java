/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.input.participant.EmInputFactory;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.container.EnergyManagementUnits;
import edu.ie3.datamodel.models.input.system.EmInput;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnergyManagementSource extends EntitySource {

  private final TypeSource typeSource;

  private final EmInputFactory emInputFactory;

  public EnergyManagementSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;

    this.emInputFactory = new EmInputFactory();
  }

  @Override
  public void validate() throws ValidationException {
    validate(EmInput.class, emInputFactory).getOrThrow();
  }

  /**
   * Returns a unique set of {@link EmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * @return a set of object and uuid unique {@link EmInput} entities
   */
  public EnergyManagementUnits getEmUnits() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getEmUnits(operators);
  }

  /**
   * This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * <p>In contrast to {@link #getEmUnits()} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link EmInput} instances depend on. Doing
   * so, already loaded nodes can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object and uuid unique {@link OperatorInput} that should be
   *     used for the returning instances
   * @return a set of object and uuid unique {@link EmInput} entities
   */
  public EnergyManagementUnits getEmUnits(Map<UUID, OperatorInput> operators)
      throws SourceException {
    Set<EmInput> emUnits =
        unpackSet(
            buildAssetInputEntityData(EmInput.class, operators).map(emInputFactory::get),
            EmInput.class);
    return new EnergyManagementUnits(emUnits);
  }
}
