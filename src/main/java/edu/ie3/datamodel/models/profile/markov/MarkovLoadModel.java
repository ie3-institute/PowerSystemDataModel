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
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

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

  /**
   * Flat lookup of the states held by {@link GmmBuckets}, indexed by {@code [bucket][state]}. The
   * nesting of {@link GmmBuckets} mirrors the JSON document, while a simulation step addresses its
   * state by index. Entries are {@code null} where the trainer did not fit a GMM.
   */
  private final GmmBuckets.GmmState[][] gmmStates;

  private final ComparableQuantity<Power> maxPowerFromModel;
  private final ComparableQuantity<Power> minPowerFromModel;

  /**
   * Builds a model from the parsed JSON sections and prepares the runtime lookup arrays.
   *
   * <p>Semantic invariants such as transition row sums and GMM component consistency are checked by
   * {@link MarkovModelValidation}, so directly constructed models are protected just like
   * JSON-parsed models.
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
            .map(power -> power.to(StandardUnits.ACTIVE_POWER_IN))
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.max_power"));
    this.minPowerFromModel =
        valueModel
            .normalization()
            .minPower()
            .map(power -> power.to(StandardUnits.ACTIVE_POWER_IN))
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.min_power"));

    MarkovModelValidation.validateNormalizationRange(minPowerFromModel, maxPowerFromModel);
    MarkovModelValidation.validateTransitionDimensions(transitions, bucketCount, stateCount);
  }

  /**
   * Flattens the nested lists of {@link GmmBuckets}, which mirror the structure of the JSON
   * document, into the {@code [bucket][state]} lookup used at simulation time.
   */
  private GmmBuckets.GmmState[][] buildGmmStates(GmmBuckets buckets) {
    List<GmmBuckets.GmmBucket> bucketList = buckets.buckets();
    MarkovModelValidation.validateGmmDimensions(bucketList, bucketCount, stateCount);

    GmmBuckets.GmmState[][] lookup = new GmmBuckets.GmmState[bucketCount][];
    for (int bucket = 0; bucket < bucketCount; bucket++) {
      lookup[bucket] = bucketList.get(bucket).states().toArray(new GmmBuckets.GmmState[0]);
    }
    return lookup;
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

  /**
   * Not implemented yet. Energy scaling is not supported by the Markov based load model, see <a
   * href="https://github.com/ie3-institute/PowerSystemDataModel/issues/1696">#1696</a>.
   */
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
    double[] row = transitions[bucket][currentState];
    int nextStateIndex = drawWeighted(row.length, i -> row[i], rng);
    double normalized = sampleNormalizedValue(bucket, nextStateIndex, rng);
    return new StepResult(nextStateIndex, normalized);
  }

  /**
   * Picks an index by weighted random sampling. The weights are read through {@code weightAt}, so
   * that both the transition rows and the GMM component weights can be drawn from without copying
   * them into a common representation.
   */
  private static int drawWeighted(int count, IntToDoubleFunction weightAt, SplittableRandom rng) {
    double sample = rng.nextDouble();
    double cumulative = 0d;
    for (int i = 0; i < count; i++) {
      cumulative += weightAt.applyAsDouble(i);
      if (sample <= cumulative) {
        return i;
      }
    }
    return count - 1;
  }

  /**
   * Draws a normalized power value from the GMM at {@code (bucket, state)}. Since the input data is
   * normalized to {@code [0, 1]} over the whole model, Gaussian tails are clamped to that range.
   */
  private double sampleNormalizedValue(int bucket, int state, SplittableRandom rng) {
    GmmBuckets.GmmState gmm = gmmStates[bucket][state];
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
        String method,
        Optional<ComparableQuantity<Power>> maxPower,
        Optional<ComparableQuantity<Power>> minPower) {}

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

  /**
   * Per-bucket Gaussian Mixture Models, nested the way the JSON document is structured. State
   * entries may be {@code null} where the trainer did not fit a GMM.
   */
  public record GmmBuckets(List<GmmBucket> buckets) {
    public record GmmBucket(List<GmmState> states) {}

    /** Parameters of one Gaussian Mixture Model. */
    public record GmmState(List<Double> weights, List<Double> means, List<Double> variances) {
      public GmmState {
        MarkovModelValidation.validateGmmComponents(weights, means, variances);
      }

      /** Draws a normalized value from this mixture. */
      private double sample(SplittableRandom rng) {
        int component = drawWeighted(weights.size(), weights::get, rng);
        double mean = means.get(component);
        double variance = Math.max(0d, variances.get(component));
        if (variance == 0d) {
          return mean;
        }
        return mean + Math.sqrt(variance) * rng.nextGaussian();
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
