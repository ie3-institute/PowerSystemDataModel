/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import java.util.List;
import java.util.Objects;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Semantic checks of a {@link MarkovLoadModel}. They are applied no matter whether a model was
 * parsed from JSON or built directly, while the checks on the document structure itself stay with
 * the parsing code.
 */
final class MarkovModelValidation {

  private static final double PROBABILITY_TOLERANCE = 1e-5;

  private MarkovModelValidation() {}

  /** Checks that the normalization range of the model is positive. */
  static void validateNormalizationRange(
      ComparableQuantity<Power> minPower, ComparableQuantity<Power> maxPower) {
    if (!maxPower.isGreaterThan(minPower)) {
      throw new IllegalArgumentException(
          "Markov model normalization has non-positive range (max <= min).");
    }
  }

  /**
   * Checks the transition tensor against the dimensions of the model and ensures that every
   * transition row is a probability distribution.
   */
  static void validateTransitionDimensions(
      double[][][] transitions, int bucketCount, int stateCount) {
    if (transitions.length != bucketCount) {
      throw new IllegalArgumentException(
          "Transition bucket count mismatch. Expected "
              + bucketCount
              + " but was "
              + transitions.length);
    }
    for (int bucket = 0; bucket < transitions.length; bucket++) {
      if (transitions[bucket].length != stateCount) {
        throw new IllegalArgumentException(
            "Transition state count mismatch in bucket "
                + bucket
                + ". Expected "
                + stateCount
                + " but was "
                + transitions[bucket].length);
      }
      for (int state = 0; state < transitions[bucket].length; state++) {
        double[] row = transitions[bucket][state];
        if (row.length != stateCount) {
          throw new IllegalArgumentException(
              "Transition next-state count mismatch in bucket "
                  + bucket
                  + ", state "
                  + state
                  + ". Expected "
                  + stateCount
                  + " but was "
                  + row.length);
        }
        validateProbabilityVector(row, "Transition row in bucket " + bucket + ", state " + state);
      }
    }
  }

  /** Checks that the GMM buckets cover every bucket and state of the model. */
  static void validateGmmDimensions(
      List<MarkovLoadModel.GmmBuckets.GmmBucket> buckets, int bucketCount, int stateCount) {
    if (buckets.size() != bucketCount) {
      throw new IllegalArgumentException(
          "GMM bucket count mismatch. Expected " + bucketCount + " but was " + buckets.size());
    }
    for (int bucket = 0; bucket < bucketCount; bucket++) {
      List<MarkovLoadModel.GmmBuckets.GmmState> states = buckets.get(bucket).states();
      if (states.size() != stateCount) {
        throw new IllegalArgumentException(
            "State count mismatch in bucket " + bucket + ". Expected " + stateCount);
      }
    }
  }

  /** Checks that weights, means and variances describe a usable Gaussian Mixture Model. */
  static void validateGmmComponents(
      List<Double> weights, List<Double> means, List<Double> variances) {
    Objects.requireNonNull(weights, "weights");
    Objects.requireNonNull(means, "means");
    Objects.requireNonNull(variances, "variances");

    if (weights.isEmpty()) {
      throw new IllegalArgumentException("GMM state must contain at least one component.");
    }
    if (weights.size() != means.size() || weights.size() != variances.size()) {
      throw new IllegalArgumentException(
          "GMM weights, means and variances must have the same number of entries.");
    }

    validateProbabilityVector(toArray(weights), "GMM component weights");

    for (int i = 0; i < means.size(); i++) {
      Double mean = means.get(i);
      Double variance = variances.get(i);
      if (mean == null || !Double.isFinite(mean)) {
        throw new IllegalArgumentException("GMM means must be finite.");
      }
      if (variance == null || !Double.isFinite(variance)) {
        throw new IllegalArgumentException("GMM variances must be finite.");
      }
      if (variance < 0d) {
        throw new IllegalArgumentException("GMM variances must be non-negative.");
      }
    }
  }

  /** Checks that all entries are non-negative, finite and sum up to one. */
  static void validateProbabilityVector(double[] weights, String context) {
    if (weights.length == 0) {
      throw new IllegalArgumentException(context + " must not be empty.");
    }
    double sum = 0d;
    for (double weight : weights) {
      if (!Double.isFinite(weight)) {
        throw new IllegalArgumentException(context + " contains a non-finite probability.");
      }
      if (weight < 0d) {
        throw new IllegalArgumentException(context + " contains a negative probability.");
      }
      sum += weight;
    }
    if (Math.abs(sum - 1d) > PROBABILITY_TOLERANCE) {
      throw new IllegalArgumentException(context + " must sum to 1.0, but summed to " + sum + ".");
    }
  }

  private static double[] toArray(List<Double> values) {
    double[] array = new double[values.size()];
    for (int i = 0; i < values.size(); i++) {
      array[i] = values.get(i);
    }
    return array;
  }
}
