/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point
import spock.lang.Specification

class IdCoordinateSourceTest extends Specification {
  private final IdCoordinateSourceMock coordinateSourceMock = new IdCoordinateSourceMock()

  private final Point point0 = GeoUtils.buildPoint(52.5, 7.5)
  private final Point point1 = GeoUtils.buildPoint(53, 7.9)
  private final Point point2 = GeoUtils.buildPoint(53, 7)
  private final Point point3 = GeoUtils.buildPoint(53, 6.2)
  private final Point point4 = GeoUtils.buildPoint(52, 7.9)
  private final Point point5 = GeoUtils.buildPoint(52, 7)
  private final Point point6 = GeoUtils.buildPoint(52, 6.2)
  private final Point point7 = GeoUtils.buildPoint(51, 7.9)
  private final Point point8 = GeoUtils.buildPoint(51, 7)
  private final Point point9 = GeoUtils.buildPoint(51, 6.2)

  private final List<Point> points = [
    point1,
    point2,
    point3,
    point4,
    point5,
    point6,
    point7,
    point8,
    point9
  ]

  def "IdCoordinateSource should return only the corner points of a collection of coordinate distances"() {
    given:
    List<Point> expectedPoints = [
      point1,
      point2,
      point4,
      point5
    ]

    when:
    List<CoordinateDistance> distances = GeoUtils.calcOrderedCoordinateDistances(point0, points)
    List<CoordinateDistance> result = coordinateSourceMock.findCornerPoints(point0, distances)

    then:
    result.size() == expectedPoints.size()
    result*.coordinateB.containsAll(expectedPoints)
  }

  def "IdCoordinateSource should return only one point if the starting coordinate exactly matched the found coordinate"() {
    given:
    Point matchingPoint = GeoUtils.buildPoint(52.5, 7.5)

    when:
    List<Point> withExactMatch = new ArrayList<>(points)
    withExactMatch.addAll(matchingPoint)

    List<CoordinateDistance> distances = GeoUtils.calcOrderedCoordinateDistances(point0, withExactMatch)
    List<CoordinateDistance> result = coordinateSourceMock.findCornerPoints(point0, distances)

    then:
    result.size() == 1
    result.get(0).coordinateB == matchingPoint
  }
}
