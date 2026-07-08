/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import edu.ie3.datamodel.io.source.PowerValueSource;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.SplittableRandom;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Container for Markov-chain-based load models produced by simonaMarkovLoad.
 *
 * <p>The model keeps the trained transition and GMM data together with the helpers used to sample
 * one power value per simulation step.
 */
public class MarkovLoadModel {

  private static final int QUARTER_HOURS_PER_DAY = 96;
  private static final int WEEKEND_FACTOR = QUARTER_HOURS_PER_DAY;
  private static final int MONTH_FACTOR = QUARTER_HOURS_PER_DAY * 2;
  private static final double PROBABILITY_TOLERANCE = 1e-5;

  private final String schema;
  private final ZonedDateTime generatedAt;
  private final Generator generator;
  private final TimeModel timeModel;
  private final ValueModel valueModel;
  private final Parameters parameters;
  private final TransitionData transitionData;
  private final Optional<GmmBuckets> gmmBuckets;

  private final ZoneId zoneId;
  private final double[][][] transitions;
  private final int bucketCount;
  private final int stateCount;
  private final int samplingIntervalMinutes;
  private final double[] discretizationThresholds;
  private final GmmStateData[][] gmmStates;
  private final ComparableQuantity<Power> maxPowerFromModel;
  private final ComparableQuantity<Power> minPowerFromModel;

