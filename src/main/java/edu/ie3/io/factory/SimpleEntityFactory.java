/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.system.ChpInput;
import edu.ie3.models.result.system.*;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

/**
 * Enum containing all {@link EntityFactory}s that can be used to create an instance of an {@link
 * UniqueEntity} based on {@link SimpleEntityData}
 *
 * @version 0.1
 * @since 28.01.20
 */
public enum SimpleEntityFactory implements EntityFactory<SimpleEntityFactory> {
  SystemParticipantResultFactory(
      LoadResult.class,
      FixedFeedInResult.class,
      BmResult.class,
      PvResult.class,
      ChpInput.class,
      WecResult.class,
      StorageResult.class,
      EvcsResult.class) {

    public final transient Logger log = LogManager.getLogger(this.getClass());

    private static final String entityUuid = "uuid";
    private static final String timestamp = "timestamp";
    private static final String inputModel = "inputModel";
    private static final String power = "p";
    private static final String reactivePower = "q";
    private static final String soc = "soc";

    @Override
    public Optional<? extends SystemParticipantResult> getEntity(EntityData entityData) {

      if (!Arrays.asList(classes()).contains(entityData.getEntityClass()))
        throw new FactoryException(
            "Cannot process "
                + entityData.getEntityClass().getSimpleName()
                + ".class with this factory!");

      // sanity checks
      /// all result models have the same constructor except StorageResult
      Set<String> minConstructorParams =
          new HashSet<>(Arrays.asList(timestamp, inputModel, power, reactivePower));
      Set<String> optionalFields1 =
          Stream.concat(
                  new HashSet<>(Collections.singletonList(entityUuid)).stream(),
                  minConstructorParams.stream())
              .collect(Collectors.toSet());

      if (entityData.getEntityClass().equals(StorageResult.class)) {
        minConstructorParams =
            new HashSet<>(Arrays.asList(timestamp, inputModel, power, reactivePower, soc));
        optionalFields1 =
            Stream.concat(
                    new HashSet<>(Collections.singletonList(entityUuid)).stream(),
                    minConstructorParams.stream())
                .collect(Collectors.toSet());
      }

      List<Set<String>> allFields = new ArrayList<>();
      allFields.add(minConstructorParams);
      allFields.add(optionalFields1);

      SimpleEntityData simpleEntityData = getSimpleEntityData(entityData);
      validParameters(simpleEntityData, allFields.toArray(Set[]::new));

      // build the model
      Optional<? extends SystemParticipantResult> result = Optional.empty();
      try {

        result = Optional.of(buildModel(simpleEntityData));

      } catch (Exception e) {
        log.error(
            "An error occurred when creating instance of "
                + entityData.getEntityClass().getSimpleName()
                + ".class.",
            e);
      }
      return result;
    }

    private SystemParticipantResult buildModel(SimpleEntityData simpleEntityData) {
      Map<String, String> fieldsToAttributes = simpleEntityData.getFieldsToValues();
      Class<? extends UniqueEntity> clazz = simpleEntityData.getEntityClass();

      ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToAttributes.get(timestamp));
      UUID inputModelUuid = UUID.fromString(fieldsToAttributes.get(inputModel));
      Quantity<Power> p =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToAttributes.get(power)), StandardUnits.ACTIVE_POWER_OUT);
      Quantity<Power> q =
          Quantities.getQuantity(
              Double.parseDouble(fieldsToAttributes.get(reactivePower)),
              StandardUnits.REACTIVE_POWER_OUT);
      Optional<UUID> uuidOpt =
          fieldsToAttributes.containsKey(entityUuid)
              ? Optional.of(UUID.fromString(fieldsToAttributes.get(entityUuid)))
              : Optional.empty();

      if (clazz.equals(LoadResult.class)) {
        return uuidOpt
            .map(uuid -> new LoadResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new LoadResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(FixedFeedInResult.class)) {
        return uuidOpt
            .map(uuid -> new FixedFeedInResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new FixedFeedInResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(BmResult.class)) {
        return uuidOpt
            .map(uuid -> new BmResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new BmResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(PvResult.class)) {
        return uuidOpt
            .map(uuid -> new PvResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new PvResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(EvcsResult.class)) {
        return uuidOpt
            .map(uuid -> new EvcsResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new EvcsResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(ChpResult.class)) {
        return uuidOpt
            .map(uuid -> new ChpResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new ChpResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(WecResult.class)) {
        return uuidOpt
            .map(uuid -> new WecResult(uuid, zdtTimestamp, inputModelUuid, p, q))
            .orElseGet(() -> new WecResult(zdtTimestamp, inputModelUuid, p, q));
      } else if (clazz.equals(StorageResult.class)) {

        Quantity<Dimensionless> quantSoc =
            Quantities.getQuantity(Double.parseDouble(fieldsToAttributes.get(soc)), Units.PERCENT);

        return uuidOpt
            .map(uuid -> new StorageResult(uuid, zdtTimestamp, inputModelUuid, p, q, quantSoc))
            .orElseGet(() -> new StorageResult(zdtTimestamp, inputModelUuid, p, q, quantSoc));
      } else {
        throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
      }
    }
  };

  SimpleEntityFactory(Class<? extends UniqueEntity>... classes) {
    this.classes = classes;
  }

  private final Class<? extends UniqueEntity>[] classes;

  @Override
  public Class<? extends UniqueEntity>[] classes() {
    return classes;
  }

  @Override
  public SimpleEntityFactory getRaw() {
    return this;
  }

  @Override
  public abstract Optional<? extends UniqueEntity> getEntity(EntityData metaData);

  private static SimpleEntityData getSimpleEntityData(EntityData entityData) {
    if (!(entityData instanceof SimpleEntityData)) {
      throw new FactoryException(
          "Invalid entity data "
              + entityData.getClass().getSimpleName()
              + " provided. Please use 'SimpleEntityData' for 'SimpleEntityFactory'!");
    } else {
      return (SimpleEntityData) entityData;
    }
  }

  private static int validParameters(SimpleEntityData simpleEntityData, Set<String>... fieldSets) {

    Map<String, String> fieldsToAttributes = simpleEntityData.getFieldsToValues();

    // get all sets that match the fields to attributes
    List<Set<String>> validFieldSets =
        Arrays.stream(fieldSets)
            .filter(x -> x.equals(fieldsToAttributes.keySet()))
            .collect(Collectors.toList());

    if (validFieldSets.size() == 1) {
      // if we can identify a unique parameter set for a constructor, we take it and return the
      // index
      Set<String> validFieldSet = validFieldSets.get(0);
      return Arrays.asList(fieldSets).indexOf(validFieldSet);
    } else {
      // build the exception string with extensive debug information
      String providedFieldMapString =
          fieldsToAttributes.keySet().stream()
              .map(key -> key + " -> " + fieldsToAttributes.get(key))
              .collect(Collectors.joining(","));

      String providedKeysString = "[" + String.join(", ", fieldsToAttributes.keySet()) + "]";

      StringBuilder possibleOptions = new StringBuilder();
      for (int i = 0; i < fieldSets.length; i++) {
        Set<String> fieldSet = fieldSets[i];
        String option = i + ": [" + String.join(", ", fieldSet) + "]\n";
        possibleOptions.append(option);
      }
      throw new FactoryException(
          "The provided fields "
              + providedKeysString
              + " with data {"
              + providedFieldMapString
              + "}"
              + " are invalid for instance of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + ". \nThe following fields to be passed to a constructor of "
              + simpleEntityData.getEntityClass().getSimpleName()
              + " are possible:\n"
              + possibleOptions.toString());
    }
  }
}
