/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.io.IoUtil
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class FileUtilsTest extends Specification {
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
    def file = FileUtils.of("name", path)

    then:
    file == expectedPath

    where:
    path || expectedPath
    IoUtil.pathOption("") || Path.of("name")
  }

  def "A file definition of a csv file is set up correctly, if the directory path has corrupt file separator" () {
    when:
    def file = FileUtils.ofCsv(fileName, manipulatedDirectory)

    then:
    file.with {
      assert it.fileName == Path.of(this.fileName)
      assert it == this.directory.resolve(this.fileName)
    }

    where:
    manipulatedDirectory                                                       || expected
    Path.of(this.directory.toString(), "/")                                    || this.directory
    Path.of(this.directory.toString().replaceAll("[\\\\/]", File.separator == "/" ? "\\\\" : "/")) || this.directory
  }

  def "A file definition of a csv file is set up correctly, if the directory path is null" () {
    when:
    def file = FileUtils.ofCsv(fileName, null)

    then:
    file.with {
      assert it.fileName == Path.of(this.fileName)
      assert it.relativize(it.fileName) == Path.of("")
      assert it.parent == null
    }
  }
}
