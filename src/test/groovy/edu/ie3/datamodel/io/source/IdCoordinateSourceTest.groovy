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

class IdCoordinateSourceTest extends Specification implements IdCoordinateSource {
  private Point point0 = GeoUtils.buildPoint(52.5, 7.5)
  private Point point1 = GeoUtils.buildPoint(53, 8)
  private Point point2 = GeoUtils.buildPoint(53, 7)
  private Point point3 = GeoUtils.buildPoint(53, 6)
  private Point point4 = GeoUtils.buildPoint(52, 8)
  private Point point5 = GeoUtils.buildPoint(52, 7)
  private Point point6 = GeoUtils.buildPoint(52, 6)
  private Point point7 = GeoUtils.buildPoint(51, 8)
  private Point point8 = GeoUtils.buildPoint(51, 7)
  private Point point9 = GeoUtils.buildPoint(51, 6)

  private ArrayList<Point> points = List.of(
  point1,
  point2,
  point3,
  point4,
  point5,
  point6,
  point7,
  point8,
  point9
  )

  private Point coordinate = GeoUtils.buildPoint(50, 7)

  def "IdCoordinateSource should calculate y-delta correctly"() {
    given:
    double distance = GeoUtils.calcHaversine(52, 7, 51, 7).getValue().doubleValue()

    when:
    double[] deltas = calculateXYDelta(coordinate, distance)

    then:
    deltas[1] == 1
  }

  def "IdCoordinateSource should calculate x-delta correctly"() {
    given:
    double distance = GeoUtils.calcHaversine(50, 6, 50, 5).getValue().doubleValue()
    when:
    double[] deltas = calculateXYDelta(coordinate, distance)

    then:
    deltas[0] == 1
  }

  def "IdCoordinateSource should return correct number of corner points restricted to the bounding box"() {
    given:
    ArrayList<Point> expectedPoints = new ArrayList<>()
    expectedPoints.addAll(
        point2,
        point4,
        point5,
        point6,
        point8
        )

    when:
    List<CoordinateDistance> distances = getNearestCoordinates(point0, 9, points)
    List<CoordinateDistance> result = restrictToBoundingBoxWithSetNumberOfCorner(point0, distances, 4)

    then:
    for(CoordinateDistance value : result){
      expectedPoints.contains(value.coordinateB)
    }
  }

  def "IdCoordinateSource should return only one point of the bounding box if the starting coordinate exactly matched the found coordinate"() {
    given:
    Point matchingPoint = GeoUtils.buildPoint(52.5, 7.5)

    when:
    List<Point> withExactMatch = new ArrayList<>(points)
    withExactMatch.addAll(matchingPoint)

    List<CoordinateDistance> distances = getNearestCoordinates(point0, 9, withExactMatch)
    List<CoordinateDistance> result = restrictToBoundingBoxWithSetNumberOfCorner(point0, distances, 4)

    then:
    result.size() == 1
    result.get(0).coordinateB == matchingPoint
  }

  @Override
  Optional<Point> getCoordinate(int id) {
    return null
  }

  @Override
  Collection<Point> getCoordinates(int ... ids) {
    return null
  }

  @Override
  Optional<Integer> getId(Point coordinate) {
    return null
  }

  @Override
  Collection<Point> getAllCoordinates() {
    return null
  }

  @Override
  List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n, double maxDistance) {
    return null
  }
}
