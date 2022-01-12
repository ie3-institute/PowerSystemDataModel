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

	static boolean equalsIgnoreUUID(IndividualTimeSeries<WeatherValue> ts1,
			IndividualTimeSeries<WeatherValue> ts2) {
		return equalsIgnoreUUID(ts1.entries, ts2.entries)
	}

	static boolean equalsIgnoreUUID(Collection<TimeBasedValue<WeatherValue>> c1,
			Collection<TimeBasedValue<WeatherValue>> c2) {
		if (c1 == null || c2 == null) return (c1 == null && c2 == null)
		if (c1.size() != c2.size()) return false
		for (TimeBasedValue<WeatherValue> value1 : c1) {
			if (!c2.stream().anyMatch({ value2 -> equalsIgnoreUUID(value1, value2) })) return false
		}
		return true
	}

	static boolean equalsIgnoreUUID(TimeBasedValue<WeatherValue> val1, TimeBasedValue<WeatherValue> val2) {
		if (val1.time != val2.time) return false

		def weatherValue1 = val1.value
		def weatherValue2 = val2.value

		return weatherValue1.solarIrradiance.directIrradiance.present == weatherValue2.solarIrradiance.directIrradiance.present && QuantityUtil.isEquivalentAbs(weatherValue1.solarIrradiance.directIrradiance.get(), weatherValue2.solarIrradiance.directIrradiance.get(), 1E-10) &&
				weatherValue1.solarIrradiance.diffuseIrradiance.present == weatherValue2.solarIrradiance.diffuseIrradiance.present && QuantityUtil.isEquivalentAbs(weatherValue1.solarIrradiance.diffuseIrradiance.get(), weatherValue2.solarIrradiance.diffuseIrradiance.get(), 1E-10) &&
				weatherValue1.temperature.temperature.present == weatherValue2.temperature.temperature.present && QuantityUtil.isEquivalentAbs(weatherValue1.temperature.temperature.get(), weatherValue2.temperature.temperature.get(), 1E-10) &&
				weatherValue1.wind.velocity.present == weatherValue2.wind.velocity.present && QuantityUtil.isEquivalentAbs(weatherValue1.wind.velocity.get(), weatherValue2.wind.velocity.get(), 1E-10) &&
				weatherValue1.wind.direction.present == weatherValue2.wind.direction.present && QuantityUtil.isEquivalentAbs(weatherValue1.wind.direction.get(), weatherValue2.wind.direction.get(), 1E-10)
	}
}
