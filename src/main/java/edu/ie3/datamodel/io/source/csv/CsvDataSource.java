/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.utils.validation.ValidationUtils;
import edu.ie3.util.StringUtils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parent class of all .csv file related sources containing methods and fields consumed by allmost
 * all implementations of .csv file related sources.
 *
 * @version 0.1
 * @since 05.04.20
 */
public abstract class CsvDataSource {

  protected static final Logger log = LoggerFactory.getLogger(CsvDataSource.class);

  // general fields
  protected final String csvSep;
  protected final CsvFileConnector connector;

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE_A = "nodeA";
  protected static final String NODE_B = "nodeB";
  protected static final String NODE = "node";
  protected static final String TYPE = "type";
  protected static final String FIELDS_TO_VALUES_MAP = "fieldsToValuesMap";

  /**
   * @deprecated ensures downward compatibility with old csv data format. Can be removed when
   *     support for old csv format is removed. *
   */
  @Deprecated private boolean notYetLoggedWarning = true;

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
   * @return a map containing the mapping of (fieldName to fieldValue) or an empty map if an error
   *     occurred
   */
  private Map<String, String> buildFieldsToAttributes(
      final String csvRow, final String[] headline) {

    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    // todo when replacing deprecated workaround code below add final modifier before parseCsvRow as
    // well as remove
    //  'finalFieldVals' and notYetLoggedWarning below!
    String[] fieldVals = parseCsvRow(csvRow, csvSep);

    // start workaround for deprecated data model processing
    if (fieldVals.length != headline.length) {
      // try to parse old structure
      fieldVals = oldFieldVals(csvSep, csvRow);
      // if this works log a warning to inform the user that this will not work much longer,
      // otherwise parsing will fail regularly as expected below
      if (fieldVals.length == headline.length && notYetLoggedWarning) {
        notYetLoggedWarning = false;
        log.warn(
            "You are using an outdated version of the data "
                + "model with invalid formatted csv rows. This is okay for now, but please updated your files, as the "
                + "support for the old model will be removed soon.");
      }
    }
    // end workaround for deprecated data model processing

    try {
      String[] finalFieldVals = fieldVals;
      insensitiveFieldsToAttributes.putAll(
          IntStream.range(0, fieldVals.length)
              .boxed()
              .collect(
                  Collectors.toMap(
                      k -> StringUtils.snakeCaseToCamelCase(headline[k]), v -> finalFieldVals[v])));

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
          csvRow.trim(),
          String.join(",", headline),
          e);
    }
    return insensitiveFieldsToAttributes;
  }

  /**
   * Parse a given row of a valid RFC 4180 formatted csv row
   *
   * @param csvRow the valid row
   * @param csvSep separator of the csv file
   * @return an array with the csv field values as strings
   */
  protected String[] parseCsvRow(String csvRow, String csvSep) {
    return Arrays.stream(csvRow.split(csvSep + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
        .map(
            maybeStartEndQuotedString ->
                StringUtils.unquoteStartEnd(maybeStartEndQuotedString.trim())
                    .replaceAll("\"{2}", "\"")
                    .trim())
        .toArray(String[]::new);
  }

  /**
   * Build an array of from the provided csv row string considering special cases where geoJson or
   * {@link edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput} are provided
   * in the csv row string.
   *
   * @param csvSep the column separator of the csv row string
   * @param csvRow the csv row string
   * @return an array with one entry per column of the provided csv row string
   * @deprecated only left for downward compatibility. Will be removed in a major release
   */
  @Deprecated
  private String[] oldFieldVals(String csvSep, String csvRow) {

    /*geo json support*/
    final String geoJsonRegex = "[\\{].+?\\}\\}\\}";
    final String geoReplacement = "geoJSON";

    /*characteristic input support */
    final String charInputRegex = "(cP:|olm:|cosPhiFixed:|cosPhiP:|qV:)\\{.+?\\}";
    final String charReplacement = "charRepl";

    /*removes double double quotes*/
    List<String> geoList = extractMatchingStrings(geoJsonRegex, csvRow.replaceAll("\"\"", "\""));
    List<String> charList = extractMatchingStrings(charInputRegex, csvRow.replaceAll("\"\"", "\""));

    AtomicInteger geoCounter = new AtomicInteger(0);
    AtomicInteger charCounter = new AtomicInteger(0);

    return Arrays.stream(
            csvRow
                .replaceAll(charInputRegex, charReplacement)
                .replaceAll(geoJsonRegex, geoReplacement)
                .replaceAll("\"*", "") // remove all quotes from
                .split(csvSep, -1))
        .map(
            fieldVal -> {
              String returningFieldVal = fieldVal;
              if (fieldVal.equalsIgnoreCase(geoReplacement)) {
                returningFieldVal = geoList.get(geoCounter.getAndIncrement());
              }
              if (fieldVal.equalsIgnoreCase(charReplacement)) {
                returningFieldVal = charList.get(charCounter.getAndIncrement());
              }
              return returningFieldVal.trim();
            })
        .toArray(String[]::new);
  }

  /**
   * Extracts all strings from the provided csvRow matching the provided regexString and returns a
   * list of strings in the order of their appearance in the csvRow string
   *
   * @param regexString regex string that should be searched for
   * @param csvRow csv row string that should be searched in for the regex string
   * @return a list of strings matching the provided regex in the order of their appearance in the
   *     provided csv row string
   */
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
      Collection<OperatorInput> operators,
      String operatorUuid,
      String entityClassName,
      String requestEntityUuid) {
    if (operatorUuid == null) {
      log.warn(
          "Input file for class '{}' is missing the 'operator' field. "
              + "This is okay, but you should consider fixing the file by adding the field. "
              + "Defaulting to 'NO OPERATOR ASSIGNED'",
          entityClassName);
      return OperatorInput.NO_OPERATOR_ASSIGNED;
    } else {
      return operatorUuid.trim().isEmpty()
          ? OperatorInput.NO_OPERATOR_ASSIGNED
          : findFirstEntityByUuid(operatorUuid, operators)
              .orElseGet(
                  () -> {
                    log.debug(
                        "Cannot find operator with uuid '{}' for element '{}' and uuid '{}'. Defaulting to 'NO OPERATOR ASSIGNED'.",
                        operatorUuid,
                        entityClassName,
                        requestEntityUuid);
                    return OperatorInput.NO_OPERATOR_ASSIGNED;
                  });
    }
  }

  /**
   * Returns a predicate that can be used to filter optionals of {@link UniqueEntity}s and keep
   * track on the number of elements that have been empty optionals. This filter let only pass
   * optionals that are non-empty. Example usage:
   *
   * <pre>{@code
   * Collection.stream().filter(isPresentCollectIfNot(NodeInput.class, new ConcurrentHashMap<>()))
   * }</pre>
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
   * Tries to open a file reader from the connector based on the provided entity class and hands it
   * over for further processing.
   *
   * @param entityClass the entity class that should be build and that is used to get the
   *     corresponding reader
   * @param connector the connector that should be used to get the reader from
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName to fieldValue)
   */
  protected Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap(
      Class<? extends UniqueEntity> entityClass, CsvFileConnector connector) {
    try {
      return buildStreamWithFieldsToAttributesMap(entityClass, connector.initReader(entityClass));
    } catch (FileNotFoundException e) {
      log.warn(
          "Unable to find file for entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }
    return Stream.empty();
  }

  /**
   * Reads the first line (considered to be the headline with headline fields) and returns a stream
   * of (fieldName to fieldValue) mapping where each map represents one row of the .csv file. Since
   * the returning stream is a parallel stream, the order of the elements cannot be guaranteed.
   *
   * @param entityClass the entity class that should be build
   * @param bufferedReader the reader to use
   * @return a parallel stream of maps, where each map represents one row of the csv file with the
   *     mapping (fieldName to fieldValue)
   */
  protected Stream<Map<String, String>> buildStreamWithFieldsToAttributesMap(
      Class<? extends UniqueEntity> entityClass, BufferedReader bufferedReader) {
    try (BufferedReader reader = bufferedReader) {
      final String[] headline = parseCsvRow(reader.readLine(), csvSep);

      // sanity check for headline
      if (!Arrays.asList(headline).contains("uuid")) {
        throw new SourceException(
            "The first line does not contain a field named 'uuid'. Is the headline valid?\nProvided headline: "
                + String.join(", ", headline));
      }

      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to avoid a lock on the file), but this causes a closing of the stream as well.
      // As we still want to consume the data at other places, we start a new stream instead of
      // returning the original one
      Collection<Map<String, String>> allRows = csvRowFieldValueMapping(reader, headline);

      return distinctRowsWithLog(
          allRows, fieldToValues -> fieldToValues.get("uuid"), entityClass.getSimpleName(), "UUID")
          .parallelStream();
    } catch (IOException e) {
      log.warn(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    } catch (SourceException e) {
      log.error(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }

    return Stream.empty();
  }

  protected List<Map<String, String>> csvRowFieldValueMapping(
      BufferedReader reader, String[] headline) {
    return reader
        .lines()
        .parallel()
        .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
        .filter(map -> !map.isEmpty())
        .collect(Collectors.toList());
  }

  /**
   * Returns a collection of maps each representing a row in csv file that can be used to built one
   * entity. The uniqueness of each row is doubled checked by a) that no duplicated rows are
   * returned that are full (1:1) matches and b) that no rows are returned that have the same
   * composite key, which gets extracted by the provided extractor. As both cases destroy uniqueness
   * constraints, an empty set is returned to indicate that these data cannot be processed safely
   * and the error is logged. For case a), only the duplicates are filtered out and a set with
   * unique rows is returned.
   *
   * @param allRows collection of rows of a csv file an entity should be built from
   * @param keyExtractor Function, that extracts the key from field to value mapping, that is meant
   *     to be unique
   * @param entityDescriptor Colloquial descriptor of the entity, the data is foreseen for (for
   *     debug String)
   * @param keyDescriptor Colloquial descriptor of the key, that is meant to be unique (for debug
   *     String)
   * @return either a set containing only unique rows or an empty set if at least two rows with the
   *     same UUID but different field values exist
   */
  protected Set<Map<String, String>> distinctRowsWithLog(
      Collection<Map<String, String>> allRows,
      final Function<Map<String, String>, String> keyExtractor,
      String entityDescriptor,
      String keyDescriptor) {
    Set<Map<String, String>> allRowsSet = new HashSet<>(allRows);
    // check for duplicated rows that match exactly (full duplicates) -> sanity only, not crucial -
    // case a)
    if (allRows.size() != allRowsSet.size()) {
      log.warn(
          "File with {} contains {} exact duplicated rows. File cleanup is recommended!",
          entityDescriptor,
          (allRows.size() - allRowsSet.size()));
    }

    /* Check for rows with the same key based on the provided key extractor function */
    Set<Map<String, String>> distinctIdSet =
        allRowsSet.parallelStream()
            .filter(ValidationUtils.distinctByKey(keyExtractor))
            .collect(Collectors.toSet());
    if (distinctIdSet.size() != allRowsSet.size()) {
      allRowsSet.removeAll(distinctIdSet);
      String affectedCoordinateIds =
          allRowsSet.stream().map(keyExtractor).collect(Collectors.joining(",\n"));
      log.error(
          "'{}' entities with duplicated {} key, but different field values found! Please review the "
              + "corresponding input file!\nAffected primary keys:\n{}",
          entityDescriptor,
          keyDescriptor,
          affectedCoordinateIds);
      // if this happens, we return an empty set to prevent further processing
      return new HashSet<>();
    }

    return allRowsSet;
  }

  /**
   * Checks if the requested type of an asset can be found in the provided collection of types based
   * on the provided fields to values mapping. The provided fields to values mapping needs to have
   * one and only one field with key {@link #TYPE} and a corresponding UUID value. If the type can
   * be found in the provided collection based on the UUID it is returned wrapped in an optional.
   * Otherwise an empty optional is returned and a warning is logged.
   *
   * @param types a collection of types that should be used for searching
   * @param fieldsToAttributes the field name to value mapping incl. the key {@link #TYPE}
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
    OperatorInput operator =
        getFirstOrDefaultOperator(
            operators,
            operatorUuid,
            entityClass.getSimpleName(),
            saveMapGet(fieldsToAttributes, "uuid", FIELDS_TO_VALUES_MAP));

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
                    assetInputEntityData.getTargetClass().getSimpleName(),
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
                      assetInputEntityData.getTargetClass(),
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
   * @param <T> Type of the {@link AssetInput} to expect
   * @return stream of optionals of the entities that has been built by the factor or empty
   *     optionals if the entity could not have been build
   */
  protected <T extends AssetInput> Stream<Optional<T>> nodeAssetEntityStream(
      Class<T> entityClass,
      EntityFactory<T, NodeAssetInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return nodeAssetInputEntityDataStream(assetInputEntityDataStream(entityClass, operators), nodes)
        .map(dataOpt -> dataOpt.flatMap(factory::get));
  }

  /**
   * Returns a stream of {@link SimpleEntityData} for result entity classes, using a
   * fields-to-attributes map.
   *
   * @param entityClass the entity class that should be build
   * @param <T> Type of the {@link ResultEntity} to expect
   * @return stream of {@link SimpleEntityData}
   */
  protected <T extends ResultEntity> Stream<SimpleEntityData> simpleEntityDataStream(
      Class<T> entityClass) {
    return buildStreamWithFieldsToAttributesMap(entityClass, connector)
        .map(fieldsToAttributes -> new SimpleEntityData(fieldsToAttributes, entityClass));
  }
}
