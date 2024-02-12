/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.InvalidColumnNameException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.util.StringUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Contains all functions that are needed to read a SQL data source. */
public class SqlDataSource implements DataSource {

  protected static final Logger log = LoggerFactory.getLogger(SqlDataSource.class);

  protected final SqlConnector connector;
  protected final DatabaseNamingStrategy databaseNamingStrategy;
  protected String schemaName;

  public SqlDataSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy databaseNamingStrategy) {
    this.connector = connector;
    this.schemaName = schemaName;
    this.databaseNamingStrategy = databaseNamingStrategy;
  }

  /**
   * Creates a base query string without closing semicolon of the following pattern: <br>
   * {@code SELECT * FROM <schema>.<table>}
   *
   * @param schemaName the name of the database schema
   * @param tableName the name of the database table
   * @return basic query string without semicolon
   */
  protected static String createBaseQueryString(String schemaName, String tableName) {
    return "SELECT * FROM " + schemaName + "." + tableName;
  }

  /**
   * Determine the corresponding database column name based on the provided factory field parameter
   * name. Needed to support camel as well as snake case database column names.
   *
   * @param factoryColumnName the name of the field parameter set in the entity factory
   * @param tableName the table name where the data is stored
   * @return the column name that corresponds to the provided field parameter or an empty optional
   *     if no matching column can be found
   */
  protected String getDbColumnName(String factoryColumnName, String tableName) {
    try {
      ResultSet rs =
          connector.getConnection().getMetaData().getColumns(null, null, tableName, null);

      while (rs.next()) {
        String databaseColumnName = rs.getString("COLUMN_NAME");
        if (StringUtils.snakeCaseToCamelCase(databaseColumnName)
            .equalsIgnoreCase(factoryColumnName)) {
          return databaseColumnName;
        }
      }
    } catch (SQLException ex) {
      log.error(
          "Cannot connect to database to retrieve db column name for factory column name '{}' in table '{}'",
          factoryColumnName,
          tableName,
          ex);
    }
    throw new InvalidColumnNameException(
        "Cannot find column for '"
            + factoryColumnName
            + "' in provided times series data configuration."
            + "Please ensure that the database connection is working and the column names are correct!");
  }

  /**
   * Determine the corresponding table names based on the provided table name pattern.
   *
   * @param schemaPattern pattern of the schema to search in
   * @param tableNamePattern pattern of the table name
   * @return matching table names
   */
  protected List<String> getDbTables(String schemaPattern, String tableNamePattern) {
    LinkedList<String> tableNames = new LinkedList<>();

    try {
      ResultSet rs =
          connector
              .getConnection()
              .getMetaData()
              .getTables(null, schemaPattern, tableNamePattern, null);
      while (rs.next()) {
        String tableName = rs.getString("TABLE_NAME");
        if (tableName != null) tableNames.add(tableName);
      }
    } catch (SQLException ex) {
      log.error("Cannot connect to database to retrieve tables meta information", ex);
    }
    return tableNames;
  }

  @Override
  public Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass) {
    String tableName = databaseNamingStrategy.getEntityName(entityClass).orElseThrow();
    return getSourceFields(tableName);
  }

  /**
   * Method that uses the table name to retrieve all field names.
   *
   * @param tableName to be used
   * @return an option for a set of found fields
   */
  public Optional<Set<String>> getSourceFields(String tableName) {
    try {
      ResultSet rs =
          connector.getConnection().getMetaData().getColumns(null, null, tableName, null);
      Set<String> columnNames = new HashSet<>();

      while (rs.next()) {
        String name = rs.getString("COLUMN_NAME");
        columnNames.add(StringUtils.snakeCaseToCamelCase(name));
      }

      return Optional.of(columnNames);
    } catch (SQLException e) {
      log.warn("The table '{}' couldn't be read and therefore not be validated!", tableName, e);
      // FIXME only return empty if table not found. Throw exception if error occurred
      return Optional.empty();
    }
  }

  // TODO: Check if uniqueFields should be used to validate the uniqueness of returned entities
  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass) {
    String explicitTableName = databaseNamingStrategy.getEntityName(entityClass).orElseThrow();
    return buildStreamByTableName(explicitTableName);
  }

  /**
   * Interface for anonymous functions that are used as a parameter for {@link #executeQuery}.
   *
   * <p>For example, it can be defined this way: {@code ps -> ps.setInt(1, 2)}
   *
   * <p>(We cannot use {@link java.util.function.Function} here because it throws SQLException).
   */
  @FunctionalInterface
  protected interface AddParams {
    /**
     * Enhance a PreparedStatement by inserting parameters for wildcards
     *
     * @param ps the PreparedStatement to enhance
     * @throws SQLException if anything goes wrong during preparation of the query
     */
    void addParams(PreparedStatement ps) throws SQLException;
  }

  /**
   * Creates a stream with maps representing a data point in the SQL data source using an entity
   * class.
   */
  protected Stream<Map<String, String>> buildStreamByEntityClass(
      Class<? extends Entity> entityClass, AddParams addParams) {
    String query = createBaseQueryString(schemaName, entityClass.getSimpleName());
    return executeQuery(query, addParams);
  }

  /**
   * Creates a stream with maps representing a data point in the SQL data source using an explicit
   * table name.
   */
  protected Stream<Map<String, String>> buildStreamByTableName(String tableName) {
    String query = createBaseQueryString(schemaName, tableName);
    return executeQuery(query);
  }

  /**
   * Creates a stream with maps representing a data point in the SQL data source using an explicit
   * table name.
   */
  protected Stream<Map<String, String>> executeQuery(String query, AddParams addParams) {
    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
      addParams.addParams(ps);

      ResultSet resultSet = ps.executeQuery();
      return connector.extractFieldMaps(resultSet).stream();
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }
    return Stream.empty();
  }

  protected Stream<Map<String, String>> executeQuery(String query) {
    return executeQuery(query, x -> {});
  }
}
