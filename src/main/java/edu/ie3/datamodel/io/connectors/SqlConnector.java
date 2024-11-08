/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.util.StringUtils;
import edu.ie3.util.TimeUtil;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a DataConnector for a native SQL connection to a relational database. It was
 * implemented with a PostgreSQL database in mind, so there might be dialect issues if used with
 * other databases.
 */
public class SqlConnector implements DataConnector {
  public static final Logger log = LoggerFactory.getLogger(SqlConnector.class);

  private final String jdbcUrl;
  private final Properties connectionProps;
  private Connection connection;

  /**
   * Initializes a SqlConnector with the given JDBC url, username, password and time util
   *
   * @param jdbcUrl the JDBC url, should start with "jdbc:postgresql://" and contain the database
   *     name
   * @param userName Name of the role used for authentication
   * @param password Password for the role
   */
  public SqlConnector(String jdbcUrl, String userName, String password) {
    this.jdbcUrl = jdbcUrl;

    // setup properties
    this.connectionProps = new Properties();
    connectionProps.put("user", userName);
    connectionProps.put("password", password);
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
    } finally {
      // commits any changes made and unlocks database
      getConnection().commit();
    }
  }

  /**
   * Executes an update query
   *
   * @param query the query to execute
   * @return The number of updates or a negative number if the execution failed
   */
  public int executeUpdate(String query) throws SQLException {
    try (Statement statement = getConnection().createStatement()) {
      return statement.executeUpdate(query);
    } catch (SQLException e) {
      throw new SQLException(
          String.format("Error at execution of query, SQLReason: '%s'", e.getMessage()), e);
    } finally {
      // commits any changes made and unlocks database
      getConnection().commit();
    }
  }

  /**
   * Establishes and returns a database connection. If a connection has not been established yet, a
   * new one is created.
   *
   * @return the connection object
   * @throws SQLException if the connection could not be established
   */
  public Connection getConnection() throws SQLException {
    return getConnection(true);
  }

  /**
   * Establishes and returns a database connection. The {@link Connection#getAutoCommit()} is set to
   * {@code false}.
   *
   * @param reuseConnection should the connection be used again, if it is still valid? If not, a new
   *     connection will be established
   * @return the connection object
   * @throws SQLException if the connection could not be established
   */
  public Connection getConnection(boolean reuseConnection) throws SQLException {
    if (!reuseConnection || connection == null || connection.isClosed()) {
      try {
        if (connection != null) connection.close();

        connection = DriverManager.getConnection(jdbcUrl, connectionProps);
        connection.setAutoCommit(false);
      } catch (SQLException e) {
        throw new SQLException("Could not establish connection: ", e);
      }
    }
    return connection;
  }

  @Override
  public void shutdown() {
    try {
      if (Objects.nonNull(connection)) connection.close();
    } catch (SQLException throwables) {
      log.error("Unable to close connection '{}' during shutdown.", connection, throwables);
    }
  }

  /**
   * Method to execute a {@link PreparedStatement} and return its result as a stream.
   *
   * @param ps to execute
   * @param fetchSize used for {@link PreparedStatement#setFetchSize(int)}
   * @return a stream of maps
   * @throws SQLException if an exception occurred while executing the query
   */
  public Stream<Map<String, String>> toStream(PreparedStatement ps, int fetchSize)
      throws SQLException {
    try {
      ps.setFetchSize(fetchSize);
      ResultSet resultSet = ps.executeQuery();
      Iterator<Map<String, String>> sqlIterator = getSqlIterator(resultSet);

      return StreamSupport.stream(
              Spliterators.spliteratorUnknownSize(
                  sqlIterator, Spliterator.NONNULL | Spliterator.IMMUTABLE),
              true)
          .onClose(() -> closeResultSet(ps, resultSet));
    } catch (SQLException e) {
      // catches the exception, closes the statement and re-throws the exception
      closeResultSet(ps, null);
      throw e;
    }
  }

  /**
   * Returns an {@link Iterator} for the given {@link ResultSet}.
   *
   * @param rs given result set
   * @return an iterator
   */
  public Iterator<Map<String, String>> getSqlIterator(ResultSet rs) {
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        try {
          return rs.next();
        } catch (SQLException e) {
          log.error("Exception at extracting next ResultSet: ", e);
          closeResultSet(null, rs);
          return false;
        }
      }

      @Override
      public Map<String, String> next() {
        try {
          return extractFieldMap(rs);
        } catch (SQLException e) {
          log.error("Exception at extracting ResultSet: ", e);
          closeResultSet(null, rs);
          return Collections.emptyMap();
        }
      }
    };
  }

  /**
   * Method for closing a {@link ResultSet}.
   *
   * @param rs to close
   */
  private void closeResultSet(PreparedStatement ps, ResultSet rs) {
    try (ps;
        rs) {
      log.debug("Resources successfully closed.");
    } catch (SQLException e) {
      log.warn("Failed to properly close sources.", e);
    }
  }

  /**
   * Extracts only the current row of the ResultSet into a field to value map
   *
   * @param rs the ResultSet to use
   * @return the field map for the current row
   */
  public Map<String, String> extractFieldMap(ResultSet rs) throws SQLException {
    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      String columnName = StringUtils.snakeCaseToCamelCase(metaData.getColumnName(i));
      String value;
      Object result = rs.getObject(i);
      if (result instanceof Timestamp) {
        value = TimeUtil.withDefaults.toString(rs.getTimestamp(i).toInstant());
      } else {
        value = String.valueOf(rs.getObject(i));
      }
      insensitiveFieldsToAttributes.put(columnName, value);
    }

    return insensitiveFieldsToAttributes;
  }
}
