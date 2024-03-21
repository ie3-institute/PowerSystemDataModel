/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors

import edu.ie3.datamodel.exceptions.ConnectorException
import edu.ie3.datamodel.io.csv.CsvFileDefinition
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class CsvFileConnectorTest extends Specification {
  @Shared
  Path tmpDirectory

  @Shared
  FileNamingStrategy fileNamingStrategy

  @Shared
  CsvFileConnector cfc

  def setupSpec() {
    tmpDirectory = Files.createTempDirectory("psdm_csv_file_connector_")
    fileNamingStrategy = new FileNamingStrategy()
    cfc = new CsvFileConnector(tmpDirectory)
    def gridPaths = [Path.of("node_input.csv")]
    gridPaths.forEach { path -> Files.createFile(tmpDirectory.resolve(path)) }
  }

  def cleanupSpec() {
    cfc.shutdown()
    FileIOUtils.deleteRecursively(tmpDirectory)
  }

  def "The csv file connector throws an Exception, if the foreseen file cannot be found"() {
    when:
    cfc.initReader(tmpDirectory.resolve("path-does-not-exist"))

    then:
    thrown(FileNotFoundException)
  }

  def "The csv file connector initializes a reader without Exception, if the foreseen file is apparent"() {
    when:
    def filePath = fileNamingStrategy.getFilePath(NodeInput).orElseThrow()
    cfc.initReader(filePath)

    then:
    noExceptionThrown()
  }

  def "The csv file connector is able to init writers utilizing a directory hierarchy"() {
    given: "a suitable connector"
    def baseDirectory = tmpDirectory.resolve("directoryHierarchy")
    def directoryHierarchy = new DefaultDirectoryHierarchy(baseDirectory, "test")
    def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), directoryHierarchy)
    def connector = new CsvFileConnector(baseDirectory)

    and: "expected results"
    def nodeFile = baseDirectory.resolve(Path.of("test", "input", "grid", "node_input.csv")).toFile()

    when:
    /* The head line is of no interest here */
    connector.getOrInitWriter(NodeInput, () -> new CsvFileDefinition(NodeInput, [] as String[], ",", fileNamingStrategy))

    then:
    noExceptionThrown()
    nodeFile.exists()
    nodeFile.file // is it a file?
  }

  def "The csv file connector is able to init writers utilizing no directory hierarchy"() {
    given: "a suitable connector"
    def baseDirectory = tmpDirectory.resolve("directoryHierarchy")
    def fileNamingStrategy = new FileNamingStrategy()
    def connector = new CsvFileConnector(baseDirectory)

    and: "expected results"
    def nodeFile = baseDirectory.resolve("node_input.csv").toFile()

    when:
    /* The head line is of no interest here */
    connector.getOrInitWriter(NodeInput, () -> new CsvFileDefinition(NodeInput, [] as String[], ",", fileNamingStrategy))

    then:
    noExceptionThrown()
    nodeFile.exists()
    nodeFile.file // is it a file?
  }

  def "Initialising a writer with incorrect base directory leads to ConnectorException"() {
    given:
    def baseFolder = tmpDirectory.resolve("helloWorld.txt")
    def baseFolderFile = baseFolder.toFile()
    baseFolderFile.createNewFile()
    def fileDefinition = new CsvFileDefinition("test.csv", Path.of(""), [] as String[], ",")

    when:
    cfc.initWriter(baseFolder, fileDefinition)

    then:
    def e = thrown(ConnectorException)
    e.message == "Directory '" + baseFolder + "' already exists and is a file!"
  }
}
