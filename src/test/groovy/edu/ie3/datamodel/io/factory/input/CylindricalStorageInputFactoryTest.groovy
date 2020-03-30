/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class CylindricalStorageInputFactoryTest  extends Specification implements FactoryTestHelper {
	def "A CylindricalStorageInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new CylindricalStorageInputFactory()
		def expectedClasses = [CylindricalStorageInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A CylindricalStorageInputFactory should parse a valid CylindricalStorageInput correctly"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new CylindricalStorageInputFactory()
		Map<String, String> parameter = [
			"uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"                 : "TestID",
			"storagevolumelvl"   : "3",
			"storagevolumelvlmin": "4",
			"inlettemp"          : "5",
			"returntemp"         : "6",
			"c"                  : "7"
		]
		def inputClass = CylindricalStorageInput
		def thermalBusInput = Mock(ThermalBusInput)

		when:
		Optional<CylindricalStorageInput> input = inputFactory.getEntity(new ThermalUnitInputEntityData(parameter, inputClass, thermalBusInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((CylindricalStorageInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == null
			assert id == parameter["id"]
			assert bus == thermalBusInput
			assert storageVolumeLvl == getQuant(parameter["storagevolumelvl"], StandardUnits.VOLUME)
			assert storageVolumeLvlMin == getQuant(parameter["storagevolumelvlmin"], StandardUnits.VOLUME)
			assert inletTemp == getQuant(parameter["inlettemp"], StandardUnits.TEMPERATURE)
			assert returnTemp == getQuant(parameter["returntemp"], StandardUnits.TEMPERATURE)
			assert c == getQuant(parameter["c"], StandardUnits.SPECIFIC_HEAT_CAPACITY)
		}
	}
}
