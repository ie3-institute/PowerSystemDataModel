/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.io.source.EntitySource.*

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.input.OperatorInputFactory
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path

class EntitySourceTest extends Specification {

  def "An EntitySource validates required built-in resources"() {
    when:
    def source = getBuildInSource(EntitySource, "/type")

    then:
    source
  }

  def "EntitySource returns the jar file system for a given jar uri"() {
    given:
    Path jarPath = new File(GroovyObject.class.protectionDomain.codeSource.location.toURI()).toPath()
    URI jarUri = URI.create("jar:" + jarPath.toUri())

    when:
    FileSystem fileSystem = getJarFileSystem(jarUri)

    then:
    fileSystem
    fileSystem == FileSystems.getFileSystem(jarUri)
  }

  def "An EntitySource can build a map of entities correctly"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString(), "id": GridTestData.profBroccoli.id]
    def source = DummyDataSource.of(parameter)

    when:
    def actual = getEntityMap(OperatorInput, source, new OperatorInputFactory())

    then:
    actual.size() == 1
    OperatorInput input = actual.get(GridTestData.profBroccoli.uuid)
    input.id == GridTestData.profBroccoli.id
  }

  def "An EntitySource throws a SourceException if an entity can not be build"() {
    given:
    Map<String, String> parameter = ["uuid": GridTestData.profBroccoli.uuid.toString()]
    def source = DummyDataSource.of(parameter)

    when:
    getEntities(OperatorInput, source, new OperatorInputFactory())

    then:
    SourceException ex = thrown()
    ex.message == "1 exception(s) occurred within \"OperatorInput\" data: \n" +
        "        An error occurred in OperatorInputFactory. Caused by: Field \"id\" not found in source."
  }
}
