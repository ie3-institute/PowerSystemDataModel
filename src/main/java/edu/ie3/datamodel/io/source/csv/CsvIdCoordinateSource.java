/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;

/**
 * Implementation of {@link IdCoordinateSource} to read the mapping between coordinate id and actual
 * coordinate from csv file and build a mapping from it.
 */
public class CsvIdCoordinateSource extends CsvDataSource implements IdCoordinateSource {
  private final IdCoordinateFactory factory;
  /** Mapping in both ways (id -> coordinate) and (coordinate -> id) have to be unique */
  private final Map<Integer, Point> idToCoordinate;

  private final Map<Point, Integer> coordinateToId;

  public CsvIdCoordinateSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      IdCoordinateFactory factory) {
    super(csvSep, folderPath, fileNamingStrategy);

    this.factory = factory;

    /* setup the coordinate id to lat/long mapping */
    idToCoordinate = setupIdToCoordinateMap();
    coordinateToId = invert(idToCoordinate);
  }

  /**
   * Read in and process the mapping
   *
   * @return Mapping from coordinate id to coordinate
   */
  private Map<Integer, Point> setupIdToCoordinateMap() {
    return buildStreamWithFieldsToAttributesMap()
        .map(fieldToValues -> new SimpleFactoryData(fieldToValues, Pair.class))
        .map(factory::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
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

  @Override
  public Optional<Point> getCoordinate(int id) {
    return Optional.ofNullable(idToCoordinate.get(id));
  }

  @Override
  public Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids)
        .mapToObj(this::getCoordinate)
        .flatMap(o -> o.map(Stream::of).orElseGet(Stream::empty))
        .collect(Collectors.toSet());
  }

  @Override
  public Optional<Integer> getId(Point coordinate) {
    return Optional.ofNullable(coordinateToId.get(coordinate));
  }

  @Override
  public Collection<Point> getAllCoordinates() {
    return coordinateToId.keySet();
  }

  public int getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }

  /**
   * Build a stream with mappings from field identifiers to attributes
   *
   * @return Stream with mappings from field identifiers to attributes
   */
  protected Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap() {
    try (BufferedReader reader = connector.initIdCoordinateReader()) {
      final String[] headline = parseCsvRow(reader.readLine(), csvSep);

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      Collection<Map<String, String>> allRows = csvRowFieldValueMapping(reader, headline);

      return distinctRowsWithLog(allRows).parallelStream();
    } catch (IOException e) {
      log.error("Cannot read the file for coordinate id to coordinate mapping.", e);
    }

    return Stream.empty();
  }

  /**
   * Returns a collection of maps each representing a row in csv file that can be used to built one
   * coordinate id mapping. The uniqueness of each row is doubled checked by a) that no duplicated
   * rows are returned that are full (1:1) matches and b) that no rows are returned that have the
   * same composite primary key (in terms of coordinate id) but different field values. As well as
   * c) if multiple coordinates map to the same id. As all cases destroy uniqueness constraints, an
   * empty set is returned to indicate that these data cannot be processed safely and the error is
   * logged. For case a), only the duplicates are filtered out and a set with unique rows is
   * returned.
   *
   * @param allRows collection of rows of a csv file an entity should be built from
   * @return either a set containing only unique rows or an empty set if at least two rows with the
   *     same UUID but different field values exist
   */
  protected Set<Map<String, String>> distinctRowsWithLog(Collection<Map<String, String>> allRows) {
    Set<Map<String, String>> allRowsSet = new HashSet<>(allRows);
    // check for duplicated rows that match exactly (full duplicates) -> sanity only, not crucial -
    // case a)
    if (allRows.size() != allRowsSet.size()) {
      log.warn(
          "File with coordinate id mapping contains {} exact duplicated rows. File cleanup is recommended!",
          (allRows.size() - allRowsSet.size()));
    }

    /* Check for rows with the same coordinate id (primary key) */
    Set<Map<String, String>> distinctIdSet =
        allRowsSet
            .parallelStream()
            .filter(distinctByKey(fieldToValues -> fieldToValues.get(factory.getIdField())))
            .collect(Collectors.toSet());
    if (distinctIdSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctIdSet);
      String affectedCoordinateIds =
          allRowsSet.stream()
              .map(row -> row.get(factory.getIdField()))
              .collect(Collectors.joining(",\n"));
      log.error(
          "Coordinate id mapping entities with duplicated composite primary key, but different field values "
              + "found! Please review the corresponding input file!\nAffected primary keys:\n{}",
          affectedCoordinateIds);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    /* Check for rows with the same coordinate */
    Set<Map<String, String>> distinctCoordinateSet =
        allRowsSet
            .parallelStream()
            .filter(
                distinctByKey(
                    fieldToValues ->
                        Pair.of(
                            fieldToValues.get(factory.getLatField()),
                            fieldToValues.get(factory.getLonField()))))
            .collect(Collectors.toSet());
    if (distinctCoordinateSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctIdSet);
      String affectedCoordinateIds =
          allRowsSet.stream()
              .map(
                  row ->
                      Pair.of(row.get(factory.getLatField()), row.get(factory.getLonField()))
                          .toString())
              .collect(Collectors.joining(",\n"));
      log.error(
          "Coordinate id mapping entities with duplicated composite primary key, but different field "
              + "values found! Please review the corresponding input file!\nAffected primary keys:\n{}",
          affectedCoordinateIds);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    return allRowsSet;
  }
}
