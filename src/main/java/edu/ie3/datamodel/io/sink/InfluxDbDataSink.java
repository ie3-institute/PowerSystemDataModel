/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.EntityNamingStrategy;
import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InfluxDbDataSink implements DataSink {
  private static final Logger log = LogManager.getLogger(InfluxDbDataSink.class);
  private final ProcessorProvider processorProvider;

  private static final String timestampFieldName = "timestamp";
  private static final String timeFieldName = "timestamp";
  private static final String inputModelFieldName = "inputModel";

  InfluxDbConnector connector;

  public InfluxDbDataSink(InfluxDbConnector connector) {
    this.connector = connector;
    this.processorProvider = new ProcessorProvider(); //MIA define classes?
  }

  @Override
  public void shutdown() {
    connector.shutdown();
  }

  @Override
  public <C extends UniqueEntity> void persist(C entity) {
    Set<Point> points = extractPoints(entity);
    writeAll(points);
  }

  @Override
  public <C extends UniqueEntity> void persistAll(Collection<C> entities) {
    Set<Point> points = new HashSet<>();
    /* Distinguish between result models and time series */
    for (C entity : entities) {
      points.addAll(extractPoints(entity));
    }
    writeAll(points);
  }

  @Override
  public <C extends UniqueEntity> void persistIgnoreNested(C entity) {
    try {
      if (!(entity instanceof ResultEntity)) throw new SinkException("Cannot handle entities that are not ResultEntities");
      Optional<Point> influxPoint = transformToPoint((ResultEntity) entity);
      write(influxPoint.orElseThrow(() -> new SinkException("Could not transform entity")));
    } catch (SinkException e) {
      log.error(
              "Cannot persist provided entity '{}'. Exception: {}",
              () -> entity.getClass().getSimpleName(),
              () -> e);
    }
  }

  @Override
  public <C extends UniqueEntity> void persistAllIgnoreNested(Collection<C> entities) {
    Set<Point> points = new HashSet<>();
    /* Distinguish between result models and time series */
    for (C entity : entities) {
      try {
        if (!(entity instanceof ResultEntity)) throw new SinkException("Cannot handle entities that are not ResultEntities");
        points.add(transformToPoint((ResultEntity) entity).orElseThrow(() -> new SinkException("Could not transform entity")));
      } catch (SinkException e) {
        log.error(
                "Cannot persist provided entity '{}'. Exception: {}",
                () -> entity.getClass().getSimpleName(),
                () -> e);
      }
    }
    writeAll(points);
  }

  @Override
  public <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(TimeSeries<E, V> timeSeries) {
    Set<Point> points = transformToPoints(timeSeries);
    writeAll(points);
  }

  @Override
  public void persistJointGrid(JointGridContainer jointGridContainer) {
    log.error("Cannot persist grids");
  }

  private Optional<Point> transformToPoint(ResultEntity entity){
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

      Optional<String> measurementName = EntityNamingStrategy.getEntityName(entity.getClass());
      if(!measurementName.isPresent()) log.warn("I could not get a measurement name for class {}. I am using its simple name instead.", entity.getClass().getSimpleName());
      entityFieldData.remove(timestampFieldName); //MIA delete after time is fixed
      return Optional.of(Point.measurement(measurementName.orElse(entity.getClass().getSimpleName()))
              .time(((ResultEntity) entity).getTimestamp().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS) //MIA replace with entityFieldData.remove
              .tag("input_model", entityFieldData.remove(inputModelFieldName))
              .tag("scenario", connector.getScenarioName())
              .fields(Collections.unmodifiableMap(entityFieldData))
              .build());
    } catch (SinkException e) {
      log.error(
              "Cannot persist provided entity '{}'. Exception: {}",
              () -> entity.getClass().getSimpleName(),
              () -> e);
    }
    return Optional.empty();
  }

  private<E extends TimeSeriesEntry<V>, V extends Value>  Set<Point> transformToPoints(TimeSeries<E, V> timeSeries){
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

      Optional<String> measurementName = EntityNamingStrategy.getEntityName(timeSeries);
      if(!measurementName.isPresent()) log.warn("I could not get a measurement name for time series {}. I am using its simple name instead.", key);
      for (LinkedHashMap<String, String> dataMapping : entityFieldData) {
        String timeString = dataMapping.remove(timeFieldName);//MIA delete after time is fixed
        long timeMillis = ZonedDateTime.parse(timeString).toInstant().toEpochMilli();
        Point point = Point.measurement(measurementName.orElse(key.getValueClass().getSimpleName()))
                .time(timeMillis, TimeUnit.MILLISECONDS) //MIA replace with entityFieldData.remove
                .tag("scenario", connector.getScenarioName())
                .fields(Collections.unmodifiableMap(dataMapping))
                .build();
        points.add(point);
      }
    } catch (SinkException e) {
      log.error(
              "Cannot persist provided time series '{}'. Exception: {}",
              () -> key,
              () -> e);
    }
    return points;
  }

  @NotNull
  private <C extends UniqueEntity> Set<Point> extractPoints(C entity) {
    Set<Point> points = new HashSet<>();
    /* Distinguish between result models and time series */
    if (entity instanceof ResultEntity) {
      /* This this a nested object or not? */
      try {
        points.add(transformToPoint((ResultEntity) entity).orElseThrow(() -> new SinkException("Could not transform entity")));
        if (entity instanceof NestedEntity) {
          for (InputEntity ent : Extractor.extractElements((NestedEntity) entity)) {
            points.add(transformToPoint((ResultEntity) entity).orElseThrow(() -> new SinkException("Could not transform entity")));
          }
        }
      } catch (ExtractorException e) {
        log.error(
                "An error occurred during extraction of nested entity'"
                        + entity.getClass().getSimpleName()
                        + "': ",
                e);
      } catch (SinkException e) {
        log.error(
                "Cannot persist provided entity '{}'. Exception: {}",
                () -> entity.getClass().getSimpleName(),
                () -> e);
      }
    } else if (entity instanceof TimeSeries) {
      TimeSeries<?, ?> timeSeries = (TimeSeries<?, ?>) entity;
      persistTimeSeries(timeSeries);
    } else {
      log.error(
              "I don't know how to handle an entity of class {}", entity.getClass().getSimpleName());
    }
    return points;
  }

  public void write(Point point) {
    if(point==null) return;
    try (InfluxDB session = connector.getSession()) {
      session.write(point);
    }
  }

  public void writeAll(Collection<Point> points) {
    if(points.isEmpty()) return;
    BatchPoints batchPoints = BatchPoints.builder().points(points).build();
    try (InfluxDB session = connector.getSession()) {
      session.write(batchPoints);
      session.flush(); //MIA necessary?
    }
  }
}
