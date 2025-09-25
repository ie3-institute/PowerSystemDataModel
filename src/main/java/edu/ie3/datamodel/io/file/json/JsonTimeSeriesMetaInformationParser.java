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
import edu.ie3.datamodel.io.file.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.file.TimeSeriesMetaInformationParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonTimeSeriesMetaInformationParser implements TimeSeriesMetaInformationParser {

  private static final String SUPPORTED_SCHEMA = "simonaMarkovLoad:psdm:1.0";

  private final Path basePath;
  private final FileNamingStrategy fileNamingStrategy;
  private final ObjectMapper objectMapper;

  public JsonTimeSeriesMetaInformationParser(Path path, FileNamingStrategy fileNamingStrategy) {
    this.basePath = Objects.requireNonNull(path, "path must not be null");
    this.fileNamingStrategy =
        Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy must not be null");
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> parseIndividualTimeSeriesMetaInformation(
      ColumnScheme... columnSchemes) {
    return Collections.emptyMap();
  }

  @Override
  public Map<String, FileLoadProfileMetaInformation> parseLoadProfileMetaInformation(
      LoadProfile... profiles) throws SourceException {
    List<Path> loadProfileFiles =
        findMatchingFiles(fileNamingStrategy.getLoadProfileTimeSeriesPattern());

    Map<String, FileLoadProfileMetaInformation> result = new HashMap<>(loadProfileFiles.size());
    for (Path relativeFile : loadProfileFiles) {
      Path absoluteFile = basePath.resolve(relativeFile);
      JsonNode root = readJson(relativeFile, absoluteFile);
      JsonMetadata metadata = validateMetadata(relativeFile, root);

      LoadProfileMetaInformation namingMeta =
          fileNamingStrategy.loadProfileTimeSeriesMetaInformation(relativeFile.toString());

      if (metadata.seriesId() != null && !metadata.seriesId().equals(namingMeta.getProfile())) {
        throw new SourceException(
            "Series id '"
                + metadata.seriesId()
                + "' in '"
                + relativeFile
                + "' does not match expected profile '"
                + namingMeta.getProfile()
                + "'.");
      }

      if (!matchesRequestedProfiles(profiles, namingMeta.getProfile())) {
        continue;
      }

      Path relativeWithoutEnding =
          Path.of(FileNamingStrategy.removeFileNameEnding(relativeFile.toString()));

      FileLoadProfileMetaInformation fileMeta =
          new FileLoadProfileMetaInformation(namingMeta, relativeWithoutEnding, FileType.JSON);

      FileLoadProfileMetaInformation previous = result.put(fileMeta.getProfile(), fileMeta);
      if (previous != null) {
        throw new SourceException(
            "Multiple JSON definitions detected for load profile '" + fileMeta.getProfile() + "'.");
      }
    }

    return result;
  }

  private boolean matchesRequestedProfiles(LoadProfile[] profiles, String profileKey) {
    if (profiles == null || profiles.length == 0) {
      return true;
    }
    return Arrays.stream(profiles).anyMatch(profile -> profile.getKey().equals(profileKey));
  }

  private List<Path> findMatchingFiles(Pattern pattern) throws SourceException {
    try (Stream<Path> stream = Files.walk(basePath)) {
      return stream
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().endsWith(FileType.JSON.extension()))
          .map(basePath::relativize)
          .filter(
              relative ->
                  pattern
                      .matcher(FileNamingStrategy.removeFileNameEnding(relative.toString()))
                      .matches())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new SourceException("Unable to inspect directory '" + basePath + "'.", e);
    }
  }

  private JsonNode readJson(Path relativeFile, Path absoluteFile) throws SourceException {
    try {
      return objectMapper.readTree(absoluteFile.toFile());
    } catch (IOException e) {
      throw new SourceException(
          "Unable to read JSON meta information from '" + relativeFile + "'.", e);
    }
  }

  private JsonMetadata validateMetadata(Path relativeFile, JsonNode root) throws SourceException {
    String schema = requireText(root, "schema", relativeFile);
    if (!SUPPORTED_SCHEMA.equals(schema)) {
      throw new SourceException(
          "Unsupported schema '"
              + schema
              + "' in '"
              + relativeFile
              + "'. Expected '"
              + SUPPORTED_SCHEMA
              + "'.");
    }

    JsonNode generatedAt = root.get("generated_at");
    if (generatedAt != null && !generatedAt.isTextual()) {
      throw new SourceException(
          "Field 'generated_at' in '" + relativeFile + "' must be textual if provided.");
    }

    JsonNode generator = requireObject(root, "generator", relativeFile);
    requireText(generator, "name", relativeFile);
    requireText(generator, "version", relativeFile);
    JsonNode generatorConfig = requireObject(generator, "config", relativeFile);
    int nStates = requirePositiveInt(generatorConfig, "n_states", relativeFile);
    double laplaceAlpha = requireDouble(generatorConfig, "laplace_alpha", relativeFile);
    if (laplaceAlpha < 0d) {
      throw new SourceException(
          "laplace_alpha must be >= 0 in '" + relativeFile + "' but was " + laplaceAlpha + ".");
    }

    JsonNode timeModel = requireObject(root, "time_model", relativeFile);
    int bucketCount = requirePositiveInt(timeModel, "bucket_count", relativeFile);
    JsonNode bucketEncoding = requireObject(timeModel, "bucket_encoding", relativeFile);
    requireText(bucketEncoding, "formula", relativeFile);
    int samplingInterval = requirePositiveInt(timeModel, "sampling_interval_minutes", relativeFile);
    String timezone = requireText(timeModel, "timezone", relativeFile);

    JsonNode valueModel = requireObject(root, "value_model", relativeFile);
    String valueUnit = requireText(valueModel, "value_unit", relativeFile);
    JsonNode normalization = requireObject(valueModel, "normalization", relativeFile);
    requireText(normalization, "method", relativeFile);
    JsonNode discretization = requireObject(valueModel, "discretization", relativeFile);
    int discretizationStates = requirePositiveInt(discretization, "states", relativeFile);
    ArrayNode thresholdsRight = requireArray(discretization, "thresholds_right", relativeFile);
    if (thresholdsRight.isEmpty()) {
      throw new SourceException(
          "value_model.discretization.thresholds_right must not be empty in '"
              + relativeFile
              + "'.");
    }
    for (JsonNode threshold : thresholdsRight) {
      if (!threshold.isNumber()) {
        throw new SourceException(
            "Non-numeric entry in value_model.discretization.thresholds_right of '"
                + relativeFile
                + "'.");
      }
    }

    JsonNode parameters = requireObject(root, "parameters", relativeFile);
    JsonNode transitionsParam = requireObject(parameters, "transitions", relativeFile);
    requireText(transitionsParam, "empty_row_strategy", relativeFile);
    requireObject(parameters, "gmm", relativeFile);

    JsonNode data = requireObject(root, "data", relativeFile);
    JsonNode transitions = requireObject(data, "transitions", relativeFile);
    ArrayNode shape = requireArray(transitions, "shape", relativeFile);
    if (shape.size() != 3) {
      throw new SourceException(
          "data.transitions.shape must contain exactly three elements in '" + relativeFile + "'.");
    }
    int shapeBuckets = requirePositiveInt(shape, 0, relativeFile, "data.transitions.shape");
    int shapeStatesRows = requirePositiveInt(shape, 1, relativeFile, "data.transitions.shape");
    int shapeStatesCols = requirePositiveInt(shape, 2, relativeFile, "data.transitions.shape");

    if (shapeBuckets != bucketCount || shapeStatesRows != nStates || shapeStatesCols != nStates) {
      throw new SourceException(
          "data.transitions.shape "
              + shape
              + " does not match bucket_count or n_states in '"
              + relativeFile
              + "'.");
    }

    String dtype = requireText(transitions, "dtype", relativeFile);
    if (!"float32".equals(dtype)) {
      throw new SourceException(
          "data.transitions.dtype must be 'float32' in '" + relativeFile + "'.");
    }
    String encoding = requireText(transitions, "encoding", relativeFile);
    if (!"nested_lists".equals(encoding)) {
      throw new SourceException(
          "data.transitions.encoding must be 'nested_lists' in '" + relativeFile + "'.");
    }
    ArrayNode values = requireArray(transitions, "values", relativeFile);
    validateTransitionValues(values, bucketCount, nStates, relativeFile);

    JsonNode gmms = requireObject(data, "gmms", relativeFile);
    ArrayNode buckets = requireArray(gmms, "buckets", relativeFile);
    if (buckets.size() != bucketCount) {
      throw new SourceException(
          "data.gmms.buckets must contain " + bucketCount + " elements in '" + relativeFile + "'.");
    }
    validateGmms(buckets, nStates, relativeFile);

    return new JsonMetadata(nStates, bucketCount, null);
  }

  private void validateTransitionValues(
      ArrayNode values, int bucketCount, int nStates, Path relativeFile) throws SourceException {
    if (values.size() != bucketCount) {
      throw new SourceException(
          "data.transitions.values must contain "
              + bucketCount
              + " buckets in '"
              + relativeFile
              + "'.");
    }
    for (int bucketIndex = 0; bucketIndex < values.size(); bucketIndex++) {
      JsonNode bucketNode = values.get(bucketIndex);
      if (!bucketNode.isArray()) {
        throw new SourceException(
            "Bucket "
                + bucketIndex
                + " in data.transitions.values of '"
                + relativeFile
                + "' is not an array.");
      }
      ArrayNode bucket = (ArrayNode) bucketNode;
      if (bucket.size() != nStates) {
        throw new SourceException(
            "Bucket "
                + bucketIndex
                + " in data.transitions.values must contain "
                + nStates
                + " rows in '"
                + relativeFile
                + "'.");
      }
      for (int rowIndex = 0; rowIndex < bucket.size(); rowIndex++) {
        JsonNode rowNode = bucket.get(rowIndex);
        if (!rowNode.isArray()) {
          throw new SourceException(
              "Row "
                  + rowIndex
                  + " in bucket "
                  + bucketIndex
                  + " of data.transitions.values in '"
                  + relativeFile
                  + "' is not an array.");
        }
        ArrayNode row = (ArrayNode) rowNode;
        if (row.size() != nStates) {
          throw new SourceException(
              "Row "
                  + rowIndex
                  + " in bucket "
                  + bucketIndex
                  + " must contain "
                  + nStates
                  + " values in '"
                  + relativeFile
                  + "'.");
        }
        for (int colIndex = 0; colIndex < row.size(); colIndex++) {
          if (!row.get(colIndex).isNumber()) {
            throw new SourceException(
                "Non-numeric entry found in data.transitions.values at bucket "
                    + bucketIndex
                    + ", row "
                    + rowIndex
                    + ", column "
                    + colIndex
                    + " in '"
                    + relativeFile
                    + "'.");
          }
        }
      }
    }
  }

  private void validateGmms(ArrayNode buckets, int nStates, Path relativeFile)
      throws SourceException {
    for (int bucketIndex = 0; bucketIndex < buckets.size(); bucketIndex++) {
      JsonNode bucketNode = buckets.get(bucketIndex);
      if (!bucketNode.isObject()) {
        throw new SourceException(
            "Bucket "
                + bucketIndex
                + " in data.gmms.buckets of '"
                + relativeFile
                + "' is not an object.");
      }
      JsonNode statesNode = requireArray(bucketNode, "states", relativeFile);
      if (statesNode.size() != nStates) {
        throw new SourceException(
            "Bucket "
                + bucketIndex
                + " in data.gmms.buckets must contain "
                + nStates
                + " state entries in '"
                + relativeFile
                + "'.");
      }
      for (int stateIndex = 0; stateIndex < statesNode.size(); stateIndex++) {
        JsonNode stateNode = statesNode.get(stateIndex);
        if (stateNode.isNull()) {
          continue;
        }
        if (!stateNode.isObject()) {
          throw new SourceException(
              "State "
                  + stateIndex
                  + " in bucket "
                  + bucketIndex
                  + " of data.gmms.buckets in '"
                  + relativeFile
                  + "' is not an object.");
        }
        ArrayNode weights = requireArray(stateNode, "weights", relativeFile);
        ArrayNode means = requireArray(stateNode, "means", relativeFile);
        ArrayNode variances = requireArray(stateNode, "variances", relativeFile);

        int mixtureSize = weights.size();
        if (means.size() != mixtureSize || variances.size() != mixtureSize) {
          throw new SourceException(
              "Gaussian mixture of state "
                  + stateIndex
                  + " in bucket "
                  + bucketIndex
                  + " must have equal number of weights, means, and variances in '"
                  + relativeFile
                  + "'.");
        }
        for (int componentIndex = 0; componentIndex < mixtureSize; componentIndex++) {
          if (!weights.get(componentIndex).isNumber()) {
            throw new SourceException(
                "Non-numeric weight found in Gaussian mixture of state "
                    + stateIndex
                    + ", bucket "
                    + bucketIndex
                    + " in '"
                    + relativeFile
                    + "'.");
          }
          if (!means.get(componentIndex).isNumber()) {
            throw new SourceException(
                "Non-numeric mean found in Gaussian mixture of state "
                    + stateIndex
                    + ", bucket "
                    + bucketIndex
                    + " in '"
                    + relativeFile
                    + "'.");
          }
          if (!variances.get(componentIndex).isNumber()) {
            throw new SourceException(
                "Non-numeric variance found in Gaussian mixture of state "
                    + stateIndex
                    + ", bucket "
                    + bucketIndex
                    + " in '"
                    + relativeFile
                    + "'.");
          }
          double variance = variances.get(componentIndex).asDouble();
          if (variance < 0d) {
            throw new SourceException(
                "Negative variance "
                    + variance
                    + " in Gaussian mixture of state "
                    + stateIndex
                    + ", bucket "
                    + bucketIndex
                    + " in '"
                    + relativeFile
                    + "'.");
          }
        }
      }
    }
  }

  private JsonNode requireObject(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isObject()) {
      throw new SourceException(
          "Field '" + field + "' in '" + relativeFile + "' must be an object.");
    }
    return node;
  }

  private ArrayNode requireArray(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isArray()) {
      throw new SourceException(
          "Field '" + field + "' in '" + relativeFile + "' must be an array.");
    }
    return (ArrayNode) node;
  }

  private JsonNode requireNode(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    if (!parent.has(field)) {
      throw new SourceException("Missing field '" + field + "' in '" + relativeFile + "'.");
    }
    return parent.get(field);
  }

  private String requireText(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isTextual()) {
      throw new SourceException("Field '" + field + "' in '" + relativeFile + "' must be textual.");
    }
    return node.asText();
  }

  private boolean requireBoolean(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isBoolean()) {
      throw new SourceException("Field '" + field + "' in '" + relativeFile + "' must be boolean.");
    }
    return node.asBoolean();
  }

  private double requireDouble(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isNumber()) {
      throw new SourceException("Field '" + field + "' in '" + relativeFile + "' must be numeric.");
    }
    return node.asDouble();
  }

  private int requirePositiveInt(JsonNode parent, String field, Path relativeFile)
      throws SourceException {
    JsonNode node = requireNode(parent, field, relativeFile);
    if (!node.isInt()) {
      throw new SourceException(
          "Field '" + field + "' in '" + relativeFile + "' must be an integer.");
    }
    int value = node.asInt();
    if (value <= 0) {
      throw new SourceException(
          "Field '"
              + field
              + "' in '"
              + relativeFile
              + "' must be positive but was "
              + value
              + ".");
    }
    return value;
  }

  private int requirePositiveInt(ArrayNode array, int index, Path relativeFile, String field)
      throws SourceException {
    if (index < 0 || index >= array.size()) {
      throw new SourceException(
          "Index " + index + " out of bounds for '" + field + "' in '" + relativeFile + "'.");
    }
    JsonNode node = array.get(index);
    if (!node.isInt()) {
      throw new SourceException(
          "Entry " + index + " of '" + field + "' in '" + relativeFile + "' must be an integer.");
    }
    int value = node.asInt();
    if (value <= 0) {
      throw new SourceException(
          "Entry "
              + index
              + " of '"
              + field
              + "' in '"
              + relativeFile
              + "' must be positive but was "
              + value
              + ".");
    }
    return value;
  }

  private record JsonMetadata(int nStates, int bucketCount, String seriesId) {}
}
