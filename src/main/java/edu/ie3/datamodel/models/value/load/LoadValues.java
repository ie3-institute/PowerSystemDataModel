/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.datamodel.models.value.Value;
import java.util.Optional;

/** Interface for load values. */
public interface LoadValues extends Value {

  /** Functional interface that is used to provide a {@link PValue}. */
  @FunctionalInterface
  interface Provider extends Value {

    /**
     * Method to provide a {@link PValue}.
     *
     * @param lastOption option for the last value.
     * @return a new value
     */
    PValue provide(Optional<PValue> lastOption);

    /** Provides a {@link PValue}. */
    default PValue provide() {
      return provide(Optional.empty());
    }

    /**
     * Provides a {@link PValue} considering the last value.
     *
     * @param last {@link PValue}.
     * @return a new {@link PValue}
     */
    default PValue withLast(PValue last) {
      return provide(Optional.of(last));
    }
  }
}
