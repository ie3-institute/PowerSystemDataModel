/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.markov;

import edu.ie3.datamodel.io.source.PowerValueSource.MarkovBased;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.profile.PowerProfile;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.GmmBuckets;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel.ValueModel;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.SplittableRandom;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Implementation of a {@link MarkovBased} power value source that converts a {@link
 * MarkovLoadModel} export into {@link PValue}s.
 */
public class MarkovLoadValueSource implements MarkovBased {

  private static final int QUARTER_HOURS_PER_DAY = 96;
  private static final int WEEKEND_FACTOR = QUARTER_HOURS_PER_DAY;
  private static final int MONTH_FACTOR = QUARTER_HOURS_PER_DAY * 2;

  private final PowerProfile profile;
  private final ZoneId zoneId;
  private final double[][][] transitions;
  private final int bucketCount;
  private final int stateCount;
  private final int samplingIntervalMinutes;
  private final double[] discretizationThresholds;
  private final GmmStateData[][] gmmStates;
  private final ComparableQuantity<Power> maxPowerFromModel;
  private final ComparableQuantity<Power> minPowerFromModel;

  public MarkovLoadValueSource(PowerProfile profile, MarkovLoadModel model) {
    this.profile = Objects.requireNonNull(profile, "profile");
    MarkovLoadModel nonNullModel = Objects.requireNonNull(model, "model");
    this.zoneId = ZoneId.of(nonNullModel.timeModel().timezone());
    this.transitions = nonNullModel.transitionData().values();
    this.bucketCount = nonNullModel.timeModel().bucketCount();
    this.stateCount = nonNullModel.valueModel().discretization().states();
    this.samplingIntervalMinutes = nonNullModel.timeModel().samplingIntervalMinutes();
    this.discretizationThresholds =
        nonNullModel.valueModel().discretization().thresholdsRight().stream()
            .mapToDouble(Double::doubleValue)
            .toArray();
    this.gmmStates =
        buildGmmStates(
            nonNullModel
                .gmmBuckets()
                .orElseThrow(() -> new IllegalArgumentException("Markov model lacks GMM data.")));
    this.maxPowerFromModel =
        nonNullModel
            .valueModel()
            .normalization()
            .maxPower()
            .map(this::convertPowerReference)
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.max_power"));
    this.minPowerFromModel =
        nonNullModel
            .valueModel()
            .normalization()
            .minPower()
            .map(this::convertPowerReference)
            .orElseThrow(
                () -> new IllegalArgumentException("Markov model lacks normalization.min_power"));
  }

  @Override
  public PowerProfile getProfile() {
    return profile;
  }

  @Override
  public MarkovValueSupplier getValueSupplier(MarkovInputValue data) {
    Objects.requireNonNull(data, "data");
    return new MarkovSupplier(data);
  }

  @Override
  public Optional<ZonedDateTime> getNextTimeKey(ZonedDateTime time) {
    return Optional.ofNullable(time).map(t -> t.plusMinutes(samplingIntervalMinutes));
  }

  @Override
  public Optional<ComparableQuantity<Power>> getMaxPower() {
    return Optional.of(maxPowerFromModel);
  }

  @Override
  public Optional<ComparableQuantity<Energy>> getProfileEnergyScaling() {
    return Optional.empty();
  }

