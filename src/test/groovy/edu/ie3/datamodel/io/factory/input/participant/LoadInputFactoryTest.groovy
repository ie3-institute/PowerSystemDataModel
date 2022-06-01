/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class LoadInputFactoryTest extends Specification implements FactoryTestHelper {
	def "A LoadInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new LoadInputFactory()
		def expectedClasses = [LoadInput]

		expect:
		inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A LoadInputFactory should parse a valid LoadInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new LoadInputFactory()
		Map<String, String> parameter = [
			"uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"                 : "TestID",
			"qcharacteristics"   : "cosPhiFixed:{(0.0,1.0)}",
			"loadprofile": "G-4",
			"dsm"                : "true",
			"econsannual"        : "3",
			"srated"             : "4",
			"cosphirated"        : "5"
		]
		def inputClass = LoadInput
		def nodeInput = Mock(NodeInput)

		when:
		Optional<LoadInput> input = inputFactory.get(
				new NodeAssetInputEntityData(parameter, inputClass, nodeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((LoadInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
			assert node == nodeInput
			assert qCharacteristics.with {
				assert uuid != null
				assert points == Collections.unmodifiableSortedSet([
					new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
				] as TreeSet)
			}
			assert loadProfile == BdewStandardLoadProfile.G4
			assert dsm
			assert eConsAnnual == getQuant(parameter["econsannual"], StandardUnits.ENERGY_IN)
			assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
			assert cosPhiRated == Double.parseDouble(parameter["cosphirated"])
		}
	}
}
