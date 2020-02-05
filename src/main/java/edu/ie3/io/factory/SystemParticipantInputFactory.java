/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.OperationTime;
import edu.ie3.models.OperationTime.OperationTimeBuilder;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.IntFunction;
import javax.measure.Quantity;
import javax.measure.quantity.Power;

public class SystemParticipantInputFactory
    extends EntityFactory<SystemParticipantInput, SystemParticipantEntityData> {
  private static final String UUID = "uuid";
  private static final String OPERATES_FROM = "operatesfrom";
  private static final String OPERATES_UNTIL = "operatesUntil";
  private static final String ID = "id";
  private static final String Q_CHARACTERISTICS = "qcharacteristics";
  private static final String COSPHI_RATED = "cosphirated";

  // used by multiple types
  private static final String S_RATED = "srated";
  private static final String MARKET_REACTION = "marketreaction";

  public SystemParticipantInputFactory() {
    super(
        FixedFeedInInput.class,
        WecInput.class,
        StorageInput.class,
        BmInput.class,
        ChpInput.class,
        LoadInput.class,
        HpInput.class,
        PvInput.class,
        EvInput.class);
  }

  // FIXME same as SimpleEntityFactory method?
  @Override
  public Optional<SystemParticipantInput> getEntity(SystemParticipantEntityData data) {
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

  // FIXME same as SimpleEntityFactory method?
  private void isValidClass(Class<? extends UniqueEntity> clazz) {
    if (!classes.contains(clazz))
      throw new FactoryException(
          "Cannot process " + clazz.getSimpleName() + ".class with this factory!");
  }

  @Override
  protected int validateParameters(SystemParticipantEntityData data, Set<String>... fieldSets) {
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
  protected List<Set<String>> getFields(SystemParticipantEntityData data) {
    Set<String> minConstructorParams = newSet(UUID, ID, Q_CHARACTERISTICS, COSPHI_RATED);
    Set<String> optConstructorParams =
        expandSet(minConstructorParams, OPERATES_FROM, OPERATES_UNTIL);

    String[] additionalFields = null;
    if (FixedFeedInInput.class.equals(data.getEntityClass())) {
      additionalFields = new String[] {S_RATED};
    } else if (WecInput.class.equals(data.getEntityClass())) {
      additionalFields = new String[] {MARKET_REACTION};
    }
    // TODO

    expandSet(minConstructorParams, additionalFields);
    expandSet(optConstructorParams, additionalFields);
    return Arrays.asList(minConstructorParams, optConstructorParams);
  }

  @Override
  protected SystemParticipantInput buildModel(SystemParticipantEntityData data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.get(ID);
    String qCharacteristics = data.get(Q_CHARACTERISTICS);
    NodeInput node = data.getNode();
    double cosPhiRated = Double.parseDouble(data.get(COSPHI_RATED));
    Optional<OperatorInput> operatorInput = data.getOperatorInput();
    Optional<OperationTime> operationTime = buildOperationTime(data);

    final boolean isOperational = operatorInput.isPresent() && operationTime.isPresent();

    if (FixedFeedInInput.class.equals(data.getEntityClass())) {
      Quantity<Power> sRated = data.get(S_RATED, StandardUnits.S_RATED);

      return isOperational
          ? new FixedFeedInInput(
              uuid,
              operationTime.get(),
              operatorInput.get(),
              id,
              node,
              qCharacteristics,
              cosPhiRated,
              sRated)
          : new FixedFeedInInput(uuid, id, node, qCharacteristics, cosPhiRated, sRated);
    }
    // TODO
    return null;
  }

  private static Optional<OperationTime> buildOperationTime(SystemParticipantEntityData data) {
    if (!data.containsKey(OPERATES_FROM) || !data.containsKey(OPERATES_UNTIL))
      return Optional.empty();

    final String from = data.get(OPERATES_FROM);
    final String until = data.get(OPERATES_UNTIL);

    OperationTimeBuilder builder = new OperationTimeBuilder();
    if (!from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (!until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return Optional.of(builder.build());
  }
}
