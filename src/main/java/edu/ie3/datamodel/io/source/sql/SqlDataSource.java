/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.InvalidColumnNameException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.factory.timeseries.IdCoordinateFactory;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.FunctionalDataSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.validation.ValidationUtils;
import edu.ie3.util.StringUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDataSource implements FunctionalDataSource {

  protected static final Logger log = LoggerFactory.getLogger(SqlDataSource.class);

  protected final SqlConnector connector;
  protected final DatabaseNamingStrategy databaseNamingStrategy;
  protected String schemaName;

  protected SqlDataSource(
      String jdbcUrl,
      String userName,
      String password,
      String schemaName,
      DatabaseNamingStrategy databaseNamingStrategy) {
    this.connector = new SqlConnector(jdbcUrl, userName, password);
    this.schemaName = schemaName;
    this.databaseNamingStrategy = databaseNamingStrategy;
  }

  protected SqlDataSource(
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

  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends UniqueEntity> entityClass) {
    try {
      String explicitPath = databaseNamingStrategy.getEntityName(entityClass).get();
      return getSourceData(entityClass, explicitPath);
    } catch (NoSuchElementException e) {
      log.error("...", e);
      return Stream.empty();
    }
  }

  @Override
  public Stream<Map<String, String>> getSourceData(
      Class<? extends UniqueEntity> entityClass, String explicitPath) {
    String query = createBaseQueryString(schemaName, explicitPath);
    return buildStreamByQuery(entityClass, connector, query);
  }

  @Override
  public Stream<Map<String, String>> getIdCoordinateSourceData(IdCoordinateFactory factory) {
    String tableName = "coordinates";
    String query = createBaseQueryString(schemaName, tableName);

    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {

      Function<Map<String, String>, String> idExtractor =
          fieldToValues -> fieldToValues.get(factory.getIdField());

      Collection<Map<String, String>> allRows = queryToListOfMaps(query);

      Set<Map<String, String>> withDistinctCoordinateId =
          distinctRowsWithLog(allRows, idExtractor, "coordinate id mapping", "coordinate id");

      Function<Map<String, String>, String> coordinateExtractor =
          fieldToValues ->
              fieldToValues
                  .get(factory.getLatField())
                  .concat(fieldToValues.get(factory.getLonField()));

      return distinctRowsWithLog(
          withDistinctCoordinateId, coordinateExtractor, "coordinate id mapping", "coordinate")
          .parallelStream();
    } catch (SQLException e) {
      log.error("Cannot read the file for coordinate id to coordinate mapping.", e);
    }
    return Stream.empty();
  }

  /**
   * Interface for anonymous functions that are used as a parameter for {@link #buildStreamByQuery}.
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
      Class<? extends UniqueEntity> entityClass, SqlConnector sqlConnector, String query) {
    try {
      return buildStreamByQuery(
          entityClass, ps -> {}, sqlConnector.getConnection().prepareStatement(query));
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }
    return Stream.empty();
  }

  protected Stream<Map<String, String>> buildStreamByQuery(
      Class<? extends UniqueEntity> entityClass,
      AddParams addParams,
      PreparedStatement preparedStatement) {
    String query = createBaseQueryString(schemaName, entityClass.getSimpleName());
    try (PreparedStatement ps = preparedStatement) {
      addParams.addParams(ps);

      ResultSet resultSet = ps.executeQuery();
      List<Map<String, String>> fieldMaps = connector.extractFieldMaps(resultSet);

      return fieldMaps.stream();
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }
    return Stream.empty();
  }

  protected Stream<Map<String, String>> buildStreamByQuery(String tableName) {
    String query = createBaseQueryString(schemaName, tableName);
    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
      ResultSet resultSet = ps.executeQuery();
      List<Map<String, String>> fieldMaps = connector.extractFieldMaps(resultSet);

      return fieldMaps.stream();
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }
    return Stream.empty();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  protected List<Map<String, String>> queryToListOfMaps(String query, AddParams addParams) {
    try (PreparedStatement ps = connector.getConnection().prepareStatement(query)) {
      addParams.addParams(ps);

      ResultSet resultSet = ps.executeQuery();
      return connector.extractFieldMaps(resultSet);
    } catch (SQLException e) {
      log.error("Error during execution of query {}", query, e);
    }
    return Collections.emptyList();
  }

  protected List<Map<String, String>> queryToListOfMaps(String query) {
    return queryToListOfMaps(query, ps -> {});
  }

  /**
   * Returns a collection of maps each representing a row in csv file that can be used to built one
   * entity. The uniqueness of each row is doubled checked by a) that no duplicated rows are
   * returned that are full (1:1) matches and b) that no rows are returned that have the same
   * composite key, which gets extracted by the provided extractor. As both cases destroy uniqueness
   * constraints, an empty set is returned to indicate that these data cannot be processed safely
   * and the error is logged. For case a), only the duplicates are filtered out and a set with
   * unique rows is returned.
   *
   * @param allRows collection of rows of a csv file an entity should be built from
   * @param keyExtractor Function, that extracts the key from field to value mapping, that is meant
   *     to be unique
   * @param entityDescriptor Colloquial descriptor of the entity, the data is foreseen for (for
   *     debug String)
   * @param keyDescriptor Colloquial descriptor of the key, that is meant to be unique (for debug
   *     String)
   * @return either a set containing only unique rows or an empty set if at least two rows with the
   *     same UUID but different field values exist
   */
  protected Set<Map<String, String>> distinctRowsWithLog(
      Collection<Map<String, String>> allRows,
      final Function<Map<String, String>, String> keyExtractor,
      String entityDescriptor,
      String keyDescriptor) {
    Set<Map<String, String>> allRowsSet = new HashSet<>(allRows);
    // check for duplicated rows that match exactly (full duplicates) -> sanity only, not crucial -
    // case a)
    if (allRows.size() != allRowsSet.size()) {
      log.warn(
          "File with {} contains {} exact duplicated rows. File cleanup is recommended!",
          entityDescriptor,
          (allRows.size() - allRowsSet.size()));
    }

    /* Check for rows with the same key based on the provided key extractor function */
    Set<Map<String, String>> distinctIdSet =
        allRowsSet.parallelStream()
            .filter(ValidationUtils.distinctByKey(keyExtractor))
            .collect(Collectors.toSet());
    if (distinctIdSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctIdSet);
      String affectedCoordinateIds =
          allRowsSet.stream().map(keyExtractor).collect(Collectors.joining(",\n"));
      log.error(
          """
                      '{}' entities with duplicated {} key, but different field values found! Please review the corresponding input file!
                      Affected primary keys:
                      {}""",
          entityDescriptor,
          keyDescriptor,
          affectedCoordinateIds);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    return allRowsSet;
  }
}
