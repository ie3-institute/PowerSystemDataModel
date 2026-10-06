/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.result;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.system.EnergyBoundariesFlexOptionsResult;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

public class EnergyBoundariesFlexOptionsResultFactory
    extends ResultEntityFactory<
        EnergyBoundariesFlexOptionsResult, EnergyBoundariesFlexOptionsResult> {

  public EnergyBoundariesFlexOptionsResultFactory() {
    super(EnergyBoundariesFlexOptionsResult.class);
  }

  @Override
  protected EnergyBoundariesFlexOptionsResult buildModel(
      Map<String, String> data, ZonedDateTime time, UUID inputModel) {
    ComparableQuantity<Energy> eState = getQuantity(data, E_STATE, StandardUnits.ENERGY_RESULT);
    ComparableQuantity<Energy> eMin = getQuantity(data, E_MIN, StandardUnits.ENERGY_RESULT);
    ComparableQuantity<Energy> eMax = getQuantity(data, E_MAX, StandardUnits.ENERGY_RESULT);
    ComparableQuantity<Power> pMin = getQuantity(data, P_MIN, StandardUnits.ACTIVE_POWER_RESULT);
    ComparableQuantity<Power> pMax = getQuantity(data, P_MAX, StandardUnits.ACTIVE_POWER_RESULT);

    return new EnergyBoundariesFlexOptionsResult(time, inputModel, eState, eMin, eMax, pMin, pMax);
  }
}
