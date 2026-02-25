/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.json;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.JsonFileConnector;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.file.FileDataSource;
import edu.ie3.datamodel.models.Entity;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/** Data source abstraction for JSON files. */
public class JsonDataSource extends FileDataSource {

  private final JsonFileConnector connector;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public JsonDataSource(Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this(new JsonFileConnector(directoryPath), fileNamingStrategy);
  }

  public JsonDataSource(JsonFileConnector connector, FileNamingStrategy fileNamingStrategy) {
    super(connector.getBaseDirectory(), fileNamingStrategy);
    this.connector = connector;
  }

  /**
   * Opens an input stream for the provided file path.
   *
   * @param filePath relative path without ending
   * @return input stream
   * @throws SourceException if the file cannot be opened
   */
  public InputStream initInputStream(Path filePath) throws SourceException {
    try {
      return connector.initInputStream(filePath);
    } catch (FileNotFoundException e) {
      throw new SourceException("Unable to open JSON file '" + filePath + "'.", e);
    }
  }

  /**
   * Reads and parses a JSON file into a {@link JsonNode} tree.
   *
   * @param filePath path to the JSON file
   * @return the parsed JSON tree
   * @throws SourceException if the file cannot be read or parsed
   */
  public JsonNode readTree(Path filePath) throws SourceException {
    try (InputStream inputStream = initInputStream(filePath)) {
      return objectMapper.readTree(inputStream);
    } catch (IOException e) {
      throw new SourceException("Unable to read JSON from '" + filePath + "'.", e);
    }
  }

  @Override
  public Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass)
      throws SourceException {
    throw unsupportedTabularAccess("getSourceFields(Class)");
  }

  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    throw unsupportedTabularAccess("getSourceData(Class)");
  }

  /**
   * Returns the set of field names present in the JSON file at the given path.
   *
   * @param filePath path to the JSON file
   * @return an optional containing the field names, or empty if none found
   * @throws SourceException if the file cannot be read
   */
  @Override
  public Optional<Set<String>> getSourceFields(Path filePath) throws SourceException {
    JsonNode root = readTree(filePath);
    return Optional.of(collectFieldNames(root));
  }

  @Override
  public Stream<Map<String, String>> getSourceData(Path filePath) throws SourceException {
    throw unsupportedTabularAccess("getSourceData(Path)");
  }

  private static Set<String> collectFieldNames(JsonNode node) {
    Set<String> fields = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    collectFields("", node, fields);
    return fields;
  }

  private static void collectFields(String prefix, JsonNode node, Set<String> collector) {
    if (node.isArray()) {
      if (!prefix.isEmpty()) {
        collector.add(prefix);
      }
      return;
    }
    if (node.isObject()) {
      node.propertyNames()
          .forEach(name -> collectFields(join(prefix, name), node.get(name), collector));
    } else if (!prefix.isEmpty()) {
      collector.add(prefix);
    }
  }

  private static String join(String prefix, String name) {
    return prefix.isEmpty() ? name : prefix + "." + name;
  }

  private UnsupportedOperationException unsupportedTabularAccess(String method) {
    return new UnsupportedOperationException(
        "JsonDataSource does not support '" + method + "', as JSON sources are not tabular.");
  }
}
