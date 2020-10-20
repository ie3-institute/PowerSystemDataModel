/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.datamodel.models.StandardUnits.IRRADIATION
import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.datamodel.models.StandardUnits.WIND_DIRECTION
import static edu.ie3.datamodel.models.StandardUnits.WIND_VELOCITY

import edu.ie3.datamodel.io.connectors.TimeSeriesReadingData
import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.value.IrradiationValue
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.datamodel.models.value.WindValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.charset.StandardCharsets

class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {
	@Shared
	Point defaultCoordinate

	@Shared
	IdCoordinateSource coordinateSource

	@Shared
	CsvTimeSeriesSource source

	def setupSpec() {
		defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
	}

	def setup() {
		source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy(), coordinateSource)
	}

	def "The csv time series source is able to provide an individual time series from given field to object function"() {
		given:
		def weatherValueFunction = { fieldToValues -> source.buildWeatherValue(fieldToValues) }
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
		readingData.reader.close()
	}

	def "The csv time series source is able to acquire all time series of a given type"() {
		given:
		def filePath0 = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_pq_1061af70-1c03-46e1-b960-940b956c429f.csv").toURI())
		def tsUuid0 = UUID.fromString("1061af70-1c03-46e1-b960-940b956c429f")
		def filePath1 = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26.csv").toURI())
		def tsUuid1 = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		def readingData = [
			new TimeSeriesReadingData(
			tsUuid0,
			ColumnScheme.APPARENT_POWER,
			new BufferedReader(
			new InputStreamReader(new FileInputStream(filePath0), StandardCharsets.UTF_8), 16384)
			),
			new TimeSeriesReadingData(
			tsUuid1,
			ColumnScheme.APPARENT_POWER,
			new BufferedReader(
			new InputStreamReader(new FileInputStream(filePath1), StandardCharsets.UTF_8), 16384)
			)
		] as Set
		def factory = new TimeBasedSimpleValueFactory<>(SValue)

		when:
		def actual = source.readIn(readingData, SValue, factory)

		then:
		Objects.nonNull(actual)
		actual.size() == 2
	}

	def "The csv time series source is able to acquire all time series"() {
		when:
		def actual = source.timeSeries

		then:
		Objects.nonNull(actual)
		actual.with {
			assert weather.size() == 1
			assert energyPrice.size() == 1
			assert heatAndApparentPower.size() == 1
			assert heatAndActivePower.size() == 1
			assert heat.size() == 1
			assert apparentPower.size() == 2
			assert activePower.size() == 1
		}
	}

	def "The csv time series source is able to provide either mapping an time series, that can be put together"() {
		when:
		def mappingEntries = source.mapping
		def timeSeries = source.timeSeries
		def mapping = new TimeSeriesMapping(mappingEntries, timeSeries.all)

		then:
		mapping.with {
			assert it.mapping.size() == 3
			assert it.mapping.containsKey(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"))
			assert it.mapping.get(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409")).uuid == UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")
			assert it.mapping.containsKey(UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8"))
			assert it.mapping.get(UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")).uuid == UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
			assert it.mapping.containsKey(UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c"))
			assert it.mapping.get(UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c")).uuid == UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		}
	}
}
