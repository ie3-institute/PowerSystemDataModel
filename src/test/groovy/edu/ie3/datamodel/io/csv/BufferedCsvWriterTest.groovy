/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.exceptions.FileException
import edu.ie3.datamodel.exceptions.SinkException
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class BufferedCsvWriterTest extends Specification {
  @Shared
  Path tmpDirectory

  def setup() {
    tmpDirectory = Files.createTempDirectory("psdm_csv_buffered_writer_")
  }

  def cleanup() {
    try {
      FileIOUtils.deleteRecursively(tmpDirectory)
    } catch (IOException e) {
      throw new FileException("Unable to delete recursively.", e)
    }
  }
  def "The convenience constructor of the BufferedCsvWriter class works as expected."() {
    given:
    def baseDirectory = tmpDirectory
    def fileDefinition = new CsvFileDefinition("test.csv", Path.of(""), ["a", "b", "c"] as String[], ",")
    def expectedFile = tmpDirectory.resolve(fileDefinition.filePath).toFile()

    when:
    def actual = new BufferedCsvWriter(baseDirectory, fileDefinition, false)

    then:
    actual.with {
      assert it.headLineElements == ["a", "b", "c"] as String[]
      assert it.csvSep == ","
    }
    expectedFile.exists()
    expectedFile.file // is it a file?
  }

  def "The buffered csv writer refuses to write entries, if their length does not conform the needed length of head line elements"() {
    given:
    def targetFile = tmpDirectory.resolve("test.csv")
    def writer = new BufferedCsvWriter(targetFile, ["a", "b", "c"] as String[], "c,", false)
    def malFormedInput = [
      "a": "z",
      "b": "y"
    ]

    when:
    writer.write(malFormedInput)

    then:
    def e = thrown(SinkException)
    e.message == "The provided data does not meet the pre-defined head line elements 'a,b,c'."
  }

  def "The buffered csv writer refuses to write entries, if keys do not match the required head line"() {
    given:
    def targetFile = tmpDirectory.resolve("test.csv")
    def writer = new BufferedCsvWriter(targetFile, ["a", "b", "c"] as String[], "c,", false)
    def malFormedInput = [
      "a": "z",
      "b": "y",
      "d": "w"
    ]

    when:
    writer.write(malFormedInput)

    then:
    def e = thrown(SinkException)
    e.message == "The provided data does not meet the pre-defined head line elements 'a,b,c'."
  }

  def "The buffered csv writer writes out content in the order specified by the headline elements"() {
    given:
    def targetFile = tmpDirectory.resolve("order_test.csv")
    def writer = new BufferedCsvWriter(targetFile, ["third_header", "second_header", "first_header"] as String[], ",", false)
    writer.writeFileHeader()
    def content = [
      "third_header": "third_value",
      "first_header": "first_value",
      "second_header": "second_value"
    ]

    when:
    writer.write(content)
    writer.close()
    /* Read in the content */
    def writtenContent = ""
    def headline = ""
    try(BufferedReader reader = new BufferedReader(new FileReader(targetFile.toFile()))) {
      headline = reader.readLine()
      writtenContent = reader.readLine()
    } catch (Exception e) {
      throw new FileException("Unable to read content of test file '"+targetFile+"'.", e)
    }

    then:
    headline == "third_header,second_header,first_header"
    writtenContent == "third_value,second_value,first_value"
  }
}
