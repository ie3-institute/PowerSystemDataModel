/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.InvalidColumnNameException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.util.StringUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SqlDataSource {

  protected static final Logger log = LoggerFactory.getLogger(SqlDataSource.class);

  private final SqlConnector connector;

  private String baseQuery;

  protected SqlDataSource(
          String jdbcUrl,
          String userName,
          String password,
          String schemaName
  ) {
    this.connector = new SqlConnector(jdbcUrl, userName, password);
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
    return "SELECT * FROM " + schemaName + ".\"" + tableName + "\"";
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

  /**
   * Interface for anonymous functions that are used as a parameter for {@link #executeQuery}.
   *
   * <p>For example, it can be defined this way: {@code ps -> ps.setInt(1, 2)}
   *
   * <p>(We cannot use {@link java.util.function.Function} here because it throws SQLException).
   */
  @FunctionalInterface
  interface AddParams {
    /**
     * Enhance a PreparedStatement by inserting parameters for wildcards
     *
     * @param ps the PreparedStatement to enhance
     * @throws SQLException if anything goes wrong during preparation of the query
     */
    void addParams(PreparedStatement ps) throws SQLException;
  }

  protected Stream<Map<String, String>> buildStreamByQuery(
          Class<? extends UniqueEntity> entityClass,
          String query,
          AddParams addParams
  ) {
    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
      addParams.addParams(ps);

      ResultSet resultSet = ps.executeQuery();
      List<Map<String, String>> fieldMaps = connector.extractFieldMaps(resultSet);

      return fieldMaps.stream();
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }

    return null;
  }



  /**
   * Executes the prepared statement after possibly adding parameters to the query using the given
   * function. Finally, processes the results and creates a list of time based values via field map
   * extraction.
   *
   * @param query the query to use
   * @param addParams function that possibly adds parameters to query
   * @return a list of resulting entities
   */

  //protected List<T> executeQuery(String query, AddParams addParams) {
    /*
    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
      addParams.addParams(ps);

      ResultSet resultSet = ps.executeQuery();
      List<Map<String, String>> fieldMaps = connector.extractFieldMaps(resultSet);

      return fieldMaps.stream().map(this::createEntity).flatMap(Optional::stream).toList();
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }

    return Collections.emptyList();

     */
  //  return null;
  //}

  /**
   * Instantiates an entity produced by this source given the required field value map.
   *
   * @param fieldToValues map of fields to their respective values
   * @return the entity if instantiation succeeds
   */
  //protected abstract Optional<T> createEntity(Map<String, String> fieldToValues);

  protected <T extends ResultEntity> Stream<SimpleEntityData> simpleEntityDataStream(
          Class<T> entityClass) {
    return buildStreamByQuery(entityClass, baseQuery, ps -> {})
            .map(fieldsToAttributes -> new SimpleEntityData(fieldsToAttributes, entityClass));
  }
}
