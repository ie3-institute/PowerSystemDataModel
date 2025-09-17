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
import java.util.Optional;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Interface defining base functionality for power value sources. */
public sealed interface PowerValueSource<
        P extends PowerProfile, ID extends PowerValueSource.InputData>
    permits PowerValueSource.MarkovBased, PowerValueSource.TimeSeriesBased {

  /** Returns the profile of this source. */
  P getProfile();

  /**
   * Method to get the next power value based on the provided input data.
   *
   * @param data input data that is used to calculate the next power value.
   * @return an option for the power value.
   */
  Optional<PValue> getPowerValue(ID data);

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
      extends PowerValueSource<LoadProfile, TimeSeriesInputValue> {}

  /** Interface for markov-chain-based power value sources. */
  non-sealed interface MarkovBased extends PowerValueSource<PowerProfile, InputData> {}

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // input data

  /**
   * Interface for the input data of {@link #getPowerValue(InputData)}. The data is used to
   * determine the next power.
   */
  sealed interface InputData permits PowerValueSource.TimeSeriesInputValue {
    /** Returns the timestamp for which a power value is needed. */
    ZonedDateTime getTimes();
  }

  /**
   * Input data for time-series-based power value sources.
   *
   * @param time
   */
  record TimeSeriesInputValue(ZonedDateTime time) implements InputData {
    @Override
    public ZonedDateTime getTimes() {
      return time;
    }
  }
}
