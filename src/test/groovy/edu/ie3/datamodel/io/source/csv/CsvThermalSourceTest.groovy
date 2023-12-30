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
    def thermalBusWoOp = resultingThermalBusesWoOperator.values().first()
    thermalBusWoOp.uuid == sptd.thermalBus.uuid
    thermalBusWoOp.id == sptd.thermalBus.id
    thermalBusWoOp.operator == sptd.thermalBus.operator
    thermalBusWoOp.operationTime == sptd.thermalBus.operationTime

    //test method when operators are provided as constructor parameters
    when:
    def resultingThermalBuses = csvThermalSource.getThermalBuses(operators)

    then:
    resultingThermalBuses.size() == 1
    def thermalBus = resultingThermalBuses.values().first()
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
    resultingCylindricalStorageWoOperator.first().uuid == sptd.thermalStorage.uuid
    resultingCylindricalStorageWoOperator.first().id == sptd.thermalStorage.id
    resultingCylindricalStorageWoOperator.first().operator == sptd.thermalStorage.operator
    resultingCylindricalStorageWoOperator.first().operationTime == sptd.thermalStorage.operationTime
    resultingCylindricalStorageWoOperator.first().thermalBus == sptd.thermalStorage.thermalBus
    resultingCylindricalStorageWoOperator.first().storageVolumeLvl == sptd.storageVolumeLvl
    resultingCylindricalStorageWoOperator.first().storageVolumeLvlMin == sptd.storageVolumeLvlMin
    resultingCylindricalStorageWoOperator.first().inletTemp == sptd.inletTemp
    resultingCylindricalStorageWoOperator.first().returnTemp == sptd.returnTemp
    resultingCylindricalStorageWoOperator.first().c == sptd.c

    //test method when operators and thermal buses are provided as constructor parameters
    when:
    def resultingCylindricalStorage = csvThermalSource.getCylindricalStorages(operators, thermalBuses)

    then:
    resultingCylindricalStorage.size() == 1
    resultingCylindricalStorage.first().uuid == sptd.thermalStorage.uuid
    resultingCylindricalStorage.first().id == sptd.thermalStorage.id
    resultingCylindricalStorage.first().operator == sptd.thermalStorage.operator
    resultingCylindricalStorage.first().operationTime == sptd.thermalStorage.operationTime
    resultingCylindricalStorage.first().thermalBus == sptd.thermalStorage.thermalBus
    resultingCylindricalStorage.first().storageVolumeLvl == sptd.storageVolumeLvl
    resultingCylindricalStorage.first().storageVolumeLvlMin == sptd.storageVolumeLvlMin
    resultingCylindricalStorage.first().inletTemp == sptd.inletTemp
    resultingCylindricalStorage.first().returnTemp == sptd.returnTemp
    resultingCylindricalStorage.first().c == sptd.c
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
    def thermalHouseWoOp = resultingThermalHouseWoOperator.values().first()
    thermalHouseWoOp.uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
    thermalHouseWoOp.id == ThermalUnitInputTestData.thermalHouseInput.id
    thermalHouseWoOp.operator == ThermalUnitInputTestData.thermalHouseInput.operator
    thermalHouseWoOp.operationTime.isLimited()
    thermalHouseWoOp.operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
    thermalHouseWoOp.thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
    thermalHouseWoOp.ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
    thermalHouseWoOp.ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa
    thermalHouseWoOp.targetTemperature == ThermalUnitInputTestData.thermalHouseInput.targetTemperature
    thermalHouseWoOp.upperTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.upperTemperatureLimit
    thermalHouseWoOp.lowerTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.lowerTemperatureLimit

    //test method when operators and thermal buses are provided as constructor parameters
    when:
    def resultingThermalHouse = csvThermalSource.getThermalHouses(operators, thermalBuses)

    then:
    resultingThermalHouse.size() == 1
    def thermalHouse = resultingThermalHouse.values().first()
    thermalHouse .uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
    thermalHouse.id == ThermalUnitInputTestData.thermalHouseInput.id
    thermalHouse.operator == ThermalUnitInputTestData.thermalHouseInput.operator
    thermalHouse.operationTime.isLimited()
    thermalHouse.operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
    thermalHouse.thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
    thermalHouse.ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
    thermalHouse.ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa
    thermalHouse.targetTemperature == ThermalUnitInputTestData.thermalHouseInput.targetTemperature
    thermalHouse.upperTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.upperTemperatureLimit
    thermalHouse.lowerTemperatureLimit == ThermalUnitInputTestData.thermalHouseInput.lowerTemperatureLimit
  }
}