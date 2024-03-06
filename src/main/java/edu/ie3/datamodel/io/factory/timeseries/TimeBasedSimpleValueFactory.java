/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.StandardUnits.*;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.util.TimeUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimeBasedSimpleValueFactory<V extends Value>
    extends TimeBasedValueFactory<SimpleTimeBasedValueData<V>, V> {
  private static final String TIME = "time";
  /* Energy price */
  private static final String PRICE = "price";
  /* Energy / Power */
  private static final String ACTIVE_POWER = "p";
  private static final String REACTIVE_POWER = "q";
  private static final String HEAT_DEMAND = "heatDemand";

  private final TimeUtil timeUtil;

  public TimeBasedSimpleValueFactory(Class<? extends V> valueClasses) {
    super(valueClasses);
    this.timeUtil = TimeUtil.withDefaults;
  }

  public TimeBasedSimpleValueFactory(
      Class<? extends V> valueClasses, DateTimeFormatter dateTimeFormatter) {
    super(valueClasses);
    this.timeUtil = new TimeUtil(dateTimeFormatter);
  }

  /**
   * Return the field name for the date time
   *
   * @return the field name for the date time
   */
  public String getTimeFieldString() {
    return TIME;
  }

  @Override
  protected TimeBasedValue<V> buildModel(SimpleTimeBasedValueData<V> data) {
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    V value;

    if (EnergyPriceValue.class.isAssignableFrom(data.getTargetClass())) {
      value = (V) new EnergyPriceValue(data.getQuantity(PRICE, ENERGY_PRICE));
    } else if (HeatAndSValue.class.isAssignableFrom(data.getTargetClass())) {
      value =
          (V)
              new HeatAndSValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(REACTIVE_POWER, REACTIVE_POWER_IN),
                  data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (HeatAndPValue.class.isAssignableFrom(data.getTargetClass())) {
      value =
          (V)
              new HeatAndPValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (HeatDemandValue.class.isAssignableFrom(data.getTargetClass())) {
      value = (V) new HeatDemandValue(data.getQuantity(HEAT_DEMAND, StandardUnits.HEAT_DEMAND));
    } else if (SValue.class.isAssignableFrom(data.getTargetClass())) {
      value =
          (V)
              new SValue(
                  data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN),
                  data.getQuantity(REACTIVE_POWER, REACTIVE_POWER_IN));
    } else if (PValue.class.isAssignableFrom(data.getTargetClass())) {
      value = (V) new PValue(data.getQuantity(ACTIVE_POWER, ACTIVE_POWER_IN));
    } else {
      throw new FactoryException(
          "The given factory cannot handle target class '" + data.getTargetClass() + "'.");
    }

    return new TimeBasedValue<>(time, value);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minConstructorParams = newSet(TIME);

    if (EnergyPriceValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.add(PRICE);
    } else if (HeatAndSValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, REACTIVE_POWER, HEAT_DEMAND));
    } else if (HeatAndPValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, HEAT_DEMAND));
    } else if (HeatDemandValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.add(HEAT_DEMAND);
    } else if (SValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, REACTIVE_POWER));
    } else if (PValue.class.isAssignableFrom(entityClass)) {
      minConstructorParams.add(ACTIVE_POWER);
    } else {
      throw new FactoryException(
          "The given factory cannot handle target class '" + entityClass + "'.");
    }

    return Collections.singletonList(minConstructorParams);
  }
}
