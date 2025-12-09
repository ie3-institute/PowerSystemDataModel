/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class ThermalSourceTest extends Specification implements FactoryTestHelper {

  @Shared
  private UUID operatorUUID = UUID.randomUUID()
  @Shared
  private UUID thermalBusUUID = UUID.randomUUID()
  @Shared
  private OperatorInput operatorInput = Mock(OperatorInput)
  @Shared
  private ThermalBusInput thermalBusInput = Mock(ThermalBusInput)

  @Shared
  private Map<UUID, OperatorInput> operators = [(operatorUUID): operatorInput]
  @Shared
  private Map<UUID, ThermalBusInput> thermalBusses = [(thermalBusUUID): thermalBusInput]

  def "A ThermalBusInput can be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil": "",
      "id"           : "TestID",
      "operator"     : operatorUUID.toString()
    ]

    when:
    def input = ThermalSource.thermalBusBuilder(operators).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
    }
  }

  def "A ThermalHouseInput can be built correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"                 : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                   : "TestID",
      "ethlosses"            : "3",
      "ethcapa"              : "4",
      "targetTemperature"    : "5",
      "upperTemperatureLimit": "6",
      "lowerTemperatureLimit": "7",
      "housingType"          : "flat",
      "numberInhabitants"    : "9",
      "operator"             : operatorUUID.toString(),
      "thermalbus"           : thermalBusUUID.toString()
    ]

    when:
    def input = ThermalSource.thermalHouseBuildFunction(operators, thermalBusses).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == operatorInput
      assert id == parameter["id"]
      assert thermalBus == thermalBusInput
      assert ethLosses == getQuant(parameter["ethlosses"], StandardUnits.THERMAL_TRANSMISSION)
      assert ethCapa == getQuant(parameter["ethcapa"], StandardUnits.HEAT_CAPACITY)
      assert targetTemperature == getQuant(parameter["targetTemperature"], StandardUnits.TEMPERATURE)
      assert upperTemperatureLimit == getQuant(parameter["upperTemperatureLimit"], StandardUnits.TEMPERATURE)
      assert lowerTemperatureLimit == getQuant(parameter["lowerTemperatureLimit"], StandardUnits.TEMPERATURE)
      assert housingType == parameter["housingType"]
      assert numberInhabitants == parameter["numberInhabitants"].toDouble()
    }
  }

  def "A CylindricalStorageInputFactory should parse a valid CylindricalStorageInput correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                 : "TestID",
      "storagevolumelvl"   : "3",
      "inlettemp"          : "4",
      "returntemp"         : "5",
      "c"                  : "6",
      "pThermalMax"        : "7",
      "operator"           : operatorUUID.toString(),
      "thermalbus"         : thermalBusUUID.toString()
    ]

    when:
    def input = ThermalSource.cylindricalStorageBuildFunction(operators, thermalBusses).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == operatorInput
      assert id == parameter["id"]
      assert thermalBus == thermalBusInput
      assert storageVolumeLvl == getQuant(parameter["storagevolumelvl"], StandardUnits.VOLUME)
      assert inletTemp == getQuant(parameter["inlettemp"], StandardUnits.TEMPERATURE)
      assert returnTemp == getQuant(parameter["returntemp"], StandardUnits.TEMPERATURE)
      assert c == getQuant(parameter["c"], StandardUnits.SPECIFIC_HEAT_CAPACITY)
      assert pThermalMax == getQuant(parameter["pThermalMax"], StandardUnits.ACTIVE_POWER_IN)
    }
  }

  def "A DomesticHotWaterStorageInputFactory should parse a valid DomesticHotWaterStorageInput correctly"() {
    given:
    Map<String, String> parameter = [
      "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"                 : "TestID",
      "storagevolumelvl"   : "3",
      "inlettemp"          : "4",
      "returntemp"         : "5",
      "c"                  : "6",
      "pThermalMax"        : "7",
      "operator"           : operatorUUID.toString(),
      "thermalbus"         : thermalBusUUID.toString()
    ]

    when:
    def input = ThermalSource.dhwsBuildFunction(operators, thermalBusses).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == operatorInput
      assert id == parameter["id"]
      assert thermalBus == thermalBusInput
      assert storageVolumeLvl == getQuant(parameter["storagevolumelvl"], StandardUnits.VOLUME)
      assert inletTemp == getQuant(parameter["inlettemp"], StandardUnits.TEMPERATURE)
      assert returnTemp == getQuant(parameter["returntemp"], StandardUnits.TEMPERATURE)
      assert c == getQuant(parameter["c"], StandardUnits.SPECIFIC_HEAT_CAPACITY)
      assert pThermalMax == getQuant(parameter["pThermalMax"], StandardUnits.ACTIVE_POWER_IN)
    }
  }
}
