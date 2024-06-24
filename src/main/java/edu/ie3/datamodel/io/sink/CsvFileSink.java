/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.csv.BufferedCsvWriter;
import edu.ie3.datamodel.io.csv.CsvFileDefinition;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.StringUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sink that provides all capabilities to write {@link Entity}s to .csv-files. Be careful about
 * using methods other than {@link #persistJointGrid(JointGridContainer)} because all other methods
 * <b>do not check</b> for duplicate entries but only dump the data they received. In contrast, when
 * using {@link #persistJointGrid(JointGridContainer)}, all nested entities get extracted first and
 * then dumped individually without any duplicate lines.
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileSink implements InputDataSink, OutputDataSink {

  private static final Logger log = LoggerFactory.getLogger(CsvFileSink.class);

  private final CsvFileConnector connector;
  private final ProcessorProvider processorProvider;
  private final FileNamingStrategy fileNamingStrategy;
  private final String csvSep;

  public CsvFileSink(Path baseFolderPath) throws EntityProcessorException {
    this(baseFolderPath, new FileNamingStrategy(), ",");
  }

  /**
   * Create an instance of a csv file sink that can be used to persist Unique entities. This
   * implementation processes in sequential order. To parallelize this process one might consider
   * starting several sinks and use them for specific entities.
   *
   * @param baseFolderPath the base folder path where the files should be put into
   * @param fileNamingStrategy the data sink file naming strategy that should be used
   * @param csvSep the csv file separator that should be use
   */
  public CsvFileSink(Path baseFolderPath, FileNamingStrategy fileNamingStrategy, String csvSep)
      throws EntityProcessorException {
    this(baseFolderPath, new ProcessorProvider(), fileNamingStrategy, csvSep);
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
   * @param processorProvider the processor provided that should be used for entity serialization
   * @param fileNamingStrategy the data sink file naming strategy that should be used
   * @param csvSep the csv file separator that should be use
   */
  public CsvFileSink(
      Path baseFolderPath,
      ProcessorProvider processorProvider,
      FileNamingStrategy fileNamingStrategy,
      String csvSep) {
    this.csvSep = csvSep;
    this.processorProvider = processorProvider;
    this.connector = new CsvFileConnector(baseFolderPath);
    this.fileNamingStrategy = fileNamingStrategy;
  }

  @Override
  public <T extends Entity> void persistAll(Collection<T> entities) {
    for (T entity : entities) {
      persist(entity);
    }
  }

  @Override
  public <T extends Entity> void persist(T entity) {
    /* Distinguish between "regular" input / result models and time series */
    if (entity instanceof InputEntity inputEntity) {
      persistIncludeNested(inputEntity);
    } else if (entity instanceof ResultEntity) {
      write(entity);
    } else if (entity instanceof TimeSeries<?, ?> timeSeries) {
      persistTimeSeries(timeSeries);
    } else {
      log.error(
          "I don't know how to handle an entity of class {}", entity.getClass().getSimpleName());
    }
  }

  @Override
  public <C extends InputEntity> void persistIgnoreNested(C entity) {
    write(entity);
  }

  @Override
  public <C extends InputEntity> void persistAllIgnoreNested(Collection<C> entities) {
    entities.parallelStream().forEach(this::persistIgnoreNested);
  }

  @Override
  public <C extends InputEntity> void persistIncludeNested(C entity) {
    if (entity instanceof NestedEntity nestedEntity) {
      try {
        write(entity);
        for (InputEntity ent : Extractor.extractElements(nestedEntity)) {
          write(ent);
        }
      } catch (ExtractorException e) {
        log.error(
            String.format(
                "An error occurred during extraction of nested entity'%s': ",
                entity.getClass().getSimpleName()),
            e);
      }
    } else {
      write(entity);
    }
  }

  @Override
  public <C extends InputEntity> void persistAllIncludeNested(Collection<C> entities) {
    entities.parallelStream().forEach(this::persistIncludeNested);
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
    Set<EvcsInput> evcs = systemParticipants.getEvcs();
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
            .map(Extractor::extractType)
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
                evcs,
                evs,
                fixedFeedIns,
                heatPumps,
                loads,
                pvPlants,
                storages,
                wecPlants)
            .flatMap(Collection::stream)
            .map(Extractor::extractOperator)
            .flatMap(Optional::stream)
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
    try {
      TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
      String[] headerElements = csvHeaderElements(processorProvider.getHeaderElements(key));
      BufferedCsvWriter writer =
          connector.getOrInitWriter(
              timeSeries,
              new CsvFileDefinition(timeSeries, headerElements, csvSep, fileNamingStrategy));
      persistTimeSeries(timeSeries, writer);
      connector.closeTimeSeriesWriter(timeSeries.getUuid());
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    } catch (ConnectorException | FileException e) {
      log.error("Exception occurred during acquisition of writer.", e);
    } catch (IOException e) {
      log.error("Exception occurred during closing of writer.", e);
    }
  }

  private <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries, BufferedCsvWriter writer) throws ProcessorProviderException {
    try {
      Set<LinkedHashMap<String, String>> entityFieldData =
          processorProvider.handleTimeSeries(timeSeries);
      entityFieldData.forEach(
          data -> {
            try {
              writer.write(csvEntityFieldData(data));
            } catch (IOException e) {
              log.error("Cannot write the following entity data: '{}'. Exception: {}", data, e);
            } catch (SinkException e) {
              log.error("Exception occurred during processing the provided data fields: ", e);
            }
          });
    } catch (ProcessorProviderException e) {
      throw new ProcessorProviderException("Exception occurred during processor request: ", e);
    }
  }

  /**
   * Writes a entity into the corresponding CSV file. Does <b>not</b> include any nested entities.
   * The header names for the fields will be determined by the given {@link ProcessorProvider}.
   *
   * @param entity the entity to write
   * @param <C> bounded to be all unique entities
   */
  private <C extends Entity> void write(C entity) {
    try {
      LinkedHashMap<String, String> entityFieldData =
          processorProvider.handleEntity(entity).map(this::csvEntityFieldData).getOrThrow();
      String[] headerElements = processorProvider.getHeaderElements(entity.getClass());
      BufferedCsvWriter writer =
          connector.getOrInitWriter(
              entity.getClass(),
              new CsvFileDefinition(entity.getClass(), headerElements, csvSep, fileNamingStrategy));
      writer.write(entityFieldData);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    } catch (ConnectorException | FileException e) {
      log.error("Exception occurred during retrieval of writer. Cannot write this element.", e);
    } catch (IOException e) {
      log.error("Exception occurred during writing of this element. Cannot write this element.", e);
    } catch (SinkException e) {
      log.error(
          "Cannot persist provided entity '{}'. Exception: {}",
          entity.getClass().getSimpleName(),
          e);
    }
  }

  /**
   * Transforms a provided array of strings to valid csv formatted strings (according to csv
   * specification RFC 4180)
   *
   * @param strings array of strings that should be processed
   * @return a new array with valid csv formatted strings
   */
  private String[] csvHeaderElements(String[] strings) {
    return Arrays.stream(strings)
        .map(inputElement -> StringUtils.csvString(inputElement, csvSep))
        .toArray(String[]::new);
  }

  /**
   * Transforms a provided map of string to string to valid csv formatted strings (according to csv
   * specification RFC 4180)
   *
   * @param entityFieldData a string to string map that should be processed
   * @return a new map with valid csv formatted keys and values strings
   */
  private LinkedHashMap<String, String> csvEntityFieldData(
      LinkedHashMap<String, String> entityFieldData) {

    return entityFieldData.entrySet().stream()
        .map(
            mapEntry ->
                new AbstractMap.SimpleEntry<>(
                    StringUtils.csvString(mapEntry.getKey(), csvSep),
                    StringUtils.csvString(mapEntry.getValue(), csvSep)))
        .collect(
            Collectors.toMap(
                AbstractMap.SimpleEntry::getKey,
                AbstractMap.SimpleEntry::getValue,
                (v1, v2) -> {
                  throw new IllegalStateException(
                      "Converting entity data to RFC 4180 compliant strings has lead to duplicate keys. Initial input:\n\t"
                          + entityFieldData.entrySet().stream()
                              .map(entry -> entry.getKey() + " = " + entry.getValue())
                              .collect(Collectors.joining(",\n\t")));
                },
                LinkedHashMap::new));
  }
}
