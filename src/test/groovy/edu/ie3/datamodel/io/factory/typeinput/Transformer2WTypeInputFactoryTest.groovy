/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class Transformer2WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

	def "A Transformer2WTypeInputFactory should contain exactly the expected class for parsing"() {
		given:
		def typeInputFactory = new Transformer2WTypeInputFactory()
		def expectedClasses = [Transformer2WTypeInput]

		expect:
		typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A Transformer2WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
		given: "a system participant input type factory and model data"
		def typeInputFactory = new Transformer2WTypeInputFactory()
		Map<String, String> parameter = [
			"uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id":       "blablub",
			"rsc":      "3",
			"xsc":      "4",
			"srated":   "5",
			"vrateda":  "6",
			"vratedb":  "7",
			"gm":       "8",
			"bm":       "9",
			"dv":       "10",
			"dphi":     "11",
			"tapside":  "1",
			"tapneutr": "12",
			"tapmin":   "13",
			"tapmax":   "14"
		]
		def typeInputClass = Transformer2WTypeInput

		when:
		Optional<Transformer2WTypeInput> typeInput = typeInputFactory.get(new SimpleEntityData(parameter, typeInputClass))

		then:
		typeInput.present
		typeInput.get().getClass() == typeInputClass

		typeInput.get().with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert id == parameter["id"]
			assert rSc == getQuant(parameter["rsc"], StandardUnits.IMPEDANCE)
			assert xSc == getQuant(parameter["xsc"], StandardUnits.IMPEDANCE)
			assert sRated == getQuant(parameter["srated"], StandardUnits.S_RATED)
			assert vRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
			assert vRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
			assert gM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
			assert bM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
			assert dV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
			assert dPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
			assert tapSide == (parameter["tapside"].trim() == "1") || parameter["tapside"].trim() == "true"
			assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
			assert tapMin == Integer.parseInt(parameter["tapmin"])
			assert tapMax == Integer.parseInt(parameter["tapmax"])
		}
	}
}
