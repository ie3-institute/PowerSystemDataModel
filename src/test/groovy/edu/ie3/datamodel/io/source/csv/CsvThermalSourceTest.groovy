/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.ThermalSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification

class CsvThermalSourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvThermalSource should return ThermalBuses from valid and invalid input data as expected"() {
    given:
    def csvTypeSource = new TypeSource(new CsvDataSource(",", typeFolderPath, new FileNamingStrategy()))
    def csvThermalSource = new ThermalSource(csvTypeSource, new CsvDataSource(csvSep, thermalFolderPath, fileNamingStrategy))
    def operators = csvTypeSource.operators

    //test method when no operators are provided as constructor parameters
    when:
    def resultingThermalBusesWoOperator = csvThermalSource.getThermalBuses()

    then:
    resultingThermalBusesWoOperator.size() == 1
    def thermalBusWoOp = resultingThermalBusesWoOperator.first()
    thermalBusWoOp.uuid == sptd.thermalBus.uuid
    thermalBusWoOp.id == sptd.thermalBus.id
    thermalBusWoOp.operator == sptd.thermalBus.operator
    thermalBusWoOp.operationTime == sptd.thermalBus.operationTime

    //test method when operators are provided as constructor parameters
    when:
    def resultingThermalBuses = csvThermalSource.getThermalBuses(operators)

    then:
    resultingThermalBuses.size() == 1
    def thermalBus = resultingThermalBuses.first()
    thermalBus.uuid == sptd.thermalBus.uuid
    thermalBus.id == sptd.thermalBus.id
    thermalBus.operator == sptd.thermalBus.operator
    thermalBus.operationTime == sptd.thermalBus.operationTime
  }

  def "A CsvThermalSource should return a CylindricalStorageInput from valid and invalid input data as expected"() {
    given:
    def csvTypeSource = new TypeSource(new CsvDataSource(",", typeFolderPath, new FileNamingStrategy()))
    def csvThermalSource = new ThermalSource(csvTypeSource, new CsvDataSource(csvSep, thermalFolderPath, fileNamingStrategy))
    def operators = csvTypeSource.operators
    def thermalBuses = csvThermalSource.thermalBuses

    //test method when operators and thermal buses are not provided as constructor parameters
    when:
    def resultingCylindricalStorageWoOperator = csvThermalSource.getCylindricalStorages()

    then:
    resultingCylindricalStorageWoOperator.size() == 1
    with(resultingCylindricalStorageWoOperator.first()) {
      uuid == sptd.thermalStorage.uuid
      id == sptd.thermalStorage.id
      operator == sptd.thermalStorage.operator
      operationTime == sptd.thermalStorage.operationTime
      thermalBus == sptd.thermalStorage.thermalBus
      storageVolumeLvl == sptd.storageVolumeLvl
      storageVolumeLvlMin == sptd.storageVolumeLvlMin
      inletTemp == sptd.inletTemp
      returnTemp == sptd.returnTemp
      c == sptd.c
    }

    //test method when operators and thermal buses are provided as constructor parameters
    when:
    def resultingCylindricalStorage = csvThermalSource.getCylindricalStorages(operators, thermalBuses)

    then:
    resultingCylindricalStorage.size() == 1
    with(resultingCylindricalStorage.first()) {
      uuid == sptd.thermalStorage.uuid
      id == sptd.thermalStorage.id
      operator == sptd.thermalStorage.operator
      operationTime == sptd.thermalStorage.operationTime
      thermalBus == sptd.thermalStorage.thermalBus
      storageVolumeLvl == sptd.storageVolumeLvl
      storageVolumeLvlMin == sptd.storageVolumeLvlMin
      inletTemp == sptd.inletTemp
      returnTemp == sptd.returnTemp
      c == sptd.c
    }
  }

  def "A CsvThermalSource should return a ThermalHouseInput from valid and invalid input data as expected"() {
    given:
    def csvTypeSource = new TypeSource(new CsvDataSource(",", typeFolderPath, new FileNamingStrategy()))
    def csvThermalSource = new ThermalSource(csvTypeSource, new CsvDataSource(csvSep, thermalFolderPath, fileNamingStrategy))
    def operators = csvTypeSource.operators
    def thermalBuses = csvThermalSource.thermalBuses

    //test method when operators and thermal buses are not provided as constructor parameters
    when:
    def resultingThermalHouseWoOperator = csvThermalSource.getThermalHouses()

    then:
    resultingThermalHouseWoOperator.size() == 1
    with(resultingThermalHouseWoOperator.first()) {
      uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
      id == ThermalUnitInputTestData.thermalHouseInput.id
      operator == ThermalUnitInputTestData.thermalHouseInput.operator
      operationTime.isLimited()
      operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
      thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
      ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
      ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa
      targetTemperature == ThermalUnitInputTestData.thermalHouseInput.targetTemperature
      upperTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.upperTemperatureLimit
      lowerTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.lowerTemperatureLimit
    }

    //test method when operators and thermal buses are provided as constructor parameters
    when:
    def resultingThermalHouse = csvThermalSource.getThermalHouses(operators, thermalBuses)

    then:
    resultingThermalHouse.size() == 1
    with(resultingThermalHouse.first()) {
      uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
      id == ThermalUnitInputTestData.thermalHouseInput.id
      operator == ThermalUnitInputTestData.thermalHouseInput.operator
      operationTime.isLimited()
      operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
      thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
      ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
      ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa
      targetTemperature == ThermalUnitInputTestData.thermalHouseInput.targetTemperature
      upperTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.upperTemperatureLimit
      lowerTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.lowerTemperatureLimit
    }
  }
}