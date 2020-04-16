/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.ThermalUnitInputEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.ThermalUnitInputTestData as tutd
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

	def "A CsvThermalSource should return a CylindricStorageInput from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def operators = csvTypeSource.operators
		def thermalBuses = csvThermalSource.thermalBuses

		//test method when operators and thermal buses are not provided as constructor parameters
		when:
		def resultingCylindricStorageWoOperator = csvThermalSource.getCylindricStorages()

		then:
		resultingCylindricStorageWoOperator.size() == 1
		resultingCylindricStorageWoOperator.first().uuid == sptd.thermalStorage.uuid
		resultingCylindricStorageWoOperator.first().id == sptd.thermalStorage.id
		resultingCylindricStorageWoOperator.first().operator == sptd.thermalStorage.operator
		resultingCylindricStorageWoOperator.first().operationTime == sptd.thermalStorage.operationTime
		resultingCylindricStorageWoOperator.first().thermalBus == sptd.thermalStorage.thermalBus
		resultingCylindricStorageWoOperator.first().storageVolumeLvl == sptd.storageVolumeLvl
		resultingCylindricStorageWoOperator.first().storageVolumeLvlMin == sptd.storageVolumeLvlMin
		resultingCylindricStorageWoOperator.first().inletTemp == sptd.inletTemp
		resultingCylindricStorageWoOperator.first().returnTemp == sptd.returnTemp
		resultingCylindricStorageWoOperator.first().c == sptd.c

		//test method when operators and thermal buses are provided as constructor parameters
		when:
		def resultingCylindricStorage = csvThermalSource.getCylindricStorages(operators, thermalBuses)

		then:
		resultingCylindricStorage.size() == 1
		resultingCylindricStorage.first().uuid == sptd.thermalStorage.uuid
		resultingCylindricStorage.first().id == sptd.thermalStorage.id
		resultingCylindricStorage.first().operator == sptd.thermalStorage.operator
		resultingCylindricStorage.first().operationTime == sptd.thermalStorage.operationTime
		resultingCylindricStorage.first().thermalBus == sptd.thermalStorage.thermalBus
		resultingCylindricStorage.first().storageVolumeLvl == sptd.storageVolumeLvl
		resultingCylindricStorage.first().storageVolumeLvlMin == sptd.storageVolumeLvlMin
		resultingCylindricStorage.first().inletTemp == sptd.inletTemp
		resultingCylindricStorage.first().returnTemp == sptd.returnTemp
		resultingCylindricStorage.first().c == sptd.c

	}

	def "A CsvThermalSource should build thermal unit input entity from valid and invalid input data as expected"() {
		given:
		def csvTypeSource = new CsvTypeSource(",", typeFolderPath, new FileNamingStrategy())
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, csvTypeSource)
		def validFieldsToAttributes = [
			"uuid"			: "717af017-cc69-406f-b452-e022d7fb516a",
			"id"			: "test_thermal_unit",
			"operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
			"operatesFrom"	: "2020-03-24 15:11:31",
			"operatesUntil"	: "2020-03-25 15:11:31",
			"thermalBus"    : "0d95d7f2-49fb-4d49-8636-383a5220384e"
		]
		def assetInputEntityData = new AssetInputEntityData(validFieldsToAttributes, ThermalUnitInput)

		when:
		def resultingDataOpt = csvThermalSource.buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses).collect(Collectors.toList())
		print(resultingDataOpt)

		then:
		resultingDataOpt.size() == 1
		resultingDataOpt.first().isPresent() == resultIsPresent
		resultingDataOpt.first().ifPresent({ resultingData ->
			assert (resultingData == expectedThermalUnitInputEntityData)
		})

		where:
		thermalBuses || resultIsPresent || expectedThermalUnitInputEntityData
		[]|| false           || null  // thermal buses are not present -> method should return an empty optional -> do not check for thermal unit entity data
		[]|| true            || new ThermalUnitInputEntityData(["uuid": "717af017-cc69-406f-b452-e022d7fb516a", "id": "test_thermal_unit", "operator": "8f9682df-0744-4b58-a122-f0dc730f6510", "operatesFrom": "2020-03-24 15:11:31", "operatesUntil": "2020-03-25 15:11:31", "thermalBus": "0d95d7f2-49fb-4d49-8636-383a5220384e"], ThermalUnitInput, new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus"))

	}

	def "A CsvThermalSource should return a ThermalHouseInput from valid and invalid input data as expected"() {
		given:
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, Mock(CsvTypeSource))
		def operators = null // todo
		def thermalBuses = null // todo

		when:
		def resultingThermalHouses = csvThermalSource.getThermalHouses(operators, thermalBuses)

		then:
		resultingThermalHouses == null // todo checks

	}
}