  /**
   * Builds a model from the parsed JSON sections and prepares the runtime lookup arrays.
   *
   * <p>Semantic invariants such as transition row sums and GMM component consistency are checked
   * here as well, so directly constructed models are protected just like JSON-parsed models.
   *
   * @throws IllegalArgumentException if GMM data is missing, the normalization range is
   *     non-positive, or the transition tensor does not match {@code bucketCount * stateCount *
   *     stateCount}
   */
  public MarkovLoadModel(
      String schema,
      ZonedDateTime generatedAt,
      Generator generator,
      TimeModel timeModel,
      ValueModel valueModel,
      Parameters parameters,
      TransitionData transitionData,
      Optional<GmmBuckets> gmmBuckets) {
    this.schema = Objects.requireNonNull(schema, "schema");
    this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt");
    this.generator = Objects.requireNonNull(generator, "generator");
    this.timeModel = Objects.requireNonNull(timeModel, "timeModel");
    this.valueModel = Objects.requireNonNull(valueModel, "valueModel");
    this.parameters = Objects.requireNonNull(parameters, "parameters");
    this.transitionData = Objects.requireNonNull(transitionData, "transitionData");
    this.gmmBuckets = Objects.requireNonNull(gmmBuckets, "gmmBuckets");

    this.zoneId = ZoneId.of(timeModel.timezone());
    this.transitions = transitionData.values();
    this.bucketCount = timeModel.bucketCount();
    this.stateCount = valueModel.discretization().states();
    this.samplingIntervalMinutes = timeModel.samplingIntervalMinutes();
    this.discretizationThresholds =
        valueModel.discretization().thresholdsRight().stream()
            .mapToDouble(Double::doubleValue)
            .toArray();
    this.gmmStates =
        buildGmmStates(
            gmmBuckets.orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks GMM data.")));
    this.maxPowerFromModel =
        valueModel
            .normalization()
            .maxPower()
            .map(this::convertPowerReference)
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.max_power"));
    this.minPowerFromModel =
        valueModel
            .normalization()
            .minPower()
            .map(this::convertPowerReference)
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.min_power"));

    if (!this.maxPowerFromModel.isGreaterThan(this.minPowerFromModel)) {
      throw new IllegalArgumentException(
          "Markov model normalization has non-positive range (max <= min).");
    }

    validateTransitionDimensions();
  }

  private void validateTransitionDimensions() {
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

  private static void validateProbabilityVector(double[] weights, String context) {
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

  private GmmStateData[][] buildGmmStates(GmmBuckets buckets) {
    List<GmmBuckets.GmmBucket> bucketList = buckets.buckets();
    if (bucketList.size() != bucketCount) {
      throw new IllegalArgumentException(
          "GMM bucket count mismatch. Expected " + bucketCount + " but was " + bucketList.size());
    }
    GmmStateData[][] lookup = new GmmStateData[bucketCount][stateCount];
    for (int bucket = 0; bucket < bucketCount; bucket++) {
      List<GmmBuckets.GmmState> states = bucketList.get(bucket).states();
      if (states.size() != stateCount) {
        throw new IllegalArgumentException(
            "State count mismatch in bucket " + bucket + ". Expected " + stateCount);
      }
      for (int state = 0; state < stateCount; state++) {
        GmmBuckets.GmmState s = states.get(state);
        lookup[bucket][state] = s != null ? s.toStateData() : null;
      }
    }
    return lookup;
  }

  private ComparableQuantity<Power> convertPowerReference(
      ValueModel.Normalization.PowerReference reference) {
    if (!"kW".equalsIgnoreCase(reference.unit())) {
      throw new IllegalArgumentException(
          "Unsupported reference power unit '" + reference.unit() + "'. Only kW is supported.");
    }
    return Quantities.getQuantity(reference.value(), StandardUnits.ACTIVE_POWER_IN);
  }

  public String schema() {
    return schema;
  }

  public ZonedDateTime generatedAt() {
    return generatedAt;
  }

  public Generator generator() {
    return generator;
  }

  public TimeModel timeModel() {
    return timeModel;
  }

  public ValueModel valueModel() {
    return valueModel;
  }

  public Parameters parameters() {
    return parameters;
  }

  public TransitionData transitionData() {
    return transitionData;
  }

  public Optional<GmmBuckets> gmmBuckets() {
    return gmmBuckets;
  }

  /**
   * Returns a supplier for a single Markov step.
   *
   * <p>Callers are expected to create a new supplier for each time step.
   */
  public Supplier<PowerValueSource.MarkovOutputValue> getValueSupplier(
      PowerValueSource.MarkovIdentifier data) {
    Objects.requireNonNull(data, "data");
    return () -> computeStep(data);
  }

  /** Convenience helper to compute a single step immediately. */
  public PowerValueSource.MarkovOutputValue getPower(PowerValueSource.MarkovIdentifier data) {
    return getValueSupplier(data).get();
  }

  /** Returns the next timestamp by advancing the model's sampling interval. */
  public Optional<ZonedDateTime> getNextTimeKey(ZonedDateTime time) {
    return Optional.ofNullable(time).map(t -> t.plusMinutes(samplingIntervalMinutes));
  }

  /** Returns the maximum power from the model's normalization configuration. */
  public Optional<ComparableQuantity<Power>> getMaxPower() {
    return Optional.of(maxPowerFromModel);
  }

  /** Not implemented yet. Future feature */
  public Optional<ComparableQuantity<Energy>> getProfileEnergyScaling() {
    return Optional.empty();
  }

  private PowerValueSource.MarkovOutputValue computeStep(PowerValueSource.MarkovIdentifier data) {
    int bucket = bucketId(data.time());
    int currentState = resolveState(data);
    SplittableRandom rng = new SplittableRandom(deriveSeed(data, bucket, currentState));
    StepResult step = simulateStep(bucket, currentState, rng);
    ComparableQuantity<Power> power = scale(step.normalizedValue());
    return new PowerValueSource.MarkovOutputValue(Optional.of(new PValue(power)), step.nextState());
  }

  /** Maps a timestamp into the trainer's bucket layout: month, weekday/weekend and quarter-hour. */
  private int bucketId(ZonedDateTime time) {
    ZonedDateTime zoned = time.withZoneSameInstant(zoneId);
    int month = zoned.getMonthValue() - 1;
    int weekendFlag = isWeekend(zoned) ? 1 : 0;
    int quarterHour = zoned.getHour() * 4 + zoned.getMinute() / 15;
    return Math.floorMod(
        month * MONTH_FACTOR + weekendFlag * WEEKEND_FACTOR + quarterHour, bucketCount);
  }

  /** True for Saturdays and Sundays. */
  private boolean isWeekend(ZonedDateTime time) {
    DayOfWeek day = time.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
  }

  /**
   * Uses the previous state if present; otherwise discretizes the initial normalized value.
   *
   * @throws IllegalArgumentException if a supplied previous state is out of bounds
   */
  private int resolveState(PowerValueSource.MarkovIdentifier input) {
    if (input.previousState().isPresent()) {
      int state = input.previousState().getAsInt();
      if (state < 0 || state >= stateCount) {
        throw new IllegalArgumentException("Previous state out of bounds: " + state);
      }
      return state;
    }
    double normalized = input.initialNormalizedValue().orElseThrow();
    return discretize(normalized);
  }

  /**
   * Returns the state-bin index for a normalized value. Boundary values map to the upper bin,
   * matching the trainer's {@code searchsorted(side="right")} assignment.
   */
  private int discretize(double normalized) {
    double value = Math.clamp(normalized, 0d, 1d);
    for (int i = 0; i < discretizationThresholds.length; i++) {
      if (value < discretizationThresholds[i]) {
        return i;
      }
    }
    return discretizationThresholds.length;
  }

  /** Produces a deterministic RNG seed from request seed, bucket, state and time slot. */
  private long deriveSeed(PowerValueSource.MarkovIdentifier input, int bucket, int state) {
    long seed = input.randomSeed();
    seed = 31 * seed + bucket;
    seed = 31 * seed + state;
    long slot = input.time().toInstant().toEpochMilli() / (samplingIntervalMinutes * 60_000L);
    return 31 * seed + slot;
  }

  /** Draws the next state from the transition row and samples the corresponding GMM. */
  private StepResult simulateStep(int bucket, int currentState, SplittableRandom rng) {
    int nextStateIndex = drawWeighted(transitions[bucket][currentState], rng);
    double normalized = sampleNormalizedValue(bucket, nextStateIndex, rng);
    return new StepResult(nextStateIndex, normalized);
  }

  /** Picks an index by weighted random sampling. */
  private static int drawWeighted(double[] weights, SplittableRandom rng) {
    double sample = rng.nextDouble();
    double cumulative = 0d;
    for (int i = 0; i < weights.length; i++) {
      cumulative += weights[i];
      if (sample <= cumulative) {
        return i;
      }
    }
    return weights.length - 1;
  }

  /**
   * Draws a normalized power value from the GMM at {@code (bucket, state)} and clamps Gaussian
   * tails to the model range.
   */
  private double sampleNormalizedValue(int bucket, int state, SplittableRandom rng) {
    GmmStateData gmm = gmmStates[bucket][state];
    if (gmm == null) {
      return 0d;
    }
    return Math.clamp(gmm.sample(rng), 0d, 1d);
  }

  /** Maps a normalized value to physical power using the model's min/max range. */
  private ComparableQuantity<Power> scale(double normalizedValue) {
    ComparableQuantity<Power> range = maxPowerFromModel.subtract(minPowerFromModel);
    return minPowerFromModel.add(range.multiply(normalizedValue)).asType(Power.class);
  }

  private record StepResult(int nextState, double normalizedValue) {}

  /** Runtime representation of a single GMM state. */
  private static final class GmmStateData {
    private final double[] weights;
    private final double[] means;
    private final double[] variances;

    private GmmStateData(double[] weights, double[] means, double[] variances) {
      this.weights = weights;
      this.means = means;
      this.variances = variances;
    }

    private double sample(SplittableRandom rng) {
      int component = drawWeighted(weights, rng);
      double mean = means[component];
      double variance = Math.max(0d, variances[component]);
      if (variance == 0d) {
        return mean;
      }
      return mean + Math.sqrt(variance) * rng.nextGaussian();
    }
  }

  /** Provenance metadata for the trained model. */
  public record Generator(String name, String version, Map<String, String> config) {}

  /** Temporal layout of the model. */
  public record TimeModel(
      int bucketCount,
      String bucketEncodingFormula,
      int samplingIntervalMinutes,
      String timezone) {}

  /** Value-space configuration: physical unit, normalization range and state discretization. */
  public record ValueModel(
      String valueUnit, Normalization normalization, Discretization discretization) {

    /**
     * Normalization range used to map between normalized values and physical power. The optional
     * wrapping reflects the JSON shape, both bounds are required at construction time.
     */
    public record Normalization(
        String method, Optional<PowerReference> maxPower, Optional<PowerReference> minPower) {

      /** A value/unit pair from the JSON normalization block. Only {@code "kW"} is supported. */
      public record PowerReference(double value, String unit) {}
    }

    /** State-bin layout and right-edge thresholds. */
    public record Discretization(int states, List<Double> thresholdsRight) {}
  }

  /** Optional metadata describing how the trainer produced the transitions and GMMs. */
  public record Parameters(
      Optional<TransitionParameters> transitions, Optional<GmmParameters> gmm) {

    /**
     * Strategy used by the trainer when a transition row had no observations. Carried through for
     * traceability. Not used at simulation time.
     */
    public record TransitionParameters(String emptyRowStrategy) {}

    /**
     * GMM-fitting metadata.
     *
     * @param valueColumn name of the column the trainer fit on
     * @param verbose scikit-learn verbosity used during fitting
     * @param heartbeatSeconds trainer watchdog interval, carried through for JSON round-trip only
     */
    public record GmmParameters(
        String valueColumn, OptionalInt verbose, OptionalInt heartbeatSeconds) {}
  }

  /**
   * Transition tensor {@code values[bucket][currentState][nextState] = probability}. Equality uses
   * array contents instead of array identity.
   */
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
      return o instanceof TransitionData(String d, String e, double[][][] v)
          && Objects.equals(dtype, d)
          && Objects.equals(encoding, e)
          && Arrays.deepEquals(values, v);
    }

    @Override
    public int hashCode() {
      return Objects.hash(dtype, encoding, Arrays.deepHashCode(values));
    }

    @Override
    public String toString() {
      return "TransitionData[dtype="
          + dtype
          + ", encoding="
          + encoding
          + ", values="
          + Arrays.deepToString(values)
          + "]";
    }
  }

  /** Per-bucket Gaussian Mixture Models. State entries may be {@code null}. */
  public record GmmBuckets(List<GmmBucket> buckets) {
    public record GmmBucket(List<GmmState> states) {}

    /** Parameters of one Gaussian Mixture Model. */
    public record GmmState(List<Double> weights, List<Double> means, List<Double> variances) {
      public GmmState {
        validateGmmComponents(weights, means, variances);
      }

      private GmmStateData toStateData() {
        return new GmmStateData(toArray(weights), toArray(means), toArray(variances));
      }

      private static void validateGmmComponents(
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

        double[] weightArray = toArray(weights);
        validateProbabilityVector(weightArray, "GMM component weights");

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

      private static double[] toArray(List<Double> values) {
        double[] array = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
          array[i] = values.get(i);
        }
        return array;
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MarkovLoadModel that)) return false;
    return Objects.equals(schema, that.schema)
        && Objects.equals(generatedAt, that.generatedAt)
        && Objects.equals(generator, that.generator)
        && Objects.equals(timeModel, that.timeModel)
        && Objects.equals(valueModel, that.valueModel)
        && Objects.equals(parameters, that.parameters)
        && Objects.equals(transitionData, that.transitionData)
        && Objects.equals(gmmBuckets, that.gmmBuckets);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        schema,
        generatedAt,
        generator,
        timeModel,
        valueModel,
        parameters,
        transitionData,
        gmmBuckets);
  }

  @Override
  public String toString() {
    return "MarkovLoadModel["
        + "schema="
        + schema
        + ", generatedAt="
        + generatedAt
        + ", generator="
        + generator
        + ", timeModel="
        + timeModel
        + ", valueModel="
        + valueModel
        + ", parameters="
        + parameters
        + ", transitionData="
        + transitionData
        + ", gmmBuckets="
        + gmmBuckets
        + ']';
  }
}
