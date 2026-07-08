/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import tools.jackson.databind.JsonNode;

/** Shared parsing helpers for Markov model JSON documents. */
interface MarkovModelParsingSupport {

  default Generator parseGenerator(JsonNode generatorNode) {
    String name = extractText(generatorNode, "name");
    String version = extractText(generatorNode, "version");
    Map<String, String> config = new LinkedHashMap<>();
    JsonNode configNode = generatorNode.path("config");
    if (configNode.isObject()) {
      for (Map.Entry<String, JsonNode> entry : configNode.properties()) {
        config.put(entry.getKey(), entry.getValue().asString());
      }
    }
    return new Generator(name, version, config);
  }

  /** Extracts the time model block. */
  default TimeModel extractTimeModel(JsonNode timeNode) {
    int bucketCount = extractInt(timeNode, "bucket_count");
    if (bucketCount <= 0) {
      throw new FactoryException("time_model.bucket_count must be positive");
    }
    String formula = extractNode(timeNode, "bucket_encoding").path("formula").asString("");
    if (formula.isEmpty()) {
      throw new FactoryException("Missing bucket encoding formula");
    }
    int samplingInterval = extractInt(timeNode, "sampling_interval_minutes");
    if (samplingInterval <= 0) {
      throw new FactoryException("time_model.sampling_interval_minutes must be positive");
    }
    String timezone = extractText(timeNode, "timezone");
    return new TimeModel(bucketCount, formula, samplingInterval, timezone);
  }

  /** Parses value model settings. */
  default ValueModel parseValueModel(JsonNode valueNode) {
    String valueUnit = extractText(valueNode, "value_unit");
    JsonNode normalizationNode = extractNode(valueNode, "normalization");
    String normalizationMethod = extractText(normalizationNode, "method");
    ValueModel.Normalization normalization =
        new ValueModel.Normalization(
            normalizationMethod,
            parsePowerReference(normalizationNode, "max_power"),
            parsePowerReference(normalizationNode, "min_power"));

    JsonNode discretizationNode = extractNode(valueNode, "discretization");
    int states = extractInt(discretizationNode, "states");
    if (states <= 0) {
      throw new FactoryException("value_model.discretization.states must be positive");
    }
    List<Double> thresholds = readDoubleArray(discretizationNode, "thresholds_right");
    if (thresholds.size() != Math.max(0, states - 1)) {
      throw new FactoryException(
          "Discretization thresholds_right must contain "
              + Math.max(0, states - 1)
              + " entries for "
              + states
              + " states, but found "
              + thresholds.size());
    }
    ValueModel.Discretization discretization = new ValueModel.Discretization(states, thresholds);

    return new ValueModel(valueUnit, normalization, discretization);
  }

  /** Parses optional parameter metadata. */
  default Parameters parseParameters(JsonNode parametersNode) {
    String emptyRowStrategy =
        parametersNode.path("transitions").path("empty_row_strategy").asString("");
    Optional<Parameters.TransitionParameters> transitions =
        emptyRowStrategy.isEmpty()
            ? Optional.empty()
            : Optional.of(new Parameters.TransitionParameters(emptyRowStrategy));

    JsonNode gmmNode = parametersNode.path("gmm");
    Optional<Parameters.GmmParameters> gmm =
        gmmNode.isMissingNode() || gmmNode.isNull() || gmmNode.isEmpty()
            ? Optional.empty()
            : Optional.of(
                new Parameters.GmmParameters(
                    gmmNode.path("value_col").asString(""),
                    optionalInt(gmmNode, "verbose"),
                    optionalInt(gmmNode, "heartbeat_seconds")));

    return new Parameters(transitions, gmm);
  }

  /** Parses the transition tensor. */
  default TransitionData parseTransitions(
      JsonNode dataNode, int expectedBucketCount, int stateCount) {
    JsonNode transitionsNode = extractNode(dataNode, "transitions");
    String dtype = extractText(transitionsNode, "dtype");
    String encoding = extractText(transitionsNode, "encoding");

    int[] shape = parseTransitionShape(transitionsNode);
    int buckets = shape[0];
    int rows = shape[1];
    int columns = shape[2];
    validateTransitionShape(expectedBucketCount, stateCount, buckets, rows, columns);

    JsonNode valuesNode = extractNode(transitionsNode, "values");
    double[][][] values = parseTransitionValues(valuesNode, buckets, stateCount);

    return new TransitionData(dtype, encoding, values);
  }

