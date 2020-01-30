/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.EntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.system.*;
import edu.ie3.util.TimeTools;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import java.time.ZonedDateTime;
import java.util.*;

public class SystemParticipantResultFactory extends SimpleEntityFactory<SystemParticipantResult> {
  private static final String entityUuid = "uuid";
  private static final String timestamp = "timestamp";
  private static final String inputModel = "inputModel";
  private static final String power = "p";
  private static final String reactivePower = "q";
  private static final String soc = "soc";

  public SystemParticipantResultFactory() {
    super(
        LoadResult.class,
        FixedFeedInResult.class,
        BmResult.class,
        PvResult.class,
        ChpResult.class,
        WecResult.class,
        StorageResult.class,
        EvcsResult.class,
        EvResult.class);
  }

  @Override
  protected List<Set<String>> getFields(EntityData entityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(timestamp, inputModel, power, reactivePower);
    Set<String> optionalFields = expandSet(minConstructorParams, entityUuid);

    if (entityData.getEntityClass().equals(StorageResult.class)
        || entityData.getEntityClass().equals(EvResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, power, reactivePower, soc);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SystemParticipantResult buildModel(EntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();
    Class<? extends UniqueEntity> clazz = simpleEntityData.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(timestamp));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(inputModel));
    Quantity<Power> p =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(power)), StandardUnits.ACTIVE_POWER_OUT);
    Quantity<Power> q =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(reactivePower)),
            StandardUnits.REACTIVE_POWER_OUT);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(entityUuid)
            ? Optional.of(UUID.fromString(fieldsToValues.get(entityUuid)))
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
    } else if (clazz.equals(EvResult.class)) {
      Quantity<Dimensionless> quantSoc =
          Quantities.getQuantity(Double.parseDouble(fieldsToValues.get(soc)), Units.PERCENT);

      return uuidOpt
          .map(uuid -> new EvResult(uuid, zdtTimestamp, inputModelUuid, p, q, quantSoc))
          .orElseGet(() -> new EvResult(zdtTimestamp, inputModelUuid, p, q, quantSoc));
    } else if (clazz.equals(StorageResult.class)) {
      Quantity<Dimensionless> socQuantity =
          Quantities.getQuantity(Double.parseDouble(fieldsToValues.get(soc)), Units.PERCENT);

      return uuidOpt
          .map(uuid -> new StorageResult(uuid, zdtTimestamp, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new StorageResult(zdtTimestamp, inputModelUuid, p, q, socQuantity));
    } else {
      throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
    }
  }
}
