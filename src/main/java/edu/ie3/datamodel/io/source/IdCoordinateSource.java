/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.utils.CoordinateDistance;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;

public interface IdCoordinateSource extends DataSource {

  /**
   * Get the matching coordinate for the given ID
   *
   * @param id the ID to look up
   * @return matching coordinate
   */
  Point getCoordinate(Integer id);

  /**
   * Get the matching coordinates for the given IDs
   *
   * @param ids the IDs to look up
   * @return the matching coordinates
   */
  Collection<Point> getCoordinates(Integer... ids);

  /**
   * Get the ID for the coordinate point
   *
   * @param coordinate the coordinate to look up
   * @return the matching ID
   */
  Integer getId(Point coordinate);

  /**
   * Returns all the coordinates of this source
   *
   * @return all available coordinates
   */
  Collection<Point> getAllCoordinates();

  /**
   * Returns the nearest n coordinate points to the given coordinate
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @return the n nearest coordinates to the given point
   */
  default List<CoordinateDistance> getNearestCoordinates(Point coordinate, Integer n) {
    SortedSet<CoordinateDistance> sortedDistances =
        GridAndGeoUtils.getCoordinateDistances(coordinate, getAllCoordinates());
    return sortedDistances.stream().limit(n).collect(Collectors.toList());
  }
}
