/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.json

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.connectors.JsonFileConnector
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.Entity
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path

class JsonDataSourceTest extends Specification {

  Path tempDir
  JsonDataSource dataSource

  def setup() {
    tempDir = Files.createTempDirectory("jsonDataSource")
    dataSource = new JsonDataSource(tempDir, new FileNamingStrategy())
  }

  def cleanup() {
    if (tempDir != null) {
      Files.walk(tempDir)
          .sorted(Comparator.reverseOrder())
          .forEach { Files.deleteIfExists(it) }
    }
  }

  def "initInputStream opens JSON file via connector"() {
    given:
    def file = tempDir.resolve("sample.json")
    Files.writeString(file, """{"key":42}""")

    when:
    def content = dataSource.initInputStream(Path.of("sample")).text

    then:
    content == """{"key":42}"""
  }

  def "readTree parses JSON file into a JsonNode tree"() {
    given:
    def file = tempDir.resolve("data.json")
    Files.writeString(file, """{"answer":42}""")

    when:
    def node = dataSource.readTree(Path.of("data"))

    then:
    node.get("answer").asInt() == 42
  }

  def "readTree throws SourceException when file does not exist"() {
    when:
    dataSource.readTree(Path.of("nonexistent"))

    then:
    thrown(SourceException)
  }

  def "getSourceFields returns field names from JSON file"() {
    given:
    def file = tempDir.resolve("fields.json")
    Files.writeString(file, """{"foo":"bar","nested":{"a":1}}""")

    when:
    def fields = dataSource.getSourceFields(Path.of("fields"))

    then:
    fields.isPresent()
    fields.get().contains("foo")
    fields.get().contains("nested.a")
  }

  def "tabular access methods are unsupported"() {
    when:
    dataSource.getSourceFields(Entity)

    then:
    thrown(UnsupportedOperationException)

    when:
    dataSource.getSourceData(Entity)

    then:
    thrown(UnsupportedOperationException)
  }
}
