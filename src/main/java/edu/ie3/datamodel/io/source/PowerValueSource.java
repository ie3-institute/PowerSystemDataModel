/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Supplier;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Interface defining base functionality for power value sources. */
public sealed interface PowerValueSource<I extends PowerValueSource.PowerValueIdentifier>
    permits PowerValueSource.MarkovBased, PowerValueSource.TimeSeriesBased {

  /** Returns the profile of this source. */
  PowerProfileKey getProfileKey();

  /**
   * Method to get a supplier for the next power value based on the provided input data. Depending
   * on the implementation the supplier will either always return the same value or each time a
   * random value.
   *
   * @param data input data that is used to calculate the next power value.
   * @return A supplier for an option on the value at the given time step.
   */
  Supplier<Optional<PValue>> getValueSupplier(I data);

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
  non-sealed interface TimeSeriesBased extends PowerValueSource<TimeSeriesInputValue> {}

  /** Interface for markov-chain-based power value sources. */
  non-sealed interface MarkovBased extends PowerValueSource<PowerValueIdentifier> {}

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // input data

  /**
   * Interface for the input data of {@link #getValueSupplier(PowerValueIdentifier)}. The data is
   * used to determine the next power.
   */
  sealed interface PowerValueIdentifier permits PowerValueSource.TimeSeriesInputValue {
    /** Returns the timestamp for which a power value is needed. */
    ZonedDateTime time();
  }

  /**
   * Input data for time-series-based power value sources.
   *
   * @param time
   */
  record TimeSeriesInputValue(ZonedDateTime time) implements PowerValueIdentifier {}
}
