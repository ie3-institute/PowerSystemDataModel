/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
      InfluxDbConnector connector,
      EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
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
  public InfluxDbSink(InfluxDbConnector connector) {
    this(connector, new EntityPersistenceNamingStrategy());
  }

  @Override
  public void shutdown() {
    connector.shutdown();
  }

  @Override
  public <C extends UniqueEntity> void persist(C entity) {
    Set<Point> points = extractPoints(entity);
    // writes only the exact one point instead of unnecessarily wrapping it in BatchPoints
    if (points.size() == 1) write(points.iterator().next());
    else writeAll(points);
  }

  @Override
  public <C extends UniqueEntity> void persistAll(Collection<C> entities) {
    Set<Point> points = new HashSet<>();
    for (C entity : entities) {
      points.addAll(extractPoints(entity));
    }
    writeAll(points);
  }

  @Override
  public <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries) {
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
  private Optional<Point> transformToPoint(ResultEntity entity) {
    Optional<String> measurementName =
        entityPersistenceNamingStrategy.getResultEntityName(entity.getClass());
    if (!measurementName.isPresent())
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
  private Optional<Point> transformToPoint(ResultEntity entity, String measurementName) {
    LinkedHashMap<String, String> entityFieldData;
    try {
      entityFieldData =
          processorProvider
              .handleEntity(entity)
              .orElseThrow(
                  () ->
                      new SinkException(
                          "Cannot persist entity of type '"
                              + entity.getClass().getSimpleName()
                              + "'. This sink can only process the following entities: ["
                              + processorProvider.getRegisteredClasses().stream()
                                  .map(Class::getSimpleName)
                                  .collect(Collectors.joining(","))
                              + "]"));
      entityFieldData.remove(FIELD_NAME_TIME);
      return Optional.of(
          Point.measurement(transformToMeasurementName(measurementName))
              .time(entity.getTime().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS)
              .tag("input_model", entityFieldData.remove(FIELD_NAME_INPUT))
              .tag("scenario", connector.getScenarioName())
              .fields(Collections.unmodifiableMap(entityFieldData))
              .build());
    } catch (SinkException e) {
      log.error(
          "Cannot persist provided entity '{}'. Exception: {}",
          entity.getClass().getSimpleName(),
          e);
    }
    return Optional.empty();
  }

  /**
   * Transforms a timeSeries to influxDB data points, one point for each value. <br>
   * As the equivalent to a relational table, the influxDB measurement point will be named using the
   * given EntityPersistenceNamingStrategy if possible, or the class name otherwise. All special
   * characters in the measurement name will be replaced by underscores.
   *
   * @param timeSeries the time series to transform
   */
  private <E extends TimeSeriesEntry<V>, V extends Value> Set<Point> transformToPoints(
      TimeSeries<E, V> timeSeries) {
    if (timeSeries.getEntries().isEmpty()) return Collections.emptySet();
    Optional<String> measurementName = entityPersistenceNamingStrategy.getEntityName(timeSeries);
    if (!measurementName.isPresent()) {
      String valueClassName =
          timeSeries.getEntries().iterator().next().getValue().getClass().getSimpleName();
      log.warn(
          "I could not get a measurement name for TimeSeries value class {}. I am using it's value's simple name instead.",
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
  private <E extends TimeSeriesEntry<V>, V extends Value> Set<Point> transformToPoints(
      TimeSeries<E, V> timeSeries, String measurementName) {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    Set<Point> points = new HashSet<>();
    try {
      Set<LinkedHashMap<String, String>> entityFieldData =
          processorProvider
              .handleTimeSeries(timeSeries)
              .orElseThrow(
                  () ->
                      new SinkException(
                          "Cannot persist time series of combination '"
                              + key
                              + "'. This sink can only process the following combinations: ["
                              + processorProvider.getRegisteredTimeSeriesCombinations().stream()
                                  .map(TimeSeriesProcessorKey::toString)
                                  .collect(Collectors.joining(","))
                              + "]"));

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
    } catch (SinkException e) {
      log.error("Cannot persist provided time series '{}'. Exception: {}", key, e);
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
  private <C extends UniqueEntity> Set<Point> extractPoints(C entity) {
    Set<Point> points = new HashSet<>();
    /* Distinguish between result models and time series */
    if (entity instanceof ResultEntity resultEntity) {
      try {
        points.add(
            transformToPoint(resultEntity)
                .orElseThrow(() -> new SinkException("Could not transform entity")));
      } catch (SinkException e) {
        log.error(
            "Cannot persist provided entity '{}'. Exception: {}",
            entity.getClass().getSimpleName(),
            e);
      }
    } else if (entity instanceof TimeSeries<?, ?> timeSeries) {
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
