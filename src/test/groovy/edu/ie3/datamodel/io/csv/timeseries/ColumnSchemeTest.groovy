/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv.timeseries

import static edu.ie3.datamodel.io.csv.timeseries.ColumnScheme.*

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
		def actual = parse(invalidColumnScheme)

		then:
		!actual.present
	}

	def "All known column schemes get parsed correctly"() {
		when:
		def actual = parse(input)

		then:
		actual.present
		actual.get() == expectedColumnScheme

		where:
		input || expectedColumnScheme
		"c" || ENERGY_PRICE
		"p" || ACTIVE_POWER
		"pq" || APPARENT_POWER
		"h" || HEAT_DEMAND
		"ph" || ACTIVE_POWER_AND_HEAT_DEMAND
		"pqh" || APPARENT_POWER_AND_HEAT_DEMAND
		"weather" || WEATHER
	}

	def "Correct value classes are returned for all column schemes"() {
		when:
		def actual = columnScheme.valueClass

		then:
		actual == expectedValueClass

		where:
		columnScheme || expectedValueClass
		ENERGY_PRICE || EnergyPriceValue
		ACTIVE_POWER || PValue
		APPARENT_POWER || SValue
		HEAT_DEMAND || HeatDemandValue
		ACTIVE_POWER_AND_HEAT_DEMAND || HeatAndPValue
		APPARENT_POWER_AND_HEAT_DEMAND || HeatAndSValue
		WEATHER || WeatherValue
	}
}
