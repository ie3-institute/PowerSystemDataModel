/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

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
public interface IdCoordinateSource {

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
   * Returns the nearest n coordinate points. If n is greater than four, this method will try to
   * return the corner points of the bounding box.
   *
   * @param coordinate the coordinate to look up
   * @param n number of searched points
   * @return the nearest n coordinates or all coordinates if n is less than all available points
   */
  List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n);

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
  List<CoordinateDistance> getClosestCoordinates(
      Point coordinate, int n, ComparableQuantity<Length> distance);

  /**
   * Calculates and returns the nearest n coordinate distances to the given coordinate from a given
   * collection of points. If the set is empty or null an empty list is returned. If n is greater
   * than four, this method will try to return the corner points of the bounding box.
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param coordinates the collection of points
   * @return a list of the nearest n coordinates to the given point or an empty list
   */
  default List<CoordinateDistance> calculateCoordinateDistances(
      Point coordinate, int n, Collection<Point> coordinates) {
    if (coordinates != null && !coordinates.isEmpty()) {
      SortedSet<CoordinateDistance> sortedDistances =
          GeoUtils.calcOrderedCoordinateDistances(coordinate, coordinates);
      return restrictToBoundingBox(coordinate, sortedDistances, n);
    } else {
      return Collections.emptyList();
    }
  }

  /**
   * Method for evaluating the found points. This method tries to return the four corner points of
   * the bounding box of the given coordinate. If one of the found points matches the given
   * coordinate, only this point is returned. If the given number of searched points is less than
   * four, this method will only return the nearest n corner points. If the given number of searched
   * points is greater than four, this method will return the four corner points plus the nearest n
   * points to match the number of searched points.
   *
   * <p>To work properly, the given collection of {@link CoordinateDistance}'s should already be
   * sorted by distance.
   *
   * @param coordinate at the center of the bounding box
   * @param distances list of found points with their distances
   * @param numberOfPoints that should be returned
   * @return list of distances
   */
  default List<CoordinateDistance> restrictToBoundingBox(
      Point coordinate, Collection<CoordinateDistance> distances, int numberOfPoints) {
    boolean topLeft = false;
    boolean topRight = false;
    boolean bottomLeft = false;
    boolean bottomRight = false;

    List<CoordinateDistance> resultingDistances = new ArrayList<>();
    List<CoordinateDistance> other = new ArrayList<>();

    // search for smallest bounding box
    for (CoordinateDistance distance : distances) {
      Point point = distance.getCoordinateB();

      // check for bounding box
      if (!topLeft && (point.getX() < coordinate.getX() && point.getY() > coordinate.getY())) {
        resultingDistances.add(distance);
        topLeft = true;
      } else if (!topRight
          && (point.getX() > coordinate.getX() && point.getY() > coordinate.getY())) {
        resultingDistances.add(distance);
        topRight = true;
      } else if (!bottomLeft
          && (point.getX() < coordinate.getX() && point.getY() < coordinate.getY())) {
        resultingDistances.add(distance);
        bottomLeft = true;
      } else if (!bottomRight
          && (point.getX() > coordinate.getX() && point.getY() < coordinate.getY())) {
        resultingDistances.add(distance);
        bottomRight = true;
      } else if (coordinate.equalsExact(point, 1e-6)) {
        // if current point is matching the given coordinate, we need to return only the current
        // point

        resultingDistances.clear();
        resultingDistances.add(distance);
        return resultingDistances;
      } else {
        other.add(distance);
      }
    }

    // check if n distances are found
    int diff = numberOfPoints - resultingDistances.size();

    if (diff > 0) {
      resultingDistances.addAll(other.stream().limit(diff).toList());
    } else if (diff < 0) {
      return resultingDistances.stream().limit(numberOfPoints).toList();
    }

    return resultingDistances;
  }
}
