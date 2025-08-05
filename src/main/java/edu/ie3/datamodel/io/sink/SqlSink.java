/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.sink;

import static edu.ie3.datamodel.io.SqlUtils.quote;
import static java.util.stream.Collectors.groupingBy;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.ExtractorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.DbGridMetadata;
import edu.ie3.datamodel.io.connectors.SqlConnector;
import edu.ie3.datamodel.io.extractor.Extractor;
import edu.ie3.datamodel.io.extractor.NestedEntity;
import edu.ie3.datamodel.io.naming.DatabaseNamingStrategy;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.io.processor.ProcessorProvider;
import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.container.JointGridContainer;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.TriFunction;
import edu.ie3.util.StringUtils;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The type Sql sink. */
public class SqlSink {

  /** The constant log. */
  protected static final Logger log = LoggerFactory.getLogger(SqlSink.class);

  private final SqlConnector connector;
  private final DatabaseNamingStrategy databaseNamingStrategy;
  private final ProcessorProvider processorProvider;
  private final String schemaName;

  private static final String TIME_SERIES = "time_series";
  private static final String LOAD_PROFILE = "load_profile";

  /**
   * Instantiates a new Sql sink.
   *
   * @param schemaName the schema name
   * @param databaseNamingStrategy the database naming strategy
   * @param connector the connector
   * @throws EntityProcessorException the entity processor exception
   */
  public SqlSink(
      String schemaName, DatabaseNamingStrategy databaseNamingStrategy, SqlConnector connector)
      throws EntityProcessorException {
    this(schemaName, new ProcessorProvider(), databaseNamingStrategy, connector);
  }

  /**
   * Instantiates a new Sql sink.
   *
   * @param schemaName the schema name
   * @param processorProvider the processor provider
   * @param databaseNamingStrategy the database naming strategy
   * @param connector the connector
   */
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

  /** Shutdown. */
  public void shutdown() {
    connector.shutdown();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Entry point of a data sink to persist multiple entities in a collection.
   *
   * @param <C> bounded to be all unique entities. Handling of specific entities is normally then
   *     executed by a specific {@link EntityProcessor}
   * @param entities a collection of entities that should be persisted
   * @param identifier identifier of the grid
   */
  public <C extends Entity> void persistAll(Collection<C> entities, DbGridMetadata identifier) {
    // Extract nested entities and add them to the set of entities
    Set<C> entitiesToAdd = new HashSet<>(entities); // entities to persist
    entities.forEach(
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
          entitiesToAdd.stream().filter(ent -> cls.isAssignableFrom(ent.getClass())).toList(),
          identifier);
      entitiesToAdd.removeIf(ent -> cls.isAssignableFrom(ent.getClass()));
    }
    persistMixedList(new ArrayList<>(entitiesToAdd), identifier); // persist left entities
  }

