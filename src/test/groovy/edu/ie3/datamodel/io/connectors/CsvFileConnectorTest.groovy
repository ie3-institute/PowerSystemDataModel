/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors

import edu.ie3.datamodel.exceptions.ConnectorException
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.csv.CsvFileDefinition
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.util.io.FileIOUtils
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

class CsvFileConnectorTest extends Specification {
	@Shared
	Path tmpDirectory

	@Shared
	CsvFileConnector cfc

	@Shared
	Set<String> timeSeriesPaths

	@Shared
	Set<String> pathsToIgnore

	def setupSpec() {
		tmpDirectory = Files.createTempDirectory("psdm_csv_file_connector_")
		cfc = new CsvFileConnector(tmpDirectory.toString(), new FileNamingStrategy())
		def gridPaths = ["node_input.csv"]
		timeSeriesPaths = [
			"its_pq_53990eea-1b5d-47e8-9134-6d8de36604bf.csv",
			"its_p_fcf0b851-a836-4bde-8090-f44c382ed226.csv",
			"its_pqh_5022a70e-a58f-4bac-b8ec-1c62376c216b.csv",
			"its_c_b88dee50-5484-4136-901d-050d8c1c97d1.csv",
			"its_c_c7b0d9d6-5044-4f51-80b4-f221d8b1f14b.csv",
			"its_weather_085d98ee-09a2-4de4-b119-83949690d7b6.csv"
		]
		pathsToIgnore = [
			"file_to_be_ignored.txt"
		]
		(gridPaths + pathsToIgnore + timeSeriesPaths).forEach { it -> Files.createFile(Paths.get(FilenameUtils.concat(tmpDirectory.toString(), it))) }
	}

	def cleanupSpec() {
		FileIOUtils.deleteRecursively(tmpDirectory)
		cfc.shutdown()
	}

	def "The csv file connector is able to provide correct paths to time series files"() {
		when:
		def actual = cfc.individualTimeSeriesFilePaths

		then:
		noExceptionThrown()

		actual.size() == timeSeriesPaths.size()
		actual.containsAll(timeSeriesPaths)
	}

	def "The csv file connector is able to build correct uuid to meta information mapping"() {
		given:
		def expected = [
			(UUID.fromString("53990eea-1b5d-47e8-9134-6d8de36604bf")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("53990eea-1b5d-47e8-9134-6d8de36604bf"), ColumnScheme.APPARENT_POWER, "its_pq_53990eea-1b5d-47e8-9134-6d8de36604bf"),
			(UUID.fromString("fcf0b851-a836-4bde-8090-f44c382ed226")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("fcf0b851-a836-4bde-8090-f44c382ed226"), ColumnScheme.ACTIVE_POWER, "its_p_fcf0b851-a836-4bde-8090-f44c382ed226"),
			(UUID.fromString("5022a70e-a58f-4bac-b8ec-1c62376c216b")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("5022a70e-a58f-4bac-b8ec-1c62376c216b"), ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND, "its_pqh_5022a70e-a58f-4bac-b8ec-1c62376c216b"),
			(UUID.fromString("b88dee50-5484-4136-901d-050d8c1c97d1")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("b88dee50-5484-4136-901d-050d8c1c97d1"), ColumnScheme.ENERGY_PRICE, "its_c_b88dee50-5484-4136-901d-050d8c1c97d1"),
			(UUID.fromString("c7b0d9d6-5044-4f51-80b4-f221d8b1f14b")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("c7b0d9d6-5044-4f51-80b4-f221d8b1f14b"), ColumnScheme.ENERGY_PRICE, "its_c_c7b0d9d6-5044-4f51-80b4-f221d8b1f14b"),
			(UUID.fromString("085d98ee-09a2-4de4-b119-83949690d7b6")): new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(UUID.fromString("085d98ee-09a2-4de4-b119-83949690d7b6"), ColumnScheme.WEATHER, "its_weather_085d98ee-09a2-4de4-b119-83949690d7b6")
		]

		when:
		def actual = cfc.buildIndividualTimeSeriesMetaInformation()

		then:
		actual == expected
	}

