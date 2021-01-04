/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.datamodel.models.StandardUnits.*

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

class CsvTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {
	def "The csv time series source is able to provide a valid time series mapping from files"() {
		given:
		def source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy())
		def expectedMapping = [
			new TimeSeriesMapping.Entry(UUID.fromString("58167015-d760-4f90-8109-f2ebd94cda91"), UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"), UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")),
			new TimeSeriesMapping.Entry(UUID.fromString("9a9ebfda-dc26-4a40-b9ca-25cd42f6cc3f"), UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8"), UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")),
			new TimeSeriesMapping.Entry(UUID.fromString("9c1c53ea-e575-41a2-a373-a8b2d3ed2c39"), UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c"), UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"))
		]

		when:
		def mappingEntries = source.mapping

		then:
		mappingEntries.size() == expectedMapping.size()

		expectedMapping.stream().allMatch { mappingEntries.contains(it) }
	}

	def "The csv time series source is able to build time based values from simple data"() {
		given:
		def defaultCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))
		def coordinateSource = Mock(IdCoordinateSource)
		coordinateSource.getCoordinate(5) >> defaultCoordinate
		def source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy())
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
		def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01 00:00:00")
		def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		def fieldToValue = [
			"uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
			"time": timeUtil.toString(time),
			"price": "52.4"
		]
		def expected = new TimeBasedValue(
				UUID.fromString("78ca078a-e6e9-4972-a58d-b2cadbc2df2c"),
				time,
				new EnergyPriceValue(Quantities.getQuantity(52.4, ENERGY_PRICE))
				)

		when:
		def actual = source.buildTimeBasedValue(fieldToValue, EnergyPriceValue, factory)

		then:
		actual.present
		actual.get() == expected
	}
}
