/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.DuplicateEntitiesException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.models.input.IdCoordinateInput;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import edu.ie3.datamodel.utils.validation.UniquenessValidationUtils;
import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Implementation of {@link IdCoordinateSource} to read the mapping between coordinate id and actual
 * coordinate from csv file and build a mapping from it.
 */
public class CsvIdCoordinateSource extends IdCoordinateSource {

  protected static final Logger log = LoggerFactory.getLogger(CsvIdCoordinateSource.class);

  /** Mapping in both ways (id -> coordinate) and (coordinate -> id) have to be unique */
  private final Map<Integer, Point> idToCoordinate;

  private final Map<Point, Integer> coordinateToId;

  private final CsvDataSource dataSource;
  private final IdCoordinateFactory factory;

  public CsvIdCoordinateSource(IdCoordinateFactory factory, CsvDataSource dataSource)
      throws SourceException {
    this.factory = factory;
    this.dataSource = dataSource;

    /* set up the coordinate id to lat/long mapping */
    idToCoordinate = setupIdToCoordinateMap();
    coordinateToId = invert(idToCoordinate);
  }

  @Override
  public void validate() throws ValidationException {
    validate(IdCoordinateInput.class, this::getSourceFields, factory);
  }

  /**
   * Read in and process the mapping
   *
   * @return Mapping from coordinate id to coordinate
   */
  private Map<Integer, Point> setupIdToCoordinateMap() throws SourceException {
    List<IdCoordinateInput> idCoordinates =
        buildStreamWithFieldsToAttributesMap()
            .map(
                data ->
                    data.map(
                            fieldToValues ->
                                new SimpleFactoryData(fieldToValues, IdCoordinateInput.class))
                        .map(factory::get))
            .flatMap(s -> Try.scanStream(s, "Pair<Integer, Point>", SourceException::new))
            .getOrThrow()
            .toList();

    try {
      // check the uniqueness of the source
      UniquenessValidationUtils.checkIdCoordinateUniqueness(idCoordinates);
    } catch (DuplicateEntitiesException de) {
      throw new SourceException("Due to: ", de);
    }

    return idCoordinates.stream()
        .collect(Collectors.toMap(IdCoordinateInput::id, IdCoordinateInput::point));
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
  public Optional<Set<String>> getSourceFields() throws SourceException {
    Path filePath = Path.of(dataSource.getNamingStrategy().getIdCoordinateEntityName());
    return dataSource.getSourceFields(filePath);
  }

  @Override
  public Optional<Point> getCoordinate(int id) {
    return Optional.ofNullable(idToCoordinate.get(id));
  }

  @Override
  public Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids)
        .mapToObj(this::getCoordinate)
        .flatMap(Optional::stream)
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

  @Override
  public List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    Set<Point> points = coordinateToId.keySet();

    if (idToCoordinate.size() > n) {
      ArrayList<Point> foundPoints = new ArrayList<>();
      ComparableQuantity<Length> distance = Quantities.getQuantity(10000, Units.METRE);

      // extends the search radius until n points are found
      while (foundPoints.size() < n) {
        foundPoints.clear();
        distance = distance.multiply(2);

        Envelope envelope = GeoUtils.calculateBoundingBox(coordinate, distance);
        points.stream()
            .filter(point -> envelope.contains(point.getCoordinate()))
            .forEach(foundPoints::add);
      }

      // replaces all point with smaller size of found points
      points.clear();
      points.addAll(foundPoints);
    }

    return calculateCoordinateDistances(coordinate, n, points);
  }

  @Override
  public List<CoordinateDistance> getClosestCoordinates(
      Point coordinate, int n, ComparableQuantity<Length> distance) {
    Collection<Point> reducedPoints = getCoordinatesInBoundingBox(coordinate, distance);
    return calculateCoordinateDistances(coordinate, n, reducedPoints);
  }

  @Override
  public List<CoordinateDistance> findCornerPoints(
      Point coordinate, ComparableQuantity<Length> distance) {
    Collection<Point> points = getCoordinatesInBoundingBox(coordinate, distance);
    return findCornerPoints(
        coordinate, GeoUtils.calcOrderedCoordinateDistances(coordinate, points));
  }

  public int getCoordinateCount() {
    return idToCoordinate.size();
  }

  private Collection<Point> getCoordinatesInBoundingBox(
      Point coordinate, ComparableQuantity<Length> distance) {
    Set<Point> points = coordinateToId.keySet();
    Envelope envelope = GeoUtils.calculateBoundingBox(coordinate, distance);
    return points.stream()
        .filter(point -> envelope.contains(point.getCoordinate()))
        .collect(Collectors.toSet());
  }

  /**
   * Build a stream with mappings from field identifiers to attributes
   *
   * @return Stream with mappings from field identifiers to attributes
   */
  protected Try<Stream<Map<String, String>>, SourceException>
      buildStreamWithFieldsToAttributesMap() {
    Path filePath = Path.of(dataSource.getNamingStrategy().getIdCoordinateEntityName());
    try (BufferedReader reader = dataSource.connector.initReader(filePath)) {
      final String[] headline = dataSource.parseCsvRow(reader.readLine(), dataSource.csvSep);

      // validating read file
      factory.validate(Set.of(headline), IdCoordinateInput.class).getOrThrow();

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      return dataSource.csvRowFieldValueMapping(reader, headline, filePath.getFileName());
    } catch (IOException e) {
      return Failure.of(
          new SourceException("Cannot read the file for coordinate id to coordinate mapping.", e));
    } catch (ValidationException ve) {
      return Failure.of(new SourceException("Creating stream failed due to failed validation", ve));
    }
  }
}
