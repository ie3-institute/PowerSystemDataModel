/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.io.source.csv.CsvTestDataMeta
import edu.ie3.util.geo.CoordinateDistance
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point
import tech.units.indriya.ComparableQuantity

import java.util.stream.Collectors
import java.util.stream.Stream
import javax.measure.quantity.Length

abstract class WeatherTestData {

  static class DummyIdCoordinateSource extends IdCoordinateSource implements CsvTestDataMeta {

    @Override
    Optional<Set<String>> getSourceFields() throws SourceException {
      return Optional.empty()
    }

    private static final Map<Integer, Point> MAPPING = [
      (193186): GeoUtils.buildPoint(7d, 49d),
      (193187): GeoUtils.buildPoint(8d, 49d),
      (193188): GeoUtils.buildPoint(7d, 50d),
      (67775) : GeoUtils.buildPoint(8d, 50d),
      (67776) : GeoUtils.buildPoint(7d, 51d)
    ]

    @Override
    Point getCoordinate(int id) throws SourceException {
      return getCoordinatesById([id]).get(id)
    }

    @Override
    Map<Integer, Point> getCoordinatesById(Collection<Integer> ids) throws SourceException {
      Map<Integer, Point> resolved = [:]
      ids.each { id ->
        if (MAPPING.containsKey(id)) resolved.put(id, MAPPING.get(id))
      }
      return checkForMissingIds(ids, resolved)
    }

    @Override
    int getId(Point coordinate) throws SourceException {
      if (coordinate.x == 49 && coordinate.y == 7) return 193186
      if (coordinate.x == 49 && coordinate.y == 8) return 193187
      if (coordinate.x == 50 && coordinate.y == 7) return 193188
      if (coordinate.x == 50 && coordinate.y == 8) return 67775
      if (coordinate.x == 51 && coordinate.y == 7) return 67776
      throw new SourceException("No coordinate id found for the point " + coordinate)
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
    List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
      throw new UnsupportedOperationException("This method is not supported!")
    }

    @Override
    List<CoordinateDistance> getClosestCoordinates(Point coordinate, int n, ComparableQuantity<Length> distance) {
      throw new UnsupportedOperationException("This method is not supported!")
    }

    @Override
    List<CoordinateDistance> findCornerPoints(Point coordinate, ComparableQuantity<Length> distance) {
      throw new UnsupportedOperationException("This method is not supported!")
    }

    @Override
    void validate() throws ValidationException {
    }
  }

  public static final IdCoordinateSource coordinateSource = new DummyIdCoordinateSource()

  public static final Point COORDINATE_193186 = coordinateSource.getCoordinate(193186)
  public static final Point COORDINATE_193187 = coordinateSource.getCoordinate(193187)
  public static final Point COORDINATE_193188 = coordinateSource.getCoordinate(193188)
  public static final Point COORDINATE_67775 = coordinateSource.getCoordinate(67775)
  public static final Point COORDINATE_67776 = coordinateSource.getCoordinate(67776)
}