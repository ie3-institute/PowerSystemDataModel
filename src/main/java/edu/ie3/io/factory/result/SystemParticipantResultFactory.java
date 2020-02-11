/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.system.*;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

public class SystemParticipantResultFactory extends ResultEntityFactory<SystemParticipantResult> {

  private static final String POWER = "p";
  private static final String REACTIVE_POWER = "q";
  private static final String SOC = "soc";

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
  protected List<Set<String>> getFields(SimpleEntityData simpleEntityData) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, POWER, REACTIVE_POWER);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    if (simpleEntityData.getEntityClass().equals(StorageResult.class)
        || simpleEntityData.getEntityClass().equals(EvResult.class)) {
      minConstructorParams = newSet(TIMESTAMP, INPUT_MODEL, POWER, REACTIVE_POWER, SOC);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SystemParticipantResult buildModel(SimpleEntityData simpleEntityData) {
    Map<String, String> fieldsToValues = simpleEntityData.getFieldsToValues();
    Class<? extends UniqueEntity> clazz = simpleEntityData.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(fieldsToValues.get(TIMESTAMP));
    UUID inputModelUuid = UUID.fromString(fieldsToValues.get(INPUT_MODEL));
    Quantity<Power> p =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(POWER)), StandardUnits.ACTIVE_POWER_RESULT);
    Quantity<Power> q =
        Quantities.getQuantity(
            Double.parseDouble(fieldsToValues.get(REACTIVE_POWER)),
            StandardUnits.REACTIVE_POWER_RESULT);
    Optional<UUID> uuidOpt =
        fieldsToValues.containsKey(ENTITY_UUID)
            ? Optional.of(UUID.fromString(fieldsToValues.get(ENTITY_UUID)))
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
          Quantities.getQuantity(Double.parseDouble(fieldsToValues.get(SOC)), Units.PERCENT);

      return uuidOpt
          .map(uuid -> new EvResult(uuid, zdtTimestamp, inputModelUuid, p, q, quantSoc))
          .orElseGet(() -> new EvResult(zdtTimestamp, inputModelUuid, p, q, quantSoc));
    } else if (clazz.equals(StorageResult.class)) {
      Quantity<Dimensionless> socQuantity =
          Quantities.getQuantity(Double.parseDouble(fieldsToValues.get(SOC)), Units.PERCENT);

      return uuidOpt
          .map(uuid -> new StorageResult(uuid, zdtTimestamp, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new StorageResult(zdtTimestamp, inputModelUuid, p, q, socQuantity));
    } else {
      throw new FactoryException("Cannot process " + clazz.getSimpleName() + ".class.");
    }
  }
}
