/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.utils.CoordinateDistance;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import java.util.*;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

/**
 * This class serves mapping purposes between the ID of a coordinate and the actual coordinate with
 * latitude and longitude values, which is especially needed for data source that don't offer
 * combined primary or foreign keys.
 */
public interface IdCoordinateSource extends DataSource {

  /**
   * Get the matching coordinate for the given ID
   *
   * @param id the ID to look up
   * @return matching coordinate
   */
  Optional<Point> getCoordinate(int id);

  /**
   * Get the matching coordinates for the given IDs
   *
   * @param ids the IDs to look up
   * @return the matching coordinates
   */
  Collection<Point> getCoordinates(int... ids);

  /**
   * Get the ID for the coordinate point
   *
   * @param coordinate the coordinate to look up
   * @return the matching ID
   */
  Optional<Integer> getId(Point coordinate);

  /**
   * Returns all the coordinates of this source
   *
   * @return all available coordinates
   */
  Collection<Point> getAllCoordinates();

  /**
   * Returns the nearest n coordinate points to the given coordinate from a collection of all
   * available points
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @return the n nearest coordinates to the given point
   */
  default List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    return getNearestCoordinates(coordinate, n, Collections.emptySet());
  }

  /**
   * Returns the nearest n coordinate points to the given coordinate from a given collection of
   * points
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param allCoordinates the collection of points, ideally containing all available coordinates
   * @return the n nearest coordinates to the given point
   */
  default List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, Collection<Point> allCoordinates) {
    SortedSet<CoordinateDistance> sortedDistances =
        GridAndGeoUtils.getCoordinateDistances(
            coordinate,
            (allCoordinates == null || allCoordinates.isEmpty())
                ? getAllCoordinates()
                : allCoordinates);
    return sortedDistances.stream().limit(n).collect(Collectors.toList());
  }
}
