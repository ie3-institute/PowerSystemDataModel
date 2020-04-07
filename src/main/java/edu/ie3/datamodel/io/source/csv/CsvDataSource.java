/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
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

  private final String csvSep;

  // field names
  protected final String OPERATOR = "operator";
  protected final String NODE_A = "nodeA";
  protected final String NODE_B = "nodeB";
  protected final String NODE = "node";
  protected final String TYPE = "type";

  public CsvDataSource(String csvSep) {
    this.csvSep = csvSep;
  }

  protected String[] readHeadline(BufferedReader reader) throws IOException {
    return reader.readLine().replaceAll("\"", "").split(csvSep);
  }

  protected Map<String, String> buildFieldsToAttributes(String csvRow, String[] headline) {
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

  protected OperatorInput getOrDefaultOperator(
      Collection<OperatorInput> operators, String operatorUuid) {
    return operators.stream()
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

  protected <T extends UniqueEntity> Stream<T> filterEmptyOptionals(Stream<Optional<T>> elements) {
    return elements.filter(Optional::isPresent).map(Optional::get);
  }

  protected Optional<NodeInput> findNodeByUuid(String nodeUuid, Collection<NodeInput> nodes) {
    return nodes.stream()
        .filter(node -> node.getUuid().toString().equalsIgnoreCase(nodeUuid))
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
      String[] headline = readHeadline(reader);
      // by default try-with-resources closes the reader directly when we leave this method (which
      // is wanted to
      // avoid a lock on the file), but this causes a closing of the stream as well.
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

  protected <T extends AssetTypeInput> Optional<T> findTypeByUuid(
      String typeUuid, Collection<T> types) {
    return types.stream()
        .filter(type -> type.getUuid().toString().equalsIgnoreCase(typeUuid))
        .findFirst();
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

  protected <T> Predicate<Optional<T>> collectIfNotPresent(List<Optional<T>> invalidList) {
    return o -> {
      if (o.isPresent()) {
        return true;
      } else {
        invalidList.add(o);
        return false;
      }
    };
  }

  protected <T> void printInvalidElementInformation(
      Class<? extends UniqueEntity> entityClass, List<T> invalidList) {

    log.error(
        "{} entities of type '{}' are missing required elements!",
        invalidList.size(),
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
          "Duplicate UUIDs found and removed in file with '{}' entities. It is highly advisable to revise the file!",
          entity.getSimpleName());
      return new HashSet<>(distinctUuidEntities);
    }
    return new HashSet<>(entities);
  }
}
