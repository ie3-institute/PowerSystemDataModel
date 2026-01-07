/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Container for Markov-chain-based load models produced by simonaMarkovLoad. */
public record MarkovLoadModel(
    String schema,
    ZonedDateTime generatedAt,
    Generator generator,
    TimeModel timeModel,
    ValueModel valueModel,
    Parameters parameters,
    TransitionData transitionData,
    Optional<GmmBuckets> gmmBuckets) {

  public record Generator(String name, String version, Map<String, String> config) {}

  public record TimeModel(
      int bucketCount,
      String bucketEncodingFormula,
      int samplingIntervalMinutes,
      String timezone) {}

  public record ValueModel(
      String valueUnit, Normalization normalization, Discretization discretization) {

    public record Normalization(
        String method, Optional<PowerReference> maxPower, Optional<PowerReference> minPower) {

      public record PowerReference(double value, String unit) {}
    }

    public record Discretization(int states, List<Double> thresholdsRight) {}
  }

  public record Parameters(TransitionParameters transitions, GmmParameters gmm) {

    public record TransitionParameters(String emptyRowStrategy) {}

    public record GmmParameters(
        String valueColumn, Optional<Integer> verbose, Optional<Integer> heartbeatSeconds) {}
  }

  public record TransitionData(String dtype, String encoding, double[][][] values) {
    public int bucketCount() {
      return values.length;
    }

    public int stateCount() {
      return values.length == 0 ? 0 : values[0].length;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TransitionData other)) return false;
      return Objects.equals(dtype, other.dtype)
          && Objects.equals(encoding, other.encoding)
          && Arrays.deepEquals(values, other.values);
    }

    @Override
    public int hashCode() {
      return Objects.hash(dtype, encoding, Arrays.deepHashCode(values));
    }

    @Override
    public String toString() {
      return "TransitionData{"
          + "dtype='"
          + dtype
          + '\''
          + ", encoding='"
          + encoding
          + '\''
          + ", values="
          + Arrays.deepToString(values)
          + '}';
    }
  }

  public record GmmBuckets(List<GmmBucket> buckets) {
    public record GmmBucket(List<Optional<GmmState>> states) {}

    public record GmmState(List<Double> weights, List<Double> means, List<Double> variances) {}
  }
}
