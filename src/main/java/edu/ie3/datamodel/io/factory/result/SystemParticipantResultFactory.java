/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Factory class for creating {@link SystemParticipantResult} entities from provided {@link
 * EntityData} data objects.
 */
public class SystemParticipantResultFactory extends ResultEntityFactory<SystemParticipantResult> {

  private static final String POWER = "p";
  private static final String REACTIVE_POWER = "q";
  private static final String SOC = "soc";
  private static final String Q_DOT = "qDot";

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
        HpResult.class,
        EmResult.class);
  }

  /**
   * Create a new factory to build {@link SystemParticipantResult}s and utilize the given date time
   * formatter pattern to parse date time strings
   *
   * @param dateTimeFormatter to parse date time strings
   */
  public SystemParticipantResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(
        dateTimeFormatter,
        LoadResult.class,
        FixedFeedInResult.class,
        BmResult.class,
        PvResult.class,
        ChpResult.class,
        WecResult.class,
        StorageResult.class,
        EvcsResult.class,
        EvResult.class,
        HpResult.class,
        EmResult.class);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    /// all result models have the same constructor except StorageResult
    Set<String> minConstructorParams = newSet(TIME, INPUT_MODEL, POWER, REACTIVE_POWER);

    if (entityClass.equals(StorageResult.class) || entityClass.equals(EvResult.class)) {
      minConstructorParams = newSet(TIME, INPUT_MODEL, POWER, REACTIVE_POWER, SOC);
    }

    if (SystemParticipantWithHeatResult.class.isAssignableFrom(entityClass)) {
      minConstructorParams = expandSet(minConstructorParams, Q_DOT);
    }

    return List.of(minConstructorParams);
  }

  @Override
  protected SystemParticipantResult buildModel(EntityData data) {
    Class<? extends Entity> entityClass = data.getTargetClass();

    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Power> p = data.getQuantity(POWER, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> q =
        data.getQuantity(REACTIVE_POWER, StandardUnits.REACTIVE_POWER_RESULT);

    if (entityClass.equals(LoadResult.class)) {
      return new LoadResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(FixedFeedInResult.class)) {
      return new FixedFeedInResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(BmResult.class)) {
      return new BmResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(PvResult.class)) {
      return new PvResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(EvcsResult.class)) {
      return new EvcsResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(EmResult.class)) {
      return new EmResult(zdtTime, inputModelUuid, p, q);
    } else if (SystemParticipantWithHeatResult.class.isAssignableFrom(entityClass)) {
      /* The following classes all have a heat component as well */
      ComparableQuantity<Power> qDot = data.getQuantity(Q_DOT, StandardUnits.Q_DOT_RESULT);

      if (entityClass.equals(ChpResult.class)) {
        return new ChpResult(zdtTime, inputModelUuid, p, q, qDot);
      } else if (entityClass.equals(HpResult.class)) {
        return new HpResult(zdtTime, inputModelUuid, p, q, qDot);
      } else {
        throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
      }
    } else if (entityClass.equals(WecResult.class)) {
      return new WecResult(zdtTime, inputModelUuid, p, q);
    } else if (entityClass.equals(EvResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = data.getQuantity(SOC, PERCENT);

      return new EvResult(zdtTime, inputModelUuid, p, q, socQuantity);
    } else if (entityClass.equals(StorageResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = data.getQuantity(SOC, PERCENT);

      return new StorageResult(zdtTime, inputModelUuid, p, q, socQuantity);
    } else {
      throw new FactoryException("Cannot process " + entityClass.getSimpleName() + ".class.");
    }
  }
}
