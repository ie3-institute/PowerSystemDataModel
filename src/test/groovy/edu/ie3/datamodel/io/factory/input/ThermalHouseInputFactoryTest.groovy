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
import spock.lang.Shared
import spock.lang.Specification

class ThermalHouseInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID busUuid = UUID.randomUUID()
  @Shared private def thermalBusInput = Mock(ThermalBusInput)

  @Shared private ThermalHouseInputFactory inputFactory

  def setupSpec() {
    thermalBusInput.getUuid() >> busUuid

    inputFactory = new ThermalHouseInputFactory(map(), map(thermalBusInput))
  }

  def "A ThermalHouseInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [ThermalHouseInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A ThermalHouseInputFactory should parse a valid ThermalHouseInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "TestID",
      "ethLosses" : "3",
      "ethCapa" : "4",
      "targetTemperature" : "5",
      "upperTemperatureLimit": "6",
      "lowerTemperatureLimit": "7",
      "housingType" : "flat",
      "numberInhabitants" : "9",
      "thermalBus": busUuid.toString()

    ]

    when:
    Try<ThermalHouseInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == ThermalHouseInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime == OperationTime.notLimited()
      operator == OperatorInput.NO_OPERATOR_ASSIGNED
      id == parameter["id"]
      thermalBus == thermalBusInput
      ethLosses == getQuant(parameter["ethLosses"], StandardUnits.THERMAL_TRANSMISSION)
      ethCapa == getQuant(parameter["ethCapa"], StandardUnits.HEAT_CAPACITY)
      targetTemperature == getQuant(parameter["targetTemperature"], StandardUnits.TEMPERATURE)
      upperTemperatureLimit == getQuant(parameter["upperTemperatureLimit"], StandardUnits.TEMPERATURE)
      lowerTemperatureLimit == getQuant(parameter["lowerTemperatureLimit"], StandardUnits.TEMPERATURE)
      housingType == parameter["housingType"]
      numberInhabitants == parameter["numberInhabitants"].toDouble()
    }
  }
}
