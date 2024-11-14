/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import static edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy.logger;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.Value;
import java.util.Optional;

/** A naming strategy for database entities */
public class DatabaseNamingStrategy {

  private static final String TIME_SERIES_PREFIX = "time_series_";

  private static final String LOAD_PROFILE_PREFIX = "load_profile_";

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

  /**
   * Provides the name of a load profile given by the load profile key
   *
   * @param lpKey Load profile key
   * @return the table name
   */
  private String getLoadProfileEntityName(String lpKey) {
    return LOAD_PROFILE_PREFIX + lpKey;
  }

  /**
   * Provides the name of a unique entity class.
   *
   * @param cls Class extends UniqueEntity
   * @return the table name
   */
  public Optional<String> getEntityName(Class<? extends Entity> cls) {
    return entityPersistenceNamingStrategy.getEntityName(cls);
  }

  /**
   * Provides the name of a time series. Used to determine the table name in SQL database.
   *
   * @param timeSeries to be named TimeSeries
   * @return the table name
   */
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getEntityName(T timeSeries) {
    if (timeSeries instanceof IndividualTimeSeries individualTimeSeries) {
      Optional<E> maybeFirstElement = individualTimeSeries.getEntries().stream().findFirst();
      if (maybeFirstElement.isPresent()) {
        Class<? extends Value> valueClass = maybeFirstElement.get().getValue().getClass();
        return Optional.of(getTimeSeriesEntityName(ColumnScheme.parse(valueClass).orElseThrow()));
      } else {
        logger.error("Unable to determine content of time series {}", timeSeries);
        return Optional.empty();
      }
    } else if (timeSeries instanceof LoadProfileInput loadProfileInput) {
      return Optional.of(getLoadProfileEntityName(loadProfileInput.getType().getKey()));
    } else {
      logger.error("There is no naming strategy defined for {}", timeSeries);
      return Optional.empty();
    }
  }
}
