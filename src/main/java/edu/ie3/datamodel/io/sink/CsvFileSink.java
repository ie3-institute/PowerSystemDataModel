/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.exceptions.SinkException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.connectors.DataConnector;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sink that provides all capabilities to write {@link UniqueEntity}s to .csv-files
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements DataSink {

  private static final Logger log = LogManager.getLogger(CsvFileSink.class);

  private final CsvFileConnector connector;
  private final ProcessorProvider processorProvider;

  private final String csvSep;

  public CsvFileSink(String baseFolderPath) {
    this(baseFolderPath, new FileNamingStrategy(), false, ",");
  }

  /**
   * Create an instance of a csv file sink that can be used to persist Unique entities. This
   * implementation processes in sequential order. To parallelize this process one might consider
   * starting several sinks and use them for specific entities.
   *
   * @param baseFolderPath the base folder path where the files should be put into
   * @param fileNamingStrategy the file naming strategy that should be used
   * @param initFiles true if the files should be created during initialization (might create files,
   *     that only consist of a headline, because no data will be written into them), false
   *     otherwise
   * @param csvSep the csv file separator that should be use
   */
  public CsvFileSink(
      String baseFolderPath,
      FileNamingStrategy fileNamingStrategy,
      boolean initFiles,
      String csvSep) {
    this(baseFolderPath, new ProcessorProvider(), fileNamingStrategy, initFiles, csvSep);
  }

  /**
   * Create an instance of a csv file sink that can be used to persist Unique entities. This
   * implementation processes in sequential order. To parallelize this process one might consider
   * starting several sinks and use them for specific entities. Be careful when providing your own
   * {@link ProcessorProvider} because if you're not 100% sure that it knows about all entities
   * you're going to process exceptions might occur. Therefore it is strongly advised to either use
   * a constructor without providing the {@link ProcessorProvider} or provide a general {@link
   * ProcessorProvider} by calling {@link ProcessorProvider()}
   *
   * @param baseFolderPath the base folder path where the files should be put into
   * @param processorProvider the processor provided that should be used for entity de-serialization
   * @param fileNamingStrategy the file naming strategy that should be used
   * @param initFiles true if the files should be created during initialization (might create files,
   *     that only consist of a headline, because no data will be written into them), false
   *     otherwise
   * @param csvSep the csv file separator that should be use
   */
  public CsvFileSink(
      String baseFolderPath,
      ProcessorProvider processorProvider,
      FileNamingStrategy fileNamingStrategy,
      boolean initFiles,
      String csvSep) {
    this.csvSep = csvSep;
    this.processorProvider = processorProvider;
    this.connector = new CsvFileConnector(baseFolderPath, fileNamingStrategy);

    if (initFiles) initFiles(processorProvider, connector);
  }

  @Override
  public DataConnector getDataConnector() {
    return connector;
  }

  @Override
  public <T extends UniqueEntity> void persistAll(Collection<T> entities) {
    for (T entity : entities) {
      persist(entity);
    }
  }

  @Override
  public <T extends UniqueEntity> void persist(T entity) {
    /* Distinguish between "regular" input / result models and time series */
    if (entity instanceof InputEntity || entity instanceof ResultEntity) {
      /* This this a nested object or not? */
      if (entity instanceof NestedEntity) {
        try {
          persistIgnoreNested(entity);
          for (InputEntity ent : Extractor.extractElements((NestedEntity) entity)) {
            persistIgnoreNested(ent);
          }
        } catch (ExtractorException e) {
          log.error(
              "An error occurred during extraction of nested entity'"
                  + entity.getClass().getSimpleName()
                  + "': ",
              e);
        }
      } else {
        persistIgnoreNested(entity);
      }
    } else if (entity instanceof TimeSeries) {
      TimeSeries<?, ?> timeSeries = (TimeSeries<?, ?>) entity;
      persistTimeSeries(timeSeries);
    } else {
      throw new SinkException(
          "I don't know how to handle an entity of class " + entity.getClass().getSimpleName());
    }
  }

  @Override
  public <C extends UniqueEntity> void persistIgnoreNested(C entity) {
    LinkedHashMap<String, String> entityFieldData =
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

    try {
      String[] headerElements = processorProvider.getHeaderElements(entity.getClass());
      BufferedWriter writer = connector.getOrInitWriter(entity.getClass(), headerElements, csvSep);
      write(entityFieldData, headerElements, writer);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element", e);
    }
  }

  @Override
  public <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries) {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    log.debug("I got a time series of type {}.", key);

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

    try {
      String[] headerElements = processorProvider.getHeaderElements(key);
      BufferedWriter writer = connector.getOrInitWriter(timeSeries, headerElements, csvSep);
      entityFieldData.forEach(fieldData -> write(fieldData, headerElements, writer));
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element", e);
    } catch (ConnectorException e) {
      log.error("Exception occurred during acquisition of writer");
    }
  }

  /**
   * Initialize files, hence create a file for each expected class that will be processed in the
   * future. Please note, that files for time series can only be create on presence of a concrete
   * time series, as their file name depends on the individual uuid of the time series.
   *
   * @param processorProvider the processor provider all files that will be processed is derived
   *     from
   * @param connector the connector to the files
   */
  private void initFiles(
      final ProcessorProvider processorProvider, final CsvFileConnector connector) {

    processorProvider
        .getRegisteredClasses()
        .forEach(
            clz -> {
              try {
                String[] headerElements = processorProvider.getHeaderElements(clz);
                connector.getOrInitWriter(clz, headerElements, csvSep);
              } catch (ProcessorProviderException e) {
                log.error(
                    "Error during receiving of head line elements. Cannot prepare writer for class {}",
                    clz,
                    e);
              }
            });
  }

  /**
   * Actually persisting the provided entity field data
   *
   * @param entityFieldData a mapping of an entity instance fields to their values
   * @param headerElements the header elements of the entity, normally all attributes of the entity
   *     class
   * @param writer the corresponding writer for that should be used
   */
  private void write(
      LinkedHashMap<String, String> entityFieldData,
      String[] headerElements,
      BufferedWriter writer) {

    try {
      for (int i = 0; i < headerElements.length; i++) {
        String attribute = headerElements[i];
        writer.append(entityFieldData.get(attribute));
        if (i + 1 < headerElements.length) {
          writer.append(csvSep);
        } else {
          writer.append("\n");
        }
      }
      writer.flush();
    } catch (IOException e) {
      log.error(
          "Error while writing entity with field data: "
              + Arrays.toString(entityFieldData.entrySet().toArray()),
          e);
    }
  }
}
