/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.EntityFactory;
import edu.ie3.models.OperationTime;
import edu.ie3.models.OperationTime.OperationTimeBuilder;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.IntFunction;

abstract class SystemParticipantInputEntityFactory<
        T extends SystemParticipantInput, D extends SystemParticipantEntityData>
    extends EntityFactory<T, D> {
  private static final String UUID = "uuid";
  private static final String OPERATES_FROM = "operatesfrom";
  private static final String OPERATES_UNTIL = "operatesUntil";
  private static final String ID = "id";
  private static final String Q_CHARACTERISTICS = "qcharacteristics";

  public SystemParticipantInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  // FIXME same as SimpleEntityFactory method?
  @Override
  public Optional<T> getEntity(D data) {
    isValidClass(data.getEntityClass());

    // magic: case-insensitive get/set calls on set strings
    final List<Set<String>> allFields = getFields(data);

    validateParameters(data, allFields.stream().toArray((IntFunction<Set<String>[]>) Set[]::new));

    try {
      // build the model
      return Optional.of(buildModel(data));

    } catch (FactoryException e) {
      // only catch FactoryExceptions, as more serious exceptions should be handled elsewhere
      log.error(
          "An error occurred when creating instance of "
              + data.getEntityClass().getSimpleName()
              + ".class.",
          e);
    }
    return Optional.empty();
  }

  // FIXME same as SimpleEntityFactory method?
  private void isValidClass(Class<? extends UniqueEntity> clazz) {
    if (!classes.contains(clazz))
      throw new FactoryException(
          "Cannot process " + clazz.getSimpleName() + ".class with this factory!");
  }

  @Override
  protected int validateParameters(D data, Set<String>... fieldSets) {
    if ((data.containsKey(OPERATES_FROM)
            || data.containsKey(OPERATES_UNTIL)
            || data.hasOperatorInput())
        && !(data.containsKey(OPERATES_FROM)
            && data.containsKey(OPERATES_UNTIL)
            && data.hasOperatorInput())) {
      // TODO handle this by throwing error
    }

    return super.validateParameters(data, fieldSets);
  }

  @Override
  protected List<Set<String>> getFields(D data) {
    Set<String> minConstructorParams = newSet(UUID, ID, Q_CHARACTERISTICS);
    Set<String> optConstructorParams =
        expandSet(minConstructorParams, OPERATES_FROM, OPERATES_UNTIL);

    final String[] additionalFields = getAdditionalFields();

    expandSet(minConstructorParams, additionalFields);
    expandSet(optConstructorParams, additionalFields);
    return Arrays.asList(minConstructorParams, optConstructorParams);
  }

  protected abstract String[] getAdditionalFields();

  @Override
  protected T buildModel(D data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
    NodeInput node = data.getNode();
    String qCharacteristics = data.getField(Q_CHARACTERISTICS);
    Optional<OperatorInput> operatorInput = data.getOperatorInput();
    Optional<OperationTime> operationTime = buildOperationTime(data);

    final boolean isOperational = operatorInput.isPresent() && operationTime.isPresent();

    return isOperational
        ? buildModel(
            data, uuid, id, node, qCharacteristics, operatorInput.get(), operationTime.get())
        : buildModel(data, uuid, id, node, qCharacteristics);
  }

  protected abstract T buildModel(
      D data,
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      OperatorInput operatorInput,
      OperationTime operationTime);

  protected abstract T buildModel(
      D data, UUID uuid, String id, NodeInput node, String qCharacteristics);

  private static Optional<OperationTime> buildOperationTime(SystemParticipantEntityData data) {
    if (!data.containsKey(OPERATES_FROM) || !data.containsKey(OPERATES_UNTIL))
      return Optional.empty();

    final String from = data.getField(OPERATES_FROM);
    final String until = data.getField(OPERATES_UNTIL);

    OperationTimeBuilder builder = new OperationTimeBuilder();
    if (!from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (!until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return Optional.of(builder.build());
  }
}
