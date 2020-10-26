/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import spock.lang.Specification

class Transformer3WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

	def "A Transformer3WTypeInputFactory should contain exactly the expected class for parsing"() {
		given:
		def typeInputFactory = new Transformer3WTypeInputFactory()
		def expectedClasses = [Transformer3WTypeInput]

		expect:
		typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A Transformer3WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
		given: "a system participant input type factory and model data"
		def typeInputFactory = new Transformer3WTypeInputFactory()
		Map<String, String> parameter = [
			"uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id":   	"blablub",
			"srateda":	"3",
			"sratedb":	"4",
			"sratedc":	"5",
			"vrateda":	"6",
			"vratedb":	"7",
			"vratedc":	"8",
			"rsca":	    "9",
			"rscb":	    "10",
			"rscc":	    "11",
			"xsca":	    "12",
			"xscb":	    "13",
			"xscc":	    "14",
			"gm":	    "15",
			"bm":	    "16",
			"dv":   	"17",
			"dphi":	    "18",
			"tapneutr":	"19",
			"tapmin":	"20",
			"tapmax":	"21"
		]
		def typeInputClass = Transformer3WTypeInput

		when:
		Optional<Transformer3WTypeInput> typeInput = typeInputFactory.get(new SimpleEntityData(parameter, typeInputClass))

		then:
		typeInput.present
		typeInput.get().getClass() == typeInputClass

		typeInput.get().with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert id == parameter["id"]
			assert sRatedA == getQuant(parameter["srateda"], StandardUnits.S_RATED)
			assert sRatedB == getQuant(parameter["sratedb"], StandardUnits.S_RATED)
			assert sRatedC == getQuant(parameter["sratedc"], StandardUnits.S_RATED)
			assert vRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
			assert vRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
			assert vRatedC == getQuant(parameter["vratedc"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
			assert rScA == getQuant(parameter["rsca"], StandardUnits.IMPEDANCE)
			assert rScB == getQuant(parameter["rscb"], StandardUnits.IMPEDANCE)
			assert rScC == getQuant(parameter["rscc"], StandardUnits.IMPEDANCE)
			assert xScA == getQuant(parameter["xsca"], StandardUnits.IMPEDANCE)
			assert xScB == getQuant(parameter["xscb"], StandardUnits.IMPEDANCE)
			assert xScC == getQuant(parameter["xscc"], StandardUnits.IMPEDANCE)
			assert gM == getQuant(parameter["gm"], StandardUnits.ADMITTANCE)
			assert bM == getQuant(parameter["bm"], StandardUnits.ADMITTANCE)
			assert dV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
			assert dPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
			assert tapNeutr == Integer.parseInt(parameter["tapneutr"])
			assert tapMin == Integer.parseInt(parameter["tapmin"])
			assert tapMax == Integer.parseInt(parameter["tapmax"])
		}
	}
}