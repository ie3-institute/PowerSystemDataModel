/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.util.*;
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
    return getNearestCoordinates(coordinate, n, getAllCoordinates());
  }

  /**
   * Returns the nearest n coordinate points to the given coordinate from a given collection of
   * points. If the set is empty or null we look through all coordinates.
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param coordinates the collection of points
   * @return the n nearest coordinates to the given point
   */
  default List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, Collection<Point> coordinates) {
    SortedSet<CoordinateDistance> sortedDistances =
        GeoUtils.calcOrderedCoordinateDistances(
            coordinate,
            (coordinates != null && !coordinates.isEmpty()) ? coordinates : getAllCoordinates());
    return sortedDistances.stream().limit(n).toList();
  }

  /**
   * Method to turn a distance into a latitude and longitude deltas. The methode can be found here:
   * <a href="https://math.stackexchange.com/questions/474602/reverse-use-of-haversine-formula">
   *
   * @param coordinate the coordinate at the center of the bounding box.
   * @return x- and y-delta in degree
   */
  default double[] calculateXYDelta(Point coordinate, double maxDistance, double earthRadius) {
    // y-degrees are evenly spaced, so we can just divide a distance
    // by the earth's radius to get a y-delta in radians
    double deltaY = maxDistance / earthRadius;

    // because the spacing between x-degrees change between the equator
    // and the poles, we need to calculate the x-delta using the inverse
    // haversine formula
    double sinus = Math.sin(deltaY / 2);
    double squaredSinus = sinus * sinus;
    double cosine = Math.cos(Math.toRadians(coordinate.getY()));
    double squaredCosine = cosine * cosine;

    double deltaX = 2 * Math.asin(Math.sqrt(squaredSinus / squaredCosine));

    // converting the deltas to degree and returning them
    return new double[] {Math.toDegrees(deltaX), Math.toDegrees(deltaY)};
  }

  /**
   * Method for evaluating the found points and returning the n corner points of the bounding box.
   *
   * @param coordinate at the center of the bounding box
   * @param distances list of found points with their distances
   * @param numberOfPoints that should be returned
   * @return list of distances
   */
  default List<CoordinateDistance> restrictToBoundingBoxWithSetNumberOfCorner(
      Point coordinate, List<CoordinateDistance> distances, int numberOfPoints) {
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
