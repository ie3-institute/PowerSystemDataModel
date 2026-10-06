/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

class CylindricalStorageInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID busUuid = UUID.randomUUID()
  @Shared private def thermalBusInput = Mock(ThermalBusInput)

  @Shared private CylindricalStorageInputFactory inputFactory

  def setupSpec() {
    thermalBusInput.getUuid() >> busUuid

    inputFactory = new CylindricalStorageInputFactory(Collections.emptyMap(), map(thermalBusInput))
  }

  def "A CylindricalStorageInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [CylindricalStorageInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A CylindricalStorageInputFactory should parse a valid CylindricalStorageInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "TestID",
      "storageVolumeLvl" : "3",
      "inletTemp" : "4",
      "returnTemp" : "5",
      "c" : "6",
      "pThermalMax" : "7",
      "operator": "",
      "thermalBus": busUuid.toString()
    ]

    when:
    Try<CylindricalStorageInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == CylindricalStorageInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime == OperationTime.notLimited()
      operator == OperatorInput.NO_OPERATOR_ASSIGNED
      id == parameter["id"]
      thermalBus == thermalBusInput
      storageVolumeLvl == getQuant(parameter["storageVolumeLvl"], StandardUnits.VOLUME)
      inletTemp == getQuant(parameter["inletTemp"], StandardUnits.TEMPERATURE)
      returnTemp == getQuant(parameter["returnTemp"], StandardUnits.TEMPERATURE)
      c == getQuant(parameter["c"], StandardUnits.SPECIFIC_HEAT_CAPACITY)
      pThermalMax == getQuant(parameter["pThermalMax"], StandardUnits.ACTIVE_POWER_IN)
    }
  }
}
