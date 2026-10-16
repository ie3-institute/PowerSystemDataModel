/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.factory.input.EmInputFactory.CONTROLLING_EM;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.input.EmInputFactory;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.ExceptionUtils;
import edu.ie3.datamodel.utils.Try;
import java.util.*;

public class EnergyManagementSource extends AssetEntitySource {

  private final TypeSource typeSource;

  public EnergyManagementSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;
  }

  @Override
  public void validate() throws ValidationException {
    validate(EmInput.class, dataSource).getOrThrow();
  }

  /**
   * Returns a unique set of {@link EmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the UUID of {@link EmInput}.
   *
   * @return a map of UUID to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getEmUnits(operators);
  }

  /**
   * This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the UUID of {@link EmInput}.
   *
   * <p>In contrast to {@link #getEmUnits()} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link EmInput} instances depend on. Doing
   * so, already loaded nodes can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @return a map of UUID to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits(Map<UUID, OperatorInput> operators) throws SourceException {
    Map<UUID, EmInput> allEms = new HashMap<>();
    EmInputFactory factory = new EmInputFactory(operators, allEms);
    createEmsRecursively(dataSource.getSourceData(EmInput.class).toList(), allEms, factory);
    return allEms;
  }

  private static void createEmsRecursively(
      List<Map<String, String>> rawData, Map<UUID, EmInput> emUnits, EmInputFactory factory)
      throws SourceException {
    List<FactoryException> exceptions = new ArrayList<>();

    List<Map<String, String>> currentLevel = new ArrayList<>();
    List<Map<String, String>> others = new ArrayList<>();

    rawData.forEach(
        t -> {
          try {
            if (!factory.isFieldBlank(t, CONTROLLING_EM)) {
              UUID controllingEm = factory.getUUID(t, CONTROLLING_EM);

              // we have to re-add the field to the map to prevent issues
              t.put(CONTROLLING_EM, controllingEm.toString());

              if (emUnits.containsKey(controllingEm)) {
                currentLevel.add(t);
              } else {
                others.add(t);
              }
            } else {
              currentLevel.add(t);
            }
          } catch (FactoryException fe) {
            // save exception
            exceptions.add(fe);
          }
        });

    if (!exceptions.isEmpty()) {
      // throw all exceptions at once
      throw new SourceException(
          "Could not process em inputs due to: " + ExceptionUtils.combineExceptions(exceptions));
    }

    Try.scanStream(currentLevel.stream().map(factory::get), "EmInput", SourceException::new)
        .getOrThrow()
        .forEach(em -> emUnits.put(em.getUuid(), em));

    if (currentLevel.isEmpty()) {
      throw new SourceException(
          "There are em inputs, where controlling ems are assigned that don't exist.");
    }

    if (!others.isEmpty()) {
      createEmsRecursively(others, emUnits, factory);
    }
  }
}
