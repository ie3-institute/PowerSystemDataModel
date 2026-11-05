/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.StandardUnits.*;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.FieldMap;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.*;
import java.time.ZonedDateTime;

public class TimeBasedSimpleValueFactory<V extends Value>
    extends TimeBasedValueFactory<FieldMap, V> {

  private final Class<V> targetClass;

  public TimeBasedSimpleValueFactory(Class<V> targetClass) {
    super(targetClass);
    this.targetClass = targetClass;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected TimeBasedValue<V> buildModel(FieldMap data) {
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    V value;

    if (EnergyPriceValue.class.isAssignableFrom(targetClass)) {
      value = (V) new EnergyPriceValue(data.getQuantity(PRICE, ENERGY_PRICE));
    } else if (HeatAndSValue.class.isAssignableFrom(targetClass)) {
      value =
          (V)
              new HeatAndSValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(REACTIVE_POWER, REACTIVE_POWER_IN),
                  data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (HeatAndPValue.class.isAssignableFrom(targetClass)) {
      value =
          (V)
              new HeatAndPValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (HeatDemandValue.class.isAssignableFrom(targetClass)) {
      value = (V) new HeatDemandValue(data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (SValue.class.isAssignableFrom(targetClass)) {
      value =
          (V)
              new SValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(REACTIVE_POWER, REACTIVE_POWER_IN));
    } else if (PValue.class.isAssignableFrom(targetClass)) {
      value = (V) new PValue(data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN));
    } else if (VoltageValue.class.isAssignableFrom(targetClass)) {
      value =
          (V)
              new VoltageValue(
                  data.getQuantity(V_MAG, VOLTAGE_MAGNITUDE),
                  data.getQuantityOptional(V_ANG, VOLTAGE_ANGLE));
    } else {
      throw new FactoryException(
          "The given factory cannot handle target class '" + targetClass + "'.");
    }

    return new TimeBasedValue<>(time, value);
  }
}
