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
import java.util.SplittableRandom;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Container for Markov-chain-based load models produced by simonaMarkovLoad.
 *
 * <p>The model bundles the static data (transition matrices, GMM parameters, normalization) with
 * the simulation helpers needed to generate stepwise power values. Each simulation step should use
 * a fresh supplier obtained via {@link #getValueSupplier(PowerValueSource.MarkovIdentifier)}.
 */
public class MarkovLoadModel {

  // Constants

  private static final int QUARTER_HOURS_PER_DAY = 96;
  private static final int WEEKEND_FACTOR = QUARTER_HOURS_PER_DAY;
  private static final int MONTH_FACTOR = QUARTER_HOURS_PER_DAY * 2;

  // Model data

  private final String schema;
  private final ZonedDateTime generatedAt;
  private final Generator generator;
  private final TimeModel timeModel;
  private final ValueModel valueModel;
  private final Parameters parameters;
  private final TransitionData transitionData;
  private final Optional<GmmBuckets> gmmBuckets;

  // Derived runtime fields

  private final ZoneId zoneId;
  private final double[][][] transitions;
  private final int bucketCount;
  private final int stateCount;
  private final int samplingIntervalMinutes;
  private final double[] discretizationThresholds;
  private final GmmStateData[][] gmmStates;
  private final ComparableQuantity<Power> maxPowerFromModel;
  private final ComparableQuantity<Power> minPowerFromModel;

  // =====================================================================================
  // Constructor
  // =====================================================================================

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
    }
  }

  // Constructor helpers

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

  // =====================================================================================
  // Accessors
  // =====================================================================================

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

  // =====================================================================================
  // Public API - Simulation
  // =====================================================================================

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

  /** Markov models do not define an energy scaling factor. */
  public Optional<ComparableQuantity<Energy>> getProfileEnergyScaling() {
    return Optional.empty();
  }

  // =====================================================================================
  // Simulation pipeline
  //   computeStep
  //     1. bucketId        => isWeekend
  //     2. resolveState    => discretize
  //     3. deriveSeed
  //     4. simulateStep    => sanitizeDistribution => drawState => sampleNormalizedValue
  //     5. scale
  // =====================================================================================

  private PowerValueSource.MarkovOutputValue computeStep(PowerValueSource.MarkovIdentifier data) {
    int bucket = bucketId(data.time());
    int currentState = resolveState(data);
    SplittableRandom rng = new SplittableRandom(deriveSeed(data, bucket, currentState));
    StepResult step = simulateStep(bucket, currentState, rng);
    ComparableQuantity<Power> power = scale(step.normalizedValue());
    return new PowerValueSource.MarkovOutputValue(Optional.of(new PValue(power)), step.nextState());
  }

  // Step 1: Determine time bucket

  private int bucketId(ZonedDateTime time) {
    ZonedDateTime zoned = time.withZoneSameInstant(zoneId);
    int month = zoned.getMonthValue() - 1;
    int weekendFlag = isWeekend(zoned) ? 1 : 0;
    int quarterHour = zoned.getHour() * 4 + zoned.getMinute() / 15;
    return Math.floorMod(
        month * MONTH_FACTOR + weekendFlag * WEEKEND_FACTOR + quarterHour, bucketCount);
  }

  private boolean isWeekend(ZonedDateTime time) {
    DayOfWeek day = time.getDayOfWeek();
    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
  }

  // Step 2: Resolve current state

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

  private int discretize(double normalized) {
    double value = clamp01(normalized);
    for (int i = 0; i < discretizationThresholds.length; i++) {
      if (value <= discretizationThresholds[i]) {
        return i;
      }
    }
    return discretizationThresholds.length;
  }

  // Step 3: Derive deterministic seed

  private long deriveSeed(PowerValueSource.MarkovIdentifier input, int bucket, int state) {
    long seed = input.randomSeed();
    seed = 31 * seed + bucket;
    seed = 31 * seed + state;
    long slot =
        input.time().withZoneSameInstant(zoneId).toInstant().toEpochMilli()
            / (samplingIntervalMinutes * 60_000L);
    return 31 * seed + slot;
  }

  // Step 4: Simulate transition and sample value

  private StepResult simulateStep(int bucket, int currentState, SplittableRandom rng) {
    double[] row = transitions[bucket][currentState];
    double[] distribution = sanitizeDistribution(bucket, row);
    if (distribution.length == 0) {
      return new StepResult(currentState, 0d);
    }
    int nextStateIndex = drawState(distribution, rng);
    double normalized = sampleNormalizedValue(bucket, nextStateIndex, rng);
    return new StepResult(nextStateIndex, normalized);
  }

  private double[] sanitizeDistribution(int bucket, double[] row) {
    // Ignore invalid probabilities and states without GMM data, then renormalize.
    double[] sanitized = new double[stateCount];
    double sum = 0d;
    for (int state = 0; state < stateCount; state++) {
      double sanitizedValue = 0d;
      if (state < row.length) {
        double value = row[state];
        if (value > 0d && gmmStates[bucket][state] != null) {
          sanitizedValue = value;
          sum += value;
        }
      }
      sanitized[state] = sanitizedValue;
    }
    if (sum <= 0d) {
      return new double[0];
    }
    for (int i = 0; i < sanitized.length; i++) {
      sanitized[i] /= sum;
    }
    return sanitized;
  }

  private int drawState(double[] distribution, SplittableRandom rng) {
    double sample = rng.nextDouble();
    double cumulative = 0d;
    for (int i = 0; i < distribution.length; i++) {
      cumulative += distribution[i];
      if (sample <= cumulative) {
        return i;
      }
    }
    return distribution.length - 1;
  }

  private double sampleNormalizedValue(int bucket, int state, SplittableRandom rng) {
    GmmStateData gmm = gmmStates[bucket][state];
    if (gmm == null) {
      return 0d;
    }
    return clamp01(gmm.sample(rng));
  }

  // Step 5: Scale normalized value to power

  private ComparableQuantity<Power> scale(double normalizedValue) {
    ComparableQuantity<Power> range = maxPowerFromModel.subtract(minPowerFromModel);
    return minPowerFromModel.add(range.multiply(normalizedValue)).asType(Power.class);
  }

  // Utility

  private double clamp01(double value) {
    if (value < 0d) return 0d;
    if (value > 1d) return 1d;
    return value;
  }

  // =====================================================================================
  // Inner helper types
  // =====================================================================================

  private record StepResult(int nextState, double normalizedValue) {}

  /**
   * Runtime representation of a single GMM state, using primitive arrays for efficient sampling.
   * Converted from {@link GmmBuckets.GmmState} during construction.
   */
  private static final class GmmStateData {
    private final double[] weights;
    private final double[] means;
    private final double[] variances;

    private GmmStateData(double[] weights, double[] means, double[] variances) {
      this.weights = weights;
      this.means = means;
      this.variances = variances;
    }

    /**
     * Draws a random value from this state's Gaussian Mixture Model. First, a mixture component is
     * selected (weighted by {@code weights}), then a value is sampled from that component's normal
     * distribution.
     */
    private double sample(SplittableRandom rng) {
      int component = drawComponent(rng.nextDouble());
      double mean = means[component];
      double variance = Math.max(0d, variances[component]);
      if (variance == 0d) {
        return mean;
      }
      return mean + Math.sqrt(variance) * nextGaussian(rng);
    }

    private int drawComponent(double sample) {
      double cumulative = 0d;
      for (int i = 0; i < weights.length; i++) {
        cumulative += weights[i];
        if (sample <= cumulative) {
          return i;
        }
      }
      return weights.length - 1;
    }

    /** Box-Muller transform to generate a standard normal random variate. */
    private double nextGaussian(SplittableRandom rng) {
      double u1 = Math.max(Double.MIN_VALUE, rng.nextDouble());
      double u2 = rng.nextDouble();
      return Math.sqrt(-2.0d * Math.log(u1)) * Math.cos(2.0d * Math.PI * u2);
    }
  }

  // =====================================================================================
  // Data records (JSON structure)
  // =====================================================================================

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
    public record GmmBucket(List<GmmState> states) {}

    public record GmmState(List<Double> weights, List<Double> means, List<Double> variances) {

      private GmmStateData toStateData() {
        return new GmmStateData(toArray(weights), toArray(means), toArray(variances));
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

  // =====================================================================================
  // equals / hashCode / toString
  // =====================================================================================

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
