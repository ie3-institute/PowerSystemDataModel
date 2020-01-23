/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.thermal;

import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/** A thermal bus, to which different {@link ThermalUnitInput} units may be connected */
public class ThermalBusInput extends AssetInput {
  /**
   * @param uuid Unique identifier of a certain thermal bus
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   */
  public ThermalBusInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id) {
    super(uuid, operationInterval, operator, id);
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   */
  public ThermalBusInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id) {
    super(uuid, operatesFrom, operatesUntil, operator, id);
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   */
  public ThermalBusInput(UUID uuid, String id) {
    super(uuid, id);
  }
}
