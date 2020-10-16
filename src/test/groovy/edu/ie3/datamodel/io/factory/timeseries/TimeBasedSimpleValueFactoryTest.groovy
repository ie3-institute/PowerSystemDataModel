/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.TimeUtil
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

import static edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory.*

class TimeBasedSimpleValueFactoryTest extends Specification {
	@Shared
	TimeUtil defaultTimeUtil

	def setupSpec() {
		defaultTimeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
	}

	def "The simple time based value factory provides correct fields"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(valueClass)
		def data = Mock(SimpleTimeBasedValueData)
		data.targetClass >> valueClass

		expect:
		factory.getFields(data) == expectedFields

		where:
		valueClass       || expectedFields
		EnergyPriceValue || [
			[
				TimeBasedSimpleValueFactory.UUID,
				TIME,
				PRICE] as Set
		]
	}

	def "The simple time based value factory builds correct energy price value"() {
		given:
		def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue.class)
		def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01 00:00:00")
		def data = new SimpleTimeBasedValueData([
			"uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
			"time": defaultTimeUtil.toString(time),
			"price": "52.4"
		], EnergyPriceValue.class)
		def expected = new TimeBasedValue(
				UUID.fromString("78ca078a-e6e9-4972-a58d-b2cadbc2df2c"),
				time,
				new EnergyPriceValue(Quantities.getQuantity(52.4, StandardUnits.ENERGY_PRICE))
				)

		expect:
		factory.buildModel(data) == expected
	}
}