	def "The csv file connector returns empty optional, if there is no meta information for queried time series"() {
		when:
		def actual = cfc.getIndividualTimeSeriesMetaInformation(UUID.fromString("2602e863-3eb6-480e-b752-a3e653af74ec"))

		then:
		!actual.present
	}

	def "The csv file connector returns correct individual time series meta information"() {
		given:
		def timeSeriesUuid = UUID.fromString("b88dee50-5484-4136-901d-050d8c1c97d1")
		def expected = Optional.of(new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(timeSeriesUuid, ColumnScheme.ENERGY_PRICE, "its_c_b88dee50-5484-4136-901d-050d8c1c97d1"))

		when:
		def actual = cfc.getIndividualTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		actual == expected
	}

	def "The csv file connector returns empty Optional of CsvTimeSeriesMetaInformation when pointed to non-individual time series"() {
		given:
		def pathString = "lpts_h0_53990eea-1b5d-47e8-9134-6d8de36604bf"

		when:
		def actual = cfc.buildCsvTimeSeriesMetaInformation(pathString)

		then:
		!actual.present
	}

	def "The csv file connector is able to build correct meta information from valid input"() {
		given:
		def pathString = "its_pq_53990eea-1b5d-47e8-9134-6d8de36604bf"
		def expected = new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(
				UUID.fromString("53990eea-1b5d-47e8-9134-6d8de36604bf"),
				ColumnScheme.APPARENT_POWER,
				""
				)

		when:
		def actual = cfc.buildCsvTimeSeriesMetaInformation(pathString)

		then:
		actual.present
		actual.get().with {
			assert uuid == expected.uuid
			assert columnScheme == expected.columnScheme
			/* Don't check the reader explicitly */
		}
	}

	def "The csv file connector throws an Exception, if the foreseen file cannot be found"() {
		given:
		def cfc = new CsvFileConnector(tmpDirectory.toString(), new FileNamingStrategy(new EntityPersistenceNamingStrategy(), new DefaultDirectoryHierarchy(tmpDirectory.toString(), "test")))

		when:
		cfc.initReader(NodeInput)

		then:
		thrown(FileNotFoundException)
	}

	def "The csv file connector initializes a reader without Exception, if the foreseen file is apparent"() {
		when:
		cfc.initReader(NodeInput)

		then:
		noExceptionThrown()
	}

