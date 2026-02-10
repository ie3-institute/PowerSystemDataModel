/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.profile.PowerProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Interface defining base functionality for power value sources. */
public sealed interface PowerValueSource<
        P extends PowerProfile,
        I extends PowerValueSource.PowerValueIdentifier,
        O extends PowerValueSource.PowerOutputValue>
    permits PowerValueSource.MarkovBased, PowerValueSource.TimeSeriesBased {

  /** Returns the profile of this source. */
  P getProfile();

  /**
   * Method to get a supplier for the next power value based on the provided input data. Depending
   * on the implementation the supplier will either always return the same value or each time a
   * random value.
   *
   * @param data input data that is used to calculate the next power value.
   * @return A supplier for an option on the value at the given time step.
   */
  Supplier<O> getValueSupplier(I data);

  /**
   * Method to determine the next timestamp for which data is present.
   *
   * @param time current time
   * @return an option for the next timestamp or {@link Optional#empty()} if no timestamp was found.
   */
  Optional<ZonedDateTime> getNextTimeKey(ZonedDateTime time);

  /** Returns the maximal power that can be returned by this source. */
  Optional<ComparableQuantity<Power>> getMaxPower();

  /** Returns the energy scaling of this power source. */
  Optional<ComparableQuantity<Energy>> getProfileEnergyScaling();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // non-sealed implementations

  /** Interface for time-series-based power value sources. */
  non-sealed interface TimeSeriesBased
      extends PowerValueSource<LoadProfile, TimeSeriesIdentifier, TimeSeriesOutputValue> {}

  /** Interface for markov-chain-based power value sources. */
  non-sealed interface MarkovBased
      extends PowerValueSource<PowerProfile, MarkovIdentifier, MarkovOutputValue> {}

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // input data

  /**
   * Interface for the input data of {@link #getValueSupplier(PowerValueIdentifier)}. The data is
   * used to determine the next power.
   */
  sealed interface PowerValueIdentifier permits TimeSeriesIdentifier, MarkovIdentifier {
    /** Returns the timestamp for which a power value is needed. */
    ZonedDateTime time();
  }

  /**
   * Input data for time-series-based power value sources.
   *
   * @param time
   */
  record TimeSeriesIdentifier(ZonedDateTime time) implements PowerValueIdentifier {}

  /**
   * Input data for Markov-based power value sources, containing everything needed for a single
   * simonaMarkovLoad step.
   *
   * <p>Provide either {@code previousState} (typical for subsequent steps) or an {@code
   * initialNormalizedValue} (for the first step). The {@code randomSeed} enables reproducible
   * sampling.
   */
  record MarkovIdentifier(
      ZonedDateTime time,
      OptionalInt previousState,
      OptionalDouble initialNormalizedValue,
      long randomSeed)
      implements PowerValueIdentifier {

    public MarkovIdentifier {
      Objects.requireNonNull(time, "time");
      Objects.requireNonNull(previousState, "previousState");
      Objects.requireNonNull(initialNormalizedValue, "initialNormalizedValue");
      if (previousState.isEmpty() && initialNormalizedValue.isEmpty()) {
        throw new IllegalArgumentException(
            "Need either previous state or an initial normalized value to start the Markov chain.");
      }
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // output data

  /** Interface for the output data of {@link #getValueSupplier(PowerValueIdentifier)}. */
  sealed interface PowerOutputValue
      permits PowerValueSource.TimeSeriesOutputValue, PowerValueSource.MarkovOutputValue {
    Optional<PValue> value();
  }

  /**
   * Interface for time-series-based power values.
   *
   * @param value
   */
  record TimeSeriesOutputValue(Optional<PValue> value) implements PowerOutputValue {
    public static Supplier<TimeSeriesOutputValue> from(Supplier<Optional<PValue>> supplier) {
      return () -> new TimeSeriesOutputValue(supplier.get());
    }
  }

  /** Output data for Markov-based power values, including the next state. */
  record MarkovOutputValue(Optional<PValue> value, int nextState) implements PowerOutputValue {}
}
