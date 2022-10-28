/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.models.value.CoordinateValue;
import edu.ie3.util.geo.CoordinateDistance;
import java.sql.Array;
import java.util.*;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Point;

/** SQL source for coordinate data */
public class SqlIdCoordinateSource extends SqlDataSource<CoordinateValue>
    implements IdCoordinateSource {
  private static final String WHERE = " WHERE ";
  private final IdCoordinateFactory factory;
  private final double maxDistance;
  private static final double earthRadiusMeter = 6378137.0;

  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String basicQuery;
  private final String queryForPoint;
  private final String queryForId;
  private final String queryForBoundingBox;

  /**
   * Initializes a new SqlIdCoordinateSource
   *
   * @param connector the connector needed for the database connection
   * @param schemaName the database schema to use
   * @param coordinateTableName the name of the table containing coordinate data
   * @param factory instance of a coordinate factory
   * @param maxDistance maximal search distance for points
   */
  public SqlIdCoordinateSource(
      SqlConnector connector,
      String schemaName,
      String coordinateTableName,
      IdCoordinateFactory factory,
      double maxDistance) {
    super(connector);

    this.factory = factory;
    this.maxDistance = maxDistance;

    String dbIdColumnName = getDbColumnName(factory.getIdField(), coordinateTableName);
    String dbLatitudeColumnName = getDbColumnName(factory.getLatField(), coordinateTableName);
    String dbLongitudeColumnName = getDbColumnName(factory.getLonField(), coordinateTableName);

    // setup queries
    this.basicQuery = "SELECT * FROM " + schemaName + ".\"" + coordinateTableName + " ?;";
    this.queryForPoint = createQueryForPoint(schemaName, coordinateTableName, dbIdColumnName);
    this.queryForId =
        createQueryForId(
            schemaName, coordinateTableName, dbLatitudeColumnName, dbLongitudeColumnName);
    this.queryForBoundingBox =
        createQueryForBoundingBox(
            schemaName, coordinateTableName, dbLatitudeColumnName, dbLongitudeColumnName);
  }

  @Override
  protected Optional<CoordinateValue> createEntity(Map<String, String> fieldToValues) {
    SimpleFactoryData factoryData = new SimpleFactoryData(fieldToValues, Pair.class);
    Optional<Pair<Integer, Point>> option = factory.get(factoryData);

    if (option.isEmpty()) return Optional.empty();

    Pair<Integer, Point> data = option.get();

    return Optional.of(new CoordinateValue(data.getKey(), data.getValue()));
  }

  @Override
  public Optional<Point> getCoordinate(int id) {
    List<CoordinateValue> values = executeQuery(queryForPoint, ps -> ps.setInt(1, id));

    return Optional.of(values.get(0).coordinate);
  }

  @Override
  public Collection<Point> getCoordinates(int... ids) {
    Object[] idSet = Arrays.asList(ids, ids.length).toArray();

    List<CoordinateValue> values =
        executeQuery(
            queryForPoint,
            ps -> {
              Array sqlArray = ps.getConnection().createArrayOf("integer", idSet);
              ps.setArray(1, sqlArray);
            });

    List<Point> points = new ArrayList<>();

    for (CoordinateValue value : values) {
      points.add(value.coordinate);
    }

    return points;
  }

  @Override
  public Optional<Integer> getId(Point coordinate) {
    double latitude = coordinate.getY();
    double longitude = coordinate.getX();

    List<CoordinateValue> values =
        executeQuery(
            queryForId,
            ps -> {
              ps.setDouble(1, latitude);
              ps.setDouble(2, longitude);
            });

    return Optional.of(values.get(0).id);
  }

  @Override
  public Collection<Point> getAllCoordinates() {
    List<CoordinateValue> values = executeQuery(basicQuery, ps -> ps.setString(1, ";"));

    ArrayList<Point> points = new ArrayList<>();

    for (CoordinateValue value : values) {
      points.add(value.coordinate);
    }

    return points;
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    double[] xyDeltas = calculateXYDelta(coordinate);

    double longitude = coordinate.getX();
    double latitude = coordinate.getY();

    List<CoordinateValue> values =
        executeQuery(
            queryForBoundingBox,
            ps -> {
              ps.setDouble(1, longitude - xyDeltas[0]);
              ps.setDouble(2, longitude + xyDeltas[0]);
              ps.setDouble(3, latitude - xyDeltas[1]);
              ps.setDouble(4, latitude + xyDeltas[1]);
            });

    ArrayList<Point> points = new ArrayList<>();

    for (CoordinateValue value : values) {
      points.add(value.coordinate);
    }

    List<CoordinateDistance> distances = getNearestCoordinates(coordinate, 2 * n, points);

    return checkForBoundingBox(coordinate, distances, n);
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, Collection<Point> coordinates) {
    return IdCoordinateSource.super.getNearestCoordinates(coordinate, n, coordinates);
  }

  private List<CoordinateDistance> checkForBoundingBox(
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

  /**
   * Method to calculate a bounding box around a point with a defined radius.
   *
   * @param coordinate the coordinate at the center of the bounding box.
   * @return x- and y-delta
   */
  private double[] calculateXYDelta(Point coordinate) {
    // calculate y-delta
    double deltaY = maxDistance / earthRadiusMeter;

    // calculate some functions
    double sinus = Math.sin(deltaY / 2);
    double squaredSinus = sinus * sinus;

    double cosine = Math.cos(Math.toRadians(coordinate.getY()));
    double squaredCosine = cosine * cosine;

    // calculate x-delta
    double deltaX = 2 * Math.asin(Math.sqrt(squaredSinus / squaredCosine));

    return new double[] {deltaX, deltaY};
  }

  /**
   * Creates a basic query to retrieve an entry for a given id with the following pattern: <br>
   * {@code <base query> WHERE <id column>=?;}
   *
   * @param schemaName the name of the database schema
   * @param coordinateTableName the name of the database table
   * @param idColumn the name of the column holding the id info
   * @return the query string
   */
  private String createQueryForPoint(
      String schemaName, String coordinateTableName, String idColumn) {
    return createBaseQueryString(schemaName, coordinateTableName) + WHERE + idColumn + " =?; ";
  }

  /**
   * Creates a basic query to retrieve an id for a given point with the following pattern: <br>
   * {@code <base query> WHERE <latitude column>=? AND <longitude column>=?;}
   *
   * @param schemaName the name of the database schema
   * @param coordinateTableName the name of the database table
   * @param latitudeColumnName the name of the latitude column
   * @param longitudeColumnName the name of the longitude column
   * @return the query string
   */
  private String createQueryForId(
      String schemaName,
      String coordinateTableName,
      String latitudeColumnName,
      String longitudeColumnName) {
    return createBaseQueryString(schemaName, coordinateTableName)
        + WHERE
        + latitudeColumnName
        + " =? AND "
        + longitudeColumnName
        + " =?; ";
  }

  /**
   * Creates a basic query to retrieve all entries in a given box. The box is defines by a latitude
   * interval and a longitude interval. The pattern looks like this: <br>
   * {@code <base query> WHERE <latitude column> BETWEEN ? AND ? AND <longitude column> BETWEEN ?
   * AND ?;}
   *
   * @param schemaName the name of the database
   * @param coordinateTableName the name of the database table
   * @param longitudeColumnName the name of the second column
   * @param latitudeColumnName the name of the first column
   * @return the query string
   */
  private String createQueryForBoundingBox(
      String schemaName,
      String coordinateTableName,
      String longitudeColumnName,
      String latitudeColumnName) {
    return createBaseQueryString(schemaName, coordinateTableName)
        + WHERE
        + longitudeColumnName
        + " BETWEEN ? AND ? "
        + " AND "
        + latitudeColumnName
        + " BETWEEN ? AND ?; ";
  }
}
