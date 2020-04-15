/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.factory.input.ThermalUnitInputEntityData
import spock.lang.Specification

import java.util.stream.Collectors

class CsvThermalSourceTest extends Specification implements CsvTestDataMeta {

	def "A CsvThermalSource should build thermal unit input entity from valid and invalid input data as expected"() {
		given:
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, Mock(CsvTypeSource))
		def fieldsToAttributes = null // todo
		def assetInputEntityData = null // todo

		when:
		def resultingDataOpt = csvThermalSource.buildThermalUnitInputEntityData(assetInputEntityData, thermalBuses).collect(Collectors.toList())

		then:
		resultingDataOpt.size() == 1
		resultingDataOpt.first().isPresent() == resultIsPresent
		resultingDataOpt.first().ifPresent({ resultingData ->
			assert (resultingData == expectedThermalUnitInputEntityData)
		})

		where:
		thermalBuses || resultIsPresent || expectedThermalUnitInputEntityData
		[]|| false           || null  // thermal buses are not present -> method should return an empty optional -> do not check for thermal unit entity data
		[]|| true            || new ThermalUnitInputEntityData()//todo add bus, fill with data etc.

	}

	def "A CsvThermalSource should return a CylindricStorageInput from valid and invalid input data as expected"() {
		given:
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, Mock(CsvTypeSource))
		def operators = null // todo
		def thermalBuses = null // todo

		when:
		def resultingCylindricStorage = csvThermalSource.getCylindricStorages(operators, thermalBuses)

		then:
		resultingCylindricStorage == null // todo checks

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

	def "A CsvThermalSource should return a ThermalBuses from valid and invalid input data as expected"() {
		given:
		def csvThermalSource = new CsvThermalSource(csvSep, thermalFolderPath, fileNamingStrategy, Mock(CsvTypeSource))
		def operators = null // todo

		when:
		def resultingThermalBuses = csvThermalSource.getThermalBuses(operators)

		then:
		resultingThermalBuses == null // todo checks

	}


}
