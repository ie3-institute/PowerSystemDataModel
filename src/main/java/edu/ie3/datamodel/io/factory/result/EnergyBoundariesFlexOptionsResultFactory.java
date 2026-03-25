/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.EnergyBoundariesFlexOptionsResult;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EnergyBoundariesFlexOptionsResultFactory extends ResultEntityFactory<EnergyBoundariesFlexOptionsResult> {

  public EnergyBoundariesFlexOptionsResultFactory() {
    super(EnergyBoundariesFlexOptionsResult.class);
  }

  /**
   * Create a new factory to build {@link EnergyBoundariesFlexOptionsResult}s and utilize the given date
   * time formatter pattern to parse date time strings
   *
   * @param dateTimeFormatter to parse date time strings
   */
  public EnergyBoundariesFlexOptionsResultFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter, EnergyBoundariesFlexOptionsResult.class);
  }

  @Override
  protected EnergyBoundariesFlexOptionsResult buildModel(EntityData data) {
    ZonedDateTime zdtTime = timeUtil.toZonedDateTime(data.getField(TIME));
    UUID inputModelUuid = data.getUUID(INPUT_MODEL);
    ComparableQuantity<Energy> eMin = data.getQuantity(E_MIN, StandardUnits.ENERGY_RESULT);
    ComparableQuantity<Energy> eMax = data.getQuantity(E_MAX, StandardUnits.ENERGY_RESULT);
    ComparableQuantity<Power> pMin = data.getQuantity(P_MIN, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMax = data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_RESULT);

    return new EnergyBoundariesFlexOptionsResult(zdtTime, inputModelUuid, eMin, eMax, pMin, pMax);
  }
}
