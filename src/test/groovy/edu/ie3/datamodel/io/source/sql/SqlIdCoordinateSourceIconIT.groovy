/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.sql

import edu.ie3.datamodel.io.connectors.SqlConnector
import edu.ie3.datamodel.io.factory.timeseries.IconIdCoordinateFactory

import edu.ie3.test.helper.TestContainerHelper
import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.testcontainers.containers.Container
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import javax.measure.quantity.Length

@Testcontainers
class SqlIdCoordinateSourceIconIT extends Specification implements TestContainerHelper {

  @Shared
  PostgreSQLContainer postgisSQLContainer = new PostgreSQLContainer(DockerImageName.parse("postgis/postgis:14-3.3").asCompatibleSubstituteFor("postgres"))

  @Shared
  SqlIdCoordinateSource source

  static String schemaName = "public"
  static String coordinateTableName = "coordinates"

  def setupSpec() {
    // Copy sql import script into docker
    MountableFile sqlImportFile = getMountableFile("_coordinates/icon/coordinates.sql")
    postgisSQLContainer.copyFileToContainer(sqlImportFile, "/home/coordinates_icon.sql")
    // Execute import script
    Container.ExecResult res = postgisSQLContainer.execInContainer("psql", "-Utest", "-f/home/coordinates_icon.sql")
    assert res.stderr.empty

    def connector = new SqlConnector(postgisSQLContainer.jdbcUrl, postgisSQLContainer.username, postgisSQLContainer.password)
    def coordinatesFactory = new IconIdCoordinateFactory()
    source = new SqlIdCoordinateSource(connector, schemaName, coordinateTableName, coordinatesFactory)
  }

  def "A SqlIdCoordinateSource can read a single coordinate"(){
    given:
    def expectedValue = new Coordinate(7.438, 51.5)

    when:
    def receivedValue = source.getCoordinate(67775)

    then:
    def coordinate = receivedValue.get().coordinate
    coordinate == expectedValue
  }

  def "A SqlIdCoordinateSource can read a list of coordinates"(){
    given:
    def expectedValues = [
      new Coordinate(7.438, 51.5),
      new Coordinate(7.375, 51.5)
    ]

    when:
    int[] arr = new int[]{
      67775, 531137
    }
    def receivedValues = source.getCoordinates(arr)

    then:
    ArrayList<Coordinate> points = receivedValues.coordinate

    points.get(0) == expectedValues.get(0)
    points.get(1) == expectedValues.get(1)
  }

  def "A SqlIdCoordinateSource can return the id of a point"(){
    given:
    int id = 67775

    when:
    def receivedValue = source.getId(GeoUtils.buildPoint(51.5, 7.438))

    then:
    receivedValue.get() == id
  }

  def "A SqlIdCoordinateSource can return all coordinates"() {
    given:
    def expectedValues = [
      GeoUtils.buildPoint(51.5,7.438),
      GeoUtils.buildPoint(51.5,7.375),
      GeoUtils.buildPoint(51.438,7.438),
      GeoUtils.buildPoint(51.438,7.375)
    ]

    when:
    def receivedValues = source.getAllCoordinates()

    then:

    for(Point point : receivedValues){
      expectedValues.contains(point.coordinate)
    }
  }

  def "A SqlIdCoordinateSource can return the nearest n coordinates if n coordinates are in the given radius"(){
    given:
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def distance = Quantities.getQuantity(200000, Units.METRE)

    when:
    def actualDistances = source.getNearestCoordinates(basePoint, 3, distance)

    then:
    actualDistances.size() == 3
  }

  def "A SqlIdCoordinateSource will return the nearest m coordinates if less than n coordinates are in the given radius"(){
    given:
    def basePoint = GeoUtils.buildPoint(51.5, 7.38)
    def distance = Quantities.getQuantity(1000, Units.METRE)

    when:
    def actualDistances = source.getNearestCoordinates(basePoint, 2, distance)

    then:
    actualDistances.size() == 1
  }

  def "A SqlIdCoordinateSource will return the nearest n coordinates of all coordinates if no coordinates are in the given radius"(){
    given:
    def basePoint = GeoUtils.buildPoint(39.617162, 1.438029)
    def expectedValues = [
      GeoUtils.buildPoint(51.5,7.438),
      GeoUtils.buildPoint(51.5,7.375),
      GeoUtils.buildPoint(51.438,7.438),
      GeoUtils.buildPoint(51.438,7.375)
    ]
    def distance = Quantities.getQuantity(1000, Units.METRE)

    when:
    def receivedValues = source.getNearestCoordinates(basePoint, 3, distance)

    then:
    for(CoordinateDistance coordinateDistance : receivedValues){
      expectedValues.contains(coordinateDistance.coordinateB)
    }
  }
}
