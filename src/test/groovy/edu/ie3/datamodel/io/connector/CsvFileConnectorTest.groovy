/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connector

import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.datamodel.io.csv.FileNamingStrategy
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CsvFileConnectorTest extends Specification {
	@Shared
	Path tmpFolder

	def setupSpec() {
		tmpFolder = Files.createTempDirectory("psdm_csv_file_connector_")
	}

	def "The csv file connector is able to provide correct paths time series files"() {
		given:
		def cfc = new CsvFileConnector(tmpFolder.toString(), new FileNamingStrategy())
		def timeSeriesPaths = [
			"its_pq_53990eea-1b5d-47e8-9134-6d8de36604bf.csv",
			"its_p_fcf0b851-a836-4bde-8090-f44c382ed226.csv",
			"its_pqh_5022a70e-a58f-4bac-b8ec-1c62376c216b.csv",
			"its_c_b88dee50-5484-4136-901d-050d8c1c97d1.csv",
			"its_weather_085d98ee-09a2-4de4-b119-83949690d7b6.csv"
		]
		def pathsToIgnore = [
			"file_to_be_ignored.txt"
		]
		(pathsToIgnore + timeSeriesPaths).forEach { it -> Files.createFile(Paths.get(FilenameUtils.concat(tmpFolder.toString(), it))) }

		when:
		def actual = cfc.getIndividualTimeSeriesFilePaths()

		then:
		noExceptionThrown()

		actual.size() == timeSeriesPaths.size()
		actual.containsAll(timeSeriesPaths)
	}
}
