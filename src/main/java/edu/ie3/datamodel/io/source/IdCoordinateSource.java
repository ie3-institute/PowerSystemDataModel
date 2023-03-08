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
import org.locationtech.jts.geom.Point;

/**
 * This class serves mapping purposes between the ID of a coordinate and the actual coordinate with
 * latitude and longitude values, which is especially needed for data source that don't offer
 * combined primary or foreign keys.
 */
public class IdCoordinateSource implements DataSource {

  public final IdCoordinateFactory factory;
  /** Mapping in both ways (id -> coordinate) and (coordinate -> id) have to be unique */
  public final Map<Integer, Point> idToCoordinate;

  public final Map<Point, Integer> coordinateToId;

  FunctionalDataSource dataSource;

  public IdCoordinateSource(IdCoordinateFactory factory, FunctionalDataSource dataSource) {
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
   * Returns the nearest n coordinate points to the given coordinate from a collection of all
   * available points
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @return the n nearest coordinates to the given point
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
    // String specialPlace = dataSource.getNamingStrategy().getIdCoordinateEntityName();
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
   * Returns the nearest n coordinate points to the given coordinate from a given collection of
   * points. If the set is empty or null we look through all coordinates.
   *
   * @param coordinate the coordinate to look up the nearest neighbours for
   * @param n how many neighbours to look up
   * @param coordinates the collection of points
   * @return the n nearest coordinates to the given point
   */
  public List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, Collection<Point> coordinates) {
    SortedSet<CoordinateDistance> sortedDistances =
        GeoUtils.calcOrderedCoordinateDistances(
            coordinate,
            (coordinates != null && !coordinates.isEmpty()) ? coordinates : getAllCoordinates());
    return sortedDistances.stream().limit(n).toList();
  }

  public int getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }
}
