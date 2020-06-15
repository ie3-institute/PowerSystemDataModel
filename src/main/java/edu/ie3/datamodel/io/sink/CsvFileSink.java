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
import edu.ie3.datamodel.io.csv.BufferedCsvWriter;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sink that provides all capabilities to write {@link UniqueEntity}s to .csv-files. Be careful
 * about using methods other than {@link #persistJointGrid(JointGridContainer)} because all other
 * methods <b>do not check</b> for duplicate entries but only dump the data they received. In
 * contrast, when using {@link #persistJointGrid(JointGridContainer)}, all nested entities get
 * extracted first and then dumped individually without any duplicate lines.
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
   * ProcessorProvider} by calling {@link ProcessorProvider#ProcessorProvider()}
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
      log.error(
          "I don't know how to handle an entity of class {}", entity.getClass().getSimpleName());
    }
  }

  @Override
  public <C extends UniqueEntity> void persistIgnoreNested(C entity) {
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

      String[] headerElements = processorProvider.getHeaderElements(entity.getClass());
      BufferedCsvWriter writer =
          connector.getOrInitWriter(entity.getClass(), headerElements, csvSep);
      LinkedHashMap<String, String> quotedEntityFieldData = quoteCSVStrings(entityFieldData);
      writer.write(quotedEntityFieldData);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    } catch (ConnectorException e) {
      log.error("Exception occurred during retrieval of writer. Cannot write this element.", e);
    } catch (IOException e) {
      log.error("Exception occurred during writing of this element. Cannot write this element.", e);
    } catch (SinkException e) {
      log.error(
          "Cannot persist provided entity '{}'. Exception: {}",
          () -> entity.getClass().getSimpleName(),
          () -> e);
    }
  }

  /**
   * Quotes all fields that contain special characters to comply with the CSV specification RFC 4180
   * (https://tools.ietf.org/html/rfc4180) The " contained in the JSON strings are escaped with the
   * same character to make the CSV data readable later
   *
   * @param entityFieldData LinkedHashMap containing all entityData
   * @return LinkedHashMap containing all entityData with the relevant data quoted
   */
  private LinkedHashMap<String, String> quoteCSVStrings(
      LinkedHashMap<String, String> entityFieldData) {
    for (Map.Entry<String, String> entry : entityFieldData.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      if (value.matches("(?:.*)\\{(?:.*)}")) {
        entityFieldData.put(
            key,
            value
                .replaceAll("\"", "\"\"")
                .replaceAll("^([^\"])", "\"$1")
                .replaceAll("([^\"])$", "$1\""));
      }
    }
    return entityFieldData;
  }

  @Override
  public <C extends UniqueEntity> void persistAllIgnoreNested(Collection<C> entities) {
    entities.parallelStream().forEach(this::persistIgnoreNested);
  }

  @Override
  public void persistJointGrid(JointGridContainer jointGridContainer) {
    // get raw grid entities with types or operators
    RawGridElements rawGridElements = jointGridContainer.getRawGrid();
    Set<NodeInput> nodes = rawGridElements.getNodes();
    Set<LineInput> lines = rawGridElements.getLines();
    Set<Transformer2WInput> transformer2Ws = rawGridElements.getTransformer2Ws();
    Set<Transformer3WInput> transformer3Ws = rawGridElements.getTransformer3Ws();
    Set<SwitchInput> switches = rawGridElements.getSwitches();
    Set<MeasurementUnitInput> measurementUnits = rawGridElements.getMeasurementUnits();

    // get system participants with types or operators
    SystemParticipants systemParticipants = jointGridContainer.getSystemParticipants();
    Set<BmInput> bmPlants = systemParticipants.getBmPlants();
    Set<ChpInput> chpPlants = systemParticipants.getChpPlants();
    Set<EvcsInput> evCS = systemParticipants.getEvCS();
    Set<EvInput> evs = systemParticipants.getEvs();
    Set<FixedFeedInInput> fixedFeedIns = systemParticipants.getFixedFeedIns();
    Set<HpInput> heatPumps = systemParticipants.getHeatPumps();
    Set<LoadInput> loads = systemParticipants.getLoads();
    Set<PvInput> pvPlants = systemParticipants.getPvPlants();
    Set<StorageInput> storages = systemParticipants.getStorages();
    Set<WecInput> wecPlants = systemParticipants.getWecPlants();

    // get graphic elements (just for better readability, we could also just get them directly
    // below)
    GraphicElements graphicElements = jointGridContainer.getGraphics();

    // extract types
    Set<AssetTypeInput> types =
        Stream.of(
                lines,
                transformer2Ws,
                transformer3Ws,
                bmPlants,
                chpPlants,
                evs,
                heatPumps,
                storages,
                wecPlants)
            .flatMap(Collection::stream)
            .map(
                    Extractor::extractType) // due to a bug in java 8 this *cannot* be replaced with
            // method reference!
            .collect(Collectors.toSet());

    // extract operators
    Set<OperatorInput> operators =
        Stream.of(
                nodes,
                lines,
                transformer2Ws,
                transformer3Ws,
                switches,
                measurementUnits,
                bmPlants,
                chpPlants,
                evCS,
                evs,
                fixedFeedIns,
                heatPumps,
                loads,
                pvPlants,
                storages,
                wecPlants)
            .flatMap(Collection::stream)
            .map(Extractor::extractOperator)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());

    // persist all entities
    Stream.of(
            rawGridElements.allEntitiesAsList(),
            systemParticipants.allEntitiesAsList(),
            graphicElements.allEntitiesAsList(),
            types,
            operators)
        .flatMap(Collection::stream)
        .forEach(this::persistIgnoreNested);
  }

  @Override
  public void shutdown() {
    // shutdown the connector
    connector.shutdown();
  }

  @Override
  public <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries) {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);

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

      String[] headerElements = processorProvider.getHeaderElements(key);
      BufferedCsvWriter writer = connector.getOrInitWriter(timeSeries, headerElements, csvSep);
      entityFieldData.forEach(
          data -> {
            try {
              writer.write(data);
            } catch (IOException e) {
              log.error(
                  "Cannot write the following entity data: '{}'. Exception: {}",
                  () -> data,
                  () -> e);
            } catch (SinkException e) {
              log.error("Exception occurred during processing the provided data fields: ", e);
            }
          });
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    } catch (ConnectorException e) {
      log.error("Exception occurred during acquisition of writer.", e);
    } catch (SinkException e) {
      log.error("Exception occurred during processor request: ", e);
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
                    "Error during receiving of head line elements. Cannot prepare writer for class {}.",
                    clz,
                    e);
              } catch (ConnectorException e) {
                log.error(
                    "Error during instantiation files. Cannot get or init writer for class {}.",
                    clz,
                    e);
              }
            });
  }
}
