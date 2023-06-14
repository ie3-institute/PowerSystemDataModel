/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.io.factory.timeseries.SqlCoordinateFactory;
import edu.ie3.datamodel.io.source.IdCoordinateSource;
import edu.ie3.datamodel.models.value.CoordinateValue;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.geo.CoordinateDistance;
import edu.ie3.util.geo.GeoUtils;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.*;
import javax.measure.quantity.Length;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/** SQL source for coordinate data */
public class SqlIdCoordinateSource extends SqlDataSource<CoordinateValue>
    implements IdCoordinateSource {
  private static final String WHERE = " WHERE ";
  private final SqlCoordinateFactory factory;

  /**
   * Queries that are available within this source. Motivation to have them as field value is to
   * avoid creating a new string each time, bc they're always the same.
   */
  private final String basicQuery;

  private final String queryForPoint;
  private final String queryForPoints;
  private final String queryForId;
  private final String queryForBoundingBox;
  private final String queryForNearestPoints;

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
      SqlCoordinateFactory factory) {
    super(connector);

    String dbIdColumnName = getDbColumnName(factory.getIdField(), coordinateTableName);
    String dbPointColumnName = getDbColumnName(factory.getCoordinateField(), coordinateTableName);

    this.factory = factory;

    // setup queries
    this.basicQuery = createBaseQueryString(schemaName, coordinateTableName);
    this.queryForPoint = createQueryForPoint(dbIdColumnName);
    this.queryForPoints = createQueryForPoints(dbIdColumnName);
    this.queryForId = createQueryForId(dbPointColumnName);
    this.queryForBoundingBox = createQueryForBoundingBox(dbPointColumnName);
    this.queryForNearestPoints =
        createQueryForNearestPoints(
            schemaName, coordinateTableName, dbIdColumnName, dbPointColumnName);
  }

  @Override
  protected Optional<CoordinateValue> createEntity(Map<String, String> fieldToValues) {
    fieldToValues.remove("distance");

    SimpleFactoryData simpleFactoryData =
        new SimpleFactoryData(new FactoryData.MapWithRowIndex("-1", fieldToValues), Pair.class);
    Try<Pair<Integer, Point>> pair = factory.get(simpleFactoryData);

    if (pair.isEmpty()) {
      return Optional.empty();
    } else {
      Pair<Integer, Point> data = pair.getOrThrow();
      return Optional.of(new CoordinateValue(data.getKey(), data.getValue()));
    }
  }

  @Override
  public Optional<Point> getCoordinate(int id) {
    List<CoordinateValue> values = executeQuery(queryForPoint, ps -> ps.setInt(1, id));

    if (values.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(values.get(0).coordinate);
    }
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

    return values.stream().map(value -> value.coordinate).toList();
  }

  @Override
  public Optional<Integer> getId(Point coordinate) {
    double latitude = coordinate.getY();
    double longitude = coordinate.getX();

    List<CoordinateValue> values =
        executeQuery(
            queryForId,
            ps -> {
              ps.setDouble(1, longitude);
              ps.setDouble(2, latitude);
            });

    if (values.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(values.get(0).id);
    }
  }

  @Override
  public Collection<Point> getAllCoordinates() {
    List<CoordinateValue> values = executeQuery(basicQuery + ";", PreparedStatement::execute);

    return values.stream().map(value -> value.coordinate).toList();
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    List<CoordinateValue> values =
        executeQuery(
            queryForNearestPoints,
            ps -> {
              ps.setDouble(1, coordinate.getX());
              ps.setDouble(2, coordinate.getY());
              ps.setInt(3, n);
            });

    List<Point> points = values.stream().map(value -> value.coordinate).toList();
    return calculateCoordinateDistances(coordinate, n, points);
  }

  @Override
  public List<CoordinateDistance> getClosestCoordinates(
      Point coordinate, int n, ComparableQuantity<Length> distance) {
    Envelope envelope = GeoUtils.calculateBoundingBox(coordinate, distance);

    List<CoordinateValue> values =
        executeQuery(
            queryForBoundingBox,
            ps -> {
              ps.setDouble(1, envelope.getMinX());
              ps.setDouble(2, envelope.getMinY());
              ps.setDouble(3, envelope.getMaxX());
              ps.setDouble(4, envelope.getMaxY());
            });

    List<Point> points = values.stream().map(value -> value.coordinate).toList();

    return calculateCoordinateDistances(coordinate, n, points);
  }

  /**
   * Creates a basic query to retrieve entries for given ids with the following pattern: <br>
   * {@code <base query> WHERE <id column>=?;}
   *
   * @param idColumn the name of the column holding the id info
   * @return the query string
   */
  private String createQueryForPoint(String idColumn) {
    return basicQuery + WHERE + idColumn + " =?; ";
  }

  /**
   * Creates a basic query to retrieve entries for given ids with the following pattern: <br>
   * {@code <base query> WHERE <id column>= ANY (?);}
   *
   * @param idColumn the name of the column holding the id info
   * @return the query string
   */
  private String createQueryForPoints(String idColumn) {
    return basicQuery + WHERE + idColumn + " = ANY (?); ";
  }

  /**
   * Creates a basic query to retrieve an id for a given point with the following pattern: <br>
   * {@code <base query> WHERE <point column> = ST_Point( ?, ?);}
   *
   * @param pointColumn the name of the column holding the geometry information
   * @return the query string
   */
  private String createQueryForId(String pointColumn) {
    return basicQuery + WHERE + pointColumn + " = ST_Point( ?, ?); ";
  }

  /**
   * Creates a basic query to retrieve all entries in a given box. The box is defines by a latitude
   * interval and a longitude interval. The intervals are provided via an envelope. The pattern
   * looks like this: <br>
   * {@code <base query> WHERE ST_Intersects(ST_MakeEnvelope(?, ?, ?, ?, 4326 ) , <point column> )
   * ;}
   *
   * @param pointColumn the name of the column holding the geometry information
   * @return the query string
   */
  private String createQueryForBoundingBox(String pointColumn) {
    return basicQuery
        + WHERE
        + " ST_Intersects(ST_MakeEnvelope(?, ?, ?, ?, 4326 ) , "
        + pointColumn
        + ");";
  }

  /**
   * Creates a query to retrieve the nearest n entries. The pattern looks like this: <br>
   * {@code SELECT <id column> AS id, <coordinate column> AS coordinate, <coordinate column> <->
   * ST_Point( ?, ?) AS distance FROM <schema>.<table> ORDER BY distance LIMIT ?;}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @param idColumn the name of the column holding the id information
   * @param pointColumn the name of the column holding the geometry information
   * @return the query string
   */
  private String createQueryForNearestPoints(
      String schemaName, String tableName, String idColumn, String pointColumn) {
    return "SELECT "
        + idColumn
        + " AS id , "
        + pointColumn
        + " AS coordinate, "
        + pointColumn
        + " <-> ST_Point( ?, ?) AS distance "
        + "FROM "
        + schemaName
        + ".\""
        + tableName
        + "\""
        + " ORDER BY distance LIMIT ?;";
  }
}
