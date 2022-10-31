/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.IconIdCoordinateFactory
import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
class SqlIdCoordinateSourceIconIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.2")

  @Shared
  SqlIdCoordinateSource source

  static String schemaName = "public"
  static String weatherTableName = "coordinates"

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_coordinates/icon/coordinates.sql")
    postgreSQLContainer.copyFileToContainer(sqlImportFile, "/home/coordinates_icon.sql")
    // Execute import script
    Container.ExecResult res = postgreSQLContainer.execInContainer("psql", "-Utest", "-f/home/coordinates_icon.sql")
    assert res.stderr.empty

    def connector = new SqlConnector(postgreSQLContainer.jdbcUrl, postgreSQLContainer.username, postgreSQLContainer.password)
    def coordinatesFactory = new IconIdCoordinateFactory()
    source = new SqlIdCoordinateSource(connector, schemaName, weatherTableName, coordinatesFactory, 1000)
  }

  def "A SqlIdCoordinateSource can read a single coordinate"(){
    given:
    def expectedValue = new Coordinate(7.438, 51.5)
    when:
    def receivedValue = source.getCoordinate(67775)
    then:
    def coordinate = receivedValue.get().coordinate
    coordinate <=> expectedValue
  }

  def "A SqlIdCoordinateSource can read a list of coordinates"(){
    given:
    def expectedValues = [
      new Coordinate(7.438, 51.5),
      new Coordinate(7.375, 51.5)
    ]
    when:
    int[] arr = {67775; 531137} as int[]
    def receivedValues = source.getCoordinates(arr)
    then:
    ArrayList<Coordinate> points = receivedValues.coordinate

    points.get(0) <=> expectedValues.get(0)
    points.get(1) <=> expectedValues.get(1)
  }

  def "A SqlIdCoordinateSource can return the id of a point"(){
    given:
    int id = 67775
    when:
    def receivedValue = source.getId(GeoUtils.buildPoint(7.438, 51.5))
    then:
    receivedValue.get() <=> id
  }

  def "A SqlIdCoordinateSource can return all coordinates"() {
    given:
    def expectedValues = [
      GeoUtils.buildPoint(7.438, 51.5),
      GeoUtils.buildPoint(7.375, 51.5),
      GeoUtils.buildPoint(7.438, 51.438),
      GeoUtils.buildPoint(7.375, 51.438)
    ]
    when:
    def receivedValues = source.getAllCoordinates()
    then:
    ArrayList<Coordinate> points = receivedValues.coordinate

    points.get(0) <=> expectedValues.get(0).coordinate
    points.get(1) <=> expectedValues.get(1).coordinate
    points.get(2) <=> expectedValues.get(2).coordinate
    points.get(3) <=> expectedValues.get(3).coordinate
  }
}
