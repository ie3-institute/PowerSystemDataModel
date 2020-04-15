/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.ValidationUtils;
import edu.ie3.util.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Parent class of all .csv file related sources containing methods and fields consumed by allmost
 * all implementations of .csv file related sources.
 *
 * @version 0.1
 * @since 05.04.20
 */
public abstract class CsvDataSource {

  protected static final Logger log = LogManager.getLogger(CsvDataSource.class);

  // general fields
  private final String csvSep;
  protected final CsvFileConnector connector;

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE_A = "nodeA";
  protected static final String NODE_B = "nodeB";
  protected static final String NODE = "node";
  protected static final String TYPE = "type";
  protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

  public CsvDataSource(String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    this.csvSep = csvSep;
    this.connector = new CsvFileConnector(folderPath, fileNamingStrategy);
  }

  /**
   * Takes a row string of a .csv file and a string array of the csv file headline, tries to split
   * the csv row string based and zip it together with the headline. This method does not contain
   * any sanity checks. Order of the headline needs to be the same as the fields in the csv row. If
   * the zipping fails, an empty map is returned and the causing error is logged.
   *
   * @param csvRow the csv row string that contains the data
   * @param headline the headline fields of the csv file
   * @return a map containing the mapping of (fieldName -> fieldValue) or an empty map if an error
   *     occurred
   */
  private Map<String, String> buildFieldsToAttributes(
      final String csvRow, final String[] headline) {

    final String[] fieldVals = fieldVals(csvSep, csvRow);

    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    try {
      insensitiveFieldsToAttributes.putAll(
          IntStream.range(0, fieldVals.length)
              .boxed()
              .collect(
                  Collectors.toMap(
                      k -> StringUtils.snakeCaseToCamelCase(headline[k]), v -> fieldVals[v])));

      if (insensitiveFieldsToAttributes.size() != headline.length) {
        Set<String> fieldsToAttributesKeySet = insensitiveFieldsToAttributes.keySet();
        insensitiveFieldsToAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        throw new SourceException(
            "The size of the headline does not fit to the size of the resulting fields to attributes mapping.\nHeadline: "
                + String.join(", ", headline)
                + "\nResultingMap: "
                + String.join(", ", fieldsToAttributesKeySet)
                + "\nCsvRow: "
                + csvRow.trim()
                + ".\nIs the csv separator in the file matching the separator provided in the constructor ('"
                + csvSep
                + "') and does the number of columns match the number of headline fields?");
      }
    } catch (Exception e) {
      log.error(
          "Cannot build fields to attributes map for row '{}' with headline '{}'.\nException: {}",
          csvRow::trim,
          () -> String.join(",", headline),
          () -> e);
    }
    return insensitiveFieldsToAttributes;
  }

  private String[] fieldVals(String csvSep, String csvRow) {

    final String geoJsonRegex = "([\\{].+\\}\\}\\})";
    final String qCharRegex = "((cP:|olm:|cosPhiFixed:|cosPhiP:|qV:)\\{.+?\\})";

    List<String> geoList = extractMatchingStrings(geoJsonRegex, csvRow);
    List<String> qList = extractMatchingStrings(qCharRegex, csvRow);

    AtomicInteger geoCounter = new AtomicInteger(0);
    AtomicInteger qCharCounter = new AtomicInteger(0);

    return Arrays.stream(
            csvRow
                .replaceAll(qCharRegex, "QCHAR")
                .replaceAll(geoJsonRegex, "GEOJSON")
                .replaceAll("\"", "")
                .split(csvSep, -1))
        .map(
            fieldVal -> {
              String returningFieldVal = fieldVal;
              if (fieldVal.equalsIgnoreCase("GEOJSON")) {
                returningFieldVal = geoList.get(geoCounter.getAndIncrement());
              }
              if (fieldVal.equalsIgnoreCase("QCHAR")) {
                returningFieldVal = qList.get(qCharCounter.getAndIncrement());
              }
              return returningFieldVal.trim();
            })
        .toArray(String[]::new);
  }

