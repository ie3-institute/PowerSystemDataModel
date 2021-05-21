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
  ENERGY_PRICE("c"),
  ACTIVE_POWER("p"),
  APPARENT_POWER("pq"),
  HEAT_DEMAND("h"),
  ACTIVE_POWER_AND_HEAT_DEMAND("ph"),
  APPARENT_POWER_AND_HEAT_DEMAND("pqh"),
  WEATHER("weather");

  private final String scheme;

  ColumnScheme(String scheme) {
    this.scheme = scheme;
  }

  public String getScheme() {
    return scheme;
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
