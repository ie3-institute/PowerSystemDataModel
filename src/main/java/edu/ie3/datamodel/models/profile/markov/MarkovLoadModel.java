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

  /**
   * Builds a model from the JSON-derived sections, deriving runtime caches (transition arrays,
   * discretization thresholds, per-state GMM data) and validating overall consistency.
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

  /** Verifies that the parsed transition tensor matches the declared bucket and state counts. */
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

  /**
   * Converts the JSON-shaped {@link GmmBuckets} (lists of doubles) into a runtime {@code
   * GmmStateData[][]} backed by primitive arrays for fast sampling. States with no GMM data are
   * preserved as {@code null} entries; {@link #sanitizeDistribution} routes around them.
   */
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

  /** Converts a JSON power reference into a typed quantity. Only {@code "kW"} is accepted. */
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

  /** Not implemented yet. Future feature */
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

  /** Runs the full five-step simulation pipeline and packages the result for the caller. */
  private PowerValueSource.MarkovOutputValue computeStep(PowerValueSource.MarkovIdentifier data) {
    int bucket = bucketId(data.time());
    int currentState = resolveState(data);
    SplittableRandom rng = new SplittableRandom(deriveSeed(data, bucket, currentState));
    StepResult step = simulateStep(bucket, currentState, rng);
    ComparableQuantity<Power> power = scale(step.normalizedValue());
    return new PowerValueSource.MarkovOutputValue(Optional.of(new PValue(power)), step.nextState());
  }

  // Step 1: Determine time bucket

  /**
   * Maps a timestamp into one of {@code bucketCount} buckets, encoded as {@code month *
   * MONTH_FACTOR + weekendFlag * WEEKEND_FACTOR + quarterHour}. Each bucket has its own transition
   * matrix and per-state GMM, so the result drives every subsequent lookup.
   */
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

  // Step 2: Resolve current state

  /**
   * Resolves the Markov state used as the starting point of this step. If the caller supplied a
   * previous state, it is used directly; otherwise the supplied {@code initialNormalizedValue} is
   * discretized via {@link #discretize}.
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
   * Returns the state-bin index for a normalized value, using the right-edges in {@code
   * discretizationThresholds}. The input is clamped to {@code [0, 1]} to absorb minor
   * floating-point drift on caller-supplied values.
   */
  private int discretize(double normalized) {
    double value = Math.clamp(normalized, 0d, 1d);
    for (int i = 0; i < discretizationThresholds.length; i++) {
      if (value <= discretizationThresholds[i]) {
        return i;
      }
    }
    return discretizationThresholds.length;
  }

  // Step 3: Derive deterministic seed

  /**
   * Produces a deterministic RNG seed for one simulation step by mixing the request seed with the
   * bucket, state and time slot. Identical inputs always yield the same output, which is required
   * for reproducibility across simulation runs.
   */
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

  /**
   * Performs one Markov step: sanitize and renormalize the transition row, draw a next state, then
   * sample its GMM. If no usable transitions remain (empty row, or every reachable state has no GMM
   * data), the model stays in {@code currentState} and emits a normalized value of {@code 0}.
   */
  private StepResult simulateStep(int bucket, int currentState, SplittableRandom rng) {
    double[] row = transitions[bucket][currentState];
    double[] distribution = sanitizeDistribution(bucket, row);
    if (distribution.length == 0) {
      return new StepResult(currentState, 0d);
    }
    int nextStateIndex = drawWeighted(distribution, rng);
    double normalized = sampleNormalizedValue(bucket, nextStateIndex, rng);
    return new StepResult(nextStateIndex, normalized);
  }

  /**
   * Filters the raw transition row so the chain never transitions into a state that lacks GMM data,
   * then renormalizes the remaining probabilities. Returns an empty array if no usable mass
   * remains.
   */
  private double[] sanitizeDistribution(int bucket, double[] row) {
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

  /**
   * Picks an index by weighted random sampling. The {@code weights} array must be non-negative and
   * sum to (approximately) one; used both to draw the next Markov state and to draw a GMM
   * component.
   */
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
   * Draws a normalized power value from the GMM at {@code (bucket, state)}. Returns {@code 0} if
   * that cell has no GMM data. The result is clamped to {@code [0, 1]} because Gaussian tails are
   * unbounded; without clamping, {@link #scale} could return values outside {@code [minPower,
   * maxPower]}.
   */
  private double sampleNormalizedValue(int bucket, int state, SplittableRandom rng) {
    GmmStateData gmm = gmmStates[bucket][state];
    if (gmm == null) {
      return 0d;
    }
    return Math.clamp(gmm.sample(rng), 0d, 1d);
  }

  // Step 5: Scale normalized value to power

  /**
   * Maps a normalized {@code [0, 1]} value into actual power via {@code minPower + value *
   * (maxPower - minPower)}.
   */
  private ComparableQuantity<Power> scale(double normalizedValue) {
    ComparableQuantity<Power> range = maxPowerFromModel.subtract(minPowerFromModel);
    return minPowerFromModel.add(range.multiply(normalizedValue)).asType(Power.class);
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
      int component = drawWeighted(weights, rng);
      double mean = means[component];
      double variance = Math.max(0d, variances[component]);
      if (variance == 0d) {
        return mean;
      }
      return mean + Math.sqrt(variance) * rng.nextGaussian();
    }
  }

  // =====================================================================================
  // Data records (JSON structure)
  // =====================================================================================

  /**
   * Provenance metadata for the trained model: trainer name, version (e.g. a git SHA) and the
   * free-form configuration that produced this JSON.
   */
  public record Generator(String name, String version, Map<String, String> config) {}

  /**
   * Temporal layout of the model: number of buckets, the (documentation-only) encoding formula used
   * by the trainer, the sampling interval in minutes, and the IANA timezone the buckets are defined
   * in.
   */
  public record TimeModel(
      int bucketCount,
      String bucketEncodingFormula,
      int samplingIntervalMinutes,
      String timezone) {}

  /** Value-space configuration: physical unit, normalization range and state discretization. */
  public record ValueModel(
      String valueUnit, Normalization normalization, Discretization discretization) {

    /**
     * Normalization range used to map between normalized {@code [0, 1]} values and physical power.
     * Both {@code maxPower} and {@code minPower} are required at construction time; the optional
     * wrapping only reflects the JSON shape.
     */
    public record Normalization(
        String method, Optional<PowerReference> maxPower, Optional<PowerReference> minPower) {

      /** A value/unit pair from the JSON normalization block. Only {@code "kW"} is supported. */
      public record PowerReference(double value, String unit) {}
    }

    /**
     * State-bin layout: the number of states and the right-edge thresholds that separate
     * neighbouring states. The list contains {@code states - 1} entries.
     */
    public record Discretization(int states, List<Double> thresholdsRight) {}
  }

  /** Optional metadata describing how the trainer produced the transitions and GMMs. */
  public record Parameters(TransitionParameters transitions, GmmParameters gmm) {

    /**
     * Strategy used by the trainer when a transition row had no observations (e.g. {@code
     * "self_loop"}). Carried through for traceability; not used at simulation time.
     */
    public record TransitionParameters(String emptyRowStrategy) {}

    /**
     * GMM-fitting metadata.
     *
     * @param valueColumn name of the column the trainer fit on
     * @param verbose scikit-learn verbosity used during fitting
     * @param heartbeatSeconds stuck-process watchdog interval used by the simonaMarkovLoad Python
     *     trainer: when set, the trainer enables {@code faulthandler.dump_traceback_later} and
     *     dumps a stack trace every N seconds so hangs during GMM fitting can be diagnosed. Carried
     *     through for JSON round-trip only; unused at simulation time.
     */
    public record GmmParameters(
        String valueColumn, Optional<Integer> verbose, Optional<Integer> heartbeatSeconds) {}
  }

  /**
   * The 3D transition tensor {@code values[bucket][currentState][nextState] = probability}, plus
   * the dtype and encoding metadata from the JSON. {@link #equals} and {@link #hashCode} are
   * overridden so two records with equal array contents compare equal; the synthetic record
   * versions would otherwise compare arrays by identity.
   */
  public record TransitionData(String dtype, String encoding, double[][][] values) {
    /** Number of time buckets, i.e. the outer dimension of {@link #values()}. */
    public int bucketCount() {
      return values.length;
    }

    /** Number of states, i.e. the inner dimension of {@link #values()}. */
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
  }

  /**
   * Per-bucket Gaussian Mixture Models. The outer list has {@code bucketCount} entries (one per
   * time bucket); each {@link GmmBucket} has {@code stateCount} {@link GmmState} entries (one per
   * state). A {@link GmmState} may be {@code null} when no observations were available for that
   * (bucket, state) pair.
   */
  public record GmmBuckets(List<GmmBucket> buckets) {
    /** GMM data for one time bucket, indexed by state. Entries may be {@code null}. */
    public record GmmBucket(List<GmmState> states) {}

    /**
     * Parameters of one Gaussian Mixture Model: per-component {@code weights}, {@code means} and
     * {@code variances}. Each component is a 1-D Gaussian; the trainer chooses 1-3 components per
     * (bucket, state) cell using BIC.
     */
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