  private GmmStateData[][] buildGmmStates(GmmBuckets buckets) {
    List<GmmBuckets.GmmBucket> bucketList = buckets.buckets();
    if (bucketList.size() != bucketCount) {
      throw new IllegalArgumentException(
          "GMM bucket count mismatch. Expected " + bucketCount + " but was " + bucketList.size());
    }
    GmmStateData[][] lookup = new GmmStateData[bucketCount][stateCount];
    for (int bucket = 0; bucket < bucketCount; bucket++) {
      List<java.util.Optional<GmmBuckets.GmmState>> states = bucketList.get(bucket).states();
      if (states.size() != stateCount) {
        throw new IllegalArgumentException(
            "State count mismatch in bucket " + bucket + ". Expected " + stateCount);
      }
      for (int state = 0; state < stateCount; state++) {
        lookup[bucket][state] = states.get(state).map(GmmStateData::from).orElse(null);
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

  private record StepResult(int nextState, double normalizedValue, Optional<PValue> value) {
    private StepResult(int nextState, double normalizedValue) {
      this(nextState, normalizedValue, Optional.empty());
    }
  }

  private static final class GmmStateData {
    private final double[] weights;
    private final double[] means;
    private final double[] variances;

    private GmmStateData(double[] weights, double[] means, double[] variances) {
      this.weights = weights;
      this.means = means;
      this.variances = variances;
    }

    private static GmmStateData from(GmmBuckets.GmmState state) {
      return new GmmStateData(
          toArray(state.weights()), toArray(state.means()), toArray(state.variances()));
    }

    private static double[] toArray(List<Double> values) {
      double[] array = new double[values.size()];
      for (int i = 0; i < values.size(); i++) {
        array[i] = values.get(i);
      }
      return array;
    }

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

    private double nextGaussian(SplittableRandom rng) {
      double u1 = Math.max(Double.MIN_VALUE, rng.nextDouble());
      double u2 = rng.nextDouble();
      return Math.sqrt(-2.0d * Math.log(u1)) * Math.cos(2.0d * Math.PI * u2);
    }
  }

  private final class MarkovSupplier implements MarkovValueSupplier {
    private final MarkovInputValue input;
    private Optional<PValue> cached = Optional.empty();
    private Integer nextState;
    private boolean evaluated;

    private MarkovSupplier(MarkovInputValue input) {
      this.input = input;
    }

    @Override
    public Optional<PValue> get() {
      evaluate();
      return cached;
    }

    @Override
    public int getNextState() {
      evaluate();
      return nextState;
    }

    private void evaluate() {
      if (evaluated) {
        return;
      }
      evaluated = true;
      StepResult result = calculate();
      this.cached = result.value();
      this.nextState = result.nextState();
    }

    private StepResult calculate() {
      int bucket = bucketId(input.time());
      int currentState = resolveState(input);
      SplittableRandom rng = new SplittableRandom(deriveSeed(input, bucket, currentState));
      StepResult step = simulateStep(bucket, currentState, rng);
      ComparableQuantity<Power> power = scale(input.referencePower(), step.normalizedValue());
      return new StepResult(
          step.nextState(), step.normalizedValue(), Optional.of(new PValue(power)));
    }

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

    private int resolveState(MarkovInputValue input) {
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
      double[] sanitized = new double[stateCount];
      double sum = 0d;
      for (int state = 0; state < stateCount; state++) {
        double sanitizedValue = 0d;
        if (state < row.length) {
          double value = row[state];
          if (value > 0d && !Double.isNaN(value) && gmmStates[bucket][state] != null) {
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

    private long deriveSeed(MarkovInputValue input, int bucket, int state) {
      long seed = input.randomSeed();
      seed = 31 * seed + bucket;
      seed = 31 * seed + state;
      long slot =
          input.time().withZoneSameInstant(zoneId).toInstant().toEpochMilli()
              / (samplingIntervalMinutes * 60_000L);
      return 31 * seed + slot;
    }

    private ComparableQuantity<Power> scale(
        ComparableQuantity<Power> referencePower, double normalizedValue) {
      Objects.requireNonNull(referencePower, "referencePower");
      if (!maxPowerFromModel.isGreaterThan(minPowerFromModel)) {
        throw new IllegalStateException(
            "Markov model normalization has non-positive range (max <= min).");
      }
      double clamped = clamp01(normalizedValue);
      ComparableQuantity<Power> range = maxPowerFromModel.subtract(minPowerFromModel);
      return minPowerFromModel.add(range.multiply(clamped)).asType(Power.class);
    }

    private double clamp01(double value) {
      if (value < 0d) return 0d;
      if (value > 1d) return 1d;
      return value;
    }
  }
}
