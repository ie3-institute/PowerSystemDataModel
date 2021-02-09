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
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

class CsvTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {

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
