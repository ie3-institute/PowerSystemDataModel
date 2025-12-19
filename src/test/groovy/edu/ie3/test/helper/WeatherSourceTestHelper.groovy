/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.quantities.QuantityUtil

trait WeatherSourceTestHelper {

  static boolean equalsIgnoreUUID(IndividualTimeSeries<WeatherValue> ts1, IndividualTimeSeries<WeatherValue> ts2) {
    return equalsIgnoreUUID(ts1.entries, ts2.entries)
  }

  static boolean equalsIgnoreUUID(Collection<TimeBasedValue<WeatherValue>> c1, Collection<TimeBasedValue<WeatherValue>> c2) {
    if (c1 == null || c2 == null) return (c1 == null && c2 == null)
    if (c1.size() != c2.size()) return false
    for (TimeBasedValue<WeatherValue> value1 : c1) {
      if (!c2.stream().anyMatch({ value2 -> equalsIgnoreUUID(value1, value2) })) return false
    }
    return true
  }

  static boolean equalsIgnoreUUID(TimeBasedValue<WeatherValue> weatherA, TimeBasedValue<WeatherValue> weatherB) {
    if (weatherA.time != weatherB.time) return false

    def weatherValueA = weatherA.value
    def weatherValueB = weatherB.value

    def mandatoryValues = weatherValueA.solarIrradiance.directIrradiance.present == weatherValueB.solarIrradiance.directIrradiance.present && QuantityUtil.isEquivalentAbs(weatherValueA.solarIrradiance.directIrradiance.get(), weatherValueB.solarIrradiance.directIrradiance.get(), 1E-10) &&
        weatherValueA.solarIrradiance.diffuseIrradiance.present == weatherValueB.solarIrradiance.diffuseIrradiance.present && QuantityUtil.isEquivalentAbs(weatherValueA.solarIrradiance.diffuseIrradiance.get(), weatherValueB.solarIrradiance.diffuseIrradiance.get(), 1E-10) &&
        weatherValueA.temperature.temperature.present == weatherValueB.temperature.temperature.present && QuantityUtil.isEquivalentAbs(weatherValueA.temperature.temperature.get(), weatherValueB.temperature.temperature.get(), 1E-10) &&
        weatherValueA.wind.velocity.present == weatherValueB.wind.velocity.present && QuantityUtil.isEquivalentAbs(weatherValueA.wind.velocity.get(), weatherValueB.wind.velocity.get(), 1E-10) &&
        weatherValueA.wind.direction.present == weatherValueB.wind.direction.present && QuantityUtil.isEquivalentAbs(weatherValueA.wind.direction.get(), weatherValueB.wind.direction.get(), 1E-10)

    if (!mandatoryValues) return false

    if (!compareOptionalTemperature(weatherValueA.groundTemperatureLevel1, weatherValueB.groundTemperatureLevel1)) return false
    if (!compareOptionalTemperature(weatherValueA.groundTemperatureLevel2, weatherValueB.groundTemperatureLevel2)) return false

    return true
  }

  static boolean compareOptionalTemperature(def optA, def optB) {
    if (optA.present != optB.present) return false
    if (!optA.present) return true
    return QuantityUtil.isEquivalentAbs(
        optA.get().getTemperature().get(),
        optB.get().getTemperature().get(),
        1E-10
        )
  }
}