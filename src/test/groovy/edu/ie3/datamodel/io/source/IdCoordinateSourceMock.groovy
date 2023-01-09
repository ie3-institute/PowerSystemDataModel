/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.util.geo.CoordinateDistance
import org.locationtech.jts.geom.Point
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Length

class IdCoordinateSourceMock implements IdCoordinateSource {
  @Override
  Optional<Point> getCoordinate(int id) {
    return Optional.empty()
  }

  @Override
  Collection<Point> getCoordinates(int ... ids) {
    return Collections.emptyList()
  }

  @Override
  Optional<Integer> getId(Point coordinate) {
    return Optional.empty()
  }

  @Override
  Collection<Point> getAllCoordinates() {
    return Collections.emptyList()
  }

  @Override
  List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    return Collections.emptyList()
  }

  @Override
  List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n, ComparableQuantity<Length> distance) {
    return Collections.emptyList()
  }
}
