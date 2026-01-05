/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.markov;

import com.fasterxml.jackson.databind.JsonNode;
import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.Generator;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.GmmBuckets;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.Parameters;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.TimeModel;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.TransitionData;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.ValueModel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Shared parsing helpers for Markov model JSON documents. This is intentionally package-private as
 * it is only meant to be reused across factory implementations in this package.
 */
interface MarkovModelParsingSupport {

  default Generator parseGenerator(JsonNode generatorNode) {
    String name = requireText(generatorNode, "name");
    String version = requireText(generatorNode, "version");
    Map<String, String> config = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    JsonNode configNode = generatorNode.path("config");
    if (configNode.isObject()) {
      Iterator<Map.Entry<String, JsonNode>> fields = configNode.fields();
      while (fields.hasNext()) {
        Map.Entry<String, JsonNode> entry = fields.next();
        config.put(entry.getKey(), entry.getValue().asText());
      }
    }
    return new Generator(name, version, config);
  }

  default TimeModel parseTimeModel(JsonNode timeNode) {
    int bucketCount = requireInt(timeNode, "bucket_count");
    String formula = requireNode(timeNode, "bucket_encoding").path("formula").asText("");
    if (formula.isEmpty()) {
      throw new FactoryException("Missing bucket encoding formula");
    }
    int samplingInterval = requireInt(timeNode, "sampling_interval_minutes");
    String timezone = requireText(timeNode, "timezone");
    return new TimeModel(bucketCount, formula, samplingInterval, timezone);
  }

  default ValueModel parseValueModel(JsonNode valueNode) {
    String valueUnit = requireText(valueNode, "value_unit");
    JsonNode normalizationNode = requireNode(valueNode, "normalization");
    String normalizationMethod = requireText(normalizationNode, "method");
    ValueModel.Normalization normalization =
        new ValueModel.Normalization(
            normalizationMethod,
            parsePowerReference(normalizationNode, "reference_power"),
            parsePowerReference(normalizationNode, "min_power"));

    JsonNode discretizationNode = requireNode(valueNode, "discretization");
    int states = requireInt(discretizationNode, "states");
    List<Double> thresholds = new ArrayList<>();
    JsonNode thresholdsNode = requireNode(discretizationNode, "thresholds_right");
    if (!thresholdsNode.isArray()) {
      throw new FactoryException("thresholds_right must be an array");
    }
    thresholdsNode.forEach(element -> thresholds.add(element.asDouble()));
    ValueModel.Discretization discretization =
        new ValueModel.Discretization(states, List.copyOf(thresholds));

    return new ValueModel(valueUnit, normalization, discretization);
  }

  default Parameters parseParameters(JsonNode parametersNode) {
    Parameters.TransitionParameters transitions =
        new Parameters.TransitionParameters(
            parametersNode.path("transitions").path("empty_row_strategy").asText(""));
    if (transitions.emptyRowStrategy().isEmpty()) {
      transitions = null;
    }

    JsonNode gmmNode = parametersNode.path("gmm");
    Parameters.GmmParameters gmm =
        gmmNode.isMissingNode() || gmmNode.isNull() || gmmNode.size() == 0
            ? null
            : new Parameters.GmmParameters(
                gmmNode.path("value_col").asText(""),
                optionalInt(gmmNode, "verbose"),
                optionalInt(gmmNode, "heartbeat_seconds"));

    return new Parameters(transitions, gmm);
  }

  default TransitionData parseTransitions(
      JsonNode dataNode, int expectedBucketCount, int stateCount) {
    JsonNode transitionsNode = requireNode(dataNode, "transitions");
    String dtype = requireText(transitionsNode, "dtype");
    String encoding = requireText(transitionsNode, "encoding");

    int[] shape = parseTransitionShape(transitionsNode);
    int buckets = shape[0];
    int rows = shape[1];
    int columns = shape[2];
    validateTransitionShape(expectedBucketCount, stateCount, buckets, rows, columns);

    JsonNode valuesNode = requireNode(transitionsNode, "values");
    double[][][] values = parseTransitionValues(valuesNode, buckets, stateCount);

    return new TransitionData(dtype, encoding, values);
  }

