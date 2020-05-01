/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.source.CoordinateSource;
import edu.ie3.util.geo.GeoUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;

public class CsvCoordinateSource extends CsvDataSource implements CoordinateSource {

  private static final String LATITUDE_HEADER = "lat";
  private static final String LONGITUDE_HEADER = "lon";
  private static final String ID_HEADER = "id";

  private HashMap<Integer, Point> idToCoordinate;
  private HashMap<Point, Integer> coordinateToId;
  private final FileNamingStrategy fileNamingStrategy;

  public CsvCoordinateSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.fileNamingStrategy = fileNamingStrategy;

    /* setup the coordinate id to lat/long mapping */
    setupCoordinateMaps(); // todo remove side effects
  }

  private void setupCoordinateMaps() {
    String fileName = fileNamingStrategy.getCoordinateFileName();
    Stream<Map<String, String>> fieldsToAttributes =
        buildStreamWithFieldsToAttributesMap(fileName, connector);
    idToCoordinate = buildIdToCoordinateMap(fieldsToAttributes);
    coordinateToId = new HashMap<>();
    idToCoordinate.forEach((k, v) -> coordinateToId.put(v, k));
  }

  public Point getCoordinate(Integer id) {
    return idToCoordinate.get(id);
  }

  @Override
  public Collection<Point> getCoordinates(Integer... ids) {
    return null;
  }

  public Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids).mapToObj(this::getCoordinate).collect(Collectors.toSet());
  }

  public Collection<Point> getCoordinatesBetween(Integer fromId, Integer toId) {
    return getCoordinates(IntStream.rangeClosed(fromId, toId).toArray());
  }

  public Integer getId(Point coordinate) {
    return coordinateToId.get(coordinate);
  }

  public Integer getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }

  private HashMap<Integer, Point> buildIdToCoordinateMap(
      Stream<Map<String, String>> fieldsToAttributes) {
    HashMap<Integer, Point> coordinateMap = new HashMap<>();

    fieldsToAttributes.forEach(
        map -> {
          try {
            double lat = Double.parseDouble(map.get(LATITUDE_HEADER));
            double lon = Double.parseDouble(map.get(LONGITUDE_HEADER));
            Integer id = Integer.valueOf(map.get(ID_HEADER));
            Point coordinate = GeoUtils.xyToPoint(lat, lon);
            coordinateMap.put(id, coordinate);
          } catch (NumberFormatException e) {
            log.warn("Could not build coordinate from given values: {}", map);
          }
        });
    return coordinateMap;
  }

  /**
   * Tries to open a file reader from the connector based on the provided file name, reads the first
   * line (considered to be the headline with headline fields) and returns a stream of (fieldName ->
   * fieldValue) mapping where each map represents one row of the .csv file. Since the returning
   * stream is a parallel stream, the order of the elements cannot be guaranteed.
   *
   * @param filename the file name that is used to get the corresponding reader
   * @param connector the connector that should be used to get the reader from
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName -> fieldValue)
   */
  private Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap(
      String filename, CsvFileConnector connector) {
    try (BufferedReader reader = connector.initReader(filename)) {
      final String[] headline = parseCsvHeadline(reader.readLine(), csvSep);

      if (!Arrays.asList(headline).containsAll(Arrays.asList("id", "lat", "lon"))) {
        throw new SourceException(
            "The first line of coordinateId to coordinates file '"
                + filename
                + "' does not contain the required fields 'id', 'lat', 'lon'. "
                + "Is the headline valid?\nProvided headline: "
                + String.join(", ", headline));
      }

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      Collection<Map<String, String>> allRows =
          new HashSet<>(csvRowFieldValueMapping(reader, headline));

      return allRows.parallelStream();

    } catch (IOException e) {
      log.error("Cannot read file with name '{}': {}", filename, e.getMessage());
    } catch (SourceException e) {
      log.error("Cannot read file with name '{}': {}", filename, e.getMessage());
    }

    return Stream.empty();
  }
}
