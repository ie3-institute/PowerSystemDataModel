/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv.timeseries

import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.datamodel.models.value.HeatAndPValue
import edu.ie3.datamodel.models.value.HeatAndSValue
import edu.ie3.datamodel.models.value.HeatDemandValue
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.SValue
import edu.ie3.datamodel.models.value.WeatherValue
import spock.lang.Specification

class ColumnSchemeTest extends Specification {
	def "An unknown column scheme gets not parsed"() {
		given:
		def invalidColumnScheme = "what's this"

		when:
		def actual = ColumnScheme.parse(invalidColumnScheme)

		then:
		!actual.present
	}

	def "All known column schemes get parsed correctly"() {
		when:
		def actual = ColumnScheme.parse(input)

		then:
		actual.present
		actual.get() == expectedColumnScheme

		where:
		input || expectedColumnScheme
		"c" || ColumnScheme.ENERGY_PRICE
		"p" || ColumnScheme.ACTIVE_POWER
		"pq" || ColumnScheme.APPARENT_POWER
		"h" || ColumnScheme.HEAT_DEMAND
		"ph" || ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND
		"pqh" || ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND
		"weather" || ColumnScheme.WEATHER
	}

	def "Correct value classes are returned for all column schemes"() {
		when:
		def actual = columnScheme.getValueClass()

		then:
		actual == expectedValueClass

		where:
		columnScheme || expectedValueClass
		ColumnScheme.ENERGY_PRICE || EnergyPriceValue.class
		ColumnScheme.ACTIVE_POWER || PValue.class
		ColumnScheme.APPARENT_POWER || SValue.class
		ColumnScheme.HEAT_DEMAND || HeatDemandValue.class
		ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND || HeatAndPValue.class
		ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND || HeatAndSValue.class
		ColumnScheme.WEATHER || WeatherValue.class
	}
}
