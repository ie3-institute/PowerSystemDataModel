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
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.SystemParticipantInput;
import java.time.ZonedDateTime;
import java.util.*;

abstract class SystemParticipantInputEntityFactory<
        T extends SystemParticipantInput, D extends SystemParticipantEntityData>
    extends EntityFactory<T, D> {
  private static final String UUID = "uuid";
  private static final String OPERATES_FROM = "operatesfrom";
  private static final String OPERATES_UNTIL = "operatesuntil";
  private static final String ID = "id";
  private static final String Q_CHARACTERISTICS = "qcharacteristics";

  public SystemParticipantInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  @Override
  protected int validateParameters(D data, Set<String>... fieldSets) {
    if ((data.containsKey(OPERATES_FROM)
        || data.containsKey(OPERATES_UNTIL)
        || data.hasOperatorInput())) {

      if ((data.containsKey(OPERATES_FROM) || data.containsKey(OPERATES_UNTIL))
          && !data.hasOperatorInput())
        throw new FactoryException(
            "Operation time (fields '"
                + OPERATES_FROM
                + "' and '"
                + OPERATES_UNTIL
                + "') are passed, but operator input is not.");

      if (data.hasOperatorInput()
          && (!data.containsKey(OPERATES_FROM) || !data.containsKey(OPERATES_UNTIL)))
        throw new FactoryException(
            "Operator input is passed, but operation time (fields '"
                + OPERATES_FROM
                + "' and '"
                + OPERATES_UNTIL
                + "') is not.");
    }

    return super.validateParameters(data, fieldSets);
  }

  @Override
  protected List<Set<String>> getFields(D data) {
    Set<String> minConstructorParams = newSet(UUID, ID, Q_CHARACTERISTICS);
    Set<String> optConstructorParams =
        expandSet(minConstructorParams, OPERATES_FROM, OPERATES_UNTIL);

    final String[] additionalFields = getAdditionalFields();

    minConstructorParams = expandSet(minConstructorParams, additionalFields);
    optConstructorParams = expandSet(optConstructorParams, additionalFields);
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