  private List<String> extractMatchingStrings(String regexString, String csvRow) {
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(csvRow);

    ArrayList<String> matchingList = new ArrayList<>();
    while (matcher.find()) {
      matchingList.add(matcher.group());
    }
    return matchingList;
  }

  /**
   * Returns either the first instance of a {@link OperatorInput} in the provided collection of or
   * {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   *
   * @param operators the collections of {@link OperatorInput}s that should be searched in
   * @param operatorUuid the operator uuid that is requested
   * @return either the first found instancen of {@link OperatorInput} or {@link
   *     OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  private OperatorInput getFirstOrDefaultOperator(
      Collection<OperatorInput> operators, String operatorUuid) {
    return findFirstEntityByUuid(operatorUuid, operators)
        .orElseGet(
            () -> {
              log.debug(
                  "Cannot find operator for node with uuid '{}'. Defaulting to 'NO OPERATOR ASSIGNED'.",
                  operatorUuid);
              return OperatorInput.NO_OPERATOR_ASSIGNED;
            });
  }

  /**
   * Returns a predicate that can be used to filter optionals of {@link UniqueEntity}s and keep
   * track on the number of elements that have been empty optionals. This filter let only pass
   * optionals that are non-empty. Example usage:
   * Collection.stream().filter(isPresentCollectIfNot(NodeInput.class, new ConcurrentHashMap<>()))
   * ...
   *
   * @param entityClass entity class that should be used as they key in the provided counter map
   * @param invalidElementsCounterMap a map that counts the number of empty optionals and maps it to
   *     the provided entity clas
   * @param <T> the type of the entity
   * @return a predicate that can be used to filter and count empty optionals
   */
  protected <T extends UniqueEntity> Predicate<Optional<T>> isPresentCollectIfNot(
      Class<? extends UniqueEntity> entityClass,
      ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> invalidElementsCounterMap) {
    return o -> {
      if (o.isPresent()) {
        return true;
      } else {
        invalidElementsCounterMap.computeIfAbsent(entityClass, k -> new LongAdder()).increment();
        return false;
      }
    };
  }

  protected void printInvalidElementInformation(
      Class<? extends UniqueEntity> entityClass, LongAdder noOfInvalidElements) {

    log.error(
        "{} entities of type '{}' are missing required elements!",
        noOfInvalidElements,
        entityClass.getSimpleName());
  }

  protected String saveMapGet(Map<String, String> map, String key, String mapName) {
    return Optional.ofNullable(map.get(key))
        .orElse(
            "Key '"
                + key
                + "' not found"
                + (mapName.isEmpty() ? "!" : " in map '" + mapName + "'!"));
  }

  protected void logSkippingWarning(
      String entityDesc, String entityUuid, String entityId, String missingElementsString) {

    log.warn(
        "Skipping '{}' with uuid '{}' and id '{}'. Not all required entities found or map is missing entity key!\nMissing elements:\n{}",
        entityDesc,
        entityUuid,
        entityId,
        missingElementsString);
  }

  protected <T extends UniqueEntity> Stream<T> filterEmptyOptionals(Stream<Optional<T>> elements) {
    return elements.filter(Optional::isPresent).map(Optional::get);
  }

  /**
   * Returns an {@link Optional} of the first {@link UniqueEntity} element of this collection
   * matching the provided UUID or an empty {@code Optional} if no matching entity can be found.
   *
   * @param entityUuid uuid of the entity that should be looked for
   * @param entities collection of entities that should be
   * @param <T> type of the entity that will be returned, derived from the provided collection
   * @return either an optional containing the first entity that has the provided uuid or an empty
   *     optional if no matching entity with the provided uuid can be found
   */
  protected <T extends UniqueEntity> Optional<T> findFirstEntityByUuid(
      String entityUuid, Collection<T> entities) {
    return entities.stream()
        .parallel()
        .filter(uniqueEntity -> uniqueEntity.getUuid().toString().equalsIgnoreCase(entityUuid))
        .findFirst();
  }

