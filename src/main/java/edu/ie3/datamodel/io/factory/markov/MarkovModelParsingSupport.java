/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.*;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.*;
import edu.ie3.util.StringUtils;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import tools.jackson.databind.JsonNode;

/** Shared parsing helpers for Markov model JSON documents. */
interface MarkovModelParsingSupport {

  default Generator parseGenerator(JsonNode generatorNode) {
    String name = extractText(generatorNode, jsonLeafField(MARKOV_GENERATOR_NAME));
    String version = extractText(generatorNode, jsonLeafField(MARKOV_GENERATOR_VERSION));
    Map<String, String> config = new LinkedHashMap<>();
    JsonNode configNode = generatorNode.path(jsonLeafField(MARKOV_GENERATOR_CONFIG));
    if (configNode.isObject()) {
      for (Map.Entry<String, JsonNode> entry : configNode.properties()) {
        config.put(entry.getKey(), entry.getValue().asString());
      }
    }
    return new Generator(name, version, config);
  }

  /** Extracts the time model block. */
  default TimeModel extractTimeModel(JsonNode timeNode) {
    int bucketCount = extractInt(timeNode, jsonLeafField(MARKOV_BUCKET_COUNT));
    if (bucketCount <= 0) {
      throw new FactoryException(jsonField(MARKOV_BUCKET_COUNT) + " must be greater than 0.");
    }
    String formula =
        extractNode(timeNode, jsonLeafField(MARKOV_BUCKET_ENCODING))
            .path(jsonLeafField(MARKOV_BUCKET_ENCODING_FORMULA))
            .asString("");
    if (formula.isEmpty()) {
      throw new FactoryException("Missing bucket encoding formula.");
    }
    int samplingInterval = extractInt(timeNode, jsonLeafField(MARKOV_SAMPLING_INTERVAL));
    if (samplingInterval <= 0) {
      throw new FactoryException(jsonField(MARKOV_SAMPLING_INTERVAL) + " must be greater than 0.");
    }
    String timezone = extractText(timeNode, jsonLeafField(MARKOV_TIMEZONE));
    return new TimeModel(bucketCount, formula, samplingInterval, timezone);
  }

  /** Parses value model settings. */
  default ValueModel parseValueModel(JsonNode valueNode) {
    String valueUnit = extractText(valueNode, jsonLeafField(MARKOV_VALUE_UNIT));
    JsonNode normalizationNode = extractNode(valueNode, jsonLeafField(MARKOV_NORMALIZATION));
    String normalizationMethod =
        extractText(normalizationNode, jsonLeafField(MARKOV_NORMALIZATION_METHOD));
    ValueModel.Normalization normalization =
        new ValueModel.Normalization(
            normalizationMethod,
            parsePowerReference(normalizationNode, jsonLeafField(MARKOV_MAX_POWER)),
            parsePowerReference(normalizationNode, jsonLeafField(MARKOV_MIN_POWER)));

    JsonNode discretizationNode = extractNode(valueNode, jsonLeafField(MARKOV_DISCRETIZATION));
    int states = extractInt(discretizationNode, jsonLeafField(MARKOV_DISCRETIZATION_STATES));
    if (states <= 0) {
      throw new FactoryException(
          jsonField(MARKOV_DISCRETIZATION_STATES) + " must be greater than 0.");
    }
    String thresholdsField = jsonLeafField(MARKOV_DISCRETIZATION_THRESHOLDS);
    List<Double> thresholds = readDoubleArray(discretizationNode, thresholdsField);
    if (thresholds.size() != Math.max(0, states - 1)) {
      throw new FactoryException(
          "Discretization "
              + thresholdsField
              + " must contain "
              + Math.max(0, states - 1)
              + " entries for "
              + states
              + " states, but found "
              + thresholds.size()
              + ".");
    }
    ValueModel.Discretization discretization = new ValueModel.Discretization(states, thresholds);

    return new ValueModel(valueUnit, normalization, discretization);
  }

  /** Parses optional parameter metadata. */
  default Parameters parseParameters(JsonNode parametersNode) {
    String emptyRowStrategy =
        parametersNode
            .path(jsonLeafField(MARKOV_PARAMETERS_TRANSITIONS))
            .path(jsonLeafField(MARKOV_EMPTY_ROW_STRATEGY))
            .asString("");
    Optional<Parameters.TransitionParameters> transitions =
        emptyRowStrategy.isEmpty()
            ? Optional.empty()
            : Optional.of(new Parameters.TransitionParameters(emptyRowStrategy));

    JsonNode gmmNode = parametersNode.path(jsonLeafField(MARKOV_PARAMETERS_GMM));
    Optional<Parameters.GmmParameters> gmm =
        gmmNode.isMissingNode() || gmmNode.isNull() || gmmNode.isEmpty()
            ? Optional.empty()
            : Optional.of(
                new Parameters.GmmParameters(
                    gmmNode.path(jsonLeafField(MARKOV_GMM_VALUE_COLUMN)).asString(""),
                    optionalInt(gmmNode, jsonLeafField(MARKOV_GMM_VERBOSE)),
                    optionalInt(gmmNode, jsonLeafField(MARKOV_GMM_HEARTBEAT_SECONDS))));

    return new Parameters(transitions, gmm);
  }

