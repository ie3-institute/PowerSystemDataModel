/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.*;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Factory class for creating {@link SystemParticipantResult} entities. */
public class SystemParticipantResultFactory<R extends SystemParticipantResult>
    extends ResultEntityFactory<SystemParticipantResult, R> {

  private final Class<R> targetClass;

  public SystemParticipantResultFactory(Class<R> targetClass) {
    super(
        LoadResult.class,
        FixedFeedInResult.class,
        AcResult.class,
        BmResult.class,
        PvResult.class,
        ChpResult.class,
        WecResult.class,
        StorageResult.class,
        EvcsResult.class,
        EvResult.class,
        HpResult.class,
        EmResult.class);
    this.targetClass = targetClass;

    isSupportedClass(targetClass);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected R buildModel(Map<String, String> data, ZonedDateTime time, UUID inputModel) {

    ComparableQuantity<Power> p = getQuantity(data, POWER, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> q =
        getQuantity(data, REACTIVE_POWER, StandardUnits.REACTIVE_POWER_RESULT);

    if (targetClass.equals(LoadResult.class)) {
      return (R) new LoadResult(time, inputModel, p, q);
    } else if (targetClass.equals(FixedFeedInResult.class)) {
      return (R) new FixedFeedInResult(time, inputModel, p, q);
    } else if (targetClass.equals(BmResult.class)) {
      return (R) new BmResult(time, inputModel, p, q);
    } else if (targetClass.equals(PvResult.class)) {
      return (R) new PvResult(time, inputModel, p, q);
    } else if (targetClass.equals(EvcsResult.class)) {
      return (R) new EvcsResult(time, inputModel, p, q);
    } else if (targetClass.equals(EmResult.class)) {
      return (R) new EmResult(time, inputModel, p, q);
    } else if (SystemParticipantWithHeatResult.class.isAssignableFrom(targetClass)) {
      /* The following classes all have a heat component as well */
      ComparableQuantity<Power> qDot = getQuantity(data, Q_DOT, StandardUnits.Q_DOT_RESULT);

      if (targetClass.equals(ChpResult.class)) {
        return (R) new ChpResult(time, inputModel, p, q, qDot);
      } else if (targetClass.equals(HpResult.class)) {
        return (R) new HpResult(time, inputModel, p, q, qDot);
      } else if (targetClass.equals(AcResult.class)) {
        return (R) new AcResult(time, inputModel, p, q, qDot);
      } else {
        throw new FactoryException("Cannot process " + targetClass.getSimpleName() + ".class.");
      }
    } else if (targetClass.equals(WecResult.class)) {
      return (R) new WecResult(time, inputModel, p, q);
    } else if (targetClass.equals(EvResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = getQuantity(data, SOC, PERCENT);

      return (R) new EvResult(time, inputModel, p, q, socQuantity);
    } else if (targetClass.equals(StorageResult.class)) {
      ComparableQuantity<Dimensionless> socQuantity = getQuantity(data, SOC, PERCENT);

      return (R) new StorageResult(time, inputModel, p, q, socQuantity);
    } else {
      throw new FactoryException("Cannot process " + targetClass.getSimpleName() + ".class.");
    }
  }
}
