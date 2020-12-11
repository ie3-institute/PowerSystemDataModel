/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.quantities.QuantityUtil

import javax.measure.Quantity

trait WeatherSourceTestHelper {

	static boolean equalsIgnoreUUID(IndividualTimeSeries<WeatherValue> ts1,
			IndividualTimeSeries<WeatherValue> ts2) {
		return equalsIgnoreUUID(ts1.entries, ts2.entries)
	}

	static boolean equalsIgnoreUUID(Collection<TimeBasedValue<WeatherValue>> c1,
			Collection<TimeBasedValue<WeatherValue>> c2) {
		if(c1 == null || c2 == null) return (c1 == null && c2 == null)
		if(c1.size() != c2.size()) return false;
		for(TimeBasedValue<WeatherValue> value1 : c1){
			if(!c2.stream().anyMatch({value2 -> equalsIgnoreUUID(value1, value2)})) return false;
		}
		return true;
	}

	static boolean equalsIgnoreUUID(TimeBasedValue<WeatherValue> val1, TimeBasedValue<WeatherValue> val2) {
		if(val1.time != val2.time) return false

		def weatherValue1 = val1.value
		def weatherValue2 = val2.value
		try {
			return weatherValue1.irradiation.directIrradiation.isPresent() == weatherValue2.irradiation.directIrradiation.isPresent() && QuantityUtil.isEquivalentAbs(weatherValue1.irradiation.directIrradiation.get(), weatherValue2.irradiation.directIrradiation.get(), 1E-10) &&
					weatherValue1.irradiation.diffuseIrradiation.isPresent() == weatherValue2.irradiation.diffuseIrradiation.isPresent() && QuantityUtil.isEquivalentAbs(weatherValue1.irradiation.diffuseIrradiation.get(), weatherValue2.irradiation.diffuseIrradiation.get(), 1E-10) &&
					weatherValue1.temperature.temperature.isPresent() == weatherValue2.temperature.temperature.isPresent() && QuantityUtil.isEquivalentAbs(weatherValue1.temperature.temperature.get(), weatherValue2.temperature.temperature.get(), 1E-10) &&
					weatherValue1.wind.velocity.isPresent() == weatherValue2.wind.velocity.isPresent() && QuantityUtil.isEquivalentAbs(weatherValue1.wind.velocity.get(), weatherValue2.wind.velocity.get(), 1E-10) &&
					weatherValue1.wind.direction.isPresent() == weatherValue2.wind.direction.isPresent() && QuantityUtil.isEquivalentAbs(weatherValue1.wind.direction.get(), weatherValue2.wind.direction.get(), 1E-10)
		} catch (NullPointerException npe) {
			return false
		}
	}
}