  /** Parses the transition tensor. */
  default TransitionData parseTransitions(
      JsonNode dataNode, int expectedBucketCount, int stateCount) {
    JsonNode transitionsNode = extractNode(dataNode, jsonLeafField(MARKOV_TRANSITIONS));
    String dtype = extractText(transitionsNode, jsonLeafField(MARKOV_TRANSITION_DTYPE));
    String encoding = extractText(transitionsNode, jsonLeafField(MARKOV_TRANSITION_ENCODING));

    int[] shape = parseTransitionShape(transitionsNode);
    int buckets = shape[0];
    int rows = shape[1];
    int columns = shape[2];
    validateTransitionShape(expectedBucketCount, stateCount, buckets, rows, columns);

    JsonNode valuesNode = extractNode(transitionsNode, jsonLeafField(MARKOV_TRANSITION_VALUES));
    double[][][] values = parseTransitionValues(valuesNode, buckets, stateCount);

    return new TransitionData(dtype, encoding, values);
  }

  /** Parses GMM buckets. Individual states may be null. */
  default GmmBuckets parseGmmBuckets(JsonNode gmmsNode) {
    if (gmmsNode == null || gmmsNode.isMissingNode() || gmmsNode.isNull()) {
      throw new FactoryException("Missing field '" + jsonLeafField(MARKOV_GMMS) + "'.");
    }
    JsonNode bucketsNode = gmmsNode.get(jsonLeafField(MARKOV_GMM_BUCKETS));
    if (bucketsNode == null || !bucketsNode.isArray()) {
      throw new FactoryException(jsonField(MARKOV_GMM_BUCKETS) + " must be an array.");
    }
    List<GmmBuckets.GmmBucket> buckets = new ArrayList<>();
    for (JsonNode bucketNode : bucketsNode) {
      JsonNode statesNode = bucketNode.get(jsonLeafField(MARKOV_GMM_STATES));
      if (statesNode == null || !statesNode.isArray()) {
        throw new FactoryException(
            "Each GMM bucket must contain an array '" + jsonLeafField(MARKOV_GMM_STATES) + "'.");
      }
      List<GmmBuckets.GmmState> states = new ArrayList<>();
      for (JsonNode stateNode : statesNode) {
        if (stateNode == null || stateNode.isNull()) {
          states.add(null);
          continue;
        }
        List<Double> weights = readDoubleArray(stateNode, jsonLeafField(MARKOV_GMM_WEIGHTS));
        List<Double> means = readDoubleArray(stateNode, jsonLeafField(MARKOV_GMM_MEANS));
        List<Double> variances = readDoubleArray(stateNode, jsonLeafField(MARKOV_GMM_VARIANCES));
        states.add(new GmmBuckets.GmmState(weights, means, variances));
      }
      buckets.add(new GmmBuckets.GmmBucket(Collections.unmodifiableList(states)));
    }
    return new GmmBuckets(List.copyOf(buckets));
  }

