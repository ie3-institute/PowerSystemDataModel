/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.system.*;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Factory class for creating {@link SystemParticipantResult} entities from provided {@link
 * SimpleEntityData} data objects.
 */
public class SystemParticipantResultFactory extends ResultEntityFactory<SystemParticipantResult> {

  private static final String POWER = "p";
  private static final String REACTIVE_POWER = "q";
  private static final String SOC = "soc";
  private static final String Q_DOT = "qdot";

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
        EvResult.class,
        HpResult.class);
  }

  @Override
  protected List<Set<String>> getFields(SimpleEntityData data) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, POWER, REACTIVE_POWER);
    Set<String> optionalFields = expandSet(minConstructorParams, ENTITY_UUID);

    if (data.getTargetClass().equals(StorageResult.class)
        || data.getTargetClass().equals(EvResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, POWER, REACTIVE_POWER, SOC);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    }

    if (data.getTargetClass().equals(HpResult.class)
        || data.getTargetClass().equals(ChpResult.class)) {
      minConstructorParams = expandSet(minConstructorParams, Q_DOT);
      optionalFields = expandSet(minConstructorParams, ENTITY_UUID);
    }

    return Arrays.asList(minConstructorParams, optionalFields);
  }

  @Override
  protected SystemParticipantResult buildModel(SimpleEntityData data) {
    Class<? extends UniqueEntity> entityClass = data.getTargetClass();

    ZonedDateTime zdtTime = TIME_UTIL.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> p = data.getQuantity(POWER, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> q =
        data.getQuantity(REACTIVE_POWER, StandardUnits.REACTIVE_POWER_RESULT);
    Optional<UUID> uuidOpt =
        data.containsKey(ENTITY_UUID) ? Optional.of(data.getUUID(ENTITY_UUID)) : Optional.empty();

    if (entityClass.equals(LoadResult.class)) {
      return uuidOpt
          .map(uuid -> new LoadResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new LoadResult(zdtTime, inputModelUuid, p, q));
    } else if (entityClass.equals(FixedFeedInResult.class)) {
      return uuidOpt
          .map(uuid -> new FixedFeedInResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new FixedFeedInResult(zdtTime, inputModelUuid, p, q));
    } else if (entityClass.equals(BmResult.class)) {
      return uuidOpt
          .map(uuid -> new BmResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new BmResult(zdtTime, inputModelUuid, p, q));
    } else if (entityClass.equals(PvResult.class)) {
      return uuidOpt
          .map(uuid -> new PvResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new PvResult(zdtTime, inputModelUuid, p, q));
    } else if (entityClass.equals(EvcsResult.class)) {
      return uuidOpt
          .map(uuid -> new EvcsResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new EvcsResult(zdtTime, inputModelUuid, p, q));
    } else if (SystemParticipantWithHeatResult.class.isAssignableFrom(entityClass)) {
      /* The following classes all have a heat component as well */
      ComparableQuantity<Power> qDot = data.getQuantity(Q_DOT, StandardUnits.Q_DOT_RESULT);

      if (entityClass.equals(ChpResult.class)) {
        return uuidOpt
            .map(uuid -> new ChpResult(uuid, zdtTime, inputModelUuid, p, q, qDot))
            .orElseGet(() -> new ChpResult(zdtTime, inputModelUuid, p, q, qDot));
      } else if (entityClass.equals(HpResult.class)) {
        return uuidOpt
            .map(uuid -> new HpResult(uuid, zdtTime, inputModelUuid, p, q, qDot))
            .orElseGet(() -> new HpResult(zdtTime, inputModelUuid, p, q, qDot));
      } else {
        throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
      }
    } else if (entityClass.equals(WecResult.class)) {
      return uuidOpt
          .map(uuid -> new WecResult(uuid, zdtTime, inputModelUuid, p, q))
          .orElseGet(() -> new WecResult(zdtTime, inputModelUuid, p, q));
    } else if (entityClass.equals(EvResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = data.getQuantity(SOC, PERCENT);

      return uuidOpt
          .map(uuid -> new EvResult(uuid, zdtTime, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new EvResult(zdtTime, inputModelUuid, p, q, socQuantity));
    } else if (entityClass.equals(StorageResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = data.getQuantity(SOC, PERCENT);

      return uuidOpt
          .map(uuid -> new StorageResult(uuid, zdtTime, inputModelUuid, p, q, socQuantity))
          .orElseGet(() -> new StorageResult(zdtTime, inputModelUuid, p, q, socQuantity));
    } else {
      throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
    }
  }
}
