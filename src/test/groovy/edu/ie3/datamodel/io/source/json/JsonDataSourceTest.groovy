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
