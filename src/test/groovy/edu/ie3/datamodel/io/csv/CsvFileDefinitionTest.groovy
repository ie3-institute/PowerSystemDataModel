/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.datamodel.io.naming.DefaultDirectoryHierarchy
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries
import edu.ie3.datamodel.models.value.EnergyPriceValue
import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.file.Files
import java.nio.file.Path
import java.time.ZonedDateTime

class CsvFileDefinitionTest extends Specification {
  @Shared
  String[] headLineElements

  @Shared
  String csvSep

  @Shared
  String fileName

  @Shared
  Path directory

  def setupSpec() {
    headLineElements = ["a", "b", "c"] as String[]
    csvSep = ","
    fileName = "node_input.csv"
    directory = Path.of("test", "grid")
  }

  def "A csv file definition throw IllegalArgumentException, if the file name is malformed"() {
    given:
    def fileName = FilenameUtils.concat("test", "node_input.csv")

    when:
    new CsvFileDefinition(fileName, directory, headLineElements, csvSep)

    then:
    def ex = thrown(IllegalArgumentException)
    ex.message == "The file name '" + fileName + "' is no valid file name. It may contain everything, except '/', '\\', '.' and any white space character."
  }

  def "A csv file definition is set up correctly, if the file name does not contain extension"() {
    given:
    def fileName = "node_input"

    when:
    def actual = new CsvFileDefinition(fileName, directory, headLineElements, csvSep)

    then:
    actual.with {
      assert it.filePath.fileName == Path.of(this.fileName)
      assert it.directoryPath == this.directory
      assert it.headLineElements() == this.headLineElements
      assert it.csvSep() == this.csvSep
    }
  }

  def "A csv file definition is set up correctly, if the file name does contain other extension, than 'csv'"() {
    given:
    def fileName = "node_input.tar.gz"

    when:
    def actual = new CsvFileDefinition(fileName, directory, headLineElements, csvSep)

    then:
    actual.with {
      assert it.filePath.fileName == Path.of(this.fileName)
      assert it.directoryPath == this.directory
      assert it.headLineElements() == this.headLineElements
      assert it.csvSep() == this.csvSep
    }
  }

  def "The csv file definition can be build correctly from class upon request"() {
    given:
    def fileNamingStrategy = new FileNamingStrategy()
    def expected = new CsvFileDefinition("node_input.csv", Path.of(""), ["a", "b", "c"] as String[], ",")

    when:
    def actual = new CsvFileDefinition(NodeInput, ["a", "b", "c"] as String[], ",", fileNamingStrategy)

    then:
    actual.with {
      assert it.filePath == expected.filePath
      assert it.headLineElements() == expected.headLineElements()
      assert it.csvSep() == expected.csvSep()
    }
  }

  def "The csv file connector can be build correctly from class upon request, utilizing directory hierarchy"() {
    given:
    def tmpDirectory = Files.createTempDirectory("psdm_csv_file_")
    def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), new DefaultDirectoryHierarchy(tmpDirectory, "test"))
    def expected = new CsvFileDefinition("node_input.csv", Path.of("test", "input", "grid"), ["a", "b", "c"] as String[], ",")

    when:
    def actual = new CsvFileDefinition(NodeInput, ["a", "b", "c"] as String[], ",", fileNamingStrategy)

    then:
    actual.with {
      assert it.filePath == expected.filePath
      assert it.headLineElements() == expected.headLineElements()
      assert it.csvSep() == expected.csvSep()
    }
  }

  def "The csv file definition throws FileException if it cannot be build from time series"() {
    given:
    def fileNamingStrategy = new FileNamingStrategy()

    and: "credible input"
    def timeSeries = Mock(RepetitiveTimeSeries)

    when:
    new CsvFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",", fileNamingStrategy)

    then:
    def ex = thrown(FileException)
    ex.message == "Cannot determine the file name for time series 'Mock for type 'RepetitiveTimeSeries' named 'timeSeries''."
  }

  def "The csv file definition can be build correctly from time series upon request"() {
    given:
    def fileNamingStrategy = new FileNamingStrategy()
    def expected = new CsvFileDefinition("its_c_0c03ce9f-ab0e-4715-bc13-f9d903f26dbf.csv", Path.of(""), ["a", "b", "c"] as String[], ",")

    and: "credible input"
    def entries = [
      new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(50d, StandardUnits.ENERGY_PRICE)))
    ] as SortedSet
    def timeSeries = Mock(IndividualTimeSeries)
    timeSeries.uuid >> UUID.fromString("0c03ce9f-ab0e-4715-bc13-f9d903f26dbf")
    timeSeries.entries >> entries

    when:
    def actual = new CsvFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",", fileNamingStrategy)

    then:
    actual.with {
      assert it.filePath == expected.filePath
      assert it.headLineElements() == expected.headLineElements()
      assert it.csvSep() == expected.csvSep()
    }
  }

  def "The csv file definition can be build correctly from time series upon request, utilizing directory hierarchy"() {
    given:
    def tmpDirectory = Files.createTempDirectory("psdm_csv_file_")
    def fileNamingStrategy = new FileNamingStrategy(new EntityPersistenceNamingStrategy(), new DefaultDirectoryHierarchy(tmpDirectory, "test"))
    def expected = new CsvFileDefinition("its_c_0c03ce9f-ab0e-4715-bc13-f9d903f26dbf.csv", Path.of("test", "input", "participants", "time_series"), ["a", "b", "c"] as String[], ",")

    and: "credible input"
    def entries = [
      new TimeBasedValue(ZonedDateTime.now(), new EnergyPriceValue(Quantities.getQuantity(50d, StandardUnits.ENERGY_PRICE)))
    ] as SortedSet
    def timeSeries = Mock(IndividualTimeSeries)
    timeSeries.uuid >> UUID.fromString("0c03ce9f-ab0e-4715-bc13-f9d903f26dbf")
    timeSeries.entries >> entries

    when:
    def actual = new CsvFileDefinition(timeSeries, ["a", "b", "c"] as String[], ",", fileNamingStrategy)

    then:
    actual.with {
      assert it.filePath == expected.filePath
      assert it.headLineElements() == expected.headLineElements()
      assert it.csvSep() == expected.csvSep()
    }
  }
}
