/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.CosmoIdCoordinateFactory
import edu.ie3.datamodel.io.factory.timeseries.IconIdCoordinateFactory
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

@Testcontainers
class SqlIdCoordinateSourceIconIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  IdCoordinateSource source

  static String schemaName = "public"
  static String coordinateTableName = "coordinates"

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_coordinates/icon/coordinates.sql")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/coordinates.sql")
    // Execute import script
    Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/coordinates.sql")
    assert res.stderr.empty

    def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    def idCoordinateFactory = new IconIdCoordinateFactory()
    source = new IdCoordinateSource(idCoordinateFactory, new SqlDataSource(connector, schemaName, new DatabaseNamingStrategy()))
  }

  def "The SqlIdCoordinateSource is able to create a valid stream from a coordinate file"() {
    def expectedStream = Stream.of(
            ["id": "67775", "latitude": "51.5", "longitude": "7.438", "coordinatetype": "ICON"],
            ["id": "531137", "latitude": "51.5", "longitude": "7.375", "coordinatetype": "ICON"],
            ["id": "551525", "latitude": "51.438", "longitude": "7.438", "coordinatetype": "ICON"],
            ["id": "278150", "latitude": "51.438", "longitude": "7.375", "coordinatetype": "ICON"]
    )

    when:
    def actualStream = source.extractSourceData()

    then:
    actualStream.collect(Collectors.toList()).containsAll(expectedStream.collect(Collectors.toList()))
  }

  def "The SqlIdCoordinateSource is able to look up a specific point or an empty Optional otherwise" () {
    given:
    def knownCoordinateId = 551525
    def expectedPointA = Optional.of(GeoUtils.buildPoint(51.438, 7.438))
    def unknownCoordinateId = 42

    when: "looking up a known coordinate id"
    def actualPointA = source.getCoordinate(knownCoordinateId)

    then: "we get the expected point"
    actualPointA == expectedPointA

    when: "looking up an unknown coordinate id"
    def actualPointB = source.getCoordinate(unknownCoordinateId)

    then: "we get an empty optional"
    actualPointB == Optional.empty()
  }

  def "The SqlIdCoordinateSource is able to look up specified points" () {
    int[] ids = [67775, 551525, 278150]
    def expectedCoordinates = [
            GeoUtils.buildPoint(51.5, 7.438),
            GeoUtils.buildPoint(51.438, 7.438),
            GeoUtils.buildPoint(51.438, 7.375)
    ].toSet()

    when:
    def actualCoordinates = source.getCoordinates(ids)

    then:
    actualCoordinates == expectedCoordinates
  }

  def "The SqlIdCoordinateSource is able to return a specific ID or an empty Optional otherwise" () {
    def knownCoordinate = GeoUtils.buildPoint(51.438, 7.438)
    def expectedIdForA = Optional.of(551525)
    def unknownCoordinate = GeoUtils.buildPoint(48.035011, 14.39335)

    when: "looking up an id of a known coordinate"
    def actualIdForA = source.getId(knownCoordinate)

    then: "we get the matching id"
    actualIdForA == expectedIdForA

    when: "looking up an unknown coordinate"
    def actualIdForB = source.getId(unknownCoordinate)

    then: "we get nothing"
    actualIdForB == Optional.empty()
  }

  def "The SqlIdCoordinateSource is able to return a count of all available coordinates" () {
    given:
    def expectedCount = 4

    when:
    def actualCount = source.coordinateCount

    then:
    actualCount == expectedCount
  }

  def "The SqlIdCoordinateSource is able to return all available coordinates" () {
    given:
    def expectedCoordinates = [
            GeoUtils.buildPoint(51.5, 7.438),
            GeoUtils.buildPoint(51.5, 7.375),
            GeoUtils.buildPoint(51.438, 7.438),
            GeoUtils.buildPoint(51.438, 7.375)
    ].toSet()

    when:
    def actualCoordinates = source.allCoordinates.toSet()

    then:
    actualCoordinates == expectedCoordinates
  }

  def "The SqlIdCoordinateSource is able to return the nearest n coordinates in a collection" () {
    given:
    def allCoordinates = [
            GeoUtils.buildPoint(39d, 1d),
            GeoUtils.buildPoint(40d, 2d),
            GeoUtils.buildPoint(40d, 1d),
            GeoUtils.buildPoint(39d, 2d)
    ]

    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def expectedDistances = [
            new CoordinateDistance(basePoint, allCoordinates[2]),
            new CoordinateDistance(basePoint, allCoordinates[1])
    ].sort()

    when:
    def actualDistances = source.getNearestCoordinates(basePoint, 2, allCoordinates)

    then:
    actualDistances == expectedDistances
  }

  def "If no collection is given, the SqlIdCoordinateSource is able to return the nearest n coordinates of all available coordinates" () {
    given:
    def n = 2
    def allCoordinates = source.allCoordinates
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def expectedDistances = source.getNearestCoordinates(basePoint, n, allCoordinates)

    when:
    def actualDistances = source.getNearestCoordinates(basePoint, n)

    then:
    actualDistances == expectedDistances
  }
}