	def "The csv file connector is able to init writers utilizing a directory hierarchy"() {
		given: "a suitable connector"
		def baseDirectory = FilenameUtils.concat(tmpDirectory.toString(), "directoryHierarchy")
		def directoryHierarchy = new DefaultDirectoryHierarchy(baseDirectory, "test")
		def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), directoryHierarchy)
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)

		and: "expected results"
		def nodeFile = new File(Stream.of(baseDirectory, "test", "input", "grid", "node_input.csv").collect(Collectors.joining(File.separator)))

		when:
		/* The head line is of no interest here */
		connector.getOrInitWriter(NodeInput, [] as String[], ",")

		then:
		noExceptionThrown()
		nodeFile.exists()
		nodeFile.file // is it a file?
	}

	def "The csv file connector is able to init writers utilizing no directory hierarchy"() {
		given: "a suitable connector"
		def baseDirectory = FilenameUtils.concat(tmpDirectory.toString(), "directoryHierarchy")
		def fileNamingStrategy = new FileNamingStrategy()
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)

		and: "expected results"
		def nodeFile = new File(FilenameUtils.concat(baseDirectory, "node_input.csv"))

		when:
		/* The head line is of no interest here */
		connector.getOrInitWriter(NodeInput, [] as String[], ",")

		then:
		noExceptionThrown()
		nodeFile.exists()
		nodeFile.file // is it a file?
	}

	def "The csv file connector throws ConnectorException if no csv file definition can be built from class information"() {
		given:
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy()
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)

		when:
		connector.buildFileDefinition(String, ["a", "b", "c"] as String[], ",")

		then:
		def ex = thrown(ConnectorException)
		ex.message == "Cannot determine the file name for class 'String'."
	}

	def "The csv file connector is able to build correct csv file definition from class upon request"() {
		given:
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy()
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)
		def expected = new CsvFileDefinition("node_input.csv", "", ["a", "b", "c"] as String[], ",")

		when:
		def actual = connector.buildFileDefinition(NodeInput, ["a", "b", "c"] as String[], ",")

		then:
		actual == expected
	}

	def "The csv file connector is able to build correct csv file definition from class upon request, utilizing directory hierarchy"() {
		given:
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), new DefaultDirectoryHierarchy(tmpDirectory.toString(), "test"))
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)
		def expected = new CsvFileDefinition("node_input.csv", Stream.of("test", "input", "grid").collect(Collectors.joining(File.separator)), ["a", "b", "c"] as String[], ",")

		when:
		def actual = connector.buildFileDefinition(NodeInput, ["a", "b", "c"] as String[], ",")

		then:
		actual == expected
	}

	def "The csv file connector throws ConnectorException if no csv file definition can be built from time series"() {
		given: "a suitable connector"
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy()
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)

		and: "credible input"
		def timeSeries = Mock(RepetitiveTimeSeries)

		when:
		connector.buildFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",")

		then:
		def ex = thrown(ConnectorException)
		ex.message == "Cannot determine the file name for time series 'Mock for type 'RepetitiveTimeSeries' named 'timeSeries''."
	}

	def "The csv file connector is able to build correct csv file definition from time series upon request"() {
		given: "a suitable connector"
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy()
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)
		def expected = new CsvFileDefinition("its_c_0c03ce9f-ab0e-4715-bc13-f9d903f26dbf.csv", "", ["a", "b", "c"] as String[], ",")

		and: "credible input"
		def entries = [
			new TimeBasedValue(UUID.fromString("5bac1c86-19d1-4145-8dae-f207a1346916"), ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(50d, StandardUnits.ENERGY_PRICE)))
		] as SortedSet
		def timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> UUID.fromString("0c03ce9f-ab0e-4715-bc13-f9d903f26dbf")
		timeSeries.entries >> entries

		when:
		def actual = connector.buildFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",")

		then:
		actual == expected
	}

	def "The csv file connector is able to build correct csv file definition from time series upon request, utilizing directory hierarchy"() {
		given: "a suitable connector"
		def baseDirectory = tmpDirectory.toString()
		def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), new DefaultDirectoryHierarchy(tmpDirectory.toString(), "test"))
		def connector = new CsvFileConnector(baseDirectory, fileNamingStrategy)
		def expected = new CsvFileDefinition("its_c_0c03ce9f-ab0e-4715-bc13-f9d903f26dbf.csv", Stream.of("test", "input", "participants", "time_series").collect(Collectors.joining(File.separator)), ["a", "b", "c"] as String[], ",")

		and: "credible input"
		def entries = [
			new TimeBasedValue(UUID.fromString("5bac1c86-19d1-4145-8dae-f207a1346916"), ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(50d, StandardUnits.ENERGY_PRICE)))
		] as SortedSet
		def timeSeries = Mock(IndividualTimeSeries)
		timeSeries.uuid >> UUID.fromString("0c03ce9f-ab0e-4715-bc13-f9d903f26dbf")
		timeSeries.entries >> entries

		when:
		def actual = connector.buildFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",")

		then:
		actual == expected
	}

	def "Initialising a writer with incorrect base directory leads to ConnectorException"() {
		given:
		def baseFolder = FilenameUtils.concat(tmpDirectory.toString(), "helloWorld.txt")
		def baseFolderFile = new File(baseFolder)
		baseFolderFile.createNewFile()
		def fileDefinition = new CsvFileDefinition("test.csv", "", [] as String[], ",")

		when:
		cfc.initWriter(baseFolder, fileDefinition)

		then:
		def e = thrown(ConnectorException)
		e.message == "Directory '" + baseFolder + "' already exists and is a file!"
	}
}
