/*
 * © 2020. TU Dortmund University,
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class TimeBasedSimpleValueFactory<V extends Value>
    extends TimeBasedValueFactory<SimpleTimeBasedValueData<V>, V> {
  private static final String UUID = "uuid";
  private static final String TIME = "time";
  /* Energy price */
  private static final String PRICE = "price";
  /* Energy / Power */
  private static final String ACTIVE_POWER = "p";
  private static final String REACTIVE_POWER = "q";
  private static final String HEAT_DEMAND = "heatdemand";

  private final TimeUtil timeUtil;

  public TimeBasedSimpleValueFactory(Class<? extends V> valueClasses) {
    this(valueClasses, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'");
  }

  public TimeBasedSimpleValueFactory(Class<? extends V> valueClasses, String timePattern) {
    super(valueClasses);
    timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, timePattern);
  }

  @Override
  protected TimeBasedValue<V> buildModel(SimpleTimeBasedValueData<V> data) {
    UUID uuid = data.getUUID(UUID);
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

    return new TimeBasedValue<>(uuid, time, value);
  }

  @Override
  protected List<Set<String>> getFields(SimpleTimeBasedValueData<V> data) {
    Set<String> minConstructorParams = newSet(UUID, TIME);

    if (EnergyPriceValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.add(PRICE);
    } else if (HeatAndSValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, REACTIVE_POWER, HEAT_DEMAND));
    } else if (HeatAndPValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, HEAT_DEMAND));
    } else if (HeatDemandValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.add(HEAT_DEMAND);
    } else if (SValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.addAll(Arrays.asList(ACTIVE_POWER, REACTIVE_POWER));
    } else if (PValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.add(ACTIVE_POWER);
    } else {
      throw new FactoryException(
          "The given factory cannot handle target class '" + data.getTargetClass() + "'.");
    }

    return Collections.singletonList(minConstructorParams);
  }
}
