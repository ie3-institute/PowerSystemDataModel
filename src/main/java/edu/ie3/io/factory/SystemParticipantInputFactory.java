/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.system.SystemParticipantInput;

import java.util.*;
import java.util.function.IntFunction;

public class SystemParticipantInputFactory
    extends EntityFactory<SystemParticipantInput, AssetEntityData> {
  private static final String entityUuid = "uuid";
  private static final String operatesFrom = "operatesfrom";
  private static final String operatesUntil = "operatesUntil";
  private static final String entityId = "id";
  private static final String qCharacteristics = "qcharacteristics";
  private static final String cosphiRated = "cosphirated";

  public SystemParticipantInputFactory() {
    super();
  }

  // FIXME same as SimpleEntityFactory method?
  @Override
  public Optional<SystemParticipantInput> getEntity(AssetEntityData data) {
    isValidClass(data.getEntityClass());

    // magic: case-insensitive get/set calls on set strings
    final List<Set<String>> allFields = getFields(data);

    validateParameters(data, allFields.toArray((IntFunction<Set<String>[]>) Set[]::new));

    try {
      // build the model
      return Optional.of(buildModel(data));

    } catch (Exception e) {
      log.error(
          "An error occurred when creating instance of "
              + data.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return Optional.empty();
  }

  private void isValidClass(Class<? extends UniqueEntity> clazz) {
    if (!classes.contains(clazz))
      throw new FactoryException(
          "Cannot process " + clazz.getSimpleName() + ".class with this factory!");
  }

  @Override
  protected List<Set<String>> getFields(AssetEntityData data) {
    Set<String> constructorParams = newSet(entityUuid, entityId, qCharacteristics, cosphiRated);

    // TODO
    return Arrays.asList(constructorParams);
  }

  @Override
  protected SystemParticipantInput buildModel(AssetEntityData data) {
    UUID uuid = data.getUUID(entityUuid);
    String id = data.get(entityId);

    // TODO
    return null;
  }
}