  default GmmBuckets parseGmmBuckets(JsonNode gmmsNode) {
    if (gmmsNode == null || gmmsNode.isMissingNode() || gmmsNode.isNull()) {
      throw new FactoryException("Missing field 'gmms'");
    }
    JsonNode bucketsNode = gmmsNode.get("buckets");
    if (!bucketsNode.isArray()) {
      throw new FactoryException("data.gmms.buckets must be an array");
    }
    List<GmmBuckets.GmmBucket> buckets = new ArrayList<>();
    for (JsonNode bucketNode : bucketsNode) {
      JsonNode statesNode = bucketNode.get("states");
      if (statesNode == null || !statesNode.isArray()) {
        throw new FactoryException("Each GMM bucket must contain an array 'states'");
      }
      List<Optional<GmmBuckets.GmmState>> states = new ArrayList<>();
      for (JsonNode stateNode : statesNode) {
        if (stateNode == null || stateNode.isNull()) {
          states.add(Optional.empty());
          continue;
        }
        List<Double> weights = readDoubleArray(stateNode, "weights");
        List<Double> means = readDoubleArray(stateNode, "means");
        List<Double> variances = readDoubleArray(stateNode, "variances");
        states.add(Optional.of(new GmmBuckets.GmmState(weights, means, variances)));
      }
      buckets.add(new GmmBuckets.GmmBucket(List.copyOf(states)));
    }
    return new GmmBuckets(List.copyOf(buckets));
  }

  default JsonNode requireNode(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    return value;
  }

  default String requireText(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    if (!value.isTextual()) {
      throw new FactoryException("Field '" + field + "' must be textual");
    }
    return value.asText();
  }

  default double requireDouble(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isMissingNode() || value.isNull()) {
      throw new FactoryException("Missing field '" + field + "'");
    }
    if (!value.isNumber()) {
      throw new FactoryException("Field '" + field + "' must be numeric");
    }
    return value.asDouble();
  }

  default int requireInt(JsonNode node, String field) {
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

  default Optional<Integer> optionalInt(JsonNode node, String field) {
    JsonNode value = node.get(field);
    if (value == null || value.isNull()) return Optional.empty();
    return Optional.of(value.asInt());
  }

  default int[] parseTransitionShape(JsonNode transitionsNode) {
    JsonNode shapeNode = requireNode(transitionsNode, "shape");
    if (!shapeNode.isArray() || shapeNode.size() != 3) {
      throw new FactoryException("Transition shape must contain three dimensions");
    }
    return new int[] {shapeNode.get(0).asInt(), shapeNode.get(1).asInt(), shapeNode.get(2).asInt()};
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
    if (!valuesNode.isArray()) {
      throw new FactoryException("Transition values must be a three dimensional array");
    }
    double[][][] values = new double[buckets][stateCount][stateCount];
    int bucketIndex = 0;
    for (JsonNode bucketNode : valuesNode) {
      fillBucket(values, bucketNode, bucketIndex, stateCount);
      bucketIndex++;
    }
    if (bucketIndex != buckets) {
      throw new FactoryException(
          "Transition values provided only " + bucketIndex + " buckets. Expected " + buckets);
    }
    return values;
  }

  default void fillBucket(
      double[][][] values, JsonNode bucketNode, int bucketIndex, int stateCount) {
    if (bucketIndex >= values.length) {
      throw new FactoryException("More transition buckets present than specified in shape");
    }
    int rowIndex = 0;
    for (JsonNode rowNode : bucketNode) {
      fillRow(values, rowNode, bucketIndex, rowIndex, stateCount);
      rowIndex++;
    }
    if (rowIndex != stateCount) {
      throw new FactoryException(
          "Bucket " + bucketIndex + " contained " + rowIndex + " rows. Expected " + stateCount);
    }
  }

  default void fillRow(
      double[][][] values, JsonNode rowNode, int bucketIndex, int rowIndex, int stateCount) {
    if (rowIndex >= stateCount) {
      throw new FactoryException("Too many rows in transition matrix for bucket " + bucketIndex);
    }
    int columnIndex = 0;
    for (JsonNode probNode : rowNode) {
      if (columnIndex >= stateCount) {
        throw new FactoryException(
            "Too many columns in transition matrix for bucket "
                + bucketIndex
                + ", row "
                + rowIndex);
      }
      values[bucketIndex][rowIndex][columnIndex] = probNode.asDouble();
      columnIndex++;
    }
    if (columnIndex != stateCount) {
      throw new FactoryException(
          "Row "
              + rowIndex
              + " in bucket "
              + bucketIndex
              + " had "
              + columnIndex
              + " columns. Expected "
              + stateCount);
    }
  }

  default List<Double> readDoubleArray(JsonNode node, String field) {
    JsonNode arrayNode = node.get(field);
    if (arrayNode == null || !arrayNode.isArray()) {
      throw new FactoryException("Field '" + field + "' must be an array");
    }
    List<Double> values = new ArrayList<>();
    arrayNode.forEach(element -> values.add(element.asDouble()));
    return List.copyOf(values);
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
    double value = requireDouble(referenceNode, "value");
    String unit = requireText(referenceNode, "unit");
    return Optional.of(new ValueModel.Normalization.PowerReference(value, unit));
  }
}
