/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.util.TimeUtil;
import java.sql.*;
import java.time.ZoneId;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements a DataConnector for a native SQL connection to a relational database. It was
 * implemented with a PostgreSQL database in mind, so there might be dialect issues if used with
 * other databases.
 */
public class SqlConnector implements DataConnector {
  private static final Logger log = LogManager.getLogger(SqlConnector.class);

  private final TimeUtil timeUtil;
  private final String jdbcUrl;
  private final String userName;
  private final String password;

  /**
   * Initializes a SqlConnector with the given JDBC url, username and password
   *
   * @param jdbcUrl the JDBC url, should start with "jdbc:postgresql://" and contain the database
   *     name
   * @param userName Name of the role used for authentication
   * @param password Password for the role
   */
  public SqlConnector(String jdbcUrl, String userName, String password) {
    this(
        jdbcUrl,
        userName,
        password,
        new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss.0"));
  }

  /**
   * Initializes a SqlConnector with the given JDBC url, username, password and time util
   *
   * @param jdbcUrl the JDBC url, should start with "jdbc:postgresql://" and contain the database
   *     name
   * @param userName Name of the role used for authentication
   * @param password Password for the role
   * @param timeUtil the time util to use for all timestamp result conversions
   */
  public SqlConnector(String jdbcUrl, String userName, String password, TimeUtil timeUtil) {
    this.jdbcUrl = jdbcUrl;
    this.userName = userName;
    this.password = password;
    this.timeUtil = timeUtil;
  }

  /**
   * Executes the given query. For update queries please use {@link
   * SqlConnector#executeUpdate(String)}.
   *
   * @param stmt the created statement
   * @param query the query to execute
   * @return the query result
   * @throws SQLException if the execution fails
   */
  public ResultSet executeQuery(Statement stmt, String query) throws SQLException {
    try {
      return stmt.executeQuery(query);
    } catch (SQLException e) {
      throw new SQLException(String.format("Error at execution of query \"%1.127s\": ", query), e);
    }
  }

  /**
   * Executes an update query
   *
   * @param updateQuery the query to execute
   * @return The number of updates or a negative number if the execution failed
   */
  public int executeUpdate(String updateQuery) {
    try (Statement stmt = getConnection().createStatement()) {
      return stmt.executeUpdate(updateQuery);
    } catch (SQLException e) {
      log.error(String.format("Error at execution of query \"%1.127s\": ", updateQuery), e);
      return -1;
    }
  }

  /**
   * Establishes and returns a database connection
   *
   * @return the connection object
   * @throws SQLException if the connection could not be established
   */
  public Connection getConnection() throws SQLException {
    Properties connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);
    try {
      return DriverManager.getConnection(jdbcUrl, connectionProps);
    } catch (SQLException e) {
      throw new SQLException("Could not establish connection: ", e);
    }
  }

  @Override
  public void shutdown() {
    // Nothing needs to be closed or shutdown
  }

  /**
   * Extracts all field to value maps from the ResultSet, one for each row
   *
   * @param rs the ResultSet to use
   * @param timeColumns names of the columns that contain a timestamp
   * @return a list of field maps
   */
  public List<Map<String, String>> extractFieldMaps(ResultSet rs, String... timeColumns) {
    List<Map<String, String>> fieldMaps = new ArrayList<>();
    try {
      while (rs.next()) {
        fieldMaps.add(extractFieldMap(rs, timeColumns));
      }
    } catch (SQLException e) {
      log.error("Exception at extracting ResultSet: ", e);
    }
    return fieldMaps;
  }

  /**
   * Extracts only the current row of the ResultSet into a field to value map
   *
   * @param rs the ResultSet to use
   * @param timeColumns names of the columns that contain a timestamp
   * @return the field map for the current row
   */
  public Map<String, String> extractFieldMap(ResultSet rs, String... timeColumns) {
    HashMap<String, String> fieldMap = new HashMap<>();
    try {
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();
      for (int i = 1; i <= columnCount; i++) {
        String columnName = metaData.getColumnName(i);
        String value;
        if (Arrays.asList(timeColumns).contains(columnName)) {
          value = timeUtil.toString(rs.getTimestamp(i).toInstant());
        } else {
          value = String.valueOf(rs.getObject(i));
        }
        fieldMap.put(columnName, value);
      }
    } catch (SQLException e) {
      log.error("Exception at extracting ResultSet: ", e);
    }
    return fieldMap;
  }
}
