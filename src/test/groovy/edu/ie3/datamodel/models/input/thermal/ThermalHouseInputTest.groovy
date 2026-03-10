/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class ThermalHouseInputTest extends Specification {

  def "A ThermalHouseInput copy method should work as expected"() {
    given:
    def thermalHouseInput = ThermalUnitInputTestData.thermalHouseInput

    when:
    def alteredUnit = thermalHouseInput.copy().ethLosses(ThermalUnitInputTestData.thermalConductance)
        .ethCapa(ThermalUnitInputTestData.ethCapa)
        .targetTemperature(ThermalUnitInputTestData.TARGET_TEMPERATURE)
        .upperTemperatureLimit(ThermalUnitInputTestData.UPPER_TEMPERATURE_LIMIT)
        .lowerTemperatureLimit(ThermalUnitInputTestData.LOWER_TEMPERATURE_LIMIT)
        .thermalBus(ThermalUnitInputTestData.thermalBus).build()


    then:
    alteredUnit.with {
      uuid == thermalHouseInput.uuid
      id == thermalHouseInput.id
      operator == thermalHouseInput.operator
      operationTime == thermalHouseInput.operationTime
      thermalBus == thermalHouseInput.thermalBus
      ethLosses == ThermalUnitInputTestData.thermalConductance
      ethCapa == ThermalUnitInputTestData.ethCapa
      targetTemperature == ThermalUnitInputTestData.TARGET_TEMPERATURE
      upperTemperatureLimit == ThermalUnitInputTestData.UPPER_TEMPERATURE_LIMIT
      lowerTemperatureLimit == ThermalUnitInputTestData.LOWER_TEMPERATURE_LIMIT
      housingType == ThermalUnitInputTestData.HOUSING_TYPE
      numberOfInhabitants == ThermalUnitInputTestData.NUMBER_INHABITANTS
    }
  }

  def "The equals methods for a ThermalHouseInput works as expected"() {
    given:
    def thermalHouseInput1 = ThermalUnitInputTestData.thermalHouseInput
    def thermalHouseInput2 = ThermalUnitInputTestData.thermalHouseInput
    def changedLowerTemperature = Quantities.getQuantity(-100, StandardUnits.TEMPERATURE)
    def thermalHouseInput3 = ThermalUnitInputTestData.thermalHouseInput.copy()
        .lowerTemperatureLimit(changedLowerTemperature).build()
    def otherObject = "otherObject"

    expect:
    Objects.equals(thermalHouseInput1, thermalHouseInput2)
    !Objects.equals(thermalHouseInput1, null)
    !Objects.equals(thermalHouseInput1, thermalHouseInput3)
    !Objects.equals(thermalHouseInput1, otherObject)
  }

  def "A ThermalHouseInput without operator and operation time is created as expected"() {
    given:
    def thermalHouseInput = new ThermalHouseInput(
        UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a"),
        "test_thermalHouseInput",
        ThermalUnitInputTestData.thermalBus,
        Quantities.getQuantity(10, StandardUnits.THERMAL_TRANSMISSION),
        Quantities.getQuantity(20, StandardUnits.HEAT_CAPACITY),
        Quantities.getQuantity(20, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(25, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(15, StandardUnits.TEMPERATURE),
        "house",
        2.0
        )

    expect:
    thermalHouseInput.targetTemperature == Quantities.getQuantity(20, StandardUnits.TEMPERATURE)
    thermalHouseInput.upperTemperatureLimit == Quantities.getQuantity(25, StandardUnits.TEMPERATURE)
    thermalHouseInput.lowerTemperatureLimit == Quantities.getQuantity(15, StandardUnits.TEMPERATURE)
    thermalHouseInput.housingType == "house"
    thermalHouseInput.numberOfInhabitants == 2.0
  }

  def "Scaling a ThermalHouseInput via builder should work as expected"() {
    given:
    def thermalHouseInput = ThermalUnitInputTestData.thermalHouseInput

    when:
    def alteredUnit = thermalHouseInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == thermalHouseInput.uuid
      id == thermalHouseInput.id
      operator == thermalHouseInput.operator
      operationTime == thermalHouseInput.operationTime
      thermalBus == thermalHouseInput.thermalBus
      ethLosses == thermalHouseInput.ethLosses * 2d
      ethCapa == thermalHouseInput.ethCapa * 2d
      targetTemperature == thermalHouseInput.targetTemperature
      upperTemperatureLimit == thermalHouseInput.upperTemperatureLimit
      lowerTemperatureLimit == thermalHouseInput.lowerTemperatureLimit
      housingType == thermalHouseInput.housingType
      numberOfInhabitants == thermalHouseInput.numberOfInhabitants * 2d
    }
  }
}
