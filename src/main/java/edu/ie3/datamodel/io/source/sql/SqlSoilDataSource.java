/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.sql;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.source.SoilDataSource;
import edu.ie3.datamodel.models.Entity;
import java.util.Map;
import java.util.stream.Stream;

/** SQL source for soil layers and soil types. */
public class SqlSoilDataSource extends SoilDataSource {
  private final SqlDataSource dataSource;

  /**
   * Constructor for a SQL source of soil data.
   *
   * @param connector the database connector
   * @param schemaName the database schema
   * @param namingStrategy the naming strategy used to locate the tables
   */
  public SqlSoilDataSource(
      SqlConnector connector, String schemaName, DatabaseNamingStrategy namingStrategy) {
    this.dataSource = new SqlDataSource(connector, schemaName, namingStrategy);
  }

  @Override
  protected Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    return dataSource.getSourceData(entityClass);
  }
}
