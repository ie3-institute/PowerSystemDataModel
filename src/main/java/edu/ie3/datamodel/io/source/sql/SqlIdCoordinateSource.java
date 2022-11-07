/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.models.value.CoordinateValue;
import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.*;
import org.locationtech.jts.geom.Point;

/** SQL source for coordinate data */
public class SqlIdCoordinateSource extends SqlDataSource<CoordinateValue>
    implements IdCoordinateSource {
  private static final String WHERE = " WHERE ";
  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String basicQuery;

  private final String queryForPoint;
  private final String queryForPoints;
  private final String queryForId;
  private final String queryForBoundingBox;

  /**
   * Initializes a new SqlIdCoordinateSource
   *
   * @param connector the connector needed for the database connection
   * @param schemaName the database schema to use
   * @param coordinateTableName the name of the table containing coordinate data
   * @param factory instance of a coordinate factory
   */
  public SqlIdCoordinateSource(
      SqlConnector connector,
      String schemaName,
      String coordinateTableName,
      IdCoordinateFactory factory) {
    super(connector);

    String dbIdColumnName = getDbColumnName(factory.getIdField(), coordinateTableName);
    String dbLatitudeColumnName = getDbColumnName(factory.getLatField(), coordinateTableName);
    String dbLongitudeColumnName = getDbColumnName(factory.getLonField(), coordinateTableName);

    // setup queries
    this.basicQuery = createBaseQueryString(schemaName, coordinateTableName) + ";";
    this.queryForPoint = createQueryForPoint(schemaName, coordinateTableName, dbIdColumnName);
    this.queryForPoints = createQueryForPoints(schemaName, coordinateTableName, dbIdColumnName);
    this.queryForId =
        createQueryForId(
            schemaName, coordinateTableName, dbLatitudeColumnName, dbLongitudeColumnName);
    this.queryForBoundingBox =
        createQueryForBoundingBox(
            schemaName, coordinateTableName, dbLatitudeColumnName, dbLongitudeColumnName);
  }

  @Override
  protected Optional<CoordinateValue> createEntity(Map<String, String> fieldToValues) {
    int id;
    double latitude;
    double longitude;

    try {
      id = Integer.parseInt(fieldToValues.get("id"));
      latitude = Double.parseDouble(fieldToValues.get("latitude"));
      longitude = Double.parseDouble(fieldToValues.get("longitude"));
    } catch (Exception e) {
      return Optional.empty();
    }

    Point point = GeoUtils.buildPoint(latitude, longitude);
    return Optional.of(new CoordinateValue(id, point));
  }

  @Override
  public Optional<Point> getCoordinate(int id) {
    List<CoordinateValue> values = executeQuery(queryForPoint, ps -> ps.setInt(1, id));

    return Optional.of(values.get(0).coordinate);
  }

  @Override
  public Collection<Point> getCoordinates(int... ids) {
    Object[] idSet = Arrays.stream(ids).boxed().distinct().toArray();

    List<CoordinateValue> values =
        executeQuery(
            queryForPoints,
            ps -> {
              Array sqlArray = ps.getConnection().createArrayOf("int", idSet);
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
    double latitude = coordinate.getX();
    double longitude = coordinate.getY();

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
    List<CoordinateValue> values = executeQuery(basicQuery, PreparedStatement::execute);

    ArrayList<Point> points = new ArrayList<>();

    for (CoordinateValue value : values) {
      points.add(value.coordinate);
    }

    return points;
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, double maxDistance) {
    double[] xyDeltas = calculateXYDelta(coordinate, maxDistance);

    double longitude = coordinate.getX();
    double latitude = coordinate.getY();

    List<CoordinateValue> values =
        executeQuery(
            queryForBoundingBox,
            ps -> {
              ps.setDouble(1, latitude - xyDeltas[1]);
              ps.setDouble(2, latitude + xyDeltas[1]);
              ps.setDouble(3, longitude - xyDeltas[0]);
              ps.setDouble(4, longitude + xyDeltas[0]);
            });

    ArrayList<Point> reducedPoints = new ArrayList<>();

    for (CoordinateValue value : values) {
      reducedPoints.add(value.coordinate);
    }

    return getNearestCoordinates(coordinate, n, reducedPoints);
  }

  /**
   * Creates a basic query to retrieve entries for given ids with the following pattern: <br>
   * {@code <base query> WHERE <id column>= ANY (?);}
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

  private String createQueryForPoints(
      String schemaName, String coordinateTableName, String idColumn) {
    return createBaseQueryString(schemaName, coordinateTableName)
        + WHERE
        + idColumn
        + " = ANY (?); ";
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
   * @param latitudeColumnName the name of the first column
   * @param longitudeColumnName the name of the second column
   * @return the query string
   */
  private String createQueryForBoundingBox(
      String schemaName,
      String coordinateTableName,
      String latitudeColumnName,
      String longitudeColumnName) {
    return createBaseQueryString(schemaName, coordinateTableName)
        + WHERE
        + latitudeColumnName
        + " BETWEEN ? AND ?"
        + " AND "
        + longitudeColumnName
        + " BETWEEN ? AND ?; ";
  }
}
