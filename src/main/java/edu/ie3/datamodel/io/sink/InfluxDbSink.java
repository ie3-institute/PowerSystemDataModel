/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** InfluxDB Sink for result and time series data */
public class InfluxDbSink implements OutputDataSink {
  public static final Logger log = LoggerFactory.getLogger(InfluxDbSink.class);
  /** Field name for time */
  private static final String FIELD_NAME_TIME = "time";
  /** Field name for input model uuid field in result entities */
  private static final String FIELD_NAME_INPUT = "inputModel";

  private final InfluxDbConnector connector;
  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;
  private final ProcessorProvider processorProvider;

  /**
   * Initializes a new InfluxDbWeatherSource
   *
   * @param connector needed for database connection
   * @param entityPersistenceNamingStrategy needed to create measurement names for entities
   */
  public InfluxDbSink(
      InfluxDbConnector connector, EntityPersistenceNamingStrategy entityPersistenceNamingStrategy)
      throws EntityProcessorException {
    this.connector = connector;
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
    this.processorProvider =
        new ProcessorProvider(
            ProcessorProvider.allResultEntityProcessors(),
            ProcessorProvider.allTimeSeriesProcessors());
  }

  /**
   * Initializes a new InfluxDbWeatherSource with a default EntityPersistenceNamingStrategy
   *
   * @param connector needed for database connection
   */
  public InfluxDbSink(InfluxDbConnector connector) throws EntityProcessorException {
    this(connector, new EntityPersistenceNamingStrategy());
  }

  @Override
  public void shutdown() {
    connector.shutdown();
  }

  @Override
  public <C extends Entity> void persist(C entity) throws ProcessorProviderException {
    Set<Point> points = extractPoints(entity);
    // writes only the exact one point instead of unnecessarily wrapping it in BatchPoints
    if (points.size() == 1) write(points.iterator().next());
    else writeAll(points);
  }

  @Override
  public <C extends Entity> void persistAll(Collection<C> entities)
      throws ProcessorProviderException {
    Set<Point> points = new HashSet<>();
    for (C entity : entities) {
      points.addAll(extractPoints(entity));
    }
    writeAll(points);
  }

  @Override
  public <E extends TimeSeriesEntry<V>, V extends Value, R extends Value> void persistTimeSeries(
      TimeSeries<E, V, R> timeSeries) throws ProcessorProviderException {
    Set<Point> points = transformToPoints(timeSeries);
    writeAll(points);
  }

  /**
   * If batch writing is enabled, this call writes everything inside the batch to the database. This
   * will block until all pending points are written.
   */
  public void flush() {
    if (connector.getSession().isBatchEnabled()) connector.getSession().flush();
  }

  /**
   * Transforms a ResultEntity to an influxDB data point. <br>
   * As the equivalent to a relational table, the influxDB measurement point will be named using the
   * given EntityPersistenceNamingStrategy if possible, or the class name otherwise. All special
   * characters in the measurement name will be replaced by underscores.
   *
   * @param entity the entity to transform
   */
  private Point transformToPoint(ResultEntity entity) throws ProcessorProviderException {
    Optional<String> measurementName =
        entityPersistenceNamingStrategy.getResultEntityName(entity.getClass());
    if (measurementName.isEmpty())
      log.warn(
          "I could not get a measurement name for class {}. I am using its simple name instead.",
          entity.getClass().getSimpleName());
    return transformToPoint(entity, measurementName.orElse(entity.getClass().getSimpleName()));
  }