  default JsonNode extractNode(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode()) {
      throw new FactoryException("Missing field '" + field + "'.");
    }
    return value;
  }

  default String extractText(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "'.");
    }
    if (!value.isString()) {
      throw new FactoryException("Field '" + field + "' must be textual.");
    }
    return value.asString();
  }

  default double extractDouble(JsonNode node, String field) {
    JsonNode value = node.get(field);
    return extractDoubleValue(value, field);
  }

  default int extractInt(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "'.");
    }
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "' must be an integer.");
    }
    return value.asInt();
  }

  default ZonedDateTime parseTimestamp(String timestamp) {
    try {
      return ZonedDateTime.parse(timestamp);
    } catch (DateTimeParseException e) {
      throw new FactoryException(
          "Unable to parse " + jsonField(MARKOV_GENERATED_AT) + " timestamp '" + timestamp + "'.",
          e);
    }
  }

  default OptionalInt optionalInt(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isNull()) return OptionalInt.empty();
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "' must be an integer.");
    }
    return OptionalInt.of(value.asInt());
  }

  default int[] parseTransitionShape(JsonNode transitionsNode) {
    JsonNode shapeNode = extractNode(transitionsNode, jsonLeafField(MARKOV_TRANSITION_SHAPE));
    if (!shapeNode.isArray() || shapeNode.size() != 3) {
      throw new FactoryException("Transition shape must contain three dimensions.");
    }
    return new int[] {
      extractInt(shapeNode, 0, "Transition shape"),
      extractInt(shapeNode, 1, "Transition shape"),
      extractInt(shapeNode, 2, "Transition shape")
    };
  }

  default void validateTransitionShape(
      int expectedBucketCount, int stateCount, int buckets, int rows, int columns) {
    if (buckets != expectedBucketCount) {
      throw new FactoryException(
          "Transition bucket count mismatch. Expected "
              + expectedBucketCount
              + " but was "
              + buckets
              + ".");
    }
    if (rows != stateCount || columns != stateCount) {
      throw new FactoryException(
          "Transition state dimension mismatch. Expected "
              + stateCount
              + " but was rows="
              + rows
              + ", columns="
              + columns
              + ".");
    }
  }

  default double[][][] parseTransitionValues(JsonNode valuesNode, int buckets, int stateCount) {
    if (!valuesNode.isArray() || valuesNode.size() != buckets) {
      throw new FactoryException(
          "Transition values must be a three dimensional array with " + buckets + " buckets.");
    }
    double[][][] values = new double[buckets][stateCount][stateCount];
    for (int b = 0; b < buckets; b++) {
      JsonNode bucketNode = valuesNode.get(b);
      if (!bucketNode.isArray()) {
        throw new FactoryException("Bucket " + b + " in transition values must be an array.");
      }
      if (bucketNode.size() != stateCount) {
        throw new FactoryException(
            "Bucket "
                + b
                + " contained "
                + bucketNode.size()
                + " rows. Expected "
                + stateCount
                + ".");
      }
      for (int r = 0; r < stateCount; r++) {
        JsonNode rowNode = bucketNode.get(r);
        if (!rowNode.isArray()) {
          throw new FactoryException(
              "Row " + r + " in bucket " + b + " of transition values must be an array.");
        }
        if (rowNode.size() != stateCount) {
          throw new FactoryException(
              "Row "
                  + r
                  + " in bucket "
                  + b
                  + " had "
                  + rowNode.size()
                  + " columns. Expected "
                  + stateCount
                  + ".");
        }
        for (int c = 0; c < stateCount; c++) {
          values[b][r][c] =
              extractDoubleValue(
                  rowNode.get(c),
                  jsonField(MARKOV_TRANSITION_VALUES) + "[" + b + "][" + r + "][" + c + "]");
        }
      }
    }
    return values;
  }

  default List<Double> readDoubleArray(JsonNode node, String field) {
    JsonNode arrayNode = node.get(field);
    if (arrayNode == null || !arrayNode.isArray()) {
      throw new FactoryException("Field '" + field + "' must be an array.");
    }
    List<Double> values = new ArrayList<>();
    for (int i = 0; i < arrayNode.size(); i++) {
      values.add(extractDoubleValue(arrayNode.get(i), field + "[" + i + "]"));
    }
    return List.copyOf(values);
  }

  default int extractInt(JsonNode node, int index, String field) {
    JsonNode value = node.get(index);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "[" + index + "]'.");
    }
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "[" + index + "]' must be an integer.");
    }
    return value.asInt();
  }

  default double extractDoubleValue(JsonNode node, String field) {
    if (node == null || node.isMissingNode() || node.isNull()) {
      throw new FactoryException("Missing field '" + field + "'.");
    }
    if (!node.isNumber()) {
      throw new FactoryException("Field '" + field + "' must be a double.");
    }
    double value = node.asDouble();
    if (!Double.isFinite(value)) {
      throw new FactoryException("Field '" + field + "' must be finite.");
    }
    return value;
  }

  default Optional<ValueModel.Normalization.PowerReference> parsePowerReference(
      JsonNode parent, String field) {
    JsonNode referenceNode = parent.get(field);
    if (referenceNode == null || referenceNode.isMissingNode() || referenceNode.isNull()) {
      return Optional.empty();
    }
    if (!referenceNode.isObject()) {
      throw new FactoryException("Field '" + field + "' must be an object.");
    }
    double value = extractDouble(referenceNode, jsonLeafField(MARKOV_MAX_POWER_VALUE));
    String unit = extractText(referenceNode, jsonLeafField(MARKOV_MAX_POWER_UNIT));
    return Optional.of(new ValueModel.Normalization.PowerReference(value, unit));
  }

  default String jsonField(String field) {
    return StringUtils.camelCaseToSnakeCase(field);
  }

  default String jsonLeafField(String field) {
    int lastSeparatorIndex = field.lastIndexOf('.');
    String leaf = lastSeparatorIndex < 0 ? field : field.substring(lastSeparatorIndex + 1);
    return jsonField(leaf);
  }
}
