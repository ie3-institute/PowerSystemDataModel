/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.InfluxDbConnector;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
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

public class InfluxDbSink implements DataSink {
    private static final Logger log = LogManager.getLogger(InfluxDbSink.class);
    private final ProcessorProvider processorProvider;

    private static final String FIELD_NAME_TIMESTAMP = "timestamp";
    private static final String FIELD_NAME_INPUT = "inputModel";

    InfluxDbConnector connector;
    private final FileNamingStrategy fileNamingStrategy;

    public InfluxDbSink(InfluxDbConnector connector, FileNamingStrategy fileNamingStrategy) {
        this.connector = connector;
        this.fileNamingStrategy = fileNamingStrategy;
        this.processorProvider = new ProcessorProvider(); //MIA define classes?
    }

    public InfluxDbSink(InfluxDbConnector connector){
        this(connector, new FileNamingStrategy());
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
        Optional<String> measurementName = fileNamingStrategy.getResultEntityFileName(entity.getClass());
        if(!measurementName.isPresent()) log.warn("I could not get a measurement name for class {}. I am using its simple name instead.", entity.getClass().getSimpleName());
        return transformToPoint(entity, measurementName.orElse(entity.getClass().getSimpleName()));
    }

    private Optional<Point> transformToPoint(ResultEntity entity, String measurementName){
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
            entityFieldData.remove(FIELD_NAME_TIMESTAMP); //MIA delete after time is fixed
            return Optional.of(Point.measurement(measurementName)
                    .time((entity).getTimestamp().toInstant().toEpochMilli(), TimeUnit.MILLISECONDS) //MIA replace with entityFieldData.remove
                    .tag("input_model", entityFieldData.remove(FIELD_NAME_INPUT))
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
        if(timeSeries.getEntries().isEmpty()) return Collections.emptySet();
        Optional<String> measurementName = fileNamingStrategy.getFileName(timeSeries);
        if(!measurementName.isPresent()){
            String valueClassName = timeSeries.getEntries().iterator().next().getValue().getClass().getSimpleName();
            log.warn("I could not get a measurement name for TimeSeries value class {}. I am using its simple name instead.", valueClassName);
            return transformToPoints(timeSeries, valueClassName);
        }
        return transformToPoints(timeSeries, measurementName.get());
    }


    private<E extends TimeSeriesEntry<V>, V extends Value>  Set<Point> transformToPoints(TimeSeries<E, V> timeSeries, String measurementName){
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
                String timeString = dataMapping.remove(FIELD_NAME_TIMESTAMP);//MIA delete after time is fixed
                long timeMillis = ZonedDateTime.parse(timeString).toInstant().toEpochMilli();
                Point point = Point.measurement(measurementName)
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
                    log.info("Nested elements will not be persisted, as they are not of ResultEntity or TimeBasedValue type");
                }
            } catch (SinkException e) {
                log.error(
                        "Cannot persist provided entity '{}'. Exception: {}",
                        () -> entity.getClass().getSimpleName(),
                        () -> e);
            }
        } else if (entity instanceof TimeSeries) {
            TimeSeries<?, ?> timeSeries = (TimeSeries<?, ?>) entity;
            points.addAll(transformToPoints(timeSeries));
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