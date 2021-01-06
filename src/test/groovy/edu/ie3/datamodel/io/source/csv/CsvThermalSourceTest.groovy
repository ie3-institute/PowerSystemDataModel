/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.ThermalUnitInputEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification

import java.util.stream.Collectors

class CsvThermalSourceTest extends Specification implements CsvTestDataMeta {

	def "A CsvThermalSource should return ThermalBuses from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def operators = csvTypeSource.operators

		//test method when no operators are provided as constructor parameters
		when:
		def resultingThermalBusesWoOperator = csvThermalSource.getThermalBuses()

		then:
		resultingThermalBusesWoOperator.size() == 1
		resultingThermalBusesWoOperator.first().uuid == sptd.thermalBus.uuid
		resultingThermalBusesWoOperator.first().id == sptd.thermalBus.id
		resultingThermalBusesWoOperator.first().operator == sptd.thermalBus.operator
		resultingThermalBusesWoOperator.first().operationTime == sptd.thermalBus.operationTime

		//test method when operators are provided as constructor parameters
		when:
		def resultingThermalBuses = csvThermalSource.getThermalBuses(operators)

		then:
		resultingThermalBuses.size() == 1
		resultingThermalBuses.first().uuid == sptd.thermalBus.uuid
		resultingThermalBuses.first().id == sptd.thermalBus.id
		resultingThermalBuses.first().operator == sptd.thermalBus.operator
		resultingThermalBuses.first().operationTime == sptd.thermalBus.operationTime
	}

	def "A CsvThermalSource should return a CylindricalStorageInput from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def operators = csvTypeSource.operators
		def thermalBuses = csvThermalSource.thermalBuses

		//test method when operators and thermal buses are not provided as constructor parameters
		when:
		def resultingCylindricalStorageWoOperator = csvThermalSource.getCylindricStorages()

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
		def resultingCylindricalStorage = csvThermalSource.getCylindricStorages(operators, thermalBuses)

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

	def "A CsvThermalSource should build thermal unit input entity from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def operator = new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "testOperator")
		def validFieldsToAttributes = [
			"uuid"			: "717af017-cc69-406f-b452-e022d7fb516a",
			"id"			: "test_thermal_unit",
			"operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
			"operatesFrom"	: "2020-03-24 15:11:31",
			"operatesUntil"	: "2020-03-25 15:11:31",
			"thermalBus"    : "0d95d7f2-49fb-4d49-8636-383a5220384e"
		]
		def assetInputEntityData = new AssetInputEntityData(validFieldsToAttributes, ThermalUnitInput, operator)

		when:
		def resultingDataOpt = csvThermalSource.buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses).collect(Collectors.toList())

		then:
		resultingDataOpt.size() == 1
		resultingDataOpt.first().present == resultIsPresent
		resultingDataOpt.first().ifPresent({ resultingData ->
			assert (resultingData == expectedThermalUnitInputEntityData)
		})

		where:
		thermalBuses || resultIsPresent || expectedThermalUnitInputEntityData
		[]|| false           || null  // thermal buses are not present -> method should return an empty optional -> do not check for thermal unit entity data
		[
			new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus")
		]|| true            ||
		new ThermalUnitInputEntityData(["uuid": "717af017-cc69-406f-b452-e022d7fb516a",
			"id": "test_thermal_unit",
			"operator": "8f9682df-0744-4b58-a122-f0dc730f6510",
			"operatesFrom": "2020-03-24 15:11:31",
			"operatesUntil": "2020-03-25 15:11:31"],
		ThermalUnitInput,
		new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "testOperator"),
		new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus"))

	}

	def "A CsvThermalSource should return a ThermalHouseInput from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def operators = csvTypeSource.operators
		def thermalBuses = csvThermalSource.thermalBuses

		//test method when operators and thermal buses are not provided as constructor parameters
		when:
		def resultingThermalHouseWoOperator = csvThermalSource.getThermalHouses()

		then:
		resultingThermalHouseWoOperator.size() == 1
		resultingThermalHouseWoOperator.first().uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
		resultingThermalHouseWoOperator.first().id == ThermalUnitInputTestData.thermalHouseInput.id
		resultingThermalHouseWoOperator.first().operator == ThermalUnitInputTestData.thermalHouseInput.operator
		resultingThermalHouseWoOperator.first().operationTime.isLimited()
		resultingThermalHouseWoOperator.first().operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
		resultingThermalHouseWoOperator.first().thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
		resultingThermalHouseWoOperator.first().ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
		resultingThermalHouseWoOperator.first().ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa

		//test method when operators and thermal buses are provided as constructor parameters
		when:
		def resultingThermalHouse = csvThermalSource.getThermalHouses(operators, thermalBuses)

		then:
		resultingThermalHouse.size() == 1
		resultingThermalHouse.first().uuid == ThermalUnitInputTestData.thermalHouseInput.uuid
		resultingThermalHouse.first().id == ThermalUnitInputTestData.thermalHouseInput.id
		resultingThermalHouse.first().operator == ThermalUnitInputTestData.thermalHouseInput.operator
		resultingThermalHouse.first().operationTime.isLimited()
		resultingThermalHouse.first().operationTime == ThermalUnitInputTestData.thermalHouseInput.operationTime
		resultingThermalHouseWoOperator.first().thermalBus == ThermalUnitInputTestData.thermalHouseInput.thermalBus
		resultingThermalHouse.first().ethLosses == ThermalUnitInputTestData.thermalHouseInput.ethLosses
		resultingThermalHouse.first().ethCapa == ThermalUnitInputTestData.thermalHouseInput.ethCapa

	}
}