  /**
   * Tries to open a file reader from the connector based on the provided entity class, reads the
   * first line (considered to be the headline with headline fields) and returns a stream of
   * (fieldName -> fieldValue) mapping where each map represents one row of the .csv file. Since the
   * returning stream is a parallel stream, the order of the elements cannot be guaranteed.
   *
   * @param entityClass the entity class that should be build and that is used to get the
   *     corresponding reader
   * @param connector the connector that should be used to get the reader from
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName -> fieldValue)
   */
  protected Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap(
      Class<? extends UniqueEntity> entityClass, CsvFileConnector connector) {
    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = reader.readLine().replaceAll("\"", "").split(csvSep);
      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      Collection<Map<String, String>> allRows =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .filter(map -> !map.isEmpty())
              .collect(Collectors.toList());

      return distinctRowsWithLog(entityClass, allRows).parallelStream();

    } catch (IOException e) {
      log.warn(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }

    return Stream.empty();
  }

  /**
   * Returns a collection of maps each representing a row in csv file that can be used to built an
   * instance of a {@link UniqueEntity}. The uniqueness of each row is doubled checked by a) that no
   * duplicated rows are returned that are full (1:1) matches and b) that no rows are returned that
   * have the same UUID but different field values. As the later case (b) is destroying the contract
   * of UUIDs an empty set is returned to indicate that these data cannot be processed safely and
   * the error is logged. For case a), only the duplicates are filtered out an a set with unique
   * rows is returned.
   *
   * @param entityClass the entity class that should be built based on the provided (fieldName ->
   *     fieldValue) collection
   * @param allRows collection of rows of a csv file an entity should be built from
   * @param <T> type of the entity
   * @return either a set containing only unique rows or an empty set if at least two rows with the
   *     same UUID but different field values exist
   */
  private <T extends UniqueEntity> Set<Map<String, String>> distinctRowsWithLog(
      Class<T> entityClass, Collection<Map<String, String>> allRows) {
    Set<Map<String, String>> allRowsSet = new HashSet<>(allRows);
    // check for duplicated rows that match exactly (full duplicates) -> sanity only, not crucial
    if (allRows.size() != allRowsSet.size()) {
      log.warn(
          "File with '{}' entities contains {} exact duplicated rows. File cleanup is recommended!",
          entityClass.getSimpleName(),
          (allRows.size() - allRowsSet.size()));
    }

    // check for rows that match exactly by their UUID, but have different fields -> crucial, we
    // allow only unique UUID entities
    Set<Map<String, String>> distinctUuidRowSet =
        allRowsSet
            .parallelStream()
            .filter(ValidationUtils.distinctByKey(x -> x.get("uuid")))
            .collect(Collectors.toSet());
    if (distinctUuidRowSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctUuidRowSet);
      String affectedUuids =
          allRowsSet.stream().map(row -> row.get("uuid")).collect(Collectors.joining(",\n"));
      log.error(
          "'{}' entities with duplicated UUIDs, but different field values found! Please review the corresponding input file!\nAffected UUIDs:\n{}",
          entityClass.getSimpleName(),
          affectedUuids);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    return allRowsSet;
  }

