/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Length
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class WeatherTestData {

  protected static final class DummyIdCoordinateSource implements IdCoordinateSource {

    @Override
    Optional<Point> getCoordinate(int id) {
      switch (id) {
        case 193186: return Optional.of(GeoUtils.buildPoint(7d, 49d))
        case 193187: return Optional.of(GeoUtils.buildPoint(8d, 49d))
        case 193188: return Optional.of(GeoUtils.buildPoint(7d, 50d))
        case 67775: return Optional.of(GeoUtils.buildPoint(8d, 50d))
        case 67776: return Optional.of(GeoUtils.buildPoint(7d, 51d))
      }
      return Optional.empty()
    }
    @Override
    Collection<Point> getCoordinates(int... ids) {
      return Stream.of(ids).map(this.&getCoordinate).filter({ c -> c != null }).collect(Collectors.toSet())
    }
    @Override
    Optional<Integer> getId(Point coordinate) {
      if (coordinate.x == 49 && coordinate.y == 7) {
        return Optional.of(193186)
      }
      if (coordinate.x == 49 && coordinate.y == 8) {
        return Optional.of(193187)
      }
      if (coordinate.x == 50 && coordinate.y == 7) {
        return Optional.of(193188)
      }
      if (coordinate.x == 50 && coordinate.y == 8) {
        return Optional.of(67775)
      }
      if (coordinate.x == 51 && coordinate.y == 7) {
        return Optional.of(67776)
      }
      return Optional.empty()
    }

    @Override
    Collection<Point> getAllCoordinates() {
      return [
        GeoUtils.buildPoint(7d, 49d),
        GeoUtils.buildPoint(8d, 49d),
        GeoUtils.buildPoint(7d, 50d),
        GeoUtils.buildPoint(8d, 50d),
        GeoUtils.buildPoint(7d, 51d)
      ]
    }

    @Override
    List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n, ComparableQuantity<Length> distance) {
      return null
    }
  }

  public static final IdCoordinateSource coordinateSource = new DummyIdCoordinateSource()

  public static final COORDINATE_193186 = coordinateSource.getCoordinate(193186).get()
  public static final COORDINATE_193187 = coordinateSource.getCoordinate(193187).get()
  public static final COORDINATE_193188 = coordinateSource.getCoordinate(193188).get()
  public static final COORDINATE_67775 = coordinateSource.getCoordinate(67775).get()
  public static final COORDINATE_67776 = coordinateSource.getCoordinate(67776).get()
}