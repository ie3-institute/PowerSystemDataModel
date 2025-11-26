/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.connectors

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class JsonFileConnectorTest extends Specification {

  Path tempDir

  def setup() {
    tempDir = Files.createTempDirectory("jsonFileConnector")
  }

  def cleanup() {
    if (tempDir != null) {
      Files.walk(tempDir)
          .sorted(Comparator.reverseOrder())
          .forEach { Files.deleteIfExists(it) }
    }
  }

  def "initInputStream resolves .json ending and reads content"() {
    given:
    def file = tempDir.resolve("model.json")
    Files.writeString(file, """{"foo":"bar"}""")
    def connector = new JsonFileConnector(tempDir)

    when:
    def content = connector.initInputStream(Path.of("model")).text

    then:
    content == """{"foo":"bar"}"""
  }

  def "initReader returns buffered reader with UTF-8 decoding"() {
    given:
    def file = tempDir.resolve("data.json")
    Files.writeString(file, "[1,2,3]")
    def connector = new JsonFileConnector(tempDir)

    when:
    def reader = connector.initReader(Path.of("data"))
    def line = reader.readLine()

    then:
    line == "[1,2,3]"
  }
}
