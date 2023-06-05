/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.csv

import edu.ie3.datamodel.io.IoUtil
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class FileDefinitionTest extends Specification {
  @Shared
  String fileName

  @Shared
  Path directory

  def setupSpec() {
    fileName = "node_input.csv"
    directory = Path.of("test", "grid")
  }

  def "A file definition is et up correctly, if an empty path is given" () {
    when:
    def file = FileDefinition.of("name", path)

    then:
    file.fullPath == expectedPath

    where:
    path || expectedPath
    IoUtil.pathOption("") || Path.of("name")
    IoUtil.pathOption("/") || Path.of("name")
  }

  def "A file definition of a csv file is set up correctly, if the directory path has corrupt file separator" () {
    when:
    def file = FileDefinition.ofCsvFile(fileName, manipulatedDirectory)

    then:
    file.with {
      assert it.directoryPath == this.directory
      assert it.fileName == this.fileName
      assert it.fullPath == this.directory.resolve(this.fileName)
    }

    where:
    manipulatedDirectory                                                       || expected
    Path.of("/").resolve(this.directory)                                       || this.directory
    Path.of(this.directory.toString(), "/")                                    || this.directory
    Path.of(this.directory.toString().replaceAll("[\\\\/]", File.separator == "/" ? "\\\\" : "/")) || this.directory
  }

  def "A file definition of a csv file is set up correctly, if the directory path is null" () {
    when:
    def file = FileDefinition.ofCsvFile(fileName, null)

    then:
    file.with {
      assert it.fileName == this.fileName
      assert it.directoryPath == Path.of("")
    }
  }

  def "A file definition returns correct file path"() {
    given:
    def file = FileDefinition.ofCsvFile(fileName, manipulatedDirectory)

    when:
    def actual = file.fullPath

    then:
    actual == expected

    where:
    manipulatedDirectory    || expected
    Path.of("")             || Path.of(this.fileName)
    Path.of("test", "grid") || Path.of("test", "grid", this.fileName)
  }
}
