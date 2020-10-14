/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.WeatherTestData
import edu.ie3.util.TimeUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities



class TimeBasedWeatherValueFactoryTest extends Specification {

	def "A TimeBasedWeatherValueFactory should be able to create time series with missing values"() {
		given:
		def factory = new TimeBasedWeatherValueFactory("yyyy-MM-dd HH:mm:ss")
		def coordinate = WeatherTestData.coordinate_193186
		def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01 00:00:00")

		Map<String, String> parameter = [
			"uuid"               : "980f7714-8def-479f-baae-4deed6c8d6d1",
			"time"               : TimeUtil.withDefaults.toString(time),
			"diffuse_irradiation": "282.671997070312",
			"direct_irradiation" : "286.872985839844",
			"temperature"        : "",
			"wind_direction"     : "0",
			"wind_velocity"      : "1.66103506088257"
		]

		def data = new TimeBasedWeatherValueData(parameter, coordinate)

		def expectedResults = new TimeBasedValue(UUID.fromString("980f7714-8def-479f-baae-4deed6c8d6d1"),
				time, new WeatherValue(coordinate,
				Quantities.getQuantity(286.872985839844d, StandardUnits.IRRADIATION),
				Quantities.getQuantity(282.671997070312d, StandardUnits.IRRADIATION),
				null,
				Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
				Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY)))

		when:
		def model = factory.buildModel(data)

		then:
		model == expectedResults
	}

	def "A TimeBasedWeatherValueFactory should be able to create time series values"() {
		given:
		def factory = new TimeBasedWeatherValueFactory("yyyy-MM-dd HH:mm:ss")
		def coordinate = WeatherTestData.coordinate_193186
		def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01 00:00:00")

		Map<String, String> parameter = [
			"time"               : TimeUtil.withDefaults.toString(time),
			"uuid"               : "980f7714-8def-479f-baae-4deed6c8d6d1",
			"diffuse_irradiation": "282.671997070312",
			"direct_irradiation" : "286.872985839844",
			"temperature"        : "278.019012451172",
			"wind_direction"     : "0",
			"wind_velocity"      : "1.66103506088257"
		]

		def data = new TimeBasedWeatherValueData(parameter, coordinate)

		def expectedResults = new TimeBasedValue(UUID.fromString("980f7714-8def-479f-baae-4deed6c8d6d1"),
				time, new WeatherValue(coordinate,
				Quantities.getQuantity(286.872985839844d, StandardUnits.IRRADIATION),
				Quantities.getQuantity(282.671997070312d, StandardUnits.IRRADIATION),
				Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE),
				Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
				Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY)))

		when:
		def model = factory.buildModel(data)

		then:
		model == expectedResults
	}
}
