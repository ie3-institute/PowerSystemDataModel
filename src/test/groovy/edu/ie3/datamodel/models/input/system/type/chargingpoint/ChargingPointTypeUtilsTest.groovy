/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type.chargingpoint

import edu.ie3.datamodel.exceptions.ChargingPointTypeException
import edu.ie3.datamodel.models.ElectricCurrentType
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import static edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils.*
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE

/**
 * //ToDo: Class Description
 *
 * @version 0.1* @since 25.07.20
 */
class ChargingPointTypeUtilsTest extends Specification {

	def "The ChargingPointTypeUtils should throw an exception on instantiation"() {
		when:
		new ChargingPointTypeUtils()

		then:
		IllegalStateException ex = thrown()
		ex.message == "This is a factory class. Don't try to instantiate it."
	}

	def "The ChargingPointTypeUtils should parse and return valid charging point type strings as expected"() {
		given:
		ChargingPointType actual = parse(parsableString)

		expect:
		actual == expectedObj
		actual.toString() == expectedString

		where:
		parsableString                 || expectedObj                                                                                               || expectedString
		"household"                    || HouseholdSocket                                                                                           || "HouseholdSocket"
		"HouseholdSocket"              || HouseholdSocket                                                                                           || "HouseholdSocket"
		"BlueHouseholdSocket"          || BlueHouseholdSocket                                                                                       || "BlueHouseholdSocket"
		"Cee16ASocket"                 || Cee16ASocket                                                                                              || "Cee16ASocket"
		"cee32"                        || Cee32ASocket                                                                                              || "Cee32ASocket"
		"CEE63"                        || Cee63ASocket                                                                                              || "Cee63ASocket"
		"csT1"                         || ChargingStationType1                                                                                      || "ChargingStationType1"
		"stationtype2"                 || ChargingStationType2                                                                                      || "ChargingStationType2"
		"ChargingStationCcsComboType1" || ChargingStationCcsComboType1                                                                              || "ChargingStationCcsComboType1"
		"csccs2"                       || ChargingStationCcsComboType2                                                                              || "ChargingStationCcsComboType2"
		"TeslaSuperChargerV1"          || TeslaSuperChargerV1                                                                                       || "TeslaSuperChargerV1"
		"tesla2"                       || TeslaSuperChargerV2                                                                                       || "TeslaSuperChargerV2"
		"supercharger3"                || TeslaSuperChargerV3                                                                                       || "TeslaSuperChargerV3"
		"FastCharger(50|DC)"           || new ChargingPointType("FastCharger", Quantities.getQuantity(50d, KILOVOLTAMPERE), ElectricCurrentType.DC) || "FastCharger(50.0|DC)"
		"household(1.8|DC)"            || new ChargingPointType("household", Quantities.getQuantity(1.8d, KILOVOLTAMPERE), ElectricCurrentType.DC)  || "household(1.8|DC)"
		"Household(2.3|AC)"            || HouseholdSocket                                                                                           || "HouseholdSocket"
		"household(2.3|AC)"            || HouseholdSocket                                                                                           || "HouseholdSocket"
	}

	def "The ChargingPointTypeUtils should throw exceptions as expected when invalid charging point type strings are provided"() {
		when:
		parse(invalidString)

		then:
		ChargingPointTypeException ex = thrown()
		ex.message == expectedExceptionMsg

		where:

		invalidString         || expectedExceptionMsg
		""                    || "Provided charging point type string '' is neither a valid custom type string nor can a common charging point type with id '' be found! Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' (e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."
		"DCFast"              || "Provided charging point type string 'DCFast' is neither a valid custom type string nor can a common charging point type with id 'DCFast' be found! Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' (e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."
		"FastCharger(50|PC)"  || "Provided charging point type string 'FastCharger(50|PC)' is neither a valid custom type string nor can a common charging point type with id 'FastCharger(50|PC)' be found! Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' (e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."
		"FastCharger(AC|2.4)" || "Provided charging point type string 'FastCharger(AC|2.4)' is neither a valid custom type string nor can a common charging point type with id 'FastCharger(AC|2.4)' be found! Please either provide a valid custom string in the format '<Name>(<kVA Value>|<AC|DC>)' (e.g. 'FastCharger(50|DC)') or a common type id (see docs for all available common types)."
	}
}
