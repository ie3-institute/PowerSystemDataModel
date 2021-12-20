/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv.timeseries;

import edu.ie3.datamodel.models.value.*;
import edu.ie3.util.StringUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/** Yet supported column schemes in individual time series */
public enum ColumnScheme {
  ENERGY_PRICE("c", EnergyPriceValue.class),
  ACTIVE_POWER("p", PValue.class),
  APPARENT_POWER("pq", SValue.class),
  HEAT_DEMAND("h", HeatDemandValue.class),
  ACTIVE_POWER_AND_HEAT_DEMAND("ph", HeatAndPValue.class),
  APPARENT_POWER_AND_HEAT_DEMAND("pqh", HeatAndSValue.class),
  WEATHER("weather", WeatherValue.class);

  private final String scheme;
  private final Class<? extends Value> valueClass;

  ColumnScheme(String scheme, Class<? extends Value> valueClass) {
    this.scheme = scheme;
    this.valueClass = valueClass;
  }

  public String getScheme() {
    return scheme;
  }

  public Class<? extends Value> getValueClass() {
    return valueClass;
  }

  public static Optional<ColumnScheme> parse(String key) {
    String cleanString = StringUtils.cleanString(key).toLowerCase();
    return Arrays.stream(ColumnScheme.values())
        .filter(entry -> Objects.equals(entry.scheme, cleanString))
        .findFirst();
  }

  public static <V extends Value> Optional<ColumnScheme> parse(Class<V> valueClass) {
    /* IMPORTANT NOTE: Make sure to start with child classes and then use parent classes to allow for most precise
     * parsing (child class instances are also assignable to parent classes) */

    if (EnergyPriceValue.class.isAssignableFrom(valueClass)) return Optional.of(ENERGY_PRICE);
    if (HeatAndSValue.class.isAssignableFrom(valueClass))
      return Optional.of(APPARENT_POWER_AND_HEAT_DEMAND);
    if (SValue.class.isAssignableFrom(valueClass)) return Optional.of(APPARENT_POWER);
    if (HeatAndPValue.class.isAssignableFrom(valueClass))
      return Optional.of(ACTIVE_POWER_AND_HEAT_DEMAND);
    if (PValue.class.isAssignableFrom(valueClass)) return Optional.of(ACTIVE_POWER);
    if (HeatDemandValue.class.isAssignableFrom(valueClass)) return Optional.of(HEAT_DEMAND);
    if (WeatherValue.class.isAssignableFrom(valueClass)) return Optional.of(WEATHER);
    return Optional.empty();
  }
}