  /**
   * Checks if the requested type of an asset can be found in the provided collection of types based
   * on the provided fields to values mapping. The provided fields to values mapping needs to have
   * one and only one field with key {@link this#TYPE} and a corresponding UUID value. If the type
   * can be found in the provided collection based on the UUID it is returned wrapped in an
   * optional. Otherwise an empty optional is returned and a warning is logged.
   *
   * @param types a collection of types that should be used for searching
   * @param fieldsToAttributes the field name to value mapping incl. the key {@link this#TYPE}
   * @param skippedClassString debug string of the class that will be skipping
   * @param <T> the type of the resulting type instance
   * @return either an optional containing the type or an empty optional if the type cannot be found
   */
  protected <T extends AssetTypeInput> Optional<T> getAssetType(
      Collection<T> types, Map<String, String> fieldsToAttributes, String skippedClassString) {

    Optional<T> assetType =
        Optional.ofNullable(fieldsToAttributes.get(TYPE))
            .flatMap(typeUuid -> findFirstEntityByUuid(typeUuid, types));

    // if the type is not present we return an empty element and
    // log a warning
    if (!assetType.isPresent()) {
      logSkippingWarning(
          skippedClassString,
          saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP),
          saveMapGet(fieldsToAttributes, "id", FIELDS_TO_VALUES_MAP),
          TYPE + ": " + saveMapGet(fieldsToAttributes, TYPE, FIELDS_TO_VALUES_MAP));
    }
    return assetType;
  }

  /**
   * Returns a stream of optional {@link AssetInputEntityData} that can be used to build instances
   * of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory} that
   * consumes this data.
   *
   * @param entityClass the entity class that should be build
   * @param operators a collection of {@link OperatorInput} entities that should be used to build
   *     the data
   * @param <T> type of the entity that should be build
   * @return stream of optionals of the entity data or empty optionals of the operator required for
   *     the data cannot be found
   */
  protected <T extends AssetInput> Stream<AssetInputEntityData> assetInputEntityDataStream(
      Class<T> entityClass, Collection<OperatorInput> operators) {
    return buildStreamWithFieldsToAttributesMap(entityClass, connector)
        .map(
            fieldsToAttributes ->
                assetInputEntityDataStream(entityClass, fieldsToAttributes, operators));
  }

  protected <T extends AssetInput> AssetInputEntityData assetInputEntityDataStream(
      Class<T> entityClass,
      Map<String, String> fieldsToAttributes,
      Collection<OperatorInput> operators) {

    // get the operator of the entity
    String operatorUuid = fieldsToAttributes.get(OPERATOR);
    OperatorInput operator = getFirstOrDefaultOperator(operators, operatorUuid);

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Collections.singletonList(OPERATOR)));

    return new AssetInputEntityData(fieldsToAttributes, entityClass, operator);
  }

  /**
   * Returns a stream of optional {@link NodeAssetInputEntityData} that can be used to build
   * instances of several subtypes of {@link UniqueEntity} by a corresponding {@link EntityFactory}
   * that consumes this data. param assetInputEntityDataStream
   *
   * @param assetInputEntityDataStream a stream consisting of {@link AssetInputEntityData} that is
   *     enriched with {@link NodeInput} data
   * @param nodes a collection of {@link NodeInput} entities that should be used to build the data
   * @return stream of optionals of the entity data or empty optionals of the node required for the
   *     data cannot be found
   */
  protected Stream<Optional<NodeAssetInputEntityData>> nodeAssetInputEntityDataStream(
      Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {

    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityData -> {

              // get the raw data
              Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

              // get the node of the entity
              String nodeUuid = fieldsToAttributes.get(NODE);
              Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

              // if the node is not present we return an empty element and
              // log a warning
              if (!node.isPresent()) {
                logSkippingWarning(
                    assetInputEntityData.getEntityClass().getSimpleName(),
                    fieldsToAttributes.get("uuid"),
                    fieldsToAttributes.get("id"),
                    NODE + ": " + nodeUuid);
                return Optional.empty();
              }

              // remove fields that are passed as objects to constructor
              fieldsToAttributes.keySet().remove(NODE);

              return Optional.of(
                  new NodeAssetInputEntityData(
                      fieldsToAttributes,
                      assetInputEntityData.getEntityClass(),
                      assetInputEntityData.getOperatorInput(),
                      node.get()));
            });
  }

  /**
   * Returns a stream of optional entities that can be build by using {@link
   * NodeAssetInputEntityData} and their corresponding factory.
   *
   * @param entityClass the entity class that should be build
   * @param factory the factory that should be used for the building process
   * @param nodes a collection of {@link NodeInput} entities that should be used to build the
   *     entities
   * @param operators a collection of {@link OperatorInput} entities should be used to build the
   *     entities
   * @param <T> type of the entity that should be build
   * @return stream of optionals of the entities that has been built by the factor or empty
   *     optionals if the entity could not have been build
   */
  protected <T extends AssetInput> Stream<Optional<T>> nodeAssetEntityStream(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes)
        .map(dataOpt -> dataOpt.flatMap(factory::getEntity));
  }
}