  /**
   * Transforms a ResultEntity to an influxDB data point. <br>
   * All special characters in the measurement name will be replaced by underscores.
   *
   * @param entity the entity to transform
   * @param measurementName equivalent to the name of a relational table
   */
  private Point transformToPoint(ResultEntity entity, String measurementName)
      throws ProcessorProviderException {

    LinkedHashMap<String, String> entityFieldData =
        processorProvider.handleEntity(entity).getOrThrow();
    entityFieldData.remove(FIELD_NAME_TIME);
    return Point.measurement(transformToMeasurementName(measurementName))
        .time(entity.getTime().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
        .tag("input_model", entityFieldData.remove(FIELD_NAME_INPUT))
        .tag("scenario", connector.getScenarioName())
        .fields(Collections.unmodifiableMap(entityFieldData))
        .build();
  }

  /**
   * Transforms a timeSeries to influxDB data points, one point for each value. <br>
   * As the equivalent to a relational table, the influxDB measurement point will be named using the
   * given EntityPersistenceNamingStrategy if possible, or the class name otherwise. All special
   * characters in the measurement name will be replaced by underscores.
   *
   * @param timeSeries the time series to transform
   */
  private <E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
      Set<Point> transformToPoints(TimeSeries<E, V, R> timeSeries)
          throws ProcessorProviderException {
    if (timeSeries.getEntries().isEmpty()) return Collections.emptySet();

    Optional<String> measurementName = entityPersistenceNamingStrategy.getEntityName(timeSeries);
    if (measurementName.isEmpty()) {
      String valueClassName =
          timeSeries.getEntries().iterator().next().getValue().getClass().getSimpleName();
      log.warn(
          "I could not get a measurement name for TimeSeries value class {}. I am using its value's simple name instead.",
          valueClassName);
      return transformToPoints(timeSeries, valueClassName);
    }
    return transformToPoints(timeSeries, measurementName.get());
  }

  /**
   * Transforms a timeSeries to influxDB data points, one point for each value. <br>
   * All special characters in the measurement name will be replaced by underscores.
   *
   * @param timeSeries the time series to transform
   * @param measurementName equivalent to the name of a relational table
   */
  private <E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
      Set<Point> transformToPoints(TimeSeries<E, V, R> timeSeries, String measurementName)
          throws ProcessorProviderException {
    Set<Point> points = new HashSet<>();
    Set<LinkedHashMap<String, String>> entityFieldData =
        processorProvider.handleTimeSeries(timeSeries);

    for (LinkedHashMap<String, String> dataMapping : entityFieldData) {
      String timeString = dataMapping.remove(FIELD_NAME_TIME);
      long timeMillis = ZonedDateTime.parse(timeString).toInstant().toEpochMilli();
      Point point =
          Point.measurement(transformToMeasurementName(measurementName))
              .time(timeMillis, TimeUnit.MILLISECONDS)
              .tag("scenario", connector.getScenarioName())
              .fields(Collections.unmodifiableMap(dataMapping))
              .build();
      points.add(point);
    }
    return points;
  }

  /**
   * Transforms an entity to an influxDB data point. <br>
   * The measurement point will be named by the given EntityPersistenceNamingStrategy if possible,
   * or the class name otherwise. All special characters in the measurement name will be replaced by
   * underscores.
   *
   * @param entity the entity of which influxDB points will be extracted
   * @param <C> bounded to be all unique entities, but logs an error and returns an empty Set if it
   *     does not extend {@link ResultEntity} or {@link TimeSeries}
   */
  private <C extends Entity> Set<Point> extractPoints(C entity) throws ProcessorProviderException {
    Set<Point> points = new HashSet<>();
    /* Distinguish between result models and time series */
    if (entity instanceof ResultEntity resultEntity) {
      points.add(transformToPoint(resultEntity));
    } else if (entity instanceof TimeSeries<?, ?, ?> timeSeries) {
      points.addAll(transformToPoints(timeSeries));
    } else {
      log.error(
          "I don't know how to handle an entity of class {}", entity.getClass().getSimpleName());
    }
    return points;
  }

  /**
   * Writes the point to the database
   *
   * @param point point to write
   */
  private void write(Point point) {
    if (point == null) return;
    connector.getSession().write(point);
  }

  /**
   * Writes all points as a batch to the database
   *
   * @param points points to write
   */
  private void writeAll(Collection<Point> points) {
    if (points.isEmpty()) return;
    BatchPoints batchPoints = BatchPoints.builder().points(points).build();
    connector.getSession().write(batchPoints);
  }

  /**
   * Remove leading and trailing whitespace and replace all special characters with underscores
   *
   * @param filename the file name to transform
   */
  private static String transformToMeasurementName(String filename) {
    return filename.trim().replaceAll("\\W", "_");
  }
}
