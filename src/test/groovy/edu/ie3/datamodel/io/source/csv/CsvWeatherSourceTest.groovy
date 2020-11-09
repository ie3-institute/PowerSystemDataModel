/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.WeatherTestData
import edu.ie3.test.helper.WeatherSourceTestHelper
import edu.ie3.util.interval.ClosedInterval
import org.locationtech.jts.geom.Point
import spock.lang.Shared
import spock.lang.Specification

class CsvWeatherSourceTest extends Specification implements CsvTestDataMeta, WeatherSourceTestHelper {

	@Shared
	CsvWeatherSource source

	@Shared
	IdCoordinateSource coordinateSource

	def setupSpec() {
		coordinateSource = WeatherTestData.coordinateSource
		source = new CsvWeatherSource(";", timeSeriesFolderPath, new FileNamingStrategy(), coordinateSource)
	}

	def "A CsvWeatherSource can read and correctly parse a single value for a specific date and coordinate"() {
		given:
		def expectedTimeBasedValue = new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193186_15H)
		when:
		def optTimeBasedValue = source.getWeather(WeatherTestData.TIME_15H, WeatherTestData.COORDINATE_193186)
		then:
		optTimeBasedValue.present
		equalsIgnoreUUID(optTimeBasedValue.get(), expectedTimeBasedValue )
	}

	def "A CsvWeatherSource can read multiple time series values for multiple coordinates"() {
		given:
		def coordinates = [
			WeatherTestData.COORDINATE_193186,
			WeatherTestData.COORDINATE_193187
		]
		def timeInterval = new ClosedInterval(WeatherTestData.TIME_16H, WeatherTestData.TIME_17H)
		def timeSeries193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(WeatherTestData.TIME_17H, WeatherTestData.WEATHER_VALUE_193186_17H)]
				as Set<TimeBasedValue>)
		def timeSeries193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193187_16H)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval, coordinates)
		then:
		coordinateToTimeSeries.keySet().size() == 2
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193186), timeSeries193186)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193187), timeSeries193187)
	}



	def "A CsvWeatherSource can read all weather data in a given time interval"() {
		given:
		def timeInterval = new ClosedInterval(WeatherTestData.TIME_15H, WeatherTestData.TIME_17H)
		def timeSeries193186 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193186_15H),
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193186_16H),
					new TimeBasedValue(WeatherTestData.TIME_17H, WeatherTestData.WEATHER_VALUE_193186_17H)] as Set<TimeBasedValue>)
		def timeSeries193187 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193187_15H),
					new TimeBasedValue(WeatherTestData.TIME_16H, WeatherTestData.WEATHER_VALUE_193187_16H)] as Set<TimeBasedValue>)
		def timeSeries193188 = new IndividualTimeSeries(null,
				[
					new TimeBasedValue(WeatherTestData.TIME_15H, WeatherTestData.WEATHER_VALUE_193188_15H)] as Set<TimeBasedValue>)
		when:
		Map<Point, IndividualTimeSeries<WeatherValue>> coordinateToTimeSeries = source.getWeather(timeInterval)
		then:
		coordinateToTimeSeries.keySet().size() == 3
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193186).entries, timeSeries193186.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193187).entries, timeSeries193187.entries)
		equalsIgnoreUUID(coordinateToTimeSeries.get(WeatherTestData.COORDINATE_193188).entries, timeSeries193188.entries)
	}
}
