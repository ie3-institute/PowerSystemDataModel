/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/**
 * This class serves mapping purposes between the ID of a coordinate and the actual coordinate with
 * latitude and longitude values, which is especially needed for data source that don't offer
 * combined primary or foreign keys.
 */
public class IdCoordinateSource {

  public final IdCoordinateFactory factory;
  /** Mapping in both ways (id -> coordinate) and (coordinate -> id) have to be unique */
  public final Map<Integer, Point> idToCoordinate;

  public final Map<Point, Integer> coordinateToId;

  DataSource dataSource;

  public IdCoordinateSource(IdCoordinateFactory factory, DataSource dataSource) {
    this.factory = factory;
    this.dataSource = dataSource;

    /* setup the coordinate id to lat/long mapping */
    idToCoordinate = setupIdToCoordinateMap();
    coordinateToId = invert(idToCoordinate);
  }

  /** For source testing */
  public Stream<Map<String, String>> extractSourceData() {
    return dataSource.getIdCoordinateSourceData(factory);
  }

  /**
   * Get the matching coordinate for the given ID
   *
   * @param id the ID to look up
   * @return matching coordinate
   */
  public Optional<Point> getCoordinate(int id) {
    return Optional.ofNullable(idToCoordinate.get(id));
  }

  /**
   * Get the matching coordinates for the given IDs
   *
   * @param ids the IDs to look up
   * @return the matching coordinates
   */
  public Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids)
        .mapToObj(this::getCoordinate)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /**
   * Get the ID for the coordinate point
   *
   * @param coordinate the coordinate to look up
   * @return the matching ID
   */
  public Optional<Integer> getId(Point coordinate) {
    return Optional.ofNullable(coordinateToId.get(coordinate));
  }

  /**
   * Returns all the coordinates of this source
   *
   * @return all available coordinates
   */
  public Collection<Point> getAllCoordinates() {
    return coordinateToId.keySet();
  }

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
  public List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    return getNearestCoordinates(coordinate, n, getAllCoordinates());
  }

  /**
   * Read in and process the mapping
   *
   * @return Mapping from coordinate id to coordinate
   */
  private Map<Integer, Point> setupIdToCoordinateMap() {
    return dataSource
        .getIdCoordinateSourceData(factory)
        .map(fieldToValues -> new SimpleFactoryData(fieldToValues, Pair.class))
        .map(factory::get)
        .flatMap(Optional::stream)
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  /**
   * Inverts the mapping, so that former values map to keys
   *
   * @param map Mapping in the "right" direction
   * @param <V> Type of values
   * @param <K> Type of keys
   * @return Mapping in the "left" direction (Bad joke, I know...)
   */
  private <V, K> Map<V, K> invert(Map<K, V> map) {
    Map<V, K> inv = new HashMap<>();
    for (Map.Entry<K, V> entry : map.entrySet()) inv.put(entry.getValue(), entry.getKey());
    return inv;
  }

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
  public List<CoordinateDistance> getNearestCoordinates(
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
  public List<CoordinateDistance> restrictToBoundingBox(
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

  public int getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }
}
