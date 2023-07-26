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
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class ThermalHouseInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A ThermalHouseInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new ThermalHouseInputFactory()
    def expectedClasses = [ThermalHouseInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A ThermalHouseInputFactory should parse a valid ThermalHouseInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new ThermalHouseInputFactory()
    Map<String, String> parameter = [
      "uuid"                 : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                   : "TestID",
      "ethlosses"            : "3",
      "ethcapa"              : "4",
      "targetTemperature"    : "5",
      "upperTemperatureLimit": "6",
      "lowerTemperatureLimit": "7"
    ]
    def inputClass = ThermalHouseInput
    def thermalBusInput = Mock(ThermalBusInput)

    when:
    Try<ThermalHouseInput, FactoryException> input = inputFactory.get(new ThermalUnitInputEntityData(parameter, inputClass, thermalBusInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
      assert id == parameter["id"]
      assert thermalBus == thermalBusInput
      assert ethLosses == getQuant(parameter["ethlosses"], StandardUnits.THERMAL_TRANSMISSION)
      assert ethCapa == getQuant(parameter["ethcapa"], StandardUnits.HEAT_CAPACITY)
      assert targetTemperature == getQuant(parameter["targetTemperature"], StandardUnits.TEMPERATURE)
      assert upperTemperatureLimit == getQuant(parameter["upperTemperatureLimit"], StandardUnits.TEMPERATURE)
      assert lowerTemperatureLimit == getQuant(parameter["lowerTemperatureLimit"], StandardUnits.TEMPERATURE)
    }
  }
}
