/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import org.apache.commons.io.FilenameUtils
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

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
}
