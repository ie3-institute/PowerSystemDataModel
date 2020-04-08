/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.UntypedSingleNodeEntityData;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 05.04.20
 */
public abstract class CsvDataSource {

  private static final Logger log = LogManager.getLogger(CsvDataSource.class);

  // general fields
  private final String csvSep;
  protected final CsvFileConnector connector;

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE_A = "nodeA";
  protected static final String NODE_B = "nodeB";
  protected static final String NODE = "node";
  protected static final String TYPE = "type";

  public CsvDataSource(String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    this.csvSep = csvSep;
    this.connector = new CsvFileConnector(folderPath, fileNamingStrategy);
  }

  private Map<String, String> buildFieldsToAttributes(String csvRow, String[] headline) {
    // sometimes we have a json string as field value -> we need to consider this one as well
    String cswRowRegex = csvSep + "(?=(?:\\{))|" + csvSep + "(?=(?:\\{*[^\\}]*$))";
    final String[] fieldVals = csvRow.split(cswRowRegex);

    TreeMap<String, String> insensitiveFieldsToAttributes =
        new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    insensitiveFieldsToAttributes.putAll(
        IntStream.range(0, fieldVals.length)
            .boxed()
            .collect(Collectors.toMap(k -> snakeCaseToCamelCase(headline[k]), v -> fieldVals[v])));
    return insensitiveFieldsToAttributes;
  }

  private OperatorInput getFirstOrDefaultOperator(
      Collection<OperatorInput> operators, String operatorUuid) {
    return operators.stream()
        .parallel()
        .filter(operator -> operator.getUuid().toString().equalsIgnoreCase(operatorUuid))
        .findFirst()
        .orElseGet(
            () -> {
              log.debug(
                  "Cannot find operator for node with uuid '{}'. Defaulting to 'NO OPERATOR ASSIGNED'.",
                  operatorUuid);
              return OperatorInput.NO_OPERATOR_ASSIGNED;
            });
  }

  private String snakeCaseToCamelCase(String snakeCaseString) {
    StringBuilder sb = new StringBuilder();
    for (String s : snakeCaseString.split("_")) {
      sb.append(Character.toUpperCase(s.charAt(0)));
      if (s.length() > 1) {
        sb.append(s.substring(1).toLowerCase());
      }
    }
    return sb.toString();
  }

  protected <T extends AssetInput> Predicate<Optional<T>> isPresentCollectIfNot(
      Class<? extends AssetInput> entityClass,
      ConcurrentHashMap<Class<? extends AssetInput>, LongAdder> invalidElementsCounterMap) {
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

  protected void logSkippingWarning(
      String entityDesc, String entityUuid, String entityId, String missingElementsString) {

    log.warn(
        "Skipping {} with uuid '{}' and id '{}'. Not all required entities found!\nMissing elements:\n{}",
        entityDesc,
        entityUuid,
        entityId,
        missingElementsString);
  }

  protected <T extends UniqueEntity> Set<T> checkForUuidDuplicates(
      Class<T> entity, Collection<T> entities) {
    Collection<T> distinctUuidEntities = ValidationUtils.distinctUuidSet(entities);
    if (distinctUuidEntities.size() != entities.size()) {
      log.warn(
          "Duplicate UUIDs found and removed in file with '{}' entities. It is highly advisable to revise the input file!",
          entity.getSimpleName());
      return new HashSet<>(distinctUuidEntities);
    }
    return new HashSet<>(entities);
  }

  protected <T extends AssetInput> Stream<AssetInputEntityData> buildAssetInputEntityData(
      Class<T> entityClass, Collection<OperatorInput> operators) {

    return buildStreamWithFieldsToAttributesMap(entityClass, connector)
        .map(
            fieldsToAttributes -> {

              // get the operator of the entity
              String operatorUuid = fieldsToAttributes.get(OPERATOR);
              OperatorInput operator = getFirstOrDefaultOperator(operators, operatorUuid);

              // remove fields that are passed as objects to constructor
              fieldsToAttributes
                  .keySet()
                  .removeAll(new HashSet<>(Collections.singletonList(OPERATOR)));

              return new AssetInputEntityData(fieldsToAttributes, entityClass, operator);
            });
  }

  protected Stream<Optional<UntypedSingleNodeEntityData>> buildUntypedEntityData(
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
                  new UntypedSingleNodeEntityData(
                      fieldsToAttributes,
                      assetInputEntityData.getEntityClass(),
                      assetInputEntityData.getOperatorInput(),
                      node.get()));
            });
  }

  protected <T extends UniqueEntity> Stream<T> filterEmptyOptionals(Stream<Optional<T>> elements) {
    return elements.filter(Optional::isPresent).map(Optional::get);
  }

  protected <T extends UniqueEntity> Optional<T> findFirstEntityByUuid(
      String typeUuid, Collection<T> types) {
    return types.stream()
        .parallel()
        .filter(type -> type.getUuid().toString().equalsIgnoreCase(typeUuid))
        .findFirst();
  }

  /**
   * TODO note that the stream is already parallel
   *
   * @param entityClass
   * @param connector
   * @return
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
              .collect(Collectors.toList());
      return allRows.stream().parallel();

    } catch (IOException e) {
      log.warn(
          "Cannot read file to build entity '{}': {}", entityClass.getSimpleName(), e.getMessage());
    }

    return Stream.empty();
  }

  protected <T extends AssetInput> Stream<Optional<T>> untypedEntityStream(
      Class<T> entityClass,
      EntityFactory<T, UntypedSingleNodeEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return buildUntypedEntityData(buildAssetInputEntityData(entityClass, operators), nodes)
        .map(dataOpt -> dataOpt.flatMap(factory::getEntity));
  }
}
