/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import static edu.ie3.datamodel.io.SqlUtils.*;
import static java.util.stream.Collectors.groupingBy;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.DbGridMetadata;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.StringUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlSink {

  protected static final Logger log = LoggerFactory.getLogger(SqlSink.class);

  private final SqlConnector connector;
  private final DatabaseNamingStrategy databaseNamingStrategy;
  private final ProcessorProvider processorProvider;
  private final String schemaName;

  private static final String TIME_SERIES = "time_series";

  public SqlSink(
      String schemaName, DatabaseNamingStrategy databaseNamingStrategy, SqlConnector connector)
      throws EntityProcessorException {
    this(schemaName, new ProcessorProvider(), databaseNamingStrategy, connector);
  }

  public SqlSink(
      String schemaName,
      ProcessorProvider processorProvider,
      DatabaseNamingStrategy databaseNamingStrategy,
      SqlConnector connector) {
    this.connector = connector;
    this.databaseNamingStrategy = databaseNamingStrategy;
    this.processorProvider = processorProvider;
    this.schemaName = schemaName;
  }

  public void shutdown() {
    connector.shutdown();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Entry point of a data sink to persist multiple entities in a collection.
   *
   * @param entities a collection of entities that should be persisted
   * @param identifier identifier of the grid
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   * @throws SQLException
   */
  public <C extends Entity> void persistAll(Collection<C> entities, DbGridMetadata identifier)
      throws SQLException {
    // Extract nested entities and add them to the set of entities
    Set<C> entitiesToAdd = new HashSet<>(entities); // entities to persist
    entities.stream()
        .forEach(
            entity -> {
              if (entity instanceof NestedEntity nestedEntity) {
                try {
                  entitiesToAdd.addAll(
                      (List<C>) Extractor.extractElements(nestedEntity).stream().toList());
                } catch (ExtractorException e) {
                  log.error(
                      String.format(
                          "An error occurred during extraction of nested entity'%s': ",
                          entity.getClass()),
                      e);
                }
              }
            });

    // Persist the entities in hierarchic order to avoid failure because of foreign keys
    for (Class<?> cls : hierarchicInsert()) {
      persistMixedList(
          entitiesToAdd.stream()
              .filter(ent -> cls.isAssignableFrom(ent.getClass()))
              .collect(Collectors.toList()),
          identifier);
      entitiesToAdd.removeIf(
          ent ->
              cls.isAssignableFrom(
                  ent.getClass())); // maybe it's not necessary but I'm not sure if there are
      // entities who aren't in the hierarchic structure
    }
    persistMixedList(new ArrayList<>(entitiesToAdd), identifier); // persist left entities
  }

  /**
   * Persist multiple input entities in a collection. In contrast to {@link SqlSink#persistAll} this
   * function does not extract nested entities.
   *
   * @param entities a collection of entities that should be persisted
   * @param identifier identifier of the grid
   */
  public <C extends InputEntity> void persistAllIgnoreNested(
      Collection<C> entities, DbGridMetadata identifier) {
    persistMixedList(new ArrayList<>(entities), identifier);
  }

  /**
   * Persist an entity. By default this method take care about the extraction process of nested
   * entities (if any)
   *
   * @param entity the entity that should be persisted
   * @param identifier identifier of the grid
   * @throws SQLException
   */
  public <C extends Entity> void persist(C entity, DbGridMetadata identifier)
      throws SQLException {
    if (entity instanceof InputEntity inputEntity) {
      persistIncludeNested(inputEntity, identifier);
    } else if (entity instanceof ResultEntity resultEntity) {
      insert(resultEntity, identifier);
    } else if (entity instanceof TimeSeries<?, ?> timeSeries) {
      persistTimeSeries(timeSeries, identifier);
    } else {
      log.error(
          "I don't know how to handle an entity of class {}", entity.getClass().getSimpleName());
    }
  }

  /**
   * Persist an entity. In contrast to {@link SqlSink#persist} this function does not extract nested
   * entities.
   *
   * @param entity the entity that should be persisted
   * @param identifier identifier of the grid
   * @throws SQLException
   */
  public <C extends InputEntity> void persistIgnoreNested(C entity, DbGridMetadata identifier)
      throws SQLException {
    insert(entity, identifier);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /*
  protected <C extends Entity> void persistListIncludeNested(
      List<C> entities, Class<C> cls, DbGridMetadata identifier) throws SQLException {
    if (NestedEntity.class.isAssignableFrom(cls)) {
      entities.forEach(
          entity -> {
            try {
              List<InputEntity> arr =
                  new ArrayList<>(Extractor.extractElements((NestedEntity) entity));
              persistAll(arr, identifier);
            } catch (ExtractorException | SQLException e) {
              log.error(
                  String.format(
                      "An error occurred during extraction of nested entity'%s': ",
                      cls.getSimpleName()),
                  e);
            }
          });
      insertListIgnoreNested(entities, cls, identifier);
    } else {
      insertListIgnoreNested(entities, cls, identifier);
    }
  }

   */

  public <C extends Entity> void persistIncludeNested(C entity, DbGridMetadata identifier)
      throws SQLException {
    Set<C> entitiesToAdd = new HashSet<>();
    entitiesToAdd.add(entity);
    persistAll(entitiesToAdd, identifier);
  }

  private <C extends Entity> void persistMixedList(
      List<C> entities, DbGridMetadata identifier) {
    Map<Class<C>, List<C>> entitiesPerClass =
        entities.stream().collect(groupingBy(entity -> (Class<C>) entity.getClass()));
    entitiesPerClass.forEach(
        (cls, ent) -> {
          try {
            persistList(ent, cls, identifier);
          } catch (SQLException e) {
            throw new RuntimeException(
                String.format(
                    "An error occurred during extraction of entity '%s', SQLReason: '%s'",
                    cls.getSimpleName(), e.getMessage()),
                e);
          }
        });
  }

  private <C extends Entity, E extends TimeSeriesEntry<V>, V extends Value> void persistList(
      List<C> entities, Class<C> cls, DbGridMetadata identifier) throws SQLException {
    // Check if there are only elements of the same class
    Class<?> firstClass = entities.get(0).getClass();
    boolean allSameClass = entities.stream().allMatch(e -> e.getClass() == firstClass);

    if (allSameClass) {
      if (InputEntity.class.isAssignableFrom(cls)) {
        insertListIgnoreNested(entities, cls, identifier, true);
      } else if (ResultEntity.class.isAssignableFrom(cls)) {
        insertListIgnoreNested(entities, cls, identifier, false);
      } else if (TimeSeries.class.isAssignableFrom(cls)) {
        entities.forEach(
            ts -> {
              try {
                persistTimeSeries((TimeSeries<E, V>) ts, identifier);
              } catch (SQLException e) {
                throw new RuntimeException(
                    String.format(
                        "An error occurred during extraction of entity '%s', SQLReason: '%s'",
                        cls.getSimpleName(), e.getMessage()),
                    e);
              }
            });
      } else {
        log.error("I don't know how to handle an entity of class {}", cls.getSimpleName());
      }
    } else {
      log.error("The list isn't homogenous regarding the classes of the elements.");
    }
  }

  /**
   * Writes a list of entities into a sql table. It's necessary that all entities have the same
   * class.
   */
  private <C extends Entity> void insertListIgnoreNested(
      List<C> entities, Class<C> cls, DbGridMetadata identifier, boolean ignoreConflict) throws SQLException {
    try {
      String[] headerElements = processorProvider.getHeaderElements(cls);
      String query =
          basicInsertQueryValuesGrid(
              schemaName, databaseNamingStrategy.getEntityName(cls).orElseThrow(), headerElements);
      query = query + createInsertQueryBodyIgnoreConflict(entities, headerElements, identifier, ignoreConflict);
      connector.executeUpdate(query);
    } catch (ProcessorProviderException e) {
      log.error("Exception occurred during processor request: ", e);
    }
  }

  protected <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries, DbGridMetadata identifier) throws SQLException {
    try {
      TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
      String[] headerElements = processorProvider.getHeaderElements(key);
      persistTimeSeries(timeSeries, headerElements, identifier);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    } catch (IOException e) {
      log.error("Exception occurred during closing of writer.", e);
    }
  }

  private <E extends TimeSeriesEntry<V>, V extends Value> void persistTimeSeries(
      TimeSeries<E, V> timeSeries, String[] headerElements, DbGridMetadata identifier)
      throws ProcessorProviderException, IOException, SQLException {
    try {
      String query =
          basicInsertQueryValuesITS(
              schemaName,
              databaseNamingStrategy.getEntityName(timeSeries).orElseThrow(),
              headerElements);
      Set<LinkedHashMap<String, String>> entityFieldData =
          processorProvider.handleTimeSeries(timeSeries);
      query =
          query
              + entityFieldData.stream()
                  .map(
                      data ->
                          queryTimeSeriesValueLine(
                              sqlEntityFieldData(data),
                              headerElements,
                              identifier,
                              timeSeries.getUuid().toString()))
                  .collect(Collectors.joining(",\n", "", ";"));
      try {
        connector.executeUpdate(query);
      } catch (SQLException e) {
        throw new RuntimeException(
            String.format(
                "An error occurred during extraction of the time series, SQLReason: '%s'",
                e.getMessage()),
            e);
      }
    } catch (ProcessorProviderException e) {
      throw new ProcessorProviderException("Exception occurred during processor request: ", e);
    }
  }

  public void persistJointGrid(JointGridContainer jointGridContainer, UUID gridUUID)
      throws SQLException {
    DbGridMetadata identifier = new DbGridMetadata(jointGridContainer.getGridName(), gridUUID);

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
    Set<EvcsInput> evCS = systemParticipants.getEvcs();
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
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());

    List<Entity> toAdd = new LinkedList<>();
    toAdd.addAll(rawGridElements.allEntitiesAsList());
    toAdd.addAll(systemParticipants.allEntitiesAsList());
    toAdd.addAll(graphicElements.allEntitiesAsList());
    toAdd.addAll(types);
    toAdd.addAll(operators);

    // persist all entities
    persistAll(toAdd, identifier);
  }

  private <C extends Entity> void insert(C entity, DbGridMetadata identifier)
      throws SQLException {
    try {
      String[] headerElements = processorProvider.getHeaderElements(entity.getClass());
      String query =
          basicInsertQueryValuesGrid(
                  schemaName,
                  databaseNamingStrategy.getEntityName(entity.getClass()).orElseThrow(),
                  headerElements)
              + queryValueLine(entity, headerElements, identifier)
              + ";";
      connector.executeUpdate(query);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Provides the value lists for an insertion query. Conflicts because of the primary key 'uuid'
   * will be ignored. Conflicts can occur if an entity (e.g. node) already exist. WARNING: It's
   * assumed that all entities are from the same class C.
   */
  private <C extends Entity> String createInsertQueryBodyIgnoreConflict(
      List<C> entities, String[] headerElements, DbGridMetadata identifier, boolean ignoreConflict)
      throws ProcessorProviderException {
    Set<LinkedHashMap<String, String>> entityFieldData = processorProvider.handleEntities(entities);
    String queryBody = "";
    if (ignoreConflict) {
      queryBody =
              queryBody
                      + entityFieldData.stream()
                      .map(data -> queryValueLine(sqlEntityFieldData(data), headerElements, identifier))
                      .collect(Collectors.joining(",\n", "", "\nON CONFLICT (uuid) DO NOTHING;"));
    } else {
      queryBody =
              queryBody
                      + entityFieldData.stream()
                      .map(data -> queryValueLine(sqlEntityFieldData(data), headerElements, identifier))
                      .collect(Collectors.joining(",\n", "", ";\n"));
    }
    return queryBody;
  }

  /**
   * Provides the value lists for an insertion query.
   *
   * <p>WARNING: It's assumed that all entities are from the same class C.
   */
  private <C extends Entity> String createInsertQueryBody(
      List<C> entities, String[] headerElements, DbGridMetadata identifier)
      throws ProcessorProviderException {
    Set<LinkedHashMap<String, String>> entityFieldData = processorProvider.handleEntities(entities);
    String queryBody = "";
    queryBody =
        queryBody
            + entityFieldData.stream()
                .map(data -> queryValueLine(sqlEntityFieldData(data), headerElements, identifier))
                .collect(Collectors.joining(",\n", "", ";"));
    return queryBody;
  }

  /**
   * Creates a line with the values of one entity for an insertion query using the entityFieldData.
   */
  private String queryValueLine(
      LinkedHashMap<String, String> entityFieldData,
      String[] headerElements,
      DbGridMetadata identifier) {
    return writeOneLine(
        Stream.concat(
            Arrays.stream(headerElements).map(entityFieldData::get),
            identifier.getStreamForQuery()));
  }

  /** Creates a line with the values of one entity for an insertion query. */
  private <C extends Entity> String queryValueLine(
      C entity, String[] headerElements, DbGridMetadata identifier)
      throws ProcessorProviderException {
    return queryValueLine(
        processorProvider.handleEntity(entity).map(this::sqlEntityFieldData).getOrThrow(),
        headerElements,
        identifier);
  }

  private String queryTimeSeriesValueLine(
      Map<String, String> entityFieldData,
      String[] headerElements,
      DbGridMetadata identifier,
      String TSuuid) {
    return writeOneLine(
        Stream.concat(
            Stream.concat(
                Arrays.stream(headerElements).map(entityFieldData::get),
                identifier.getStreamForQuery()),
            Stream.of(quote(TSuuid, "'"))));
  }

  private LinkedHashMap<String, String> sqlEntityFieldData(
      LinkedHashMap<String, String> entityFieldData) {
    LinkedHashMap<String, String> quotedEntityFieldData = new LinkedHashMap<>(entityFieldData);
    quotedEntityFieldData.replaceAll((key, ent) -> quote(ent, "'"));

    return quotedEntityFieldData;
  }

  /** "INSERT INTO" line with schemaName.tableName */
  private static String basicInsertQuery(String schemaName, String tableName) {
    return "INSERT INTO\n\t" + schemaName + "." + tableName;
  }

  /** Provides the insert, column names, grid identifier and the VALUES statement for a query. */
  private String basicInsertQueryValuesGrid(
      String schemaName, String tableName, String[] headerElements) {
    String[] addParams = {
            DbGridMetadata.GRID_UUID
    };
    return basicInsertQuery(schemaName, tableName)
        + " "
        + writeOneLine(StringUtils.camelCaseToSnakeCase(headerElements), addParams)
        + "\nVALUES\n";
  }

  /**
   * Provides the insert, column names, grid identifier, time_series uuid and the VALUES statement
   * for a query.
   */
  private String basicInsertQueryValuesITS(
      String schemaName, String tableName, String[] headerElements) {
    String[] addParams = {
            DbGridMetadata.GRID_UUID,
            TIME_SERIES
    };
    return basicInsertQuery(schemaName, tableName)
        + " "
        + writeOneLine(StringUtils.camelCaseToSnakeCase(headerElements), addParams)
        + "\nVALUES\n";
  }

  /** Converts a stream of strings into an one line string with brackets. */
  private String writeOneLine(Stream<String> entries) {
    return "(" + entries.collect(Collectors.joining(",")) + ")";
  }

  /**
   * Converts an array of strings and an array of strings (for additional parameters) into an one
   * line string with brackets.
   */
  private String writeOneLine(String[] entries, String[] addParams) {
    return writeOneLine(Stream.concat(Arrays.stream(entries), Arrays.stream(addParams)));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  public void createClassTable(Class<? extends Entity> cls, String schemaName)
      throws SQLException, ProcessorProviderException, EntityProcessorException {
    String query = queryCreateTableUniqueEntity(cls, schemaName);
    connector.executeUpdate(query);
  }

  public void createGridTable(String schemaName) throws SQLException {
    String query = queryCreateGridTable(schemaName);
    connector.executeUpdate(query);
  }

  /** @return insertion order for unique entities */
  private static List<Class<?>> hierarchicInsert() {
    List<Class<?>> sortedInsert = new ArrayList<>();
    sortedInsert.add(AssetTypeInput.class); // 1. Types
    sortedInsert.add(OperatorInput.class); // 2. Operators
    sortedInsert.add(NodeInput.class); // 3. Nodes
    sortedInsert.add(ThermalBusInput.class); // 4. ThermalBus
    sortedInsert.add(ThermalUnitInput.class); // 5. ThermalUnit
    sortedInsert.add(ConnectorInput.class); // 6a. ConnectorInput
    sortedInsert.add(SystemParticipantInput.class); // 6b. SystemParticipantInput
    sortedInsert.add(GraphicInput.class); // 7. GraphicInput
    // 8. Rest
    return sortedInsert;
  }
}
