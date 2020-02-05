/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.result;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.SimpleEntityData;
import edu.ie3.io.factory.SimpleEntityFactory;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.system.*;
import edu.ie3.util.TimeTools;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tec.uom.se.unit.Units;

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
  protected List<Set<String>> getFields(SimpleEntityData data) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(timestamp, inputModel, power, reactivePower);
    Set<String> optionalFields = expandSet(minConstructorParams, entityUuid);

    if (data.getEntityClass().equals(StorageResult.class)
        || data.getEntityClass().equals(EvResult.class)) {
      minConstructorParams = newSet(timestamp, inputModel, power, reactivePower, soc);
      optionalFields = expandSet(minConstructorParams, entityUuid);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SystemParticipantResult buildModel(SimpleEntityData data) {
    Class<? extends UniqueEntity> entityClass = data.getEntityClass();

    ZonedDateTime zdtTimestamp = TimeTools.toZonedDateTime(data.get(timestamp));
    UUID inputModelUuid = data.getUUID(inputModel);
    Quantity<Power> p = data.get(power, StandardUnits.ACTIVE_POWER_OUT);
    Quantity<Power> q = data.get(reactivePower, StandardUnits.REACTIVE_POWER_OUT);
    Optional<UUID> uuidOpt =
        data.containsKey(entityUuid) ? Optional.of(data.getUUID(entityUuid)) : Optional.empty();

    if (entityClass.equals(LoadResult.class)) {
      return uuidOpt
          .map(uuid -> new LoadResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new LoadResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(FixedFeedInResult.class)) {
      return uuidOpt
          .map(uuid -> new FixedFeedInResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new FixedFeedInResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(BmResult.class)) {
      return uuidOpt
          .map(uuid -> new BmResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new BmResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(PvResult.class)) {
      return uuidOpt
          .map(uuid -> new PvResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new PvResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(EvcsResult.class)) {
      return uuidOpt
          .map(uuid -> new EvcsResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new EvcsResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(ChpResult.class)) {
      return uuidOpt
          .map(uuid -> new ChpResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new ChpResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(WecResult.class)) {
      return uuidOpt
          .map(uuid -> new WecResult(uuid, zdtTimestamp, inputModelUuid, p, q))
          .orElseGet(() -> new WecResult(zdtTimestamp, inputModelUuid, p, q));
    } else if (entityClass.equals(EvResult.class)) {
      Quantity<Dimensionless> socQuantity = data.get(soc, Units.PERCENT);

      return uuidOpt
          .map(uuid -> new EvResult(uuid, zdtTimestamp, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new EvResult(zdtTimestamp, inputModelUuid, p, q, socQuantity));
    } else if (entityClass.equals(StorageResult.class)) {
      Quantity<Dimensionless> socQuantity = data.get(soc, Units.PERCENT);

      return uuidOpt
          .map(uuid -> new StorageResult(uuid, zdtTimestamp, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new StorageResult(zdtTimestamp, inputModelUuid, p, q, socQuantity));
    } else {
      throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
    }
  }
}
