/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.util.geo.GeoUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Point;

public class CsvIdCoordinateSource extends CsvDataSource implements IdCoordinateSource {

  private static final String LATITUDE_HEADER = "lat";
  private static final String LONGITUDE_HEADER = "lon";
  private static final String ID_HEADER = "id";

  private final Map<Integer, Point> idToCoordinate;
  private final Map<Point, Integer>
      coordinateToId; // requires maps to to be unique in both ways -> keys and values

  public CsvIdCoordinateSource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(csvSep, folderPath, fileNamingStrategy);

    /* setup the coordinate id to lat/long mapping */
    idToCoordinate = setupIdToCoordinateMap(fileNamingStrategy.getIdCoordinateFileName());
    coordinateToId = invert(idToCoordinate);
  }

  private Map<Integer, Point> setupIdToCoordinateMap(String idCoordinateFileName) {
    Stream<Map<String, String>> fieldsToAttributes =
        buildStreamWithFieldsToAttributesMap(idCoordinateFileName, connector);

    return buildIdToCoordinateMap(fieldsToAttributes);
  }

  private <V, K> Map<V, K> invert(Map<K, V> map) {
    Map<V, K> inv = new HashMap<>();
    for (Map.Entry<K, V> entry : map.entrySet()) inv.put(entry.getValue(), entry.getKey());
    return inv;
  }

  public Point getCoordinate(Integer id) {
    return idToCoordinate.get(id);
  }

  @Override
  public Collection<Point> getCoordinates(Integer... ids) {
    return Stream.of(ids).map(this::getCoordinate).collect(Collectors.toList());
  }

  public Collection<Point> getCoordinates(int... ids) {
    return Arrays.stream(ids).mapToObj(this::getCoordinate).collect(Collectors.toSet());
  }

  public Integer getId(Point coordinate) {
    return coordinateToId.get(coordinate);
  }

  public Integer getCoordinateCount() {
    return idToCoordinate.keySet().size();
  }

  private Map<Integer, Point> buildIdToCoordinateMap(
      Stream<Map<String, String>> fieldsToAttributes) {

    return fieldsToAttributes
        .map(
            map -> {
              Optional<AbstractMap.SimpleEntry<Integer, Point>> res = Optional.empty();
              try {
                double lat = Double.parseDouble(map.get(LATITUDE_HEADER));
                double lon = Double.parseDouble(map.get(LONGITUDE_HEADER));
                int id = Integer.parseInt(map.get(ID_HEADER));
                Point coordinate = GeoUtils.xyToPoint(lat, lon);
                res = Optional.of(new AbstractMap.SimpleEntry<>(id, coordinate));
              } catch (NumberFormatException e) {
                log.warn(
                    "Could not build coordinate from given values: {}",
                    map.entrySet().stream()
                        .map(entry -> "(" + entry.getKey() + ":" + entry.getValue() + ")")
                        .collect(Collectors.joining("\n")));
              }
              return res;
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(
            Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
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
    // by default try-with-resources closes the reader directly when we leave this method (which
    // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
    // As we still want to consume the data at other places, we start a new stream instead of
    // returning the original one
    try (BufferedReader reader = connector.initReader(filename)) {
      final String[] headline = parseCsvRow(reader.readLine(), csvSep);

      if (!Arrays.asList(headline).containsAll(Arrays.asList("id", "lat", "lon"))) {
        throw new SourceException(
            "The first line of coordinateId to coordinates file '"
                + filename
                + "' does not contain the required fields 'id', 'lat', 'lon'. "
                + "Is the headline valid?\nProvided headline: "
                + String.join(", ", headline));
      }

      Collection<Map<String, String>> allRows =
          new HashSet<>(csvRowFieldValueMapping(reader, headline));

      return allRows.parallelStream();

    } catch (IOException e) {
      log.warn("Cannot read file with name '{}': {}", filename, e.getMessage());
    } catch (SourceException e) {
      log.error("Cannot read file with name '{}': {}", filename, e.getMessage());
    }

    return Stream.empty();
  }
}
