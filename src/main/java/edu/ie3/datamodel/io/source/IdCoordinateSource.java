/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.util.*;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/**
 * This class serves mapping purposes between the ID of a coordinate and the actual coordinate with
 * latitude and longitude values, which is especially needed for data source that don't offer
 * combined primary or foreign keys.
 */
public abstract class IdCoordinateSource extends EntitySource {

  /**
   * Method to retrieve the fields found in the source.
   *
   * @return an option for the found fields
   */
  public abstract Optional<Set<String>> getSourceFields() throws SourceException;

  /**
   * Get the matching coordinate for the given ID
   *
   * @param id the ID to look up
   * @return matching coordinate
   */
  public abstract Optional<Point> getCoordinate(int id);

  /**
   * Get the matching coordinates for the given IDs
   *
   * @param ids the IDs to look up
   * @return the matching coordinates
   */
  public abstract Collection<Point> getCoordinates(int... ids);

  /**
   * Get the ID for the coordinate point
   *
   * @param coordinate the coordinate to look up
   * @return the matching ID
   */
  public abstract Optional<Integer> getId(Point coordinate);

  /**
   * Returns all the coordinates of this source
   *
   * @return all available coordinates
   */
  public abstract Collection<Point> getAllCoordinates();

  /**
   * Returns the nearest n coordinate points. If n is greater than four, this method will try to
   * return the corner points of the bounding box.
   *
   * @param coordinate the coordinate to look up
   * @param n number of searched points
   * @return the nearest n coordinates or all coordinates if n is less than all available points
   */
  public abstract List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n);

  /**
   * Returns the closest n coordinate points to the given coordinate, that are inside a given
   * bounding box, from a collection of all available points. The bounding box is calculated with
   * the given distance. If n is greater than four, this method will try to return the corner points
   * of the bounding box.
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param distance to the borders of the envelope that contains the coordinates
   * @return the nearest n coordinates to the given point
   */
  public abstract List<CoordinateDistance> getClosestCoordinates(
      Point coordinate, int n, ComparableQuantity<Length> distance);

  /**
   * Calculates and returns the nearest n coordinate distances to the given coordinate from a given
   * collection of points. If the set is empty or null an empty list is returned.
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param coordinates the collection of points
   * @return a list of the nearest n coordinates to the given point or an empty list
   */
  public List<CoordinateDistance> calculateCoordinateDistances(
      Point coordinate, int n, Collection<Point> coordinates) {
    if (coordinates != null && !coordinates.isEmpty()) {
      return GeoUtils.calcOrderedCoordinateDistances(coordinate, coordinates).stream()
          .limit(n)
          .toList();
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Method for finding the corner points of a given coordinate within a given distance.
   *
   * <p>The max. number of returned corner points is set by the implementation (default: 4).
   *
   * @param coordinate at the center
   * @param distance list of fount points with their distances
   * @return either a list with one exact match or a list of corner points (default implementation:
   *     max. 4 points)
   */
  List<CoordinateDistance> findCornerPoints(Point coordinate, ComparableQuantity<Length> distance);

  /**
   * Method for finding the corner points of a given coordinate. If a point matches the given
   * coordinate, only this point is returned.
   *
   * <p>The max. number of returned corner points is set by the implementation (default: 4).
   *
   * <p>To work properly, the given collection of {@link CoordinateDistance}'s should already be
   * sorted by distance.
   *
   * @param coordinate at the center
   * @param coordinateDistances list of fount points with their distances
   * @return either a list with one exact match or a list of corner points (default implementation:
   *     max. 4 points)
   */
  public List<CoordinateDistance> findCornerPoints(
      Point coordinate, Collection<CoordinateDistance> coordinateDistances) {
    boolean topLeft = false;
    boolean topRight = false;
    boolean bottomLeft = false;
    boolean bottomRight = false;

    List<CoordinateDistance> resultingDistances = new ArrayList<>();

    // search for smallest bounding box
    for (CoordinateDistance distance : coordinateDistances) {
      Point point = distance.getCoordinateB();

      // check for bounding box
      if (coordinate.equalsExact(point, 1e-6)) {
        // if current point is matching the given coordinate, we need to return only the current
        // point
        return List.of(distance);
      } else if (!topLeft
          && (point.getX() <= coordinate.getX() && point.getY() >= coordinate.getY())) {
        resultingDistances.add(distance);
        topLeft = true;
      } else if (!topRight
          && (point.getX() >= coordinate.getX() && point.getY() >= coordinate.getY())) {
        resultingDistances.add(distance);
        topRight = true;
      } else if (!bottomLeft
          && (point.getX() <= coordinate.getX() && point.getY() <= coordinate.getY())) {
        resultingDistances.add(distance);
        bottomLeft = true;
      } else if (!bottomRight
          && (point.getX() >= coordinate.getX() && point.getY() <= coordinate.getY())) {
        resultingDistances.add(distance);
        bottomRight = true;
      }
    }

    return resultingDistances;
  }
}
