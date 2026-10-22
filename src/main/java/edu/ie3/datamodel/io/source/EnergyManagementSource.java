/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.factory.input.EmInputFactory.CONTROLLING_EM;
import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.UUID_FIELD;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.FieldMap;
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
    createEmsRecursively(
        dataSource.getSourceData(EmInput.class).map(FieldMap::new).toList(), allEms, factory);
    return allEms;
  }

  /**
   * Method for creating em units recursively.
   *
   * @param rawData provided by the source.
   * @param emUnits map that contains all created em units.
   * @param factory for build an em unit.
   * @throws SourceException - if an error occurred in the source
   */
  private static void createEmsRecursively(
      List<FieldMap> rawData, Map<UUID, EmInput> emUnits, EmInputFactory factory)
      throws SourceException {
    List<FactoryException> exceptions = new ArrayList<>();

    List<FieldMap> currentLevel = new ArrayList<>();
    List<FieldMap> others = new ArrayList<>();

    rawData.forEach(
        t -> {
          try {
            if (!t.isFieldBlank(CONTROLLING_EM)) {
              UUID controllingEm = t.viewUUID(CONTROLLING_EM);

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

    exceptions.addAll(processData(currentLevel, emUnits, factory));
    checkData(exceptions, currentLevel.isEmpty(), others);

    if (!others.isEmpty()) {
      createEmsRecursively(others, emUnits, factory);
    }
  }

  /**
   * Method for building em units.
   *
   * @param currentLevel the em units that can be built on the current level.
   * @param emUnits map that contains all created em units.
   * @param factory for build an em unit.
   * @return a list with found errors
   * @throws SourceException - if an error occurred in the source
   */
  private static List<FactoryException> processData(
      List<FieldMap> currentLevel, Map<UUID, EmInput> emUnits, EmInputFactory factory)
      throws SourceException {
    List<FactoryException> exceptions = new ArrayList<>();

    Try.scanStream(currentLevel.stream().map(factory::get), "EmInput", SourceException::new)
        .getOrThrow()
        .forEach(
            em -> {
              UUID uuid = em.getUuid();
              if (!emUnits.containsKey(uuid)) {
                emUnits.put(em.getUuid(), em);
              } else {
                exceptions.add(new FactoryException("Duplicate em uuid: " + uuid));
              }
            });

    return exceptions;
  }

  /**
   * Checks the processed data.
   *
   * @param exceptions a list of found exceptions.
   * @param emptyCurrentLevel true, if the current level has no em units.
   * @param others the em units that can be built on one of the next levels.
   * @throws SourceException - if an error occurred in the source
   */
  private static void checkData(
      List<FactoryException> exceptions, boolean emptyCurrentLevel, List<FieldMap> others)
      throws SourceException {
    if (!exceptions.isEmpty()) {
      // throw all exceptions at once
      throw new SourceException(
          "Could not process em inputs due to: " + ExceptionUtils.combineExceptions(exceptions));
    }

    if (emptyCurrentLevel && !others.isEmpty()) {
      String errorUuids =
          String.join(",", others.stream().map(d -> d.viewUUID(UUID_FIELD).toString()).toList());

      throw new SourceException(
          "There are em inputs, where controlling ems are assigned that don't exist. These are: "
              + errorUuids);
    }
  }
}
