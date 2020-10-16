/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.models.StandardUnits.*;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.EnergyPriceValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.TimeUtil;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class TimeBasedSimpleValueFactory<V extends Value>
    extends TimeBasedValueFactory<SimpleTimeBasedValueData<V>, V> {
  private static final String UUID = "uuid";
  private static final String TIME = "time";
  /* Energy price */
  private static final String PRICE = "price";

  private final TimeUtil timeUtil;

  public TimeBasedSimpleValueFactory(Class<? extends V> valueClasses) {
    this(valueClasses, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'");
  }

  public TimeBasedSimpleValueFactory(Class<? extends V> valueClasses, String timestampPattern) {
    super(valueClasses);
    timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, timestampPattern);
  }

  @Override
  protected TimeBasedValue<V> buildModel(SimpleTimeBasedValueData<V> data) {
    UUID uuid = data.getUUID(UUID);
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    V value;

    if (EnergyPriceValue.class.isAssignableFrom(data.getTargetClass())) {
      value = (V) new EnergyPriceValue(data.getQuantity(PRICE, ENERGY_PRICE));
    } else {
      throw new FactoryException(
          "The given factory cannot handle target class '"
              + data.getTargetClass()
              + "'. Supported classes: '"
              + getSupportedClasses().stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining(","))
              + "'");
    }

    return new TimeBasedValue<>(uuid, time, value);
  }

  @Override
  protected List<Set<String>> getFields(SimpleTimeBasedValueData<V> data) {
    Set<String> minConstructorParams = newSet(UUID, TIME);

    if (EnergyPriceValue.class.isAssignableFrom(data.getTargetClass())) {
      minConstructorParams.add(PRICE);
    }

    return Collections.singletonList(minConstructorParams);
  }
}