  /** Parses GMM buckets. Individual states may be null. */
  default GmmBuckets parseGmmBuckets(JsonNode gmmsNode) {
    if (gmmsNode == null || gmmsNode.isMissingNode() || gmmsNode.isNull()) {
      throw new FactoryException("Missing field 'gmms'");
    }
    JsonNode bucketsNode = gmmsNode.get("buckets");
    if (bucketsNode == null || !bucketsNode.isArray()) {
      throw new FactoryException("data.gmms.buckets must be an array");
    }
    List<GmmBuckets.GmmBucket> buckets = new ArrayList<>();
    for (JsonNode bucketNode : bucketsNode) {
      JsonNode statesNode = bucketNode.get("states");
      if (statesNode == null || !statesNode.isArray()) {
        throw new FactoryException("Each GMM bucket must contain an array 'states'");
      }
      List<GmmBuckets.GmmState> states = new ArrayList<>();
      for (JsonNode stateNode : statesNode) {
        if (stateNode == null || stateNode.isNull()) {
          states.add(null);
          continue;
        }
        List<Double> weights = readDoubleArray(stateNode, "weights");
        List<Double> means = readDoubleArray(stateNode, "means");
        List<Double> variances = readDoubleArray(stateNode, "variances");
        states.add(new GmmBuckets.GmmState(weights, means, variances));
      }
      buckets.add(new GmmBuckets.GmmBucket(Collections.unmodifiableList(states)));
    }
    return new GmmBuckets(List.copyOf(buckets));
  }

  default JsonNode extractNode(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    return value;
  }

  default String extractText(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    if (!value.isString()) {
      throw new FactoryException("Field '" + field + "' must be textual");
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
      throw new FactoryException("Missing field '" + field + "'");
    }
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "' must be an integer");
    }
    return value.asInt();
  }

  default ZonedDateTime parseTimestamp(String timestamp) {
    try {
      return ZonedDateTime.parse(timestamp);
    } catch (DateTimeParseException e) {
      throw new FactoryException("Unable to parse generated_at timestamp '" + timestamp + "'", e);
    }
  }

  default OptionalInt optionalInt(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isNull()) return OptionalInt.empty();
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "' must be an integer");
    }
    return OptionalInt.of(value.asInt());
  }

  default int[] parseTransitionShape(JsonNode transitionsNode) {
    JsonNode shapeNode = extractNode(transitionsNode, "shape");
    if (!shapeNode.isArray() || shapeNode.size() != 3) {
      throw new FactoryException("Transition shape must contain three dimensions");
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
              + buckets);
    }
    if (rows != stateCount || columns != stateCount) {
      throw new FactoryException(
          "Transition state dimension mismatch. Expected "
              + stateCount
              + " but was rows="
              + rows
              + ", columns="
              + columns);
    }
  }

  default double[][][] parseTransitionValues(JsonNode valuesNode, int buckets, int stateCount) {
    if (!valuesNode.isArray() || valuesNode.size() != buckets) {
      throw new FactoryException(
          "Transition values must be a three dimensional array with " + buckets + " buckets");
    }
    double[][][] values = new double[buckets][stateCount][stateCount];
    for (int b = 0; b < buckets; b++) {
      JsonNode bucketNode = valuesNode.get(b);
      if (!bucketNode.isArray()) {
        throw new FactoryException("Bucket " + b + " in transition values must be an array");
      }
      if (bucketNode.size() != stateCount) {
        throw new FactoryException(
            "Bucket " + b + " contained " + bucketNode.size() + " rows. Expected " + stateCount);
      }
      for (int r = 0; r < stateCount; r++) {
        JsonNode rowNode = bucketNode.get(r);
        if (!rowNode.isArray()) {
          throw new FactoryException(
              "Row " + r + " in bucket " + b + " of transition values must be an array");
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
                  + stateCount);
        }
        for (int c = 0; c < stateCount; c++) {
          values[b][r][c] =
              extractDoubleValue(
                  rowNode.get(c), "data.transitions.values[" + b + "][" + r + "][" + c + "]");
        }
      }
    }
    return values;
  }

  default List<Double> readDoubleArray(JsonNode node, String field) {
    JsonNode arrayNode = node.get(field);
    if (arrayNode == null || !arrayNode.isArray()) {
      throw new FactoryException("Field '" + field + "' must be an array");
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
      throw new FactoryException("Missing field '" + field + "[" + index + "]'");
    }
    if (!value.canConvertToInt()) {
      throw new FactoryException("Field '" + field + "[" + index + "]' must be an integer");
    }
    return value.asInt();
  }

  default double extractDoubleValue(JsonNode node, String field) {
    if (node == null || node.isMissingNode() || node.isNull()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    if (!node.isNumber()) {
      throw new FactoryException("Field '" + field + "' must be a double");
    }
    double value = node.asDouble();
    if (!Double.isFinite(value)) {
      throw new FactoryException("Field '" + field + "' must be finite");
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
      throw new FactoryException("Field '" + field + "' must be an object");
    }
    double value = extractDouble(referenceNode, "value");
    String unit = extractText(referenceNode, "unit");
    return Optional.of(new ValueModel.Normalization.PowerReference(value, unit));
  }
}