  /**
   * Persist an entity. By default, this method takes care of the extraction process of nested
   * entities (if any)
   *
   * @param <C> the type parameter
   * @param entity the entity that should be persisted
   * @param identifier identifier of the grid
   * @throws SQLException if an error occurred
   */
  public <C extends Entity> void persist(C entity, DbGridMetadata identifier) throws SQLException {
    if (entity instanceof InputEntity inputEntity) {
      persistIncludeNested(inputEntity, identifier);
    } else if (entity instanceof ResultEntity resultEntity) {
      insert(resultEntity, identifier);
    } else if (entity instanceof TimeSeries<?, ?, ?> timeSeries) {
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
   * @param <C> the type parameter
   * @param entity the entity that should be persisted
   * @param identifier identifier of the grid
   * @throws SQLException the sql exception
   */
  public <C extends Entity> void persistIgnoreNested(C entity, DbGridMetadata identifier)
      throws SQLException {
    insert(entity, identifier);
  }

  /**
   * Persist an entity and all nested entities.
   *
   * @param <C> the type parameter
   * @param entity the entity that should be persisted
   * @param identifier identifier of the grid
   */
  public <C extends Entity> void persistIncludeNested(C entity, DbGridMetadata identifier) {
    Set<C> entitiesToAdd = new HashSet<>();
    entitiesToAdd.add(entity);
    persistAll(entitiesToAdd, identifier);
  }

  /**
   * Persist a list of entities with different types. To minimize the number of queries, the
   * entities will be grouped by their class.
   */
  private <C extends Entity> void persistMixedList(List<C> entities, DbGridMetadata identifier) {
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

  /**
   * Persist a list of entities with same types. To minimize the number of queries, the entities
   * will be grouped by their class.
   */
  private <C extends Entity, E extends TimeSeriesEntry<V>, V extends Value, R extends Value>
      void persistList(List<C> entities, Class<C> cls, DbGridMetadata identifier)
          throws SQLException {
    // Check if there are only elements of the same class
    Class<?> firstClass = entities.get(0).getClass();
    boolean allSameClass = entities.stream().allMatch(e -> e.getClass() == firstClass);

    if (allSameClass) {
      if (InputEntity.class.isAssignableFrom(cls)) {
        insertListIgnoreNested(entities, cls, identifier, true);
      } else if (ResultEntity.class.isAssignableFrom(cls)) {
        insertListIgnoreNested(entities, cls, identifier, false);
      } else if (TimeSeries.class.isAssignableFrom(cls)) {
        entities.forEach(ts -> persistTimeSeries((TimeSeries<E, V, R>) ts, identifier));
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
      List<C> entities, Class<C> cls, DbGridMetadata identifier, boolean ignoreConflict)
      throws SQLException {
    try {
      String[] headerElements = processorProvider.getHeaderElements(cls);
      String query =
          basicInsertQueryValuesGrid(
              schemaName, databaseNamingStrategy.getEntityName(cls).orElseThrow(), headerElements);
      query =
          query
              + createInsertQueryBodyIgnoreConflict(
                  entities, headerElements, identifier, ignoreConflict);
      connector.executeUpdate(query);
    } catch (ProcessorProviderException e) {
      log.error("Exception occurred during processor request: ", e);
    }
  }

  /**
   * Persist one time series.
   *
   * @param <E> the type parameter
   * @param <V> the type parameter
   * @param <R> the type parameter
   * @param timeSeries the time series
   * @param identifier the identifier
   */
  protected <E extends TimeSeriesEntry<V>, V extends Value, R extends Value> void persistTimeSeries(
      TimeSeries<E, V, R> timeSeries, DbGridMetadata identifier) {
    try {
      TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
      String[] headerElements = processorProvider.getHeaderElements(key);
      persistTimeSeries(timeSeries, headerElements, identifier);
    } catch (ProcessorProviderException e) {
      log.error(
          "Exception occurred during receiving of header elements. Cannot write this element.", e);
    }
  }

  private <E extends TimeSeriesEntry<V>, V extends Value, R extends Value> void persistTimeSeries(
      TimeSeries<E, V, R> timeSeries, String[] headerElements, DbGridMetadata identifier)
      throws ProcessorProviderException {
    try {

      TriFunction<String, String, String[], String> queryBuilder;
      String timeSeriesIdentifier;

      if (timeSeries instanceof LoadProfileTimeSeries<?> lpts) {
        timeSeriesIdentifier = lpts.getLoadProfile().getKey();
        queryBuilder = this::basicInsertQueryValuesLPTS;
      } else {
        timeSeriesIdentifier = timeSeries.getUuid().toString();
        queryBuilder = this::basicInsertQueryValuesITS;
      }

      String query =
          queryBuilder.apply(
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
                              timeSeriesIdentifier))
                  .collect(Collectors.joining(",\n", "", ";"));
      executeQueryToPersist(query);
    } catch (ProcessorProviderException e) {
      throw new ProcessorProviderException("Exception occurred during processor request: ", e);
    }
  }

  private void executeQueryToPersist(String query) {
    try {
      connector.executeUpdate(query);
    } catch (SQLException e) {
      throw new RuntimeException(
          String.format(
              "An error occurred during extraction of the time series, SQLReason: '%s'",
              e.getMessage()),
          e);
    }
  }

  /**
   * Persists a whole {@link JointGridContainer}.
   *
   * @param jointGridContainer the joint grid container
   * @param gridUUID the grid uuid
   */
  public void persistJointGrid(JointGridContainer jointGridContainer, UUID gridUUID) {
    DbGridMetadata identifier = new DbGridMetadata(jointGridContainer.getGridName(), gridUUID);
    List<Entity> toAdd = new LinkedList<>(jointGridContainer.allEntitiesAsList());
    persistAll(toAdd, identifier);
  }

  /** Executes a query to insert a single entity to a SQL database. */
  private <C extends Entity> void insert(C entity, DbGridMetadata identifier) throws SQLException {
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
    String suffix;
    if (ignoreConflict) {
      suffix = "\nON CONFLICT (uuid) DO NOTHING;";
    } else {
      suffix = ";\n";
    }
    String queryBody = "";
    queryBody =
        queryBody
            + entityFieldData.stream()
                .map(data -> queryValueLine(sqlEntityFieldData(data), headerElements, identifier))
                .collect(Collectors.joining(",\n", "", suffix));
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
      String timeSeriesIdentifier) {
    return writeOneLine(
        Stream.concat(
            Stream.concat(
                Arrays.stream(headerElements).map(entityFieldData::get),
                identifier.getStreamForQuery()),
            Stream.of(quote(timeSeriesIdentifier, "'"))));
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
    String[] addParams = {DbGridMetadata.GRID_UUID_COLUMN};
    return basicInsertQueryWith(
        schemaName, tableName, StringUtils.camelCaseToSnakeCase(headerElements), addParams);
  }

  /**
   * Provides the insert, column names, grid identifier, time_series uuid and the VALUES statement
   * for a query.
   */
  private String basicInsertQueryValuesITS(
      String schemaName, String tableName, String[] headerElements) {
    String[] addParams = {DbGridMetadata.GRID_UUID_COLUMN, TIME_SERIES};
    return basicInsertQueryWith(
        schemaName, tableName, StringUtils.camelCaseToSnakeCase(headerElements), addParams);
  }

  /** Provides the insert, column names, grid identifier, and the VALUES statement for a query. */
  private String basicInsertQueryValuesLPTS(
      String schemaName, String tableName, String[] headerElements) {
    String[] addParams = {DbGridMetadata.GRID_UUID_COLUMN, LOAD_PROFILE};
    return basicInsertQueryWith(
        schemaName, tableName, StringUtils.camelCaseToSnakeCase(headerElements), addParams);
  }

  /** Provides the insert, column names, grid identifier, and the VALUES statement for a query */
  private String basicInsertQueryWith(
      String schemaName, String tableName, String[] headerElements, String[] addParams) {
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

  /**
   * @return insertion order for unique entities
   */
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
    return sortedInsert;
  }
}
