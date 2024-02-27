/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.Entity;
import java.util.Optional;

/** A naming strategy for database entities */
public class DatabaseNamingStrategy {

  private static final String TIME_SERIES_PREFIX = "time_series_";
  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;

  public DatabaseNamingStrategy(EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
  }

  public DatabaseNamingStrategy() {
    this(new EntityPersistenceNamingStrategy());
  }

  /**
   * Provides the String that all time series tables are prefixed with
   *
   * @return the time series prefix
   */
  public String getTimeSeriesPrefix() {
    return TIME_SERIES_PREFIX;
  }

  /**
   * Provides the name of a time series table given a column scheme
   *
   * @param columnScheme the column scheme of the source data
   * @return the table name
   */
  public String getTimeSeriesEntityName(ColumnScheme columnScheme) {
    return TIME_SERIES_PREFIX + columnScheme.getScheme();
  }

  public Optional<String> getEntityName(Class<? extends Entity> cls) {
    return entityPersistenceNamingStrategy.getEntityName(cls);
  }
}
