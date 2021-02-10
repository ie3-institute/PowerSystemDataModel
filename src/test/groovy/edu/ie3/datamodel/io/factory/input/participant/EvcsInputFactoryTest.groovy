/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import java.time.ZonedDateTime
/**
 * Testing EvcsInputFactory
 *
 * @version 0.1* @since 26.07.20
 */
class EvcsInputFactoryTest extends Specification implements FactoryTestHelper {

	def "A EvcsInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new EvcsInputFactory()
		def expectedClasses = [EvcsInput]

		expect:
		inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A EvcsInputFactory should parse a valid EvcsInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new EvcsInputFactory()
		Map<String, String> parameter = [
				"uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
				"operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
				"operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
				"id"              : "TestID",
				"qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
				"type"            : "Household",
				"chargingpoints"  : "4",
				"cosphirated"     : "0.95",
		]
		def inputClass = EvcsInput
		def nodeInput = Mock(NodeInput)
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<EvcsInput> input = inputFactory.get(
				new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((EvcsInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == operatorInput
			assert id == parameter["id"]
			assert node == nodeInput
			assert qCharacteristics.with {
				assert uuid != null
				assert points == Collections.unmodifiableSortedSet([
					new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PowerSystemUnits.PU), Quantities.getQuantity(1d, PowerSystemUnits.PU))
				] as TreeSet)
			}
			assert type == ChargingPointTypeUtils.HouseholdSocket
			assert chargingPoints == Integer.parseInt(parameter["chargingpoints"])
			assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
		}
	}

	def "A EvcsInputFactory should fail when passing an invalid ChargingPointType"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new EvcsInputFactory()
		Map<String, String> parameter = [
				"uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
				"operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
				"operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
				"id"              : "TestID",
				"qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
				"type"            : "-- invalid --",
				"chargingpoints"  : "4",
				"cosphirated"     : "0.95",
		]
		def inputClass = EvcsInput
		def nodeInput = Mock(NodeInput)
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<EvcsInput> input = inputFactory.get(
				new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

		then:
		// FactoryException is caught in Factory.java. We get an empty Option back
		!input.present
	}
}
