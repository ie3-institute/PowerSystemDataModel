/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
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

    public record Normalization(String method) {}

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
  }

  public record GmmBuckets(List<GmmBucket> buckets) {
    public record GmmBucket(List<Optional<GmmState>> states) {}

    public record GmmState(List<Double> weights, List<Double> means, List<Double> variances) {}
  }
}
