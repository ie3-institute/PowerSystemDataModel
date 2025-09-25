/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.file.TimeSeriesMappingParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonTimeSeriesMappingParser implements TimeSeriesMappingParser {

  private static final String SUPPORTED_SCHEMA = "simonaMarkovLoad:psdm:1.0";
  private static final String ATTRIBUTE_KEY = "attribute";

  private final Path basePath;
  private final FileNamingStrategy fileNamingStrategy;
  private final ObjectMapper objectMapper;

  public JsonTimeSeriesMappingParser(Path path, FileNamingStrategy fileNamingStrategy) {
    this(path, fileNamingStrategy, new ObjectMapper());
  }

  JsonTimeSeriesMappingParser(
      Path path, FileNamingStrategy fileNamingStrategy, ObjectMapper objectMapper) {
    this.basePath = Objects.requireNonNull(path, "path must not be null");
    this.fileNamingStrategy =
        Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy must not be null");
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
  }

  @Override
  public Stream<Map<String, String>> parse() throws SourceException {
    return loadMappings().entries().stream();
  }

  @Override
  public Optional<Set<String>> availableFields() throws SourceException {
    MappingLoadResult result = loadMappings();
    if (!result.filePresent()) {
      return Optional.empty();
    }
    if (result.entries().isEmpty()) {
      return Optional.of(Set.of("asset", "timeSeries", "targetType", ATTRIBUTE_KEY));
    }
    Set<String> fields =
        result.entries().stream()
            .flatMap(entry -> entry.keySet().stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    return Optional.of(fields);
  }

  private MappingLoadResult loadMappings() throws SourceException {
    Path relative =
        fileNamingStrategy
            .getFilePath(TimeSeriesMappingSource.MappingEntry.class)
            .orElseThrow(
                () ->
                    new SourceException(
                        "Unable to determine path to time series mapping file for JSON parsing."));
    Path file = basePath.resolve(relative.toString() + FileType.JSON.extension());
    if (!Files.exists(file)) {
      return new MappingLoadResult(Collections.emptyList(), false);
    }

    JsonNode root = readJson(relative, file);
    String schema = requireText(root, "schema", relative);
    if (!SUPPORTED_SCHEMA.equals(schema)) {
      throw new SourceException(
          "Unsupported schema '"
              + schema
              + "' in '"
              + relative
              + "'. Expected '"
              + SUPPORTED_SCHEMA
              + "'.");
    }

    ArrayNode mappings = requireArray(root, "mappings", relative);
    List<Map<String, String>> entries = new ArrayList<>(mappings.size());
    for (JsonNode mappingNode : mappings) {
      if (!mappingNode.isObject()) {
        throw new SourceException("Found non-object mapping entry in '" + relative + "'.");
      }
      Map<String, String> entry = new LinkedHashMap<>();
      entry.put("asset", requireText(mappingNode, "target_id", relative));
      entry.put("timeSeries", requireText(mappingNode, "time_series_id", relative));
      entry.put("targetType", requireText(mappingNode, "target_type", relative));
      entry.put(ATTRIBUTE_KEY, requireText(mappingNode, ATTRIBUTE_KEY, relative));
      entries.add(entry);
    }

    return new MappingLoadResult(entries, true);
  }

  private JsonNode readJson(Path relative, Path file) throws SourceException {
    try {
      return objectMapper.readTree(file.toFile());
    } catch (IOException e) {
      throw new SourceException("Unable to read mapping file '" + relative + "'.", e);
    }
  }

  private ArrayNode requireArray(JsonNode parent, String field, Path relative)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relative);
    if (!node.isArray()) {
      throw new SourceException("Field '" + field + "' in '" + relative + "' must be an array.");
    }
    return (ArrayNode) node;
  }

  private String requireText(JsonNode parent, String field, Path relative) throws SourceException {
    JsonNode node = requireNode(parent, field, relative);
    if (!node.isTextual()) {
      throw new SourceException("Field '" + field + "' in '" + relative + "' must be textual.");
    }
    return node.asText();
  }

  private JsonNode requireNode(JsonNode parent, String field, Path relative)
      throws SourceException {
    if (!parent.has(field)) {
      throw new SourceException("Missing field '" + field + "' in '" + relative + "'.");
    }
    return parent.get(field);
  }

  private record MappingLoadResult(List<Map<String, String>> entries, boolean filePresent) {}
}
