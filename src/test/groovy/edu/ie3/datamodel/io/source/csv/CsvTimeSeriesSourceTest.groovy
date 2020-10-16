/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.connectors.TimeSeriesReadingData
import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.value.IrradiationValue
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.datamodel.models.value.WindValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.charset.StandardCharsets

import static edu.ie3.datamodel.models.StandardUnits.*

class CsvTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {
	def "The csv time series source is able to provide a valid time series mapping from files"() {
		given:
		def coordinateSource = Mock(IdCoordinateSource)
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def expectedMapping = [
			new TimeSeriesMapping.Entry(UUID.fromString("58167015-d760-4f90-8109-f2ebd94cda91"), UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"), UUID.fromString("67600124-2475-4a62-a410-0dd6eabb9441")),
			new TimeSeriesMapping.Entry(UUID.fromString("9a9ebfda-dc26-4a40-b9ca-25cd42f6cc3f"), UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8"), UUID.fromString("05a25fe7-11e5-4732-92b0-490cec171c78")),
			new TimeSeriesMapping.Entry(UUID.fromString("9c1c53ea-e575-41a2-a373-a8b2d3ed2c39"), UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c"), UUID.fromString("05a25fe7-11e5-4732-92b0-490cec171c78"))
		]

		when:
		def mappingEntries = source.mapping

		then:
		mappingEntries.size() == expectedMapping.size()

		expectedMapping.stream().allMatch { mappingEntries.contains(it) }
	}

	def "The csv time series source is able to build a single WeatherValue from field to value mapping"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def fieldToValues = [
			"uuid": "71a79f59-eebf-40c1-8358-ba7414077d57",
			"time": "2020-10-16T12:40:42Z",
			"coordinate": "5",
			"directirradiation": "1.234",
			"diffuseirradiation": "5.678",
			"temperature": "9.1011",
			"windvelocity": "12.1314",
			"winddirection": "15.1617"
		]
		def expectedValue = new TimeBasedValue(
				UUID.fromString("71a79f59-eebf-40c1-8358-ba7414077d57"),
				TimeUtil.withDefaults.toZonedDateTime("2020-10-16 12:40:42"),
				new WeatherValue(
				defaultCoordinate,
				new IrradiationValue(
				Quantities.getQuantity(1.234, IRRADIATION),
				Quantities.getQuantity(5.678, IRRADIATION)
				),
				new TemperatureValue(
				Quantities.getQuantity(9.1011, TEMPERATURE)
				),
				new WindValue(
				Quantities.getQuantity(12.1314, WIND_DIRECTION),
				Quantities.getQuantity(15.1617, WIND_VELOCITY)
				)
				)
				)

		when:
		def actual = source.buildWeatherValue(fieldToValues)

		then:
		actual.present
		actual.get() == expectedValue
	}

	def "The csv time series source returns no WeatherValue, if the coordinate field is empty"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def fieldToValues = [
			"uuid": "71a79f59-eebf-40c1-8358-ba7414077d57",
			"time": "2020-10-16T12:40:42Z",
			"coordinate": "",
			"directirradiation": "1.234",
			"diffuseirradiation": "5.678",
			"temperature": "9.1011",
			"windvelocity": "12.1314",
			"winddirection": "15.1617"
		]

		when:
		def actual = source.buildWeatherValue(fieldToValues)

		then:
		!actual.present
	}

	def "The csv time series source returns no WeatherValue, if the coordinate field is missing"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def fieldToValues = [
			"uuid": "71a79f59-eebf-40c1-8358-ba7414077d57",
			"time": "2020-10-16T12:40:42Z",
			"directirradiation": "1.234",
			"diffuseirradiation": "5.678",
			"temperature": "9.1011",
			"windvelocity": "12.1314",
			"winddirection": "15.1617"
		]

		when:
		def actual = source.buildWeatherValue(fieldToValues)

		then:
		!actual.present
	}

	def "The csv time series source returns no WeatherValue, if the coordinate cannot be obtained"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def fieldToValues = [
			"uuid": "71a79f59-eebf-40c1-8358-ba7414077d57",
			"time": "2020-10-16T12:40:42Z",
			"coordinate": "6",
			"directirradiation": "1.234",
			"diffuseirradiation": "5.678",
			"temperature": "9.1011",
			"windvelocity": "12.1314",
			"winddirection": "15.1617"
		]

		when:
		def actual = source.buildWeatherValue(fieldToValues)

		then:
		!actual.present
	}

	def "The csv time series source is able to provide an individual time series from given field to object function"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", participantsFolderPath, new FileNamingStrategy(), coordinateSource)
		def weatherValueFunction = {fieldToValues -> source.buildWeatherValue(fieldToValues)}
		def tsUuid = UUID.fromString("8bc9120d-fb9b-4484-b4e3-0cdadf0feea9")
		def filePath = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_weather_8bc9120d-fb9b-4484-b4e3-0cdadf0feea9.csv").toURI())
		def readingData = new TimeSeriesReadingData(
				tsUuid,
				ColumnScheme.WEATHER,
				new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8), 16384)
				)
		def expected = new IndividualTimeSeries(
				tsUuid,
				[
					new TimeBasedValue(
					UUID.fromString("71a79f59-eebf-40c1-8358-ba7414077d57"),
					TimeUtil.withDefaults.toZonedDateTime("2020-10-16 12:40:42"),
					new WeatherValue(
					defaultCoordinate,
					new IrradiationValue(
					Quantities.getQuantity(1.234, IRRADIATION),
					Quantities.getQuantity(5.678, IRRADIATION)
					),
					new TemperatureValue(
					Quantities.getQuantity(9.1011, TEMPERATURE)
					),
					new WindValue(
					Quantities.getQuantity(12.1314, WIND_DIRECTION),
					Quantities.getQuantity(15.1617, WIND_VELOCITY)
					)
					)
					),
					new TimeBasedValue(
					UUID.fromString("e66ea05d-8968-423a-903a-e4cf22ab995a"),
					TimeUtil.withDefaults.toZonedDateTime("2020-10-16 13:20:42"),
					new WeatherValue(
					defaultCoordinate,
					new IrradiationValue(
					Quantities.getQuantity(4.321, IRRADIATION),
					Quantities.getQuantity(8.765, IRRADIATION)
					),
					new TemperatureValue(
					Quantities.getQuantity(11.109, TEMPERATURE)
					),
					new WindValue(
					Quantities.getQuantity(14.1312, WIND_DIRECTION),
					Quantities.getQuantity(17.1615, WIND_VELOCITY)
					)
					)
					)
				] as Set
				)

		when:
		def actual = source.buildIndividualTimeSeries(readingData, weatherValueFunction)

		then:
		actual.with {
			assert uuid == tsUuid
			assert entries.size() == expected.entries.size()
			assert entries.containsAll(expected.entries)
		}
		/* Close the reader */
		readingData.getReader().close()
	}
}
