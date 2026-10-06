/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.PowerLimitFlexOptionsResult;
import tech.units.indriya.ComparableQuantity;

import javax.measure.quantity.Power;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

public class PowerLimitFlexOptionsResultFactory
    extends ResultEntityFactory<PowerLimitFlexOptionsResult, PowerLimitFlexOptionsResult> {

  public PowerLimitFlexOptionsResultFactory() {
    super(PowerLimitFlexOptionsResult.class);
  }

  @Override
  protected PowerLimitFlexOptionsResult buildModel(
      Map<String, String> data, ZonedDateTime time, UUID inputModel) {
    ComparableQuantity<Power> pRef = getQuantity(data, P_REF, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMin = getQuantity(data, P_MIN, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMax = getQuantity(data, P_MAX, StandardUnits.ACTIVE_POWER_RESULT);

    return new PowerLimitFlexOptionsResult(time, inputModel, pRef, pMin, pMax);
  }
}
