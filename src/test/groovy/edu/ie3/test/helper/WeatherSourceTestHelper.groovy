/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue

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
		return val1.getValue() == val2.getValue()
	}
}
