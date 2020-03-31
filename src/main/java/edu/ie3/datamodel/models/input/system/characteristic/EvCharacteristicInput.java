/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import java.util.UUID;

public class EvCharacteristicInput extends AssetCharacteristicInput<EvTypeInput> {

  /** @deprecated only added to remove compile error. Please implement a real constructor */
  @Deprecated
  public EvCharacteristicInput(UUID uuid, EvTypeInput type, String characteristic) {
    super(uuid, type, characteristic);
  }

  // TODO please fill the void inside me :'(

  @Override
  public String toString() {
    return "EvCharacteristicInput{"
        + "type="
        + type
        + ", characteristic='"
        + characteristic
        + '\''
        + '}';
  }
}